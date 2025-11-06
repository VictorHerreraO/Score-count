---
description: Implement a task from /task/ directory with critical evaluation
---

# Implement Task

When the user asks you to implement a task, follow this rigorous process:

## 1. Review Task File

Using the GitHub MCP tool `mcp__github__issue_read` with method='get', read the GitHub issue #$ARGUMENTS to understand requirements.

Check if the task is still relevant given recent project changes.

## 2. Critical Evaluation (MANDATORY)

Before implementing, analyze:

**Question the premise:**
- Does this task solve a real problem?
- Is the existing code already adequate?
- Is this refactoring for refactoring's sake?

**Analyze trade-offs:**
- Will this make code objectively better or just different?
- What complexity does it add vs. benefits gained?
- Is this over-engineering a simple problem?

**Push back immediately if:**
- Existing code is clean and maintainable
- The "problem" is subjective preference
- Solution adds complexity without clear benefits
- Task is unnecessary given recent changes

If the task fails this evaluation, respond with clear analysis and recommend closing/modifying it.

## 3. Research Context

Use codebase-analyzer subagent to:
- Find relevant files and patterns
- Understand affected architecture layers
- Identify constraints and existing patterns

## 4. Plan Approach

Think through approach using TodoWrite:
- Break down into implementation steps
- Identify which files to explore/modify
- Note dependencies and order
- Consider testing approach

**Ask questions if critical info missing**. Make decisions autonomously when context is sufficient.

## 5. Verify Dependencies

Check for latest library versions:
- Search online for current versions
- Don't assume your knowledge is current
- Update `gradle/libs.versions.toml` if needed

## 6. Implement Autonomously

Implement systematically:
- Explore referenced files to understand patterns
- Make implementation decisions (naming, structure, approach)
- Work incrementally, test as you go
- Follow existing patterns found in codebase
- Keep commits small and focused

## 7. Validate

All checks must pass:
```bash
./gradlew clean build
./gradlew test
./gradlew lint
```

**No shortcuts.** Iterate until green.

## 8. Document

Update if needed:
- `MEMORY.md` - Next steps if relevant

## Guidelines

- **Exercise autonomy** - make engineering decisions
- **Push back** on unnecessary work
- **Explore code** - don't expect everything documented
- **Prioritize simplicity** over complexity
- **All tests must pass** - no exceptions
- **Use sub-agents** for heavy exploration

