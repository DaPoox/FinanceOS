# Git Workflow

## Principles

- Every task gets its own branch — never work directly on `main`
- One branch = one concern
- Commit messages explain what and why, not how
- PR created on GitHub via MCP after execution is complete

---

## Branch Naming

```
<type>/<short-description>
```

| Type      | When                                      | Example                        |
|-----------|-------------------------------------------|--------------------------------|
| `feat`    | New feature or screen                     | `feat/budget-screen`           |
| `fix`     | Bug fix                                   | `fix/expense-amount-overflow`  |
| `refactor`| Code change without behavior change       | `refactor/budget-viewmodel`    |
| `chore`   | Setup, config, dependencies, MD files     | `chore/gradle-convention-plugins` |
| `docs`    | Documentation only                        | `docs/update-architecture-md`  |

Keep it short. No ticket numbers. No dates.

---

## Workflow Per Task

```
1. git checkout -b <type>/<description>
2. Implement (following workflow.md)
3. git add -A
4. git commit -m "<message>"
5. git push -u origin <branch>
6. Open PR on GitHub via MCP
```

---

## Commit Messages

```
<type>: <what changed and why>
```

Examples:
```
feat: add BudgetViewModel with allocation computation
fix: prevent negative remaining amount when expenses exceed budget
refactor: extract validation logic into BudgetValidationDelegate
chore: add Room convention plugin to build-logic
docs: update architecture.md with UiModel naming convention
```

Rules:
- Lowercase
- Present tense — "add" not "added"
- No period at the end
- Max 72 characters
- If more context is needed, add a blank line then a body

---

## PR Description Template

When opening a PR via GitHub MCP, use this structure:

```
## What
<One sentence — what this PR does>

## Why
<Why this change was needed>

## Changes
- <file or module>: <what changed>
- <file or module>: <what changed>

## Checklist
- [ ] Build passes
- [ ] No business logic in Composables
- [ ] No Android deps in :domain
- [ ] Koin modules updated if needed
- [ ] Nav graph updated if needed
```

---

## Rules

- Never commit directly to `main`
- Never force push to `main`
- One PR per feature — no bundling unrelated changes
- Merge only after build passes
- Delete branch after merge

---

## Claude Staging & Commit Rules

- **Auto-stage**: after modifying or creating files, immediately `git add` them — no need for the user to ask
- **Never auto-commit**: always wait for explicit user instruction ("commit", "commit push", etc.)
- **Ask when ready**: once implementation is complete and staged, ask "Ready to commit?" before doing anything