// screen-empty-states.jsx — Finance OS — fresh install / no-data states.
// All components render BODY-ONLY (no PhoneShell or BottomNav wrapper) so they
// can drop straight into the existing app shell. The canvas review file wraps
// them in PhoneShell + BottomNav for standalone display.

function EmptyState({ icon, title, subtitle, cta, onCta }) {
  return (
    <div style={{
      flex: 1, minHeight: 0,
      display: 'flex', flexDirection: 'column',
      alignItems: 'center', justifyContent: 'center',
      padding: 32, textAlign: 'center',
      paddingBottom: 120,
    }}>
      <div style={{
        width: 72, height: 72, borderRadius: 20,
        background: FOS.surfaceVar, color: FOS.textDim,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>
        <Icon name={icon} size={36} strokeWidth={1.6} />
      </div>
      <div style={{
        marginTop: 20, fontFamily: 'DM Sans, sans-serif', fontSize: 18, fontWeight: 600,
        color: FOS.text, maxWidth: 280, lineHeight: 1.3, letterSpacing: -0.1,
      }}>{title}</div>
      <div style={{
        marginTop: 8, fontFamily: 'DM Sans, sans-serif', fontSize: 14,
        color: FOS.textDim, maxWidth: 280, lineHeight: 1.5,
      }}>{subtitle}</div>
      {cta && (
        <button onClick={onCta} style={{
          marginTop: 24, padding: '14px 24px', borderRadius: 16,
          background: FOS.primary, color: FOS.onPrimary, border: 'none',
          fontFamily: 'DM Sans, sans-serif', fontSize: 15, fontWeight: 700,
          cursor: 'pointer', letterSpacing: 0.3,
          display: 'inline-flex', alignItems: 'center', gap: 8,
          boxShadow: `0 8px 20px ${FOS.primary}33`,
        }}>
          {cta}
          <Icon name="arrow-right" size={16} strokeWidth={2.2} />
        </button>
      )}
    </div>
  );
}

// Soft section header at the top of empty screens, so users always
// know where they are.
function EmptyTopBar({ kicker, title, right }) {
  return (
    <div style={{
      padding: '8px 20px 0',
      display: 'flex', justifyContent: 'space-between', alignItems: 'center',
    }}>
      <div>
        {kicker && <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim }}>{kicker}</div>}
        <div style={{ fontFamily: 'DM Sans', fontSize: 22, fontWeight: 600, color: FOS.text }}>{title}</div>
      </div>
      {right}
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// 1. HomeScreen — no month allocated yet
// ────────────────────────────────────────────────────────────
function EmptyHomeScreen({ onAllocate, monthLabel = 'Juin 2026' }) {
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <EmptyTopBar
        kicker={monthLabel}
        title="Accueil"
        right={
          <div style={{
            width: 40, height: 40, borderRadius: 100,
            background: FOS.surface, border: `1px solid ${FOS.outline}`,
            display: 'flex', alignItems: 'center', justifyContent: 'center', color: FOS.text,
          }}>
            <Icon name="bell" size={18} />
          </div>
        }
      />
      <EmptyState
        icon="wallet"
        title="Bienvenue sur Finance OS"
        subtitle="Commence par allouer ton premier mois pour suivre ton budget."
        cta="Allouer le mois"
        onCta={onAllocate}
      />
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// 2. BudgetScreen — no envelopes
// ────────────────────────────────────────────────────────────
function EmptyBudgetScreen({ onAllocate, monthLabel = 'Juin 2026' }) {
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <EmptyTopBar kicker="Budget" title={monthLabel} />
      <EmptyState
        icon="layout-grid"
        title="Aucune enveloppe"
        subtitle="Alloue ton premier mois pour créer tes enveloppes de budget."
        cta="Allouer le mois"
        onCta={onAllocate}
      />
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// 3. PatrimoineScreen — no accounts
// ────────────────────────────────────────────────────────────
function EmptyPatrimoineScreen({ onAddAccount }) {
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <EmptyTopBar kicker="Patrimoine" title="0 €" />
      <EmptyState
        icon="building-2"
        title="Aucun compte ajouté"
        subtitle="Ajoute tes comptes bancaires et livrets pour suivre ton patrimoine."
        cta="Ajouter un compte"
        onCta={onAddAccount}
      />
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// 4. HistoryScreen — no months yet
// ────────────────────────────────────────────────────────────
function EmptyHistoryScreen() {
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <EmptyTopBar kicker="Historique" title="Tes mois" />
      <EmptyState
        icon="calendar-days"
        title="Pas encore d'historique"
        subtitle="Ton historique apparaîtra ici après ton premier mois complet."
      />
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// 5. EnvelopeDetailScreen — no transactions this month
//    Used as a slot inside the existing EnvelopeDetailScreen body, so it
//    takes only the transaction area — no header, no card.
// ────────────────────────────────────────────────────────────
function EmptyEnvelopeTransactions() {
  return (
    <EmptyState
      icon="receipt"
      title="Aucune dépense ce mois"
      subtitle="Appuie sur + pour enregistrer ta première dépense."
    />
  );
}

Object.assign(window, {
  EmptyState, EmptyTopBar,
  EmptyHomeScreen, EmptyBudgetScreen, EmptyPatrimoineScreen,
  EmptyHistoryScreen, EmptyEnvelopeTransactions,
});
