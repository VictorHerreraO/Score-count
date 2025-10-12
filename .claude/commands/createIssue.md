---
description: Create a development task (issue) for a new feature, bug fix, or research task.
---

# Create Issue

When the user asks you to create a task, an issue, a ticket, or a new feature request, follow this process:

## 1. Understand and Refine the Request

- **Analyze**: Start by analyzing the user's request to identify the core problem to be solved or the feature to be added.
- **Groom**: Refine the request with the user until there is a clear and unambiguous understanding of the task. This may involve several back-and-forth questions to groom the issue.
- **Goal**: The goal is to have a well-defined task that is actionable by a developer. Do not create issues for vague tasks. If the request is ambiguous, ask clarifying questions before proceeding.

## 2. Determine Issue Type

Classify the issue into one of the following types:
- **FEATURE**: A new feature or functionality enhancement.
- **BUGFIX**: A bug fix, typo, or correction.
- **RESEARCH**: An investigation or exploration task.

## 3. Structure the Issue

Use the appropriate template below based on the issue type. Fill every section with clear, concise, and actionable content.

### Feature Template

```markdown
### Description

[Detailed description of the feature to be implemented. Provide context about the problem or the user need.]

### Value

[Explain the value this feature will add to the product.]

### Acceptance Criteria

- [ ] [Acceptance criterion 1]
- [ ] [Acceptance criterion 2]
- [ ] [List more criteria as needed]
```

### Bugfix Template

```markdown
### Description

[Detailed description of the bug. Include steps to reproduce if applicable.]

### Value

[Explain the impact of the bug that is being fixed.]

### Acceptance Criteria

- [ ] [The bug is resolved]
- [ ] [Add any other specific verification steps]
```

### Research Template

```markdown
### Description

[Describe the topic to be researched and the goals of the investigation.]

### Questions to Answer

- [What specific questions should be answered by this research?]
- [Are there any secondary questions to explore?]

### Deliverables

- [What is the expected output of this research (e.g., a document, a presentation, a proof of concept)?]
```

## 4. Create the Issue

Use the `gh` CLI to create the issue in the repository. Use a descriptive title with the appropriate prefix (`Feature`, `Bugfix`, `Research`) and paste the filled template into the body.

```bash
gh issue create --title "[Feature/Bugfix/Research]: [Provide a clear and descriptive title]" --body "$(cat <<'EOF'
[Paste filled template here]

🤖 Generated with [Your-Name](link-to-your-official-page)
EOF
)"
```

## Important Notes

- The title should be descriptive and concise.
- The description should provide enough context for a developer to understand the task.
- Acceptance criteria should be specific, measurable, and testable.
