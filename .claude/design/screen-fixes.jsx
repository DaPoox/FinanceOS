// screen-fixes.jsx — Finance OS — All fixed expenses

function FixesScreen({ onBack, onAddEnvelope }) {
  const [filter, setFilter] = React.useState('all');

  const fixes = ENVELOPES.filter(e => e.type === 'fixe');
  const sorted = [...fixes].sort((a, b) => b.allocated - a.allocated);
  const total = sorted.reduce((s, e) => s + e.allocated, 0);

  const filters = [
    { id: 'all',     label: 'Toutes' },
    { id: 'logement',label: 'Logement' },
    { id: 'transport',label: 'Transport' },
    { id: 'sante',   label: 'Santé' },
    { id: 'loisirs', label: 'Loisirs' },
  ];

  return (
    <div style={{ flex: 1, overflow: 'auto', paddingBottom: 100 }}>
      <ScreenHeader title="Charges fixes" onBack={onBack} subtitle={`${fixes.length} prélèvements automatiques`} />

      {/* Hero */}
      <div style={{ padding: '8px 20px 0' }}>
        <Card style={{ padding: 20 }}>
          <SectionLabel>Total mensuel</SectionLabel>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 6, marginTop: 8 }}>
            <Num size={36} weight={500}>{total.toLocaleString('fr-FR')}</Num>
            <Num size={18} color={FOS.textDim}>€</Num>
          </div>
          <div style={{ display: 'flex', gap: 6, marginTop: 12, flexWrap: 'wrap' }}>
            <Pill color={FOS.pos} bg={`${FOS.pos}1F`}>
              <svg width="10" height="10" viewBox="0 0 24 24" style={{ marginRight: 4 }} fill="none">
                <path d="M5 12l5 5L19 7" stroke={FOS.pos} strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              Tout réglé ce mois
            </Pill>
            <Pill color={FOS.textDim} bg="rgba(255,255,255,0.04)">
              <Num size={12} color={FOS.textDim}>{Math.round((total / CURRENT.income) * 100)}%</Num>
              <span style={{ marginLeft: 4 }}>du revenu</span>
            </Pill>
          </div>
        </Card>
      </div>

      {/* Filters */}
      <div style={{ padding: '18px 0 0' }}>
        <div style={{ display: 'flex', gap: 6, overflowX: 'auto', padding: '0 20px', scrollbarWidth: 'none' }}>
          {filters.map(f => {
            const active = f.id === filter;
            return (
              <button key={f.id} onClick={() => setFilter(f.id)} style={{
                flexShrink: 0, padding: '6px 12px', borderRadius: 100,
                border: `1px solid ${active ? FOS.outlineSt : FOS.outline}`,
                background: active ? FOS.surfaceVar : 'transparent',
                color: active ? FOS.text : FOS.textDim,
                fontFamily: 'DM Sans, sans-serif', fontSize: 12, fontWeight: 500,
                cursor: 'pointer',
              }}>{f.label}</button>
            );
          })}
        </div>
      </div>

      {/* List */}
      <div style={{ padding: '16px 20px 0' }}>
        <SectionLabel right={<Num size={11} color={FOS.textDim}>Par montant</Num>}>
          {sorted.length} charges
        </SectionLabel>
        <div style={{ marginTop: 10, background: FOS.surface, border: `1px solid ${FOS.outline}`, borderRadius: 18, overflow: 'hidden' }}>
          {sorted.map((e, i) => (
            <div key={e.id} style={{
              display: 'flex', alignItems: 'center', gap: 12,
              padding: '14px 16px',
              borderBottom: i < sorted.length - 1 ? `1px solid ${FOS.outline}` : 'none',
            }}>
              <div style={{
                width: 36, height: 36, borderRadius: 10,
                background: FOS.surfaceVar, color: FOS.textDim,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                flexShrink: 0,
              }}><Icon name={iconFor(e)} size={17} /></div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 14, color: FOS.text, fontWeight: 500 }}>{e.name}</div>
                <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 11, color: FOS.textDim, marginTop: 2 }}>
                  Prélèvement le {((i * 3) % 28) + 1} du mois
                </div>
              </div>
              <Num size={14} weight={500}>{e.allocated.toLocaleString('fr-FR')} €</Num>
            </div>
          ))}
        </div>
      </div>

      {/* Add */}
      <div style={{ padding: '14px 20px 0' }}>
        <AddEnvelopeRow label="+ Nouvelle charge fixe" onClick={() => onAddEnvelope('fixe')} />
      </div>
    </div>
  );
}

Object.assign(window, { FixesScreen });
