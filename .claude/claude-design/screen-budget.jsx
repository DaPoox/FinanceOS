// screen-budget.jsx — Finance OS — Budget monthly view

function BudgetScreen({ onOpenEnvelope, onOpenAllocation }) {
  const groups = [
    { id: 'fixe',  label: 'Fixes',                 type: 'fixe',  hint: 'Reconduits chaque mois' },
    { id: 'var',   label: 'Variables',             type: 'var',   hint: 'Ajustables' },
    { id: 'month', label: 'Du mois',               type: 'month', hint: 'One-shot' },
    { id: 'perm',  label: 'Permanentes',           type: 'perm',  hint: 'Accumulées' },
    { id: 'save',  label: 'Épargne',               type: 'save',  hint: 'Versements mensuels' },
    { id: 'inv',   label: 'Investissement',        type: 'inv',   hint: 'Portefeuille' },
  ];

  const totalAlloc = ENVELOPES.reduce((s, e) => s + e.allocated, 0);
  const totalSpent = ENVELOPES.reduce((s, e) => s + e.spent, 0);

  return (
    <div style={{ flex: 1, overflow: 'auto', paddingBottom: 100 }}>
      {/* Header */}
      <div style={{ padding: '8px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim }}>Budget</div>
          <div style={{ fontFamily: 'DM Sans', fontSize: 22, fontWeight: 600, color: FOS.text }}>Mai 2026</div>
        </div>
        <button onClick={onOpenAllocation} style={{
          border: `1px solid ${FOS.outlineSt}`, background: FOS.surface,
          color: FOS.text, fontFamily: 'DM Sans', fontSize: 12, fontWeight: 600,
          padding: '8px 14px', borderRadius: 100, cursor: 'pointer',
          display: 'inline-flex', alignItems: 'center', gap: 6,
        }}>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
            <path d="M4 21V10M4 8V3M12 21v-7M12 12V3M20 21v-3M20 16V3" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
            <circle cx="4" cy="9" r="2" fill={FOS.surface} stroke="currentColor" strokeWidth="1.8"/>
            <circle cx="12" cy="13" r="2" fill={FOS.surface} stroke="currentColor" strokeWidth="1.8"/>
            <circle cx="20" cy="17" r="2" fill={FOS.surface} stroke="currentColor" strokeWidth="1.8"/>
          </svg>
          Allouer
        </button>
      </div>

      {/* Global progress card */}
      <div style={{ padding: '14px 20px 0' }}>
        <Card style={{ padding: 18 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <div style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>Revenu — Dépenses</div>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: 4, marginTop: 6 }}>
                <Num size={28} weight={500}>{(CURRENT.income - totalSpent).toLocaleString('fr-FR')}</Num>
                <Num size={16} color={FOS.textDim}>€ restant</Num>
              </div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <Num size={14} color={FOS.textDim}>{CURRENT.income.toLocaleString('fr-FR')} €</Num>
              <div style={{ fontFamily: 'DM Sans', fontSize: 10, color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 0.8 }}>revenu</div>
            </div>
          </div>
          <div style={{ marginTop: 14 }}>
            <Progress value={totalSpent} max={CURRENT.income} color={FOS.primary} height={8}/>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 10 }}>
            <span style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim }}>
              <Num size={11} color={FOS.text}>{totalSpent.toLocaleString('fr-FR')} €</Num> dépensé
            </span>
            <span style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim }}>
              <Num size={11} color={FOS.text}>{totalAlloc.toLocaleString('fr-FR')} €</Num> alloué
            </span>
          </div>
        </Card>
      </div>

      {/* Groups */}
      <div style={{ padding: '8px 20px 0' }}>
        {groups.map(g => {
          const items = ENVELOPES.filter(e => e.type === g.type);
          if (!items.length) return null;
          const groupTotal = items.reduce((s, e) => s + e.allocated, 0);
          return (
            <div key={g.id} style={{ marginTop: 18 }}>
              <SectionLabel right={<Num size={11} color={FOS.textDim}>{groupTotal.toLocaleString('fr-FR')} €</Num>}>
                {g.label}
              </SectionLabel>
              <div style={{ marginTop: 8, display: 'flex', flexDirection: 'column', gap: 6 }}>
                {items.map((e, i) => (
                  <EnvelopeRow key={e.id} env={e} delay={i * 40} onClick={() => onOpenEnvelope(e.id)} />
                ))}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function EnvelopeRow({ env, onClick, delay = 0 }) {
  const pct = env.spent / env.allocated;
  const over = env.spent > env.allocated;
  const warn = !over && pct > 0.85;
  const fixed = env.type === 'fixe';
  const isSave = env.type === 'save';
  const isInv  = env.type === 'inv';
  const isPerm = env.type === 'perm';
  const isMonth = env.type === 'month';

  const barColor = over ? FOS.err : warn ? FOS.warn : isSave ? FOS.save : isInv ? FOS.invest : fixed ? FOS.textDim : FOS.text;
  const iconColor = isSave ? FOS.save : isInv ? FOS.invest : fixed ? FOS.textDim : FOS.text;

  return (
    <div onClick={onClick} style={{
      background: FOS.surface, border: `1px solid ${FOS.outline}`,
      borderRadius: 16, padding: '12px 14px', cursor: 'pointer',
      display: 'flex', alignItems: 'center', gap: 12,
    }}>
      <div style={{
        width: 38, height: 38, borderRadius: 12,
        background: FOS.surfaceVar, color: iconColor,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        fontSize: 16, flexShrink: 0,
      }}>{env.icon}</div>

      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
          <div style={{ fontFamily: 'DM Sans', fontSize: 14, fontWeight: 500, color: FOS.text }}>
            {env.name}
          </div>
          {isMonth && <Pill color={FOS.primary} bg={`${FOS.primary}1F`} style={{ padding: '3px 7px', fontSize: 9 }}>NEW</Pill>}
          {isPerm  && <Pill color={FOS.text} bg={FOS.surfaceVar} style={{ padding: '3px 7px', fontSize: 11, fontWeight: 700 }}>∞</Pill>}
          {over    && <Pill color={FOS.err} bg={`${FOS.err}1F`} style={{ padding: '3px 7px', fontSize: 9 }}>DÉPASSÉ</Pill>}
        </div>
        <div style={{ marginTop: 8 }}>
          <Progress value={env.spent} max={env.allocated} color={barColor} height={4} delay={delay}/>
        </div>
      </div>

      <div style={{ textAlign: 'right', minWidth: 78 }}>
        {isPerm ? (
          <>
            <Num size={14} weight={500}>{env.accumulated.toLocaleString('fr-FR')}</Num>
            <Num size={11} color={FOS.textDim}> €</Num>
            <div style={{ fontFamily: 'DM Sans', fontSize: 10, color: FOS.textDim, marginTop: 2 }}>
              +{env.allocated}€/mois
            </div>
          </>
        ) : (
          <>
            <Num size={14} weight={500} color={over ? FOS.err : FOS.text}>{env.spent}</Num>
            <Num size={11} color={FOS.textDim}> / {env.allocated} €</Num>
          </>
        )}
      </div>
    </div>
  );
}

Object.assign(window, { BudgetScreen, EnvelopeRow });
