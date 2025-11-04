# Parallel Execution with Git Worktrees

This document describes the future enhancement for parallel task execution using git worktrees.

## Current Limitation

**Sequential Mode Only**: Tasks are processed one at a time to avoid workspace conflicts.

```
Task 30 (0-11 min) → Task 31 (11-22 min) → Task 32 (22-33 min)
Total Time: 33 minutes for 3 tasks
```

## The Problem with Shared Workspace

When multiple agents work on the same repository simultaneously:

```
workspace/
├── src/
│   └── File.kt  ← Agent 30 editing
│                ← Agent 31 editing (CONFLICT!)
├── build/
└── .git/
```

**Issues**:
- File system conflicts (two agents editing same file)
- Git state conflicts (different branches checked out)
- Build artifacts colliding
- Test execution interfering with each other

## Solution: Git Worktrees

Git worktrees create multiple working directories for the same repository:

```
scorecount/                    # Main workspace
├── src/
└── .git/
    └── worktrees/            # Worktree metadata

../scorecount-task-30/        # Isolated workspace for task 30
├── src/
└── .git → scorecount/.git    # Links to main .git

../scorecount-task-31/        # Isolated workspace for task 31
├── src/
└── .git → scorecount/.git    # Links to main .git
```

**Benefits**:
- Independent file systems (no conflicts)
- Separate branches checked out
- Isolated build artifacts
- Shared git history (efficient)
- Easy cleanup

## Implementation Design

### 1. Worktree Creation

When `/autoImplement --parallel 30, 31, 32` is invoked:

```bash
# For each task ID
for TASK_ID in 30 31 32; do
  # Create worktree in parent directory
  WORKTREE_PATH="../scorecount-task-${TASK_ID}"
  BRANCH_NAME="[type]/task-${TASK_ID}-[short-desc]"

  # Create worktree with new branch
  git worktree add "$WORKTREE_PATH" -b "$BRANCH_NAME" origin/main

  # Copy workflow state into worktree
  mkdir -p "${WORKTREE_PATH}/.claude/workflow/task-${TASK_ID}"
done
```

### 2. Agent Isolation

Each agent works in its own worktree:

```markdown
Analyzer for Task 30:
- Working directory: ../scorecount-task-30
- Branch: feature/task-30-reduce-params
- State: ../scorecount-task-30/.claude/workflow/task-30/

Builder for Task 31:
- Working directory: ../scorecount-task-31
- Branch: feature/task-31-reduce-complexity
- State: ../scorecount-task-31/.claude/workflow/task-31/

Reviewer for Task 32:
- Working directory: ../scorecount-task-32
- Branch: chore/task-32-naming-conventions
- State: ../scorecount-task-32/.claude/workflow/task-32/
```

### 3. Parallel Execution

```
Time 0:  [Analyzer-30] [Analyzer-31] [Analyzer-32]  ← All analyzing in parallel
         ↓ worktree-30  ↓ worktree-31  ↓ worktree-32

Time 5:  [Builder-30]  [Builder-31]  [Builder-32]   ← All building in parallel
         ↓ worktree-30  ↓ worktree-31  ↓ worktree-32

Time 15: [Reviewer-30] [Reviewer-31] [Reviewer-32]  ← All reviewing in parallel
         ↓ worktree-30  ↓ worktree-31  ↓ worktree-32

Time 18: All PRs created

Total Time: ~18 minutes for 3 tasks (vs. 33 sequential)
```

### 4. Orchestrator Changes

```bash
# Enhanced orchestrator for parallel mode
if [[ "$MODE" == "parallel" ]]; then
  # Create worktrees
  for TASK_ID in $TASK_IDS; do
    create_worktree_for_task $TASK_ID
  done

  # Spawn all Analyzer agents in parallel
  for TASK_ID in $TASK_IDS; do
    spawn_analyzer_in_worktree $TASK_ID &
  done

  # Wait for all Analyzers to complete
  wait

  # Spawn all Builder agents in parallel
  for TASK_ID in $TASK_IDS; do
    if [[ $(get_task_decision $TASK_ID) == "APPROVE" ]]; then
      spawn_builder_in_worktree $TASK_ID &
    fi
  done

  # Wait for all Builders
  wait

  # Spawn all Reviewer agents in parallel
  for TASK_ID in $TASK_IDS; do
    if [[ $(get_task_status $TASK_ID) == "REVIEWING" ]]; then
      spawn_reviewer_in_worktree $TASK_ID &
    fi
  done

  # Wait for all Reviewers
  wait

  # Cleanup worktrees
  for TASK_ID in $TASK_IDS; do
    cleanup_worktree $TASK_ID
  done
fi
```

### 5. Conflict Detection

Even with worktrees, tasks might touch the same files:

```bash
# Before spawning parallel agents, analyze task overlap
detect_file_overlap() {
  local TASK_IDS=$1

  # Quick heuristic: Check issue descriptions for file mentions
  # Better: Use Analyzer's file list from PLAN.md

  for TASK_A in $TASK_IDS; do
    for TASK_B in $TASK_IDS; do
      if [[ $TASK_A != $TASK_B ]]; then
        FILES_A=$(extract_files_from_plan $TASK_A)
        FILES_B=$(extract_files_from_plan $TASK_B)

        OVERLAP=$(comm -12 <(echo "$FILES_A") <(echo "$FILES_B"))

        if [[ -n "$OVERLAP" ]]; then
          echo "Warning: Tasks $TASK_A and $TASK_B may conflict on: $OVERLAP"
          # Option 1: Run these sequentially
          # Option 2: Let human decide
          # Option 3: Continue anyway, handle merge conflicts later
        fi
      fi
    done
  done
}
```

### 6. Cleanup

After completion:

```bash
cleanup_worktree() {
  local TASK_ID=$1
  local WORKTREE_PATH="../scorecount-task-${TASK_ID}"

  # Copy artifacts back to main workspace for archival
  cp -r "${WORKTREE_PATH}/.claude/workflow/task-${TASK_ID}" \
        ".claude/workflow/task-${TASK_ID}"

  # Remove worktree
  git worktree remove "$WORKTREE_PATH" --force

  # Optionally delete branch if PR wasn't created (rejected/escalated)
  if [[ $(get_task_status $TASK_ID) != "COMPLETE" ]]; then
    git branch -D "[type]/task-${TASK_ID}-[desc]"
  fi
}
```

## Benefits

### Performance
```
Sequential:  N_tasks × ~11 min = 33 min for 3 tasks
Parallel:    max(task_durations) = ~18 min for 3 tasks

Speedup: ~1.8x for 3 tasks (varies based on task complexity)
```

### Resource Utilization
- Better CPU utilization (multiple agents working)
- Better I/O utilization (parallel file operations)
- Better API utilization (parallel GitHub API calls)

### Developer Experience
- Multiple small PRs ready at once
- Faster feedback on batch of tasks
- Can review related changes together

## Challenges

### 1. Merge Order Dependencies

If task 31 depends on changes from task 30:

```
Task 30: Adds new function foo()
Task 31: Uses function foo()

If both implemented in parallel:
- Task 31 won't see foo() during implementation
- Build will fail or tests will fail
- Need to detect and sequence these
```

**Solution**: Dependency detection in planning phase
```
# Analyzer for task 31 checks open PRs using GitHub MCP tool
# Use mcp__github__list_pull_requests with:
# - owner: Repository owner
# - repo: Repository name
# - state: "open"

# If finds task 30's PR touches files task 31 needs:
# → Mark as DEPENDENT on task 30
# → Run task 31 after task 30 completes
```

### 2. Test Flakiness

Parallel test execution might cause flakiness:
- Database access conflicts
- Port conflicts
- Race conditions in tests

**Solution**: Each worktree runs tests independently
- No shared test state
- Each has own build directory
- Proper test isolation already in place

### 3. Resource Consumption

Running 3 agents in parallel:
- 3× memory usage
- 3× CPU load
- 3× API calls

**Mitigation**:
- Limit max parallel tasks (e.g., 3-5)
- Monitor system resources
- Adjust based on machine capacity

### 4. Escalation Handling

If task 30 escalates mid-execution:

```
Time 10: Task 30 escalates (stuck in Builder loop)
         Tasks 31, 32 still running

Action: Let 31 and 32 complete independently
```

**No cascade failures** - each task is independent.

## Worktree Commands Reference

### Create Worktree
```bash
git worktree add <path> -b <branch> <base-commit>

# Example
git worktree add ../scorecount-task-30 -b feature/task-30-params origin/main
```

### List Worktrees
```bash
git worktree list

# Output:
# /Users/victor/scorecount              abc123 [main]
# /Users/scorecount-task-30             def456 [feature/task-30-params]
# /Users/scorecount-task-31             ghi789 [feature/task-31-complexity]
```

### Remove Worktree
```bash
git worktree remove <path>

# Example
git worktree remove ../scorecount-task-30
```

### Prune Stale Worktrees
```bash
git worktree prune
```

## Implementation Phases

### Phase 1: Sequential Mode (Current)
- ✓ Single task at a time
- ✓ No workspace conflicts
- ✓ Simple state management
- ✓ Easy to debug

### Phase 2: Parallel Mode with Worktrees
- Create worktree infrastructure
- Modify orchestrator for parallel spawning
- Add conflict detection
- Test with non-overlapping tasks

### Phase 3: Intelligent Scheduling
- Dependency detection
- Automatic sequencing of dependent tasks
- Parallel execution of independent tasks
- Resource-aware scheduling (limit to N parallel)

### Phase 4: Advanced Features
- Dynamic work stealing (fast tasks help slow tasks)
- Priority queues (urgent bugs before features)
- Incremental results (show PRs as they complete)
- Resume after failure (restart failed task without redoing successful ones)

## Testing Plan

### 1. Test with Simple Independent Tasks
```bash
/autoImplement --parallel 32, 33  # Both are test file cleanups
```

Expected:
- No file overlap
- Both complete successfully
- ~50% time reduction

### 2. Test with Related Tasks
```bash
/autoImplement --parallel 30, 31  # Both touch UI composables
```

Expected:
- Some file overlap
- Possible merge conflicts
- Need to detect and sequence

### 3. Test with Mixed Complexity
```bash
/autoImplement --parallel 33, 30, 31
```

Expected:
- Task 33 completes quickly (~8 min)
- Tasks 30, 31 take longer (~15 min)
- Total time = max(durations) not sum

## Rollout Strategy

1. **Build Sequential Mode**: Proven, simple foundation
2. **Dogfood on Simple Tasks**: Gain confidence with single-task runs
3. **Add Worktree Support**: Infrastructure for parallel
4. **Test Parallel on Safe Tasks**: Independent, small tasks
5. **Add Conflict Detection**: Prevent overlap issues
6. **Full Rollout**: Default to parallel for independent tasks

## Future: Multi-Repository Support

Worktrees enable even more:

```bash
# Multiple projects
git worktree add ../project-A/scorecount -b feature/cross-project
git worktree add ../project-B/otherrepo -b feature/cross-project

# Agents work across repos
# Coordinate changes in multiple codebases
# Create PRs in all affected repos
```

---

**Status**: Design complete, ready for Phase 2 implementation

**Next Steps**:
1. Validate Phase 1 (sequential) works well
2. Implement worktree infrastructure
3. Test with task #32 and #33 (minimal overlap)
4. Gather feedback and iterate

**Decision Point**: Implement after sequential mode proves stable
