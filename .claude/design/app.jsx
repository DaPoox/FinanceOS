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

  const goTab = (t) => { setTab(t); setTransitionKey(k => k + 1); };
  const openEnvelope = (id) => setOverlay({ kind: 'envelope', id });
  const openAllocation = () => setOverlay({ kind: 'allocation' });
  const openFixes = () => setOverlay({ kind: 'fixes' });
  const openNewEnvelope = (type) => setNewEnv({ type });
  const closeOverlay = () => setOverlay(null);

  const showFAB = (tab === 'budget' || tab === 'home') && !overlay;

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
              <HomeScreen
                onOpenEnvelope={openEnvelope}
                onOpenAllocation={openAllocation}
                onGoTo={goTab}
              />
            )}
            {tab === 'budget' && (
              <BudgetScreen
                onOpenEnvelope={openEnvelope}
                onOpenAllocation={openAllocation}
                onOpenFixes={openFixes}
                onAddEnvelope={openNewEnvelope}
              />
            )}
            {tab === 'patrimoine' && <PatrimoineScreen />}
            {tab === 'history'    && <HistoryScreen />}
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
              <AllocationScreen onBack={closeOverlay} onComplete={closeOverlay} onAddEnvelope={openNewEnvelope} />
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
