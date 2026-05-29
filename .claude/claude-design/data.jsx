// data.jsx — mock state for Finance OS
// All amounts in EUR. Dates are short FR-style.

const ENVELOPES = [
  // Fixes
  { id: 'rent',     name: 'Loyer',             type: 'fixe', icon: '⌂',  allocated: 1180, spent: 1180, color: '#8ba4be' },
  { id: 'credit',   name: 'Crédit auto',        type: 'fixe', icon: '▬',  allocated: 215,  spent: 215,  color: '#8ba4be' },
  { id: 'utils',    name: 'Énergie & Box',     type: 'fixe', icon: '◇',  allocated: 96,   spent: 96,   color: '#8ba4be' },
  { id: 'subs',     name: 'Abonnements',       type: 'fixe', icon: '◐',  allocated: 84,   spent: 84,   color: '#8ba4be' },
  { id: 'mutuelle', name: 'Mutuelle santé',    type: 'fixe', icon: '◍',  allocated: 72,   spent: 72,   color: '#8ba4be' },
  { id: 'sport',    name: 'Salle de sport',    type: 'fixe', icon: '◉',  allocated: 35,   spent: 35,   color: '#8ba4be' },
  { id: 'tel',      name: 'Téléphone mobile',  type: 'fixe', icon: '◑',  allocated: 28,   spent: 28,   color: '#8ba4be' },
  { id: 'assur',    name: 'Assurance hab.',    type: 'fixe', icon: '◒',  allocated: 22,   spent: 22,   color: '#8ba4be' },

  // Variables (rebalanced to keep total at 4200)
  { id: 'groceries', name: 'Courses',        type: 'var',  icon: '◢',  allocated: 320, spent: 287, color: '#e8eef5' },
  { id: 'restos',    name: 'Restos & cafés', type: 'var',  icon: '◆',  allocated: 180, spent: 246, color: '#f87171' },
  { id: 'transport', name: 'Transport',      type: 'var',  icon: '◣',  allocated: 90,  spent: 41,  color: '#e8eef5' },
  { id: 'shopping',  name: 'Shopping',       type: 'var',  icon: '○',  allocated: 100, spent: 128, color: '#fb923c' },

  // Du mois
  { id: 'ams',     name: 'Amsterdam',       type: 'month', icon: '✈', allocated: 300, spent: 312, color: '#f87171' },

  // Permanentes
  { id: 'vacances', name: 'Vacances',       type: 'perm', icon: '∞', allocated: 150, spent: 0,    color: '#e8eef5', accumulated: 1840 },
  { id: 'cadeaux',  name: 'Cadeaux femme',  type: 'perm', icon: '∞', allocated: 78,  spent: 0,    color: '#e8eef5', accumulated: 420 },

  // Épargne — vide. Livret A et LDDS sont pleins, le surplus reste sur le compte courant.

  // Investissement
  { id: 'pea',     name: 'PEA',             type: 'inv',  icon: '◈', allocated: 500, spent: 500, color: '#a78bfa' },
  { id: 'cto',     name: 'CTO',             type: 'inv',  icon: '◈', allocated: 150, spent: 150, color: '#a78bfa' },
];

const TRANSACTIONS = {
  restos: [
    { date: '14 mai', note: 'Le Servan',     amount: 68 },
    { date: '12 mai', note: 'Café Méricourt', amount: 14 },
    { date: '11 mai', note: 'Septime',       amount: 92 },
    { date: '08 mai', note: 'Le Mary Celeste', amount: 41 },
    { date: '05 mai', note: 'Yard',          amount: 31 },
  ],
  groceries: [
    { date: '15 mai', note: 'Franprix',      amount: 38 },
    { date: '13 mai', note: 'Carrefour Express', amount: 62 },
    { date: '10 mai', note: 'Marché Aligre', amount: 28 },
    { date: '08 mai', note: 'Naturalia',     amount: 47 },
    { date: '04 mai', note: 'Franprix',      amount: 41 },
    { date: '02 mai', note: 'Course semaine', amount: 71 },
  ],
  ams: [
    { date: '06 mai', note: 'Hôtel 2 nuits', amount: 184 },
    { date: '06 mai', note: 'Train Thalys',  amount: 88 },
    { date: '07 mai', note: 'Anne Frank Huis', amount: 16 },
    { date: '08 mai', note: 'Restos',        amount: 24 },
  ],
};

const ACCOUNTS = [
  { name: 'Boursorama',    type: 'Compte courant',    balance: 4820,   color: '#e8eef5' },
  { name: 'Livret A',      type: 'Épargne',           balance: 18400, cap: 22950, color: '#7eb8f7' },
  { name: 'LDDS',          type: 'Épargne',           balance: 8200,  cap: 12000, color: '#7eb8f7' },
  { name: 'PEA',           type: 'Investissement',    balance: 22680, color: '#a78bfa' },
  { name: 'CTO Trade Rep.',type: 'Investissement',    balance: 6420,  color: '#a78bfa' },
];

const MONTH_HISTORY = [
  { month: 'mai 26',  income: 4200, contrib: 1840, status: 'best',   spent: 2960 },
  { month: 'avr 26',  income: 4200, contrib: 1410, status: 'good',   spent: 3210 },
  { month: 'mar 26',  income: 3950, contrib: 980,  status: 'mid',    spent: 3380 },
  { month: 'fév 26',  income: 4400, contrib: 1620, status: 'good',   spent: 3210 },
  { month: 'jan 26',  income: 4200, contrib: 1280, status: 'good',   spent: 3340 },
  { month: 'déc 25',  income: 4800, contrib: 520,  status: 'hard',   spent: 4520 },
  { month: 'nov 25',  income: 4200, contrib: 1390, status: 'good',   spent: 3120 },
  { month: 'oct 25',  income: 4200, contrib: 1180, status: 'good',   spent: 3290 },
];

// 6-month patrimoine series, in €
const NETWORTH_6M = [54200, 55610, 56590, 58210, 58740, 60580];
// 12-month for patrimoine screen
const NETWORTH_12M = [48200, 49100, 50320, 51100, 51980, 52900, 54200, 55610, 56590, 58210, 58740, 60580];

const CURRENT = {
  netWorth: 60580,
  netWorthDelta: 1840,
  netWorthDeltaPct: 3.13,
  income: 4200,
  // contributions this month
  contribSavings: 0,
  contribInvest: 650,
  contribMarket: 590,
};

Object.assign(window, {
  ENVELOPES, TRANSACTIONS, ACCOUNTS, MONTH_HISTORY, NETWORTH_6M, NETWORTH_12M, CURRENT,
});
