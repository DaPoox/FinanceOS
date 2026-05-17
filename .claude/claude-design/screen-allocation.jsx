// screen-allocation.jsx — Finance OS — Monthly allocation flow

function AllocationScreen({ onBack, onComplete }) {
  const [step, setStep] = React.useState(0);
  const [income, setIncome] = React.useState('4200');
  const [template, setTemplate] = React.useState('previous');
  const [edits, setEdits] = React.useState(() =>
    Object.fromEntries(ENVELOPES.map(e => [e.id, e.allocated]))
  );

  const totalAlloc = Object.values(edits).reduce((s, v) => s + (parseInt(v) || 0), 0);
  const remaining  = (parseInt(income) || 0) - totalAlloc;

  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <ScreenHeader
        title="Allouer mai 2026"
        onBack={step === 0 ? onBack : () => setStep(step - 1)}
        right={
          <div style={{ display: 'flex', gap: 4 }}>
            {[0,1,2].map(i => (
              <div key={i} style={{
                width: i === step ? 20 : 6, height: 6, borderRadius: 100,
                background: i <= step ? FOS.primary : FOS.surfaceVar,
                transition: 'width 240ms ease',
              }} />
            ))}
          </div>
        }
      />

      <div style={{ flex: 1, overflow: 'auto', paddingBottom: 110 }}>
        {step === 0 && <StepIncome value={income} onChange={setIncome} />}
        {step === 1 && <StepTemplate value={template} onChange={setTemplate} />}
        {step === 2 && <StepAdjust edits={edits} setEdits={setEdits} income={parseInt(income) || 0}/>}
      </div>

      {/* Sticky footer */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0,
        background: FOS.surfaceCt,
        borderTop: `1px solid ${FOS.outline}`,
        padding: '14px 20px 18px',
      }}>
        {step === 2 && (
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
            <span style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>Non alloué</span>
            <Num size={15} weight={600} color={remaining < 0 ? FOS.err : remaining < 50 ? FOS.warn : FOS.pos}>
              {remaining >= 0 ? '' : '−'}{Math.abs(remaining).toLocaleString('fr-FR')} €
            </Num>
          </div>
        )}
        <button onClick={() => step < 2 ? setStep(step + 1) : onComplete?.()} style={{
          width: '100%', padding: '16px', borderRadius: 16,
          background: FOS.primary, color: FOS.onPrimary, border: 'none',
          fontFamily: 'DM Sans', fontWeight: 700, fontSize: 15,
          cursor: 'pointer', letterSpacing: 0.3,
        }}>
          {step < 2 ? 'Continuer' : 'Valider l\'allocation'}
        </button>
      </div>
    </div>
  );
}

function StepIncome({ value, onChange }) {
  return (
    <div style={{ padding: '8px 20px 0' }}>
      <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim, marginTop: 8 }}>
        Étape 1 sur 3
      </div>
      <div style={{ fontFamily: 'DM Sans', fontSize: 22, fontWeight: 600, color: FOS.text, marginTop: 4, lineHeight: 1.3 }}>
        Quel est ton revenu ce mois ?
      </div>

      <div style={{ marginTop: 40, textAlign: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'center', gap: 6 }}>
          <input
            value={value}
            onChange={(e) => onChange(e.target.value.replace(/[^0-9]/g, ''))}
            inputMode="numeric"
            style={{
              width: 220, textAlign: 'center',
              background: 'transparent', border: 'none', outline: 'none',
              fontFamily: '"Geist Mono", monospace',
              fontSize: 56, fontWeight: 500, color: FOS.text, letterSpacing: -2,
              caretColor: FOS.primary,
            }}
          />
          <Num size={28} color={FOS.textDim}>€</Num>
        </div>
        <div style={{
          margin: '8px auto 0', width: 160, height: 1,
          background: `linear-gradient(90deg, transparent, ${FOS.primary}, transparent)`,
        }} />
      </div>

      <div style={{ marginTop: 28, display: 'flex', gap: 8, flexWrap: 'wrap', justifyContent: 'center' }}>
        {['4 000', '4 200', '4 500', '5 000'].map(v => {
          const num = v.replace(/\s/g, '');
          const active = num === value;
          return (
            <button key={v} onClick={() => onChange(num)} style={{
              padding: '10px 16px', borderRadius: 100,
              background: active ? `${FOS.primary}1F` : FOS.surfaceVar,
              color: active ? FOS.primary : FOS.text,
              border: active ? `1px solid ${FOS.primary}` : `1px solid ${FOS.outlineSt}`,
              fontFamily: '"Geist Mono", monospace', fontSize: 13, cursor: 'pointer',
            }}>{v} €</button>
          );
        })}
      </div>
    </div>
  );
}

function StepTemplate({ value, onChange }) {
  const opts = [
    { id: 'previous', title: 'Mois précédent', sub: 'Avril 2026 · recommandé', tag: 'Conseillé' },
    { id: 'past',     title: 'Un mois passé', sub: 'Choisir n\'importe quel mois' },
    { id: 'default',  title: 'Template par défaut', sub: 'Ta config sauvegardée' },
    { id: 'scratch',  title: 'From scratch', sub: 'Tout à zéro' },
  ];
  return (
    <div style={{ padding: '8px 20px 0' }}>
      <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim, marginTop: 8 }}>
        Étape 2 sur 3
      </div>
      <div style={{ fontFamily: 'DM Sans', fontSize: 22, fontWeight: 600, color: FOS.text, marginTop: 4, lineHeight: 1.3 }}>
        Sur quelle base partir ?
      </div>

      <div style={{ marginTop: 18, display: 'flex', flexDirection: 'column', gap: 8 }}>
        {opts.map(o => {
          const active = value === o.id;
          return (
            <button key={o.id} onClick={() => onChange(o.id)} style={{
              textAlign: 'left', padding: 16, borderRadius: 16,
              background: active ? `${FOS.primary}14` : FOS.surface,
              border: active ? `1px solid ${FOS.primary}` : `1px solid ${FOS.outline}`,
              color: FOS.text, cursor: 'pointer',
              display: 'flex', alignItems: 'center', gap: 14,
            }}>
              <div style={{
                width: 22, height: 22, borderRadius: 100,
                border: `2px solid ${active ? FOS.primary : FOS.outlineSt}`,
                display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
              }}>
                {active && <div style={{ width: 10, height: 10, borderRadius: 100, background: FOS.primary }} />}
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                  <div style={{ fontFamily: 'DM Sans', fontSize: 15, fontWeight: 500 }}>{o.title}</div>
                  {o.tag && <Pill color={FOS.primary} bg={`${FOS.primary}1F`} style={{ padding: '3px 7px', fontSize: 9 }}>{o.tag}</Pill>}
                </div>
                <div style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim, marginTop: 2 }}>{o.sub}</div>
              </div>
            </button>
          );
        })}
      </div>
    </div>
  );
}

function StepAdjust({ edits, setEdits, income }) {
  const groups = [
    { label: 'Fixes',           type: 'fixe' },
    { label: 'Variables',       type: 'var' },
    { label: 'Du mois',         type: 'month' },
    { label: 'Permanentes',     type: 'perm' },
    { label: 'Épargne',         type: 'save' },
    { label: 'Investissement',  type: 'inv' },
  ];

  return (
    <div style={{ padding: '8px 20px 0' }}>
      <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim, marginTop: 8 }}>
        Étape 3 sur 3
      </div>
      <div style={{ fontFamily: 'DM Sans', fontSize: 22, fontWeight: 600, color: FOS.text, marginTop: 4, lineHeight: 1.3 }}>
        Ajuste tes enveloppes
      </div>

      {groups.map(g => {
        const items = ENVELOPES.filter(e => e.type === g.type);
        if (!items.length) return null;
        return (
          <div key={g.type} style={{ marginTop: 18 }}>
            <SectionLabel>{g.label}</SectionLabel>
            <div style={{ marginTop: 8, display: 'flex', flexDirection: 'column', gap: 4 }}>
              {items.map(e => (
                <div key={e.id} style={{
                  display: 'flex', alignItems: 'center', gap: 12,
                  background: FOS.surface, border: `1px solid ${FOS.outline}`,
                  borderRadius: 14, padding: '10px 14px',
                }}>
                  <div style={{
                    width: 28, height: 28, borderRadius: 8,
                    background: FOS.surfaceVar, color: e.type === 'save' ? FOS.save : e.type === 'inv' ? FOS.invest : FOS.textDim,
                    display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 13,
                  }}>{e.icon}</div>
                  <div style={{ flex: 1, fontFamily: 'DM Sans', fontSize: 14, color: FOS.text }}>{e.name}</div>
                  <input
                    value={edits[e.id]}
                    onChange={(ev) => setEdits({ ...edits, [e.id]: ev.target.value.replace(/[^0-9]/g, '') })}
                    inputMode="numeric"
                    style={{
                      width: 64, padding: '6px 8px',
                      background: FOS.surfaceVar, border: `1px solid ${FOS.outline}`,
                      borderRadius: 8, textAlign: 'right',
                      fontFamily: '"Geist Mono", monospace', fontSize: 14, color: FOS.text, outline: 'none',
                    }}
                  />
                  <span style={{ fontFamily: '"Geist Mono", monospace', fontSize: 13, color: FOS.textDim }}>€</span>
                </div>
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );
}

Object.assign(window, { AllocationScreen });
