---
description: Autonomously implement GitHub issues using multi-agent workflow
---

# Auto-Implement Tasks

This command implements GitHub issues autonomously using a three-agent workflow system:
- **Analyzer**: Critically evaluates task and creates implementation plan
- **Builder**: Implements the plan, runs tests, creates commits
- **Reviewer**: Validates implementation and creates pull request

## Usage

```bash
/autoImplement 33          # Single task
/autoImplement 30, 31, 32  # Multiple tasks (sequential)
```

## How It Works

### Phase 1: Research (Analyzer Agent - Haiku)
1. Fetches task from GitHub using `mcp__github__issue_read`
2. **Critically evaluates** whether task should be implemented
3. If approved: Researches codebase (delegates to sub-agents)
4. Finds relevant files and patterns
5. Creates feature branch
6. Generates `CONTEXT.md` artifact (NOT a detailed plan)

**Decision Points:**
- `APPROVE` → Proceed to Building
- `REJECT` → Task closed with brief reasoning

**Token Optimization**: Analyzer uses sub-agents for exploration, outputs concise context (max 150 lines, NO code blocks)

### Phase 2: Building (Builder Agent - Sonnet)
1. Reads `CONTEXT.md` (research pointers, not detailed plan)
2. **Explores files** and **makes implementation decisions** autonomously
3. Implements changes following existing patterns
4. Runs tests: `./gradlew test`
5. Runs lint: `./gradlew ktlintCheck` (auto-format if needed)
6. Creates focused commits
7. Generates `IMPLEMENTATION.md` artifact

**Decision Points:**
- `COMPLETE_IMPLEMENTATION` → Proceed to Review
- `REQUEST_CONTEXT_REVISION` → Back to Analyzer (max 3 iterations)
- `ESCALATE` → Human intervention required

### Phase 4: Review (Reviewer Agent)
1. Validates all acceptance criteria met
2. Reviews code quality and test coverage
3. Checks commit quality
4. Generates `REVIEW.md` artifact
5. Generates `COMPLETION.md` workflow summary

**Decision Points:**
- `APPROVE` → Push branch and create PR
- `REQUEST_CHANGES` → Back to Builder (max 3 iterations)
- `ESCALATE` → Human intervention required

## Workflow State

Each task maintains state in `.claude/workflow/task-{id}/`:
```
task-33/
├── state.yml                  # Workflow state tracking
├── CONTEXT.md                 # Analyzer output (research)
├── IMPLEMENTATION.md          # Builder output
├── REVIEW.md                  # Reviewer output
└── COMPLETION.md              # Workflow summary
```

### State Machine
```
INITIALIZED → RESEARCHING → BUILDING → REVIEWING → COMPLETE
                 ↓             ↑           ↓
             REJECTED      (iteration)  (iteration)
                                          ↓
                                     ESCALATED
```

## Time Budget

- **Default**: 20 minutes per task
- **Extension**: +5 minutes if needed (25 min hard limit)
- **Escalation**: Automatic if time exceeded

## Iteration Limits

- **Analyzer ↔ Builder**: Max 3 iterations
- **Reviewer ↔ Builder**: Max 3 iterations
- **Escalation**: Automatic if max iterations exceeded

## Escalation Scenarios

Tasks escalate to human when:
1. Max iterations exceeded (circular feedback)
2. Time budget exceeded (25 min hard limit)
3. Agent explicitly requests escalation
4. Build/test failures can't be resolved
5. Merge conflicts detected

## Orchestration Process

You are the orchestrator. For each task ID in $ARGUMENTS:

### 1. Initialize Task Workspace

Create the workflow directory and initial state file:

```bash
mkdir -p .claude/workflow/task-$TASK_ID
```

Create state.yml tracking file with initial values.

### 2. Launch Analyzer Agent

Use the Task tool to spawn the Analyzer agent:

```
Task tool parameters:
- subagent_type: "general-purpose"
- model: "haiku"
- description: "Research task #$TASK_ID"
- prompt: "
  Run as Analyzer agent (.claude/agents/analyzer.md).

  Task ID: $TASK_ID
  Workspace: .claude/workflow/task-$TASK_ID
  {{Iteration: N if revision}}
  {{Previous Feedback: [Builder's request] if applicable}}

  1. Fetch and evaluate issue #$TASK_ID
  2. APPROVE or REJECT
  3. If APPROVE: Research codebase, write CONTEXT.md

  Keep output concise. Use sub-agents for exploration.
"
```

### 3. Process Analyzer Output

Read `.claude/workflow/task-$TASK_ID/CONTEXT.md`.

Check decision:
- If `REJECT`: Update state to REJECTED, report to user, DONE
- If `APPROVE`: Update state to BUILDING, proceed to step 4

### 4. Spawn Builder Agent

Use the Task tool to spawn the Builder agent:

```
Task tool parameters:
- subagent_type: "general-purpose"
- model: "sonnet"
- description: "Implement task #$TASK_ID"
- prompt: "
  Run as Builder agent (.claude/agents/builder.md).

  Task ID: $TASK_ID
  Workspace: .claude/workflow/task-$TASK_ID
  Iteration: {{N}}/3
  {{Previous Feedback: [Reviewer's concerns] if revision}}

  1. Read CONTEXT.md (research pointers)
  2. Explore files and make implementation decisions
  3. Implement, test, lint (all must pass)
  4. Create commits (local only)
  5. Write IMPLEMENTATION.md

  You have autonomy. Make engineering decisions.
"
```

### 5. Process Builder Output

Read `.claude/workflow/task-$TASK_ID/IMPLEMENTATION.md`.

Check decision:
- If `REQUEST_CONTEXT_REVISION`:
  - Increment `iteration_count.builder_to_analyzer`
  - If < 3: Go back to step 2 (Analyzer provides more context)
  - If >= 3: ESCALATE (max iterations exceeded)
- If `COMPLETE_IMPLEMENTATION`: Update state to REVIEWING, proceed to step 6
- If `ESCALATE`: Update state to ESCALATED, create escalation report, DONE

### 6. Spawn Reviewer Agent

Use the Task tool to spawn the Reviewer agent:

```
Task tool parameters:
- subagent_type: "general-purpose"
- description: "Review task #$TASK_ID"
- prompt: "
  Run as Reviewer agent (.claude/agents/reviewer.md).

  Task ID: $TASK_ID
  Workspace: .claude/workflow/task-$TASK_ID
  Iteration: {{N}}/3

  1. Read CONTEXT.md and IMPLEMENTATION.md
  2. Verify acceptance criteria met
  3. Review code and commit quality
  4. APPROVE, REQUEST_CHANGES, or ESCALATE
  5. If APPROVE: Push branch, create PR (mcp__github__create_pull_request)
  6. Write REVIEW.md and COMPLETION.md
"
```

### 7. Process Reviewer Output

Read `.claude/workflow/task-$TASK_ID/REVIEW.md`.

Check decision:
- If `REQUEST_CHANGES`:
  - Increment `iteration_count.reviewer_to_builder`
  - If < 3: Go back to step 4 (Builder addresses feedback)
  - If >= 3: ESCALATE (max iterations exceeded)
- If `APPROVE`: Update state to COMPLETE, report success to user, DONE
- If `ESCALATE`: Update state to ESCALATED, create escalation report, DONE

### 8. Time Budget Monitoring

Throughout the workflow:
- Track elapsed time
- At 15 min (75%): Log warning
- At 20 min (100%): Evaluate if extension possible
- At 25 min (hard limit): ESCALATE with current progress

### 9. Escalation Handling

When escalating, create `.claude/workflow/task-$TASK_ID/ESCALATION.md`:

```markdown
# ESCALATION: Task #$TASK_ID

## Reason
{{MAX_ITERATIONS|TIME_EXCEEDED|AGENT_REQUEST|BUILD_FAILURE|MERGE_CONFLICT}}

## Current State
**Status**: {{current status}}
**Time Elapsed**: {{X}}/20 minutes
**Branch**: {{branch name}}
**Last Agent**: {{analyzer|builder|reviewer}}
**Iteration Counts**:
- Analyzer ↔ Builder: {{N}}/3
- Reviewer ↔ Builder: {{N}}/3

## Progress Summary
- [x] Analysis {{completed|in progress|not started}}
- [x] Planning {{completed|in progress|not started}}
- [~] Implementation {{completed|in progress|not started}}
- [ ] Review {{completed|in progress|not started}}

## Issue
{{Clear description of why escalation occurred}}

## Artifacts Available
- CONTEXT.md {{exists|missing}}
- IMPLEMENTATION.md {{exists|missing}}
- REVIEW.md {{exists|missing}}

## Recommendation
{{What human should do next}}

## Next Steps for Human
1. Review artifacts in `.claude/workflow/task-$TASK_ID/`
2. {{Specific action recommendation}}
```

Report escalation to user and stop.

### 10. Multi-Task Handling

For multiple tasks in $ARGUMENTS (sequential mode only):

Process each task ID sequentially using steps 1-9. Continue to next task if one is REJECTED or ESCALATED.

After all tasks complete, provide summary report:
- Tasks completed successfully with PRs
- Tasks rejected (with reasons)
- Tasks escalated (with escalation reasons)

## Important Notes

- **Autonomous**: No human input required during execution
- **Observable**: All artifacts preserved for transparency
- **Fail-safe**: Multiple escalation triggers prevent runaway processes
- **Auditable**: State tracking and artifacts enable debugging
- **Respectful**: Critical evaluation prevents unnecessary work

## Future Enhancements

### Parallel Execution (Git Worktrees)
For parallel task execution, use git worktrees to give each task isolated workspace:

```bash
# For each task, create separate worktree
git worktree add ../scorecount-task-$TASK_ID -b [type]/task-$TASK_ID-[desc]

# Each agent works in its own directory
# No file conflicts between parallel tasks
# Merge results when complete
```

This enables true parallel execution without workspace conflicts. See `.claude/workflow/PARALLEL_EXECUTION.md` for detailed design.

---

## Begin Orchestration

Parse task IDs from $ARGUMENTS and begin sequential processing.

**For each task**:
1. Create workspace
2. Track start time
3. Spawn Analyzer → Process output
4. If APPROVED: Spawn Builder → Process output
5. If COMPLETE: Spawn Reviewer → Process output
6. Handle iterations (max 3 per handoff)
7. Monitor time budget
8. Report result (COMPLETE/REJECTED/ESCALATED)

Proceed with task #$ARGUMENTS.
