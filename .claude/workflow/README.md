# Multi-Agent Workflow System

This directory contains the autonomous task implementation workflow system for Score-Count.

## Overview

This workflow system uses a multi-agent architecture to autonomously implement and maintain GitHub issues:

### Core Workflow (`/autoImplement`)

1. **Analyzer** - Critical evaluation and planning (read-only)
2. **Builder** - Implementation and testing (write access)
3. **Reviewer** - Validation and PR creation (review access)

### Human-in-the-Loop Iteration (`/maintainPr`)

4. **Maintainer** - Address human PR feedback (coordination + write access)

## Directory Structure

```
.claude/workflow/
├── README.md                          # This file
├── templates/                         # Artifact templates
│   ├── PLAN.md.template              # Analyzer output template
│   ├── IMPLEMENTATION.md.template    # Builder output template
│   ├── REVIEW.md.template            # Reviewer output template
│   └── COMPLETION.md.template        # Completion summary template
└── task-{id}/                        # Per-task workspace (created during execution)
    ├── state.yml                     # Workflow state tracking
    ├── PLAN.md                       # Analyzer's plan
    ├── IMPLEMENTATION.md             # Builder's report
    ├── REVIEW.md                     # Reviewer's validation
    ├── COMPLETION.md                 # Workflow summary
    ├── MAINTENANCE.md                # Maintainer's feedback report (if /maintainPr used)
    └── ESCALATION.md                 # Escalation details (if escalated)
```

## Workflow States

```
INITIALIZED     → Task workspace created
ANALYZING       → Analyzer evaluating task
PLANNING        → Analyzer creating plan
BUILDING        → Builder implementing
REVIEWING       → Reviewer validating
COMPLETE        → PR created, task done
REJECTED        → Analyzer rejected task
ESCALATED       → Human intervention needed
```

## State Transitions

```
INITIALIZED
    ↓
ANALYZING ──(REJECT)──→ REJECTED
    ↓
PLANNING
    ↓
BUILDING ←──────────────┐ (iteration, max 3)
    ↓                   │
    ├──(PLAN_REVISION)──┘
    ├──(ESCALATE)──→ ESCALATED
    ↓
REVIEWING
    ↓
    ├──(REQUEST_CHANGES)──→ BUILDING (iteration, max 3)
    ├──(ESCALATE)──→ ESCALATED
    ↓
COMPLETE
```

## Iteration Limits

- **Analyzer ↔ Builder**: Maximum 3 iterations
- **Reviewer ↔ Builder**: Maximum 3 iterations
- **Reason**: Prevent infinite feedback loops

## Time Budget

- **Default**: 20 minutes per task
- **Extension**: +5 minutes if near completion (25 min hard limit)
- **Escalation**: Automatic if time exceeded

## Artifacts

Each task can produce up to five artifacts:

### PLAN.md (Analyzer)
- Critical evaluation decision (APPROVE/REJECT)
- Problem analysis and trade-offs
- Implementation plan with steps
- Risk assessment
- Branch name and setup

### IMPLEMENTATION.md (Builder)
- Changes made with code snippets
- Test results (unit, lint, build)
- Acceptance criteria status
- Commit list with SHAs
- Challenges and solutions

### REVIEW.md (Reviewer)
- Acceptance criteria validation
- Code review (strengths/concerns)
- Commit quality assessment
- PR description (if approved)
- Requested changes (if issues found)

### COMPLETION.md (Reviewer)
- Workflow execution summary
- Phase-by-phase breakdown (all 3 agents)
- Metrics table (duration, iterations, changes)
- Deliverables (PR URL, artifacts)
- Success factors or escalation reasons
- Next steps for human

### MAINTENANCE.md (Maintainer)
- PR feedback addressed
- In-scope changes made (with commits)
- Out-of-scope issues created
- Comment replies sent
- Test results after changes
- Final status

## Usage

### Core Workflow: Implement Task

```bash
/autoImplement 33          # Single task
/autoImplement 30, 31, 32  # Multiple tasks (sequential)
```

This creates a PR autonomously.

### Human-in-the-Loop: Address PR Feedback

After human reviews the PR and adds comments:

```bash
/maintainPr 56             # Address feedback on PR #56
/maintainPr 56 --task 33   # Specify task context (optional)
```

This addresses all feedback and updates the PR.

### Complete Lifecycle

```
/autoImplement 33
  ↓ Analyzer evaluates and plans
  ↓ Builder implements and tests
  ↓ Reviewer validates and creates PR #56
  ↓
Human reviews PR #56, adds comments
  ↓
/maintainPr 56
  ↓ Maintainer triages feedback
  ↓ Maintainer makes changes (may spawn Builder)
  ↓ Maintainer replies to comments
  ↓ Maintainer pushes updates
  ↓
Human re-reviews → Merge
```

## Escalation Triggers

Tasks escalate to human when:

1. **Max Iterations**: Agent feedback loop exceeds 3 iterations
2. **Time Exceeded**: Task exceeds 25-minute hard limit
3. **Agent Request**: Agent explicitly requests escalation
4. **Build Failure**: Tests or build fail repeatedly
5. **Merge Conflict**: Git conflicts detected

## Escalation Handling

When escalated:
1. `ESCALATION.md` created with details
2. All artifacts preserved
3. Current state documented
4. Recommendations provided
5. User notified with summary

## Parallel Execution (Future)

Currently: Sequential only (one task at a time)

**Future Enhancement**: Use git worktrees for parallel execution

```bash
# Each task gets isolated workspace
git worktree add ../scorecount-task-30 -b feature/task-30-...
git worktree add ../scorecount-task-31 -b feature/task-31-...

# Agents work in parallel without conflicts
# PRs created independently
```

### Why Git Worktrees?

- **Isolation**: Each task has independent file system
- **No Conflicts**: Tasks can't interfere with each other
- **Same Repo**: Shares .git directory, saves space
- **Clean Branches**: Easy to manage and merge
- **Parallel Safety**: True concurrent execution

### Implementation Approach

1. Create worktree for each task
2. Spawn agents in parallel (one per worktree)
3. Monitor progress independently
4. Merge successful PRs
5. Clean up worktrees when done

## Monitoring Progress

Check task status:
```bash
cat .claude/workflow/task-33/state.yml
```

View artifacts:
```bash
# See the plan
cat .claude/workflow/task-33/PLAN.md

# Check implementation
cat .claude/workflow/task-33/IMPLEMENTATION.md

# Review validation
cat .claude/workflow/task-33/REVIEW.md
```

## Cleanup

Task workspaces are preserved for debugging and audit purposes.

To clean up completed tasks:
```bash
rm -rf .claude/workflow/task-{id}
```

To clean up all task workspaces:
```bash
rm -rf .claude/workflow/task-*
```

Templates are never deleted.

## Design Philosophy

This system embodies the critical evaluation philosophy from CLAUDE.md:

- **Question the premise**: Analyzer can reject unnecessary tasks
- **Analyze trade-offs**: Detailed evaluation before implementation
- **Push back immediately**: Multiple review gates
- **Engineering judgment**: Not blind execution

## Success Criteria

A successful autonomous implementation:

- ✓ Task complexity matches agent capabilities
- ✓ All acceptance criteria met
- ✓ Tests pass (100%)
- ✓ Lint checks pass
- ✓ Clear, focused commits
- ✓ PR created and ready for human review
- ✓ Completed within time budget
- ✓ No escalation required

## Best Suited For

**Good candidates**:
- Clear bug fixes with reproduction steps
- Well-defined feature additions
- Code quality improvements (detekt cleanup)
- Test coverage additions
- Documentation updates

**Poor candidates**:
- Architectural changes requiring design decisions
- Features with ambiguous requirements
- Changes affecting multiple systems
- Breaking changes
- Complex refactorings

## Maintainer Feedback Handling

The `/maintainPr` command can handle various types of PR feedback:

**In-scope (addressed immediately)**:
- Code fixes directly related to the PR
- Test improvements or additions
- Documentation updates
- Style/lint violations
- Bug fixes in newly added code
- Minor refactoring within PR scope

**Out-of-scope (issues created)**:
- Broader refactoring affecting untouched code
- Future enhancements beyond PR scope
- Performance optimizations system-wide
- Architectural changes
- Technical debt in legacy code

The Maintainer agent automatically triages feedback and handles both types appropriately.

## Troubleshooting

### Task Stuck in ANALYZING
- Check if Analyzer agent is still running
- Review partial PLAN.md if exists
- May need more time for complex analysis

### Repeated REQUEST_PLAN_REVISION
- Builder finding technical issues with plan
- Check iteration count in state.yml
- Will escalate at 3 iterations

### Repeated REQUEST_CHANGES
- Reviewer finding quality issues
- Check REVIEW.md for specific feedback
- Will escalate at 3 iterations

### Early Escalation
- Check ESCALATION.md for reason
- Review available artifacts
- May need human judgment on approach

---

For more details, see `.claude/commands/autoImplement.md`
