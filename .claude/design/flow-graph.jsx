// flow-graph.jsx — Finance OS — the user-flow graph: nodes + edges + bands.
// Nodes carry a render() that returns the *inner* phone content; the canvas
// wraps it in a device frame. Coordinates are world-space top-left.

const NODE = { w: 140, h: 303 };          // a scaled phone (412×892 · 0.34)
const DEC  = { w: 188, h: 104 };          // decision diamond
const ENT  = { w: 168, h: 56 };           // entry pill

// Path categories → accent colour (border + glow + arrow tint)
const CAT = {
  happy: '#22c55e',   // data exists, primary journey
  empty: '#f0b429',   // empty / onboarding state
  modal: '#7eb8f7',   // sheet / modal / form overlay
  gap:   '#f87171',   // MISSING — to design
  nav:   '#5b6b80',   // tab-bar navigation
  route: '#8ba4be',   // routing / decision
};

const noop = () => {};

// ─────────────────────────────────────────────────────────────
// BANDS — translucent grouping zones drawn behind the nodes.
// ─────────────────────────────────────────────────────────────
const BANDS = [
  { id: 'route',  x: 20,  y: -40,  w: 1700, h: 540, title: 'Lancement · routage · onboarding', sub: 'Au démarrage, l’app lit l’état en base et choisit l’écran', tint: CAT.route },
  { id: 'alloc',  x: 20,  y: 540,  w: 1180, h: 470, title: 'Allocation du mois', sub: '1er mois = 2 étapes · mois récurrent = 3 étapes', tint: CAT.happy },
  { id: 'app',    x: 20,  y: 1050, w: 860,  h: 470, title: 'Application — 4 onglets', sub: 'Tableau de bord (données présentes)', tint: CAT.happy },
  { id: 'detail', x: 20,  y: 1560, w: 860,  h: 470, title: 'Détails & sous-écrans', tint: CAT.happy },
  { id: 'modal',  x: 20,  y: 2070, w: 1740, h: 470, title: 'Overlays & modales', sub: 'Sheets · formulaires · confirmations', tint: CAT.modal },
  { id: 'gap',    x: 20,  y: 2580, w: 1900, h: 480, title: '⚠  Manquants — à concevoir', sub: 'États & écrans absents du design actuel', tint: CAT.gap },
];

// ─────────────────────────────────────────────────────────────
// NODES
// ─────────────────────────────────────────────────────────────
const NODES = [
  // ── Routing ──────────────────────────────────────────────
  { id: 'launch', kind: 'entry', cat: 'route', x: 60, y: 150,
    title: 'Lancement de l’app', size: ENT },
  { id: 'dHasData', kind: 'decision', cat: 'route', x: 250, y: 70,
    title: 'Données\nen base ?', size: DEC },
  { id: 'dMonthAlloc', kind: 'decision', cat: 'route', x: 250, y: 300,
    title: 'Mois courant\nalloué ?', size: DEC },

  // ── Empty states (install vierge) ────────────────────────
  { id: 'emptyHome', kind: 'phone', cat: 'empty', tab: 'home', x: 620, y: 120,
    title: 'Accueil — vide', state: 'Premier lancement',
    entry: 'hasData = false', render: () => <EmptyHomeScreen onAllocate={noop} /> },
  { id: 'emptyBudget', kind: 'phone', cat: 'empty', tab: 'budget', x: 820, y: 120,
    title: 'Budget — vide', state: 'Aucune enveloppe',
    render: () => <EmptyBudgetScreen onAllocate={noop} /> },
  { id: 'emptyPatrimoine', kind: 'phone', cat: 'empty', tab: 'patrimoine', x: 1020, y: 120,
    title: 'Patrimoine — vide', state: 'Aucun compte',
    render: () => <EmptyPatrimoineScreen onAddAccount={noop} /> },
  { id: 'emptyHistory', kind: 'phone', cat: 'empty', tab: 'history', x: 1220, y: 120,
    title: 'Historique — vide', state: 'Pas d’historique',
    render: () => <EmptyHistoryScreen /> },

  // ── Returning user, month not yet allocated ──────────────
  { id: 'homeBanner', kind: 'phone', cat: 'empty', tab: 'home', x: 1480, y: 120,
    title: 'Accueil — mois non alloué', state: 'Bannière « Nouveau mois »',
    entry: 'hasData=true · monthAllocated=false', render: () => <HomeWithUnallocatedBanner onAllocate={noop} /> },

  // ── Allocation flow ──────────────────────────────────────
  { id: 'allocIncomeFirst', kind: 'phone', cat: 'happy', x: 60, y: 620,
    title: 'Allouer — Revenu (1er mois)', state: 'Étape 1 / 2',
    render: () => <AllocationStep1NewMonth /> },
  { id: 'allocAdjustFirst', kind: 'phone', cat: 'happy', x: 260, y: 620,
    title: 'Allouer — Crée tes enveloppes', state: 'Étape 2 / 2 · from scratch',
    render: () => <AllocationScreen fromScratch initialStep={1} monthLabel="juin 2026" onBack={noop} onComplete={noop} onAddEnvelope={noop} /> },
  { id: 'allocIncomeRecurring', kind: 'phone', cat: 'happy', x: 560, y: 620,
    title: 'Allouer — Revenu (récurrent)', state: 'Étape 1 / 3',
    render: () => <AllocationScreen hasTemplate initialStep={0} monthLabel="juin 2026" previousMonth="mai 2026" onBack={noop} onComplete={noop} /> },
  { id: 'allocTemplate', kind: 'phone', cat: 'happy', x: 760, y: 620,
    title: 'Allouer — Base / template', state: 'Étape 2 / 3',
    render: () => <AllocationTemplatePicker previousMonth="Mai 2026" /> },
  { id: 'allocAdjust', kind: 'phone', cat: 'happy', x: 960, y: 620,
    title: 'Allouer — Ajuste', state: 'Étape 3 / 3',
    render: () => <AllocationScreen hasTemplate initialStep={2} monthLabel="juin 2026" previousMonth="mai 2026" onBack={noop} onComplete={noop} onAddEnvelope={noop} /> },

  // ── Main app (allocated) ─────────────────────────────────
  { id: 'home', kind: 'phone', cat: 'happy', tab: 'home', x: 60, y: 1130,
    title: 'Accueil', state: 'Tableau de bord', entry: 'monthAllocated = true',
    render: () => <HomeScreen monthLabel="Juin 2026" onOpenEnvelope={noop} onOpenAllocation={noop} onGoTo={noop} /> },
  { id: 'budget', kind: 'phone', cat: 'happy', tab: 'budget', x: 260, y: 1130,
    title: 'Budget', state: 'Enveloppes par groupe',
    render: () => <BudgetScreen onOpenEnvelope={noop} onOpenAllocation={noop} onOpenFixes={noop} onAddEnvelope={noop} /> },
  { id: 'patrimoine', kind: 'phone', cat: 'happy', tab: 'patrimoine', x: 460, y: 1130,
    title: 'Patrimoine', state: 'Donut + comptes',
    render: () => <PatrimoineScreen /> },
  { id: 'history', kind: 'phone', cat: 'happy', tab: 'history', x: 660, y: 1130,
    title: 'Historique', state: '12 derniers mois',
    render: () => <HistoryScreen /> },

  // ── Detail & sub-screens ─────────────────────────────────
  { id: 'envDetail', kind: 'phone', cat: 'happy', x: 60, y: 1640,
    title: 'Détail enveloppe', state: 'Avec transactions (dépassé)',
    render: () => <EnvelopeDetailScreen envId="restos" onBack={noop} /> },
  { id: 'envDetailPerm', kind: 'phone', cat: 'happy', x: 260, y: 1640,
    title: 'Détail enveloppe — permanente', state: 'Cumul + historique mensuel',
    render: () => <EnvelopeDetailScreen envId="vacances" onBack={noop} /> },
  { id: 'envDetailEmpty', kind: 'phone', cat: 'empty', x: 460, y: 1640,
    title: 'Détail enveloppe — vide', state: 'Aucune dépense ce mois',
    render: () => <EnvelopeDetailEmptyScreen /> },
  { id: 'fixes', kind: 'phone', cat: 'happy', x: 660, y: 1640,
    title: 'Charges fixes', state: 'Liste complète + filtres',
    render: () => <FixesScreen onBack={noop} onAddEnvelope={noop} /> },

  // ── Overlays & modals ────────────────────────────────────
  { id: 'expenseSheet', kind: 'phone', cat: 'modal', tab: 'home', x: 60, y: 2140,
    title: 'Sheet — Nouvelle dépense', state: 'Pavé numérique',
    render: () => <WithOverlay background={<HomeScreen monthLabel="Juin 2026" onOpenEnvelope={noop} onOpenAllocation={noop} onGoTo={noop} />}>
      <ExpenseSheet open={true} onClose={noop} onSave={noop} />
    </WithOverlay> },
  { id: 'expenseConfirm', kind: 'phone', cat: 'modal', tab: 'home', x: 260, y: 2140,
    title: 'Sheet — Dépense confirmée', state: 'Animation de validation',
    render: () => <WithOverlay background={<HomeScreen monthLabel="Juin 2026" onOpenEnvelope={noop} onOpenAllocation={noop} onGoTo={noop} />}>
      <ExpenseSheet open={true} demoConfirmed demoAmount="41" demoEnvelope="restos" onClose={noop} onSave={noop} />
    </WithOverlay> },
  { id: 'newEnvSheet', kind: 'phone', cat: 'modal', x: 460, y: 2140,
    title: 'Sheet — Nouvelle enveloppe', state: 'Depuis Allouer (Étape 2)',
    render: () => <WithOverlay background={<AllocationScreen fromScratch initialStep={1} monthLabel="juin 2026" onBack={noop} onComplete={noop} onAddEnvelope={noop} />}>
      <NewEnvelopeSheet open={true} type="var" onClose={noop} onSave={noop} />
    </WithOverlay> },
  { id: 'newEnvSheetBudget', kind: 'phone', cat: 'modal', tab: 'budget', x: 660, y: 2140,
    title: 'Sheet — Nouvelle enveloppe', state: 'Type variable (Budget)',
    render: () => <WithOverlay background={<BudgetScreen onOpenEnvelope={noop} onOpenAllocation={noop} onOpenFixes={noop} onAddEnvelope={noop} />}>
      <NewEnvelopeSheet open={true} type="var" onClose={noop} onSave={noop} />
    </WithOverlay> },
  { id: 'newEnvAccount', kind: 'phone', cat: 'modal', tab: 'budget', x: 660, y: 2140,
    title: 'Sheet — Enveloppe (compte)', state: 'Redirection Patrimoine',
    render: () => <WithOverlay background={<BudgetScreen onOpenEnvelope={noop} onOpenAllocation={noop} onOpenFixes={noop} onAddEnvelope={noop} />}>
      <NewEnvelopeSheet open={true} type="inv" onClose={noop} onSave={noop} />
    </WithOverlay> },
  { id: 'envFormCreate', kind: 'phone', cat: 'modal', x: 860, y: 2140,
    title: 'Formulaire enveloppe — création', state: 'Mode create',
    render: () => <EnvelopeFormScreen mode="create" /> },
  { id: 'envFormEdit', kind: 'phone', cat: 'modal', x: 1060, y: 2140,
    title: 'Formulaire enveloppe — édition', state: 'Mode edit (+ supprimer)',
    render: () => <EnvelopeFormScreen mode="edit" /> },
  { id: 'accFormCreate', kind: 'phone', cat: 'modal', x: 1260, y: 2140,
    title: 'Formulaire compte — création', state: 'Mode create',
    render: () => <AccountFormScreen mode="create" initial={{ name: 'Boursorama', type: 'Compte courant', balance: '4820', color: '#e8eef5' }} /> },
  { id: 'accFormEdit', kind: 'phone', cat: 'modal', x: 1460, y: 2140,
    title: 'Formulaire compte — édition', state: 'Mode edit (Livret A)',
    render: () => <AccountFormScreen mode="edit" /> },

  // ── Gaps (MISSING) ───────────────────────────────────────
  { id: 'loadHome', kind: 'phone', cat: 'gap', x: 60, y: 2650,
    title: 'Accueil — chargement', state: 'Skeleton',
    render: () => <MissingScreen mode="loading" kicker="Juin 2026" title="Chargement de l’Accueil" caption="Le skeleton de chargement n’est pas encore conçu." /> },
  { id: 'loadBudget', kind: 'phone', cat: 'gap', x: 260, y: 2650,
    title: 'Budget — chargement', state: 'Skeleton',
    render: () => <MissingScreen mode="loading" kicker="Budget" title="Chargement du Budget" caption="Le skeleton de chargement n’est pas encore conçu." /> },
  { id: 'loadPatrimoine', kind: 'phone', cat: 'gap', x: 460, y: 2650,
    title: 'Patrimoine — chargement', state: 'Skeleton',
    render: () => <MissingScreen mode="loading" kicker="Patrimoine" title="Chargement Patrimoine" caption="Le skeleton de chargement n’est pas encore conçu." /> },
  { id: 'loadHistory', kind: 'phone', cat: 'gap', x: 660, y: 2650,
    title: 'Historique — chargement', state: 'Skeleton',
    render: () => <MissingScreen mode="loading" kicker="Historique" title="Chargement Historique" caption="Le skeleton de chargement n’est pas encore conçu." /> },
  { id: 'loadEnv', kind: 'phone', cat: 'gap', x: 860, y: 2650,
    title: 'Détail enveloppe — chargement', state: 'Skeleton',
    render: () => <MissingScreen mode="loading" kicker="Enveloppe" title="Chargement enveloppe" caption="Le skeleton de chargement n’est pas encore conçu." /> },
  { id: 'errData', kind: 'phone', cat: 'gap', x: 1060, y: 2650,
    title: 'Erreur — chargement données', state: 'Erreur + réessayer',
    render: () => <MissingScreen mode="error" kicker="Erreur" title="Échec du chargement" caption="L’écran d’erreur (avec action « Réessayer ») n’est pas conçu." /> },
  { id: 'errNetwork', kind: 'phone', cat: 'gap', x: 1260, y: 2650,
    title: 'Erreur — hors-ligne', state: 'Bannière réseau',
    render: () => <MissingScreen mode="error" kicker="Réseau" title="Hors-ligne" caption="L’état hors-ligne / bannière réseau n’est pas conçu." /> },
  { id: 'notif', kind: 'phone', cat: 'gap', x: 1460, y: 2650,
    title: 'Notifications', state: 'Écran entier manquant',
    entry: 'Depuis l’icône cloche (Accueil)',
    render: () => <MissingScreen mode="screen" title="Notifications" caption="Ouvert via l’icône 🔔 de l’Accueil — écran non conçu." /> },
  { id: 'settings', kind: 'phone', cat: 'gap', x: 1660, y: 2650,
    title: 'Réglages / Profil', state: 'Écran entier manquant',
    entry: 'Depuis l’avatar (Accueil)',
    render: () => <MissingScreen mode="screen" title="Réglages & profil" caption="Aucun écran de réglages / profil n’existe encore." /> },
];

// ─────────────────────────────────────────────────────────────
// EDGES   from → to,  label,  cat,  + optional fromSide/toSide, bidir, dashed
// ─────────────────────────────────────────────────────────────
const EDGES = [
  // routing
  { from: 'launch', to: 'dHasData', label: 'ouverture', cat: 'route' },
  { from: 'dHasData', to: 'emptyHome', label: 'NON · install vierge', cat: 'empty', fromSide: 'right', toSide: 'left' },
  { from: 'dHasData', to: 'dMonthAlloc', label: 'OUI', cat: 'route', fromSide: 'bottom', toSide: 'top' },
  { from: 'dMonthAlloc', to: 'homeBanner', label: 'NON · nouveau mois', cat: 'empty', fromSide: 'right', toSide: 'bottom' },
  { from: 'dMonthAlloc', to: 'home', label: 'OUI · déjà alloué', cat: 'happy', fromSide: 'left', toSide: 'top' },

  // empty-state tab nav
  { from: 'emptyHome', to: 'emptyBudget', label: 'onglets', cat: 'nav', bidir: true, fromSide: 'right', toSide: 'left' },
  { from: 'emptyBudget', to: 'emptyPatrimoine', label: '', cat: 'nav', bidir: true, fromSide: 'right', toSide: 'left' },
  { from: 'emptyPatrimoine', to: 'emptyHistory', label: '', cat: 'nav', bidir: true, fromSide: 'right', toSide: 'left' },

  // onboarding → allocation
  { from: 'emptyHome', to: 'allocIncomeFirst', label: 'Allouer le mois · 1er mois', cat: 'happy', fromSide: 'bottom', toSide: 'top' },
  { from: 'homeBanner', to: 'allocIncomeRecurring', label: 'Allouer le mois', cat: 'happy', fromSide: 'bottom', toSide: 'top' },

  // allocation steps
  { from: 'allocIncomeFirst', to: 'allocAdjustFirst', label: 'Continuer', cat: 'happy', fromSide: 'right', toSide: 'left' },
  { from: 'allocAdjustFirst', to: 'home', label: 'Valider', cat: 'happy', fromSide: 'bottom', toSide: 'top' },
  { from: 'allocIncomeRecurring', to: 'allocTemplate', label: 'Continuer', cat: 'happy', fromSide: 'right', toSide: 'left' },
  { from: 'allocTemplate', to: 'allocAdjust', label: 'Continuer', cat: 'happy', fromSide: 'right', toSide: 'left' },
  { from: 'allocAdjust', to: 'home', label: 'Valider l’allocation', cat: 'happy', fromSide: 'bottom', toSide: 'right' },
  { from: 'allocAdjustFirst', to: 'newEnvSheet', label: '+ créer enveloppe', cat: 'modal', dashed: true, fromSide: 'bottom', toSide: 'top' },
  { from: 'budget', to: 'newEnvSheetBudget', label: '+ ajouter', cat: 'modal', fromSide: 'bottom', toSide: 'top' },

  // main tab nav
  { from: 'home', to: 'budget', label: 'onglets', cat: 'nav', bidir: true, fromSide: 'right', toSide: 'left' },
  { from: 'budget', to: 'patrimoine', label: '', cat: 'nav', bidir: true, fromSide: 'right', toSide: 'left' },
  { from: 'patrimoine', to: 'history', label: '', cat: 'nav', bidir: true, fromSide: 'right', toSide: 'left' },

  // main actions
  { from: 'home', to: 'envDetail', label: 'tap enveloppe', cat: 'happy', fromSide: 'bottom', toSide: 'top' },
  { from: 'home', to: 'expenseSheet', label: 'FAB +', cat: 'modal', fromSide: 'left', toSide: 'top' },
  { from: 'budget', to: 'allocIncomeRecurring', label: 'Allouer', cat: 'happy', dashed: true, fromSide: 'top', toSide: 'bottom' },
  { from: 'budget', to: 'envDetail', label: 'tap enveloppe', cat: 'happy', fromSide: 'bottom', toSide: 'top' },
  { from: 'budget', to: 'fixes', label: 'voir charges fixes', cat: 'happy', fromSide: 'bottom', toSide: 'top' },
  { from: 'patrimoine', to: 'accFormCreate', label: '+ Ajouter compte', cat: 'modal', fromSide: 'bottom', toSide: 'top' },
  { from: 'patrimoine', to: 'accFormEdit', label: 'tap compte', cat: 'modal', fromSide: 'bottom', toSide: 'top' },

  // gap entries (whole-screen, reachable directly)
  { from: 'home', to: 'notif', label: 'icône 🔔', cat: 'gap', dashed: true, fromSide: 'bottom', toSide: 'top' },
  { from: 'home', to: 'settings', label: 'avatar profil', cat: 'gap', dashed: true, fromSide: 'bottom', toSide: 'top' },

  // detail actions & variants
  { from: 'envDetail', to: 'envFormEdit', label: 'Modifier l’allocation', cat: 'modal', fromSide: 'bottom', toSide: 'top' },
  { from: 'envDetail', to: 'envDetailEmpty', label: 'si 0 transaction', cat: 'empty', dashed: true, fromSide: 'right', toSide: 'left' },
  { from: 'envDetail', to: 'envDetailPerm', label: 'type permanente', cat: 'happy', dashed: true, fromSide: 'right', toSide: 'left' },
  { from: 'fixes', to: 'newEnvSheet', label: '+ charge fixe', cat: 'modal', fromSide: 'bottom', toSide: 'top' },

  // modal resolutions
  { from: 'expenseSheet', to: 'expenseConfirm', label: 'valider', cat: 'modal', fromSide: 'right', toSide: 'left' },
  { from: 'newEnvAccount', to: 'patrimoine', label: 'Aller dans Patrimoine →', cat: 'happy', dashed: true, fromSide: 'top', toSide: 'bottom' },
  { from: 'accFormCreate', to: 'patrimoine', label: 'Ajouter le compte', cat: 'happy', dashed: true, fromSide: 'top', toSide: 'bottom' },
];

Object.assign(window, { NODES, EDGES, BANDS, CAT, NODE, DEC, ENT });
