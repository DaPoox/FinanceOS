# Workflow — Linear + Claude Code

## Comment récupérer le prochain ticket

1. Lire les issues Linear du projet FinanceOS avec le statut `Todo`, triées par priorité
2. Prendre la première issue `[Composant]` non commencée
3. Produire un plan en 5-6 lignes max avant de coder :
    - Nom du composant et écran concerné
    - Ce qu'il affiche (données, états principaux)
    - Composants existants réutilisables
    - Fichiers à créer / modifier
    - Une question si quelque chose est ambigu — sinon démarrer

Attendre la validation avant de coder — voir `workflow.md` Step 3 pour le process.

---

## Ordre de travail sur un écran

### La règle : Composants d'abord, Screen ensuite

```
Étape 1 — Composants isolés (un ticket chacun)
  → Chaque composant dans :presentation/designsystem/component/
  → Testé avec @Preview dans tous ses états
  → Zéro dépendance au ViewModel de l'écran

Étape 2 — Ticket Screen + ViewModel squelette
  → Crée le Screen Composable s'il n'existe pas déjà
  → Si le Screen existe : l'adapter pour accueillir les nouveaux composants
  → Importe les composants validés à l'étape 1
  → Crée ou met à jour le ViewModel avec :
      - UiState data class couvrant tous les composants de l'écran
      - StateFlow<UiState> émettant des mock data hardcodées
      - Fonctions d'action comme stubs vides (onExpenseAdded, etc.)
      - Zéro use case, zéro repository — les mocks suffisent
  → Gère scroll, padding, callbacks de navigation

⚠️ Pas de vraies données à cette étape. Le ViewModel reste sur mocks
jusqu'à ce que tous les écrans soient validés visuellement.
```

### Pourquoi cet ordre

Si on fait le Screen d'abord, on code les composants inline dans le Screen — ils deviennent non-réutilisables et difficiles à tester. En faisant les composants d'abord, chaque pièce est validée visuellement avant l'assemblage. Le Screen est alors trivial à écrire.

---

## Quand faire le vrai data layer ?

**Après que tous les écrans sont validés visuellement.**

Pas avant. Le schéma Room dépend de ce que les écrans ont besoin d'afficher.
Faire la DB trop tôt = modifier les entités en cours de route = migrations cassées.

```
Phase actuelle (maintenant)
  Composants + Screens + ViewModels mock
  → Tout fonctionne visuellement avec des données hardcodées

Phase data (plus tard, tous les écrans validés)
  → Domain : entités, use cases, interfaces repo
  → Data : Room entities, DAOs, implémentations repo
  → Brancher les VMs : remplacer les mock StateFlow par de vrais flows
  → La UI ne change pas d'une ligne
```

Le ViewModel squelette est conçu pour rendre ce branchement trivial :
les mock data sont dans une seule fonction, les use cases se substituent directement.

```
:presentation/
  designsystem/
    component/
      NetWorthHero.kt        ← composants isolés, un par fichier
      InsightCard.kt
      BudgetMonthCard.kt
      EnvelopeMiniGrid.kt
      SparklineCard.kt
      RecentMonthsSection.kt
  feature/
    home/
      HomeScreen.kt          ← assemble les composants ci-dessus
      HomeViewModel.kt
    budget/
      BudgetScreen.kt
      BudgetViewModel.kt
```

---

## Référence design

Tous les composants ont une référence JSX dans `design_handoff/` :
- `primitives.jsx` → tokens FOS, composants de base (Card, Progress, Pill, Num, Sparkline)
- `screen-home.jsx` → Home screen
- `screen-budget.jsx` → Budget screen
- `screen-expense.jsx` → Bottom sheet saisie dépense
- `screen-envelope.jsx` → Détail enveloppe
- `screen-allocation.jsx` → Flow allocation mensuelle
- `screen-patrimoine.jsx` → Patrimoine screen
- `screen-history.jsx` → Historique screen

**Règle :** traduire le rendu visuel JSX en Compose. Ne pas porter la logique React. Les `useState` deviennent UiState dans le ViewModel. Les `useEffect` deviennent des flows ou des `LaunchedEffect`.

---

## Format du plan de validation

Quand tu récupères un ticket, réponds avec ce format exact avant de coder :

```
Ticket : PRO-X — [Nom du composant]
Écran  : Home / Budget / etc.

Ce composant affiche : [1-2 phrases]

États à gérer :
- [état 1]
- [état 2]

Je vais réutiliser : [composants existants si applicable]

Fichier créé : [chemin exact]

Prêt à démarrer — tu valides ?
```