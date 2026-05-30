// app.jsx — Finance OS root: navigation + screen switcher

function useFitScale(w, h, pad = 32) {
  const [s, setS] = React.useState(1);
  React.useEffect(() => {
    const fit = () => {
      const sx = (window.innerWidth - pad * 2) / w;
      const sy = (window.innerHeight - pad * 2) / h;
      setS(Math.min(1, sx, sy));
    };
    fit();
    window.addEventListener('resize', fit);
    return () => window.removeEventListener('resize', fit);
  }, [w, h, pad]);
  return s;
}

function App() {
  const scale = useFitScale(412, 892, 24);
  const W = 412 * scale, H = 892 * scale;
  const [tab, setTab] = React.useState('home');
  const [sheet, setSheet] = React.useState(false);
  // overlay: null | { kind: 'envelope', id } | { kind: 'allocation' } | { kind: 'fixes' }
  const [overlay, setOverlay] = React.useState(null);
  // newEnv: null | { type: 'fixe'|'var'|'month'|'perm'|'inv' }
  const [newEnv, setNewEnv] = React.useState(null);
  const [transitionKey, setTransitionKey] = React.useState(0);

  // ── App data lifecycle ────────────────────────────────────
  // Three nested states drive the whole shell:
  //   hasData === false                      → FRESH INSTALL. Nothing exists.
  //                                            Every tab shows its empty state.
  //   hasData && !monthAllocated             → returning user, new month begun
  //                                            (Brief 3 unallocated banner).
  //   hasData && monthAllocated              → full dashboard.
  // Defaults to a completely empty app — the true first-open experience.
  const CUR_MONTH = 'Juin 2026';
  const [hasData, setHasData] = React.useState(() => {
    try { return localStorage.getItem('fos_has_data') === '1'; } catch { return false; }
  });
  const [monthAllocated, setMonthAllocated] = React.useState(() => {
    try { return localStorage.getItem('fos_month_allocated') === '1'; } catch { return false; }
  });
  // Completing the first allocation seeds data AND marks the month allocated.
  const allocateMonth = () => {
    setHasData(true); setMonthAllocated(true);
    try {
      localStorage.setItem('fos_has_data', '1');
      localStorage.setItem('fos_month_allocated', '1');
    } catch (e) {}
  };
  // Demo reset → wipe back to the absolute empty state.
  const resetMonth = () => {
    setHasData(false); setMonthAllocated(false);
    try {
      localStorage.setItem('fos_has_data', '0');
      localStorage.setItem('fos_month_allocated', '0');
    } catch (e) {}
    setOverlay(null); setTab('home'); setTransitionKey(k => k + 1);
  };

  const goTab = (t) => { setTab(t); setTransitionKey(k => k + 1); };
  const openEnvelope = (id) => setOverlay({ kind: 'envelope', id });
  const openAllocation = () => setOverlay({ kind: 'allocation' });
  const openFixes = () => setOverlay({ kind: 'fixes' });
  const openNewEnvelope = (type) => setNewEnv({ type });
  const closeOverlay = () => setOverlay(null);

  const showFAB = hasData && (tab === 'budget' || tab === 'home') && !overlay;

  // Which tab shows the floating bottom nav?
  const showNav = !overlay;

  return (
    <div style={{
      width: '100%', height: '100%',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: '#06080d',
      padding: 24, boxSizing: 'border-box',
      minHeight: '100vh',
    }}>
      <div style={{ width: W, height: H, position: 'relative' }}>
      <div style={{
        transform: `scale(${scale})`, transformOrigin: 'top left',
        position: 'absolute', top: 0, left: 0,
      }}>
      <PhoneShell>
        {/* Main tab content */}
        <ScreenSlide keyId={`tab-${tab}-${transitionKey}`}>
          <div style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
            {tab === 'home' && (
              !hasData ? (
                <EmptyHomeScreen monthLabel={CUR_MONTH} onAllocate={openAllocation} />
              ) : monthAllocated ? (
                <HomeScreen
                  monthLabel={CUR_MONTH}
                  onOpenEnvelope={openEnvelope}
                  onOpenAllocation={openAllocation}
                  onGoTo={goTab}
                />
              ) : (
                <HomeWithUnallocatedBanner onAllocate={openAllocation} />
              )
            )}
            {tab === 'budget' && (
              !hasData ? (
                <EmptyBudgetScreen monthLabel={CUR_MONTH} onAllocate={openAllocation} />
              ) : (
                <BudgetScreen
                  onOpenEnvelope={openEnvelope}
                  onOpenAllocation={openAllocation}
                  onOpenFixes={openFixes}
                  onAddEnvelope={openNewEnvelope}
                />
              )
            )}
            {tab === 'patrimoine' && (
              !hasData ? <EmptyPatrimoineScreen onAddAccount={openAllocation} /> : <PatrimoineScreen />
            )}
            {tab === 'history' && (
              !hasData ? <EmptyHistoryScreen /> : <HistoryScreen />
            )}
          </div>
        </ScreenSlide>

        {/* Overlay screens */}
        {overlay?.kind === 'envelope' && (
          <div style={{ position: 'absolute', inset: 0, background: FOS.bg, display: 'flex', flexDirection: 'column', zIndex: 80 }}>
            <ScreenSlide keyId={`env-${overlay.id}`} dir="right">
              <EnvelopeDetailScreen envId={overlay.id} onBack={closeOverlay} />
            </ScreenSlide>
          </div>
        )}
        {overlay?.kind === 'allocation' && (
          <div style={{ position: 'absolute', inset: 0, background: FOS.bg, display: 'flex', flexDirection: 'column', zIndex: 80 }}>
            <ScreenSlide keyId={`alloc`} dir="up">
              <AllocationScreen
                monthLabel="juin 2026"
                previousMonth="mai 2026"
                hasTemplate={hasData && MONTH_HISTORY.length > 0}
                fromScratch={!hasData}
                onBack={closeOverlay}
                onComplete={() => { allocateMonth(); closeOverlay(); }}
                onAddEnvelope={openNewEnvelope}
              />
            </ScreenSlide>
          </div>
        )}
        {overlay?.kind === 'fixes' && (
          <div style={{ position: 'absolute', inset: 0, background: FOS.bg, display: 'flex', flexDirection: 'column', zIndex: 80 }}>
            <ScreenSlide keyId={`fixes`} dir="right">
              <FixesScreen onBack={closeOverlay} onAddEnvelope={openNewEnvelope} />
            </ScreenSlide>
          </div>
        )}

        {/* FAB */}
        {showFAB && <FAB onClick={() => setSheet(true)} />}

        {/* Bottom nav */}
        {showNav && <BottomNav tab={tab} onChange={goTab} />}

        {/* Demo control — reset the prototype back to the absolute empty
            (fresh-install) state. Prototype-only affordance. */}
        {hasData && showNav && (
          <button
            onClick={resetMonth}
            title="Réinitialiser (démo) — repartir d'une app vide"
            style={{
              position: 'absolute', left: 16, bottom: 34, zIndex: 60,
              display: 'flex', alignItems: 'center', gap: 6,
              padding: '7px 11px', borderRadius: 100,
              background: 'rgba(255,255,255,0.05)', color: FOS.textDim,
              border: `1px solid ${FOS.outline}`, cursor: 'pointer',
              fontFamily: '"Geist Mono", monospace', fontSize: 10, letterSpacing: 0.3,
              backdropFilter: 'blur(8px)',
            }}
          >
            <Icon name="repeat" size={12} />
            démo
          </button>
        )}

        {/* Expense sheet */}
        <ExpenseSheet open={sheet} onClose={() => setSheet(false)} />

        {/* New envelope creation sheet */}
        <NewEnvelopeSheet
          open={!!newEnv}
          type={newEnv?.type}
          onClose={() => setNewEnv(null)}
          onSave={() => setNewEnv(null)}
        />
      </PhoneShell>
      </div>
      </div>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
