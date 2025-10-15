---
description: Address PR feedback by reviewing comments, making changes, and replying to each comment
---

# Address PR Feedback

When the user informs you that they've added comments to a PR, follow this process:

## 1. Fetch and Review Comments

Use the GitHub CLI to fetch PR comments:
```bash
gh api repos/{owner}/{repo}/pulls/{pr_number}/comments
```

Read and understand each piece of feedback carefully.

## 2. Evaluate Scope

For each comment, determine:

**In-scope feedback** (address immediately):
- Issues directly related to the PR's changes
- Code improvements that enhance the current implementation
- Test improvements or corrections
- Documentation fixes

**Out-of-scope feedback** (create issue):
- Valid concerns but broader refactoring needed
- Future enhancements not related to current changes
- Technical debt that affects multiple areas

## 3. Address Feedback

**For in-scope items:**
- Make the necessary code changes
- Run tests to verify changes work correctly
- Commit with clear, descriptive messages
- Push changes to the PR branch

**For out-of-scope items:**
- Create a GitHub issue documenting the concern
- Include context about why it's being deferred
- Reference the PR where it was identified

## 4. Reply to Comments

After addressing each comment, add a reply using:
```bash
gh pr review {pr_number} --comment --body "..."
```

Keep replies brief and informative:
- State what action was taken
- Include relevant commit hashes or issue numbers
- Mention test results if applicable

**Always sign your comments:**
```
-[Your Name]
```

## Example Reply Format

**For code changes:**
```
Updated the tests to use `.value` for synchronous access.
All tests pass (104/104). Changes in commit abc1234.

- Claude
```

**For created issues:**
```
Created issue #3 to track this as a future enhancement.

- Claude
```
