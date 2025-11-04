---
description: Address PR feedback by reviewing comments, making changes, and replying to each comment
---

# Maintain Pull Request

Address human feedback on a pull request using the Maintainer agent.

## Usage

```bash
/maintainPr 56              # Address feedback on PR #56
/maintainPr 56 --task 33    # Specify associated task (optional)
```

## How It Works

This command spawns the **Maintainer agent** (see `.claude/agents/maintainer.md`) to:

1. **Fetch PR comments** from human reviewers
2. **Triage feedback** into in-scope vs out-of-scope
3. **Make changes** for in-scope items (may spawn Builder agent)
4. **Create issues** for out-of-scope items
5. **Reply to all comments** with actions taken
6. **Push changes** to the PR branch
7. **Generate maintenance report**

## Process

### 1. Parse Arguments

Extract PR number from `$ARGUMENTS`:
- Required: PR number (e.g., `56`)
- Optional: `--task XX` to link to task workspace

### 2. Validate PR Exists

Use the GitHub MCP tool `mcp__github__pull_request_read` with method='get':
```
Use mcp__github__pull_request_read with:
- method: "get"
- owner: Repository owner
- repo: Repository name
- pullNumber: $PR_NUMBER
```

If PR doesn't exist or is closed, abort with error message.

### 3. Check for Task Workspace

If `--task` flag provided:
- Verify `.claude/workflow/task-$TASK_ID/` exists
- This provides context for the Maintainer agent

Otherwise, search for task workspace automatically:
```bash
grep -r "pull/$PR_NUMBER" .claude/workflow/task-*/REVIEW.md 2>/dev/null | head -n1
```

### 4. Spawn Maintainer Agent

Use the Task tool to spawn the Maintainer agent:

```
Task tool parameters:
- subagent_type: "general-purpose"
- description: "Address feedback on PR #$PR_NUMBER"
- prompt: "
  You are running as the Maintainer agent (see .claude/agents/maintainer.md for your full instructions).

  **PR Number**: $PR_NUMBER
  **Repository**: {{owner/repo from git config}}
  {{**Task Workspace**: .claude/workflow/task-$TASK_ID if found}}

  Follow your Maintainer agent instructions to:
  1. Fetch all comments on PR #$PR_NUMBER
  2. Triage each comment (in-scope vs out-of-scope)
  3. Address in-scope items (spawn Builder if needed for code changes)
  4. Create GitHub issues for out-of-scope items
  5. Reply to each comment with action taken
  6. Push all changes to the PR branch
  7. Generate maintenance report

  Report back when all comments have been addressed.
"
```

### 5. Wait for Completion

The Maintainer agent will:
- Fetch and analyze comments
- Make necessary changes (potentially spawning Builder)
- Create issues for out-of-scope items
- Reply to all comments
- Push changes
- Generate report

### 6. Report Results

After Maintainer completes, read the maintenance report and summarize for user:

```
✅ Addressed all feedback on PR #$PR_NUMBER

In-scope changes: {{N}}
Out-of-scope issues created: {{N}}

See full report: {{path}}
```

## Integration with autoImplement

If the PR was created by `/autoImplement`, the Maintainer agent will:
- Find the task workspace automatically
- Read existing artifacts (PLAN.md, IMPLEMENTATION.md, REVIEW.md) for context
- Save MAINTENANCE.md to the task workspace
- Maintain the complete audit trail

**Full lifecycle:**
```
/autoImplement 33
  → Analyzer creates plan
  → Builder implements
  → Reviewer creates PR #56
  → Human reviews and adds comments
/maintainPr 56
  → Maintainer addresses feedback
  → PR updated and ready for re-review
```

## Standalone Usage

The Maintainer agent also works for PRs NOT created by `/autoImplement`:

```bash
# Address feedback on any PR
/maintainPr 42
```

In this case:
- No task workspace available
- Maintainer creates `.claude/maintenance/pr-42-maintenance.md`
- Works the same way, just without historical context

## Examples

### Example 1: Simple Feedback

```
Human comments on PR #56:
- "Add a comment explaining why we use IOException here"
- "Fix typo: 'helpper' should be 'helper'"

/maintainPr 56

Maintainer:
- Makes both changes directly (simple edits)
- Commits: "docs: Add exception choice comment and fix typo"
- Replies to both comments
- Pushes changes
```

### Example 2: Code Changes Needed

```
Human comments on PR #56:
- "This should handle null values gracefully"
- "Add a test case for the error scenario"

/maintainPr 56

Maintainer:
- Identifies as in-scope but requires code changes
- Spawns Builder agent with feedback
- Builder implements null handling and test
- Builder runs tests (all pass)
- Maintainer verifies changes
- Maintainer replies to comments with commit SHA
- Maintainer pushes all changes
```

### Example 3: Out-of-Scope Feedback

```
Human comments on PR #56:
- "This test file has 500 lines, should be split up"
- "Consider refactoring the entire ViewModel pattern"

/maintainPr 56

Maintainer:
- Identifies as out-of-scope (affects code beyond this PR)
- Creates issue #60: "[Future] Split large test file"
- Creates issue #61: "[Future] Refactor ViewModel pattern"
- Replies to comments with issue numbers
- Explains why deferred
- No code changes (nothing to push)
```

### Example 4: Mixed Feedback

```
Human comments on PR #56:
- "Add null check here" (in-scope)
- "Fix typo in comment" (in-scope)
- "Entire error handling system needs redesign" (out-of-scope)

/maintainPr 56

Maintainer:
- Spawns Builder for null check
- Fixes typo directly
- Creates issue #62 for error handling redesign
- Replies to all three comments
- Pushes null check + typo fix
```

## Error Handling

### No Comments Found

If PR has no comments:
```
ℹ️  No feedback to address on PR #56

The PR has no review comments or change requests.
```

### PR Already Merged

If PR is already merged:
```
❌ Cannot maintain PR #56 - already merged

Use this command only on open PRs.
```

### PR Closed

If PR is closed without merging:
```
⚠️  PR #56 is closed

Re-open the PR before addressing feedback, or create a new PR.
```

## Best Practices

1. **Run after human review** - Wait for reviewer to finish before running
2. **One iteration at a time** - Address feedback, wait for re-review, repeat
3. **Check the report** - Always review MAINTENANCE.md before asking for re-review
4. **Verify tests pass** - Maintainer runs tests, but double-check the output
5. **Monitor scope** - Ensure Maintainer isn't expanding the PR beyond its purpose

## Limitations

- **Cannot resolve review threads** - Only humans can mark comments as resolved
- **Cannot approve PR** - Maintainer addresses feedback but doesn't approve
- **Cannot merge** - Merging remains a human decision
- **Sequential only** - Handles one PR at a time

## Future Enhancements

Potential improvements:
- Auto-detect when new comments are added
- Support batch processing multiple PRs
- Integration with CI/CD status checks
- Proactive suggestions before human review

---

## Arguments

**Required:**
- `PR_NUMBER` - The pull request number to maintain

**Optional:**
- `--task TASK_ID` - Explicitly specify the associated task workspace

## Begin Maintenance

Parse PR number from $ARGUMENTS and spawn Maintainer agent.

If no arguments provided, show usage:
```
Usage: /maintainPr <PR_NUMBER> [--task TASK_ID]

Example:
  /maintainPr 56
  /maintainPr 56 --task 33
```
