---
description: Document task completion with detailed results and learnings
---

# Document Task Result

When the user asks you to document a completed task, using the `gh` CLI tool add a comment to the issue number #$ARGUMENTS.

## File Structure

Use this template:

```markdown
# Task $ARGUMENTS Completion Report

**Title:** [Brief task title]
**Date:** [YYYY-MM-DD]
**Status:** ✅ Completed

---

## Summary

[2-3 sentences describing what was accomplished]

---

## Implementation Details

### Architecture Impact
- **Layers affected:** [Domain/Data/UI]
- **New components:** [List key files created]
- **Modified components:** [List key files changed]

### Key Changes
- `path/to/file.kt:123` - [Description of change]
- `path/to/file.kt:456` - [Description of change]

### Technical Decisions
[Explain any important architectural or design choices made]

---

## Challenges & Solutions

| Challenge | Solution |
|-----------|----------|
| [Problem encountered] | [How it was resolved] |
| [Problem encountered] | [How it was resolved] |

---

## Testing

### Test Results
```bash
[Command used to run tests]
```

**Result:** [X/Y tests passed]

### Verification Steps
1. [How to verify the implementation works]
2. [What to check]

---

## Future Improvements

[Optional: List any follow-up tasks or enhancements identified]

---

## References

- Related PR: #[pr-number]
```

## Guidelines

- Be concise but complete
- Include specific file references with line numbers
- Document decisions and trade-offs
- List actual test results
- Identify future work if applicable