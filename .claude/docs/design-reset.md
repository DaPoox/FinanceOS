# Context — Design Reset

## Situation

Le projet FinanceOS est en cours. La fondation technique est solide et on la garde intacte. On repart uniquement sur le design et les écrans.

**Ce qu'on garde sans toucher :**
- `:build-logic` — convention plugins, Gradle setup
- `:core` — Result<T>, UiText
- `:domain` — vide mais structure en place
- `:data` — vide mais structure en place
- Bottom nav avec routes typées sérialisées
- Koin DI skeleton
- Pattern UiState / UiAction

**Ce qu'on remplace complètement :**
- Tout le design system dans `:presentation` — `Theme.kt`, `Color.kt`, `Typography.kt`, spacing tokens, composants partagés
- Les 3 écrans existants (Dashboard, Envelopes, Envelopes Edit) — UI uniquement, les ViewModels mock restent
- Les stubs Growth et Vault — on les redéfinit

---

## Pourquoi on repart sur le design

Le design a été validé via Claude Design avec un nouveau système visuel complet. Les écrans existants ne correspondent plus au design cible. On repart de la bonne base plutôt que de patcher.

---

## Nouvelle direction design

**Style :** Dark mode uniquement. Navy profond. Accent gold. Revolut UX + Finary patrimoine. ADHD-friendly.

**Material Design 3** — slots M3 avec nos valeurs custom.

| Slot M3 | Valeur |
|---|---|
| `background` | `#090c12` |
| `surface` | `#0f1420` |
| `surfaceVariant` | `#161d2e` |
| `surfaceContainer` | `#1c263a` |
| `primary` | `#f0b429` |
| `onPrimary` | `#1a1000` |
| `onSurface` | `#e8eef5` |
| `onSurfaceVariant` | `#8ba4be` |
| `outline` | `rgba(255,255,255,0.06)` |
| `error` | `#f87171` |

**Tokens custom :**
| Token | Valeur | Usage |
|---|---|---|
| `positive` | `#22c55e` | Indicateurs OK uniquement |
| `warning` | `#fb923c` | Enveloppe proche du max |
| `savings` | `#7eb8f7` | Charts épargne |
| `investment` | `#a78bfa` | Charts investissement |

**Typographie :** DM Sans (UI) + Geist Mono (tous les chiffres financiers)

---

## Ta première tâche

**Audit uniquement. Ne modifie rien.**

Lis tout le contenu de `:presentation` et produis un rapport avec :

1. **À supprimer** — fichiers du design system actuel et composants qui seront remplacés
2. **À garder** — pattern UiState/UiAction, structure des ViewModels, routes
3. **À vider partiellement** — fichiers à modifier (ex: screen files dont on garde la structure ViewModel mais on remplace l'UI)

Format attendu : liste claire par catégorie avec le chemin complet de chaque fichier.

Ne touche à rien avant validation.
