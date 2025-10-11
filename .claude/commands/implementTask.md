---
description: Implement a task from /task/ directory with critical evaluation
---

# Implement Task

When the user asks you to implement a task, follow this rigorous process:

## 1. Critical Evaluation (MANDATORY)

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

## 2. Review Task File

Read `/task/task-[number].md` to understand requirements.

Check if the task is still relevant given recent project changes.

## 3. Understand Architecture

Review `ARCHITECTURE.md` to understand:
- Project structure (Domain/Data/UI layers)
- Existing patterns and conventions
- How to integrate changes properly

## 4. Plan Implementation

Create a detailed plan using TodoWrite:
- Break down into specific, actionable steps
- Identify files to create/modify
- Note dependencies and order of work
- Consider testing requirements

**Ask questions before proceeding** if anything is unclear. Do not start until all questions are answered.

## 5. Verify Dependencies

Check for latest library versions:
- Search online for current versions
- Don't assume your knowledge is current
- Update `gradle/libs.versions.toml` if needed

## 6. Implement

Follow your plan systematically:
- Work layer by layer (Domain → Data → UI)
- Write tests alongside implementation
- Update TodoWrite as you progress
- Keep commits small and focused

## 7. Validate

Ensure code quality:
```bash
./gradlew clean build
./gradlew test
./gradlew lint
```

**Iterate until all checks pass.** Do not consider the task complete if:
- Build fails
- Tests fail
- Lint errors exist

## 8. Document

Update relevant files:
- `MEMORY.md` - Current state and next steps
- `COMPLETED.md` - Log the completed work

## Guidelines

- Be brutally honest and straight to the point
- Push back on unnecessary work
- Prioritize simplicity over complexity
- Ensure code compiles and tests pass
- Follow existing project patterns
- Don't deviate from your plan without good reason

