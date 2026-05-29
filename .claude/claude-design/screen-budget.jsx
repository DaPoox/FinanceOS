// screen-budget.jsx — Finance OS — Budget monthly view

function BudgetScreen({ onOpenEnvelope, onOpenAllocation, onOpenFixes, onAddEnvelope }) {
  const groups = [
    { id: 'var',   label: 'Variables',             type: 'var',   hint: 'Ajustables' },
    { id: 'month', label: 'Du mois',               type: 'month', hint: 'One-shot' },
    { id: 'perm',  label: 'Permanentes',           type: 'perm',  hint: 'Accumulées' },
    { id: 'inv',   label: 'Investissement',        type: 'inv',   hint: 'Portefeuille' },
    { id: 'fixe',  label: 'Fixes',                 type: 'fixe',  hint: 'Reconduits chaque mois' },
  ];

  const totalAlloc = ENVELOPES.reduce((s, e) => s + e.allocated, 0);
  const totalSpent = ENVELOPES.reduce((s, e) => s + e.spent, 0);

  // Allocation breakdown by group — includes Épargne (it's still part of the income split,
  // even though we don't show it as a manageable section anymore).
  const allocSlices = [
    { id: 'fixe',  label: 'Fixes',          color: '#4a5568' },
    { id: 'var',   label: 'Variables',      color: FOS.primary },
    { id: 'month', label: 'Du mois',        color: FOS.warn },
    { id: 'perm',  label: 'Permanentes',    color: FOS.pos },
    { id: 'save',  label: 'Épargne',        color: FOS.save },
    { id: 'inv',   label: 'Investissement', color: FOS.invest },
  ].map(g => ({
    ...g,
    value: ENVELOPES.filter(e => e.type === g.id).reduce((s, e) => s + e.allocated, 0),
  })).filter(s => s.value > 0);
  const allocTotal = allocSlices.reduce((s, sl) => s + sl.value, 0);

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

      {/* Allocation donut */}
      <div style={{ padding: '14px 20px 0' }}>
        <Card style={{ padding: 18 }}>
          <SectionLabel right={<Num size={11} color={FOS.textDim}>{CURRENT.income.toLocaleString('fr-FR')} € revenu</Num>}>
            Allocation
          </SectionLabel>
          <div style={{ display: 'flex', alignItems: 'center', gap: 18, marginTop: 14 }}>
            <Donut
              size={132} stroke={13} gap={3}
              slices={allocSlices.map(s => ({ value: s.value, color: s.color }))}
              center={
                <div style={{ textAlign: 'center' }}>
                  <Num size={18} weight={500}>{allocTotal.toLocaleString('fr-FR')}</Num>
                  <div style={{ fontFamily: 'DM Sans', fontSize: 9, color: FOS.textDim, marginTop: 2, letterSpacing: 0.8, textTransform: 'uppercase' }}>alloué</div>
                </div>
              }
            />
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 7 }}>
              {allocSlices.map(s => (
                <AllocLegend key={s.id} color={s.color} label={s.label} value={s.value} total={allocTotal} />
              ))}
            </div>
          </div>
        </Card>
      </div>

      {/* Global progress card */}
      <div style={{ padding: '10px 20px 0' }}>
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
              <Num size={14} color={FOS.textDim}>{totalAlloc.toLocaleString('fr-FR')} €</Num>
              <div style={{ fontFamily: 'DM Sans', fontSize: 10, color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 0.8 }}>alloué</div>
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
              <Num size={11} color={FOS.text}>{CURRENT.income.toLocaleString('fr-FR')} €</Num> revenu
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

          if (g.type === 'fixe') {
            return (
              <div key={g.id} style={{ marginTop: 18 }}>
                <SectionLabel right={<Num size={11} color={FOS.textDim}>{groupTotal.toLocaleString('fr-FR')} €</Num>}>
                  {g.label}
                </SectionLabel>
                <div style={{ marginTop: 8 }}>
                  <FixesSummary items={items} onSeeAll={onOpenFixes} />
                </div>
              </div>
            );
          }

          return (
            <div key={g.id} style={{ marginTop: 18 }}>
              <SectionLabel right={<Num size={11} color={FOS.textDim}>{groupTotal.toLocaleString('fr-FR')} €</Num>}>
                {g.label}
              </SectionLabel>
              <div style={{ marginTop: 8, display: 'flex', flexDirection: 'column', gap: 6 }}>
                {items.map((e, i) => (
                  <EnvelopeRow key={e.id} env={e} delay={i * 40} onClick={() => onOpenEnvelope(e.id)} />
                ))}
                <AddEnvelopeRow
                  label={addLabelFor(g.type)}
                  prominent={g.type === 'month'}
                  onClick={() => onAddEnvelope(g.type)}
                />
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
        flexShrink: 0,
      }}><Icon name={iconFor(env)} size={18} /></div>

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

function FixesSummary({ items, onSeeAll }) {
  const [open, setOpen] = React.useState(false);
  const total = items.reduce((s, e) => s + e.allocated, 0);

  const sorted = [...items].sort((a, b) => b.allocated - a.allocated);
  const SHOW = 5;
  const visible = sorted.slice(0, SHOW);
  const hidden = Math.max(0, sorted.length - SHOW);

  return (
    <div style={{
      background: FOS.surface, border: `1px solid ${FOS.outline}`,
      borderRadius: 16, overflow: 'hidden',
    }}>
      <button onClick={() => setOpen(!open)} style={{
        width: '100%', padding: '14px 16px',
        background: 'transparent', border: 'none', cursor: 'pointer',
        display: 'flex', alignItems: 'center', gap: 12, textAlign: 'left',
        color: FOS.text,
      }}>
        <div style={{
          width: 38, height: 38, borderRadius: 12,
          background: `${FOS.pos}14`, color: FOS.pos,
          display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
        }}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M5 12l5 5L19 7" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 14, fontWeight: 500, color: FOS.text }}>
            Charges fixes réglées
          </div>
          <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 11, color: FOS.textDim, marginTop: 2 }}>
            {items.length} prélèvements automatiques
          </div>
        </div>
        <div style={{ textAlign: 'right' }}>
          <Num size={15} weight={500}>{total.toLocaleString('fr-FR')}</Num>
          <Num size={11} color={FOS.textDim}> €</Num>
        </div>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" style={{
          transform: open ? 'rotate(180deg)' : 'rotate(0deg)',
          transition: 'transform 200ms ease',
          color: FOS.textDim, flexShrink: 0,
        }}>
          <path d="M6 9l6 6 6-6" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
      </button>

      {open && (
        <div style={{ borderTop: `1px solid ${FOS.outline}` }}>
          {visible.map((e, i) => (
            <div key={e.id} style={{
              display: 'flex', alignItems: 'center', gap: 12,
              padding: '12px 16px',
              borderTop: i > 0 ? `1px solid ${FOS.outline}` : 'none',
            }}>
              <div style={{
                width: 28, height: 28, borderRadius: 8,
                background: FOS.surfaceVar, color: FOS.textDim,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                flexShrink: 0,
              }}><Icon name={iconFor(e)} size={14} /></div>
              <div style={{ flex: 1, fontFamily: 'DM Sans, sans-serif', fontSize: 13, color: FOS.text }}>
                {e.name}
              </div>
              <Num size={13} weight={500}>{e.allocated.toLocaleString('fr-FR')} €</Num>
            </div>
          ))}
          {hidden > 0 && (
            <button onClick={(ev) => { ev.stopPropagation(); onSeeAll?.(); }} style={{
              width: '100%', padding: '12px 16px',
              background: 'transparent', border: 'none',
              borderTop: `1px solid ${FOS.outline}`,
              display: 'flex', alignItems: 'center', justifyContent: 'space-between',
              color: FOS.primary, fontFamily: 'DM Sans, sans-serif', fontSize: 13, fontWeight: 600,
              cursor: 'pointer', textAlign: 'left',
            }}>
              <span>+ {hidden} {hidden > 1 ? 'autres charges' : 'autre charge'}</span>
              <span style={{ fontSize: 16 }}>→</span>
            </button>
          )}
        </div>
      )}
    </div>
  );
}

function addLabelFor(type) {
  return {
    var:   '+ Ajouter une catégorie',
    month: '+ Nouvelle dépense du mois',
    perm:  '+ Nouvel objectif',
    inv:   '+ Nouveau placement',
    fixe:  '+ Nouvelle charge',
  }[type] || '+ Nouvelle enveloppe';
}

function AddEnvelopeRow({ label, prominent, onClick }) {
  const [hover, setHover] = React.useState(false);
  const baseBorder = prominent ? `${FOS.primary}66` : FOS.outlineSt;
  const hoverBorder = prominent ? FOS.primary : 'rgba(255,255,255,0.18)';
  return (
    <button
      onClick={onClick}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      style={{
        width: '100%', padding: '12px 14px',
        background: hover ? (prominent ? `${FOS.primary}0F` : 'rgba(255,255,255,0.02)') : 'transparent',
        border: `1px dashed ${hover ? hoverBorder : baseBorder}`,
        borderRadius: 16,
        color: prominent ? FOS.primary : (hover ? FOS.text : FOS.textDim),
        fontFamily: 'DM Sans, sans-serif', fontSize: 13, fontWeight: 500,
        cursor: 'pointer', letterSpacing: 0.2,
        transition: 'background 150ms ease, border-color 150ms ease, color 150ms ease',
      }}>
      {label}
    </button>
  );
}

function AllocLegend({ color, label, value, total }) {
  const pct = Math.round((value / total) * 100);
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
      <div style={{ width: 7, height: 7, borderRadius: 100, background: color, flexShrink: 0 }} />
      <span style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.text, flex: 1, minWidth: 0, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{label}</span>
      <Num size={11} color={FOS.textDim}>{pct}%</Num>
      <Num size={12} weight={500} style={{ width: 52, textAlign: 'right' }}>{value.toLocaleString('fr-FR')} €</Num>
    </div>
  );
}

Object.assign(window, { BudgetScreen, EnvelopeRow, FixesSummary, AddEnvelopeRow, addLabelFor });
