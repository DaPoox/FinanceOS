// screen-home.jsx — Finance OS — Home Dashboard

function HomeScreen({ onOpenEnvelope, onOpenAllocation, onGoTo, monthLabel = 'Mai 2026' }) {
  const shortMonth = monthLabel.split(' ')[0].toLowerCase();
  const nw = useCountUp(CURRENT.netWorth, { ms: 1100 });
  const dContrib = CURRENT.contribSavings + CURRENT.contribInvest + CURRENT.contribMarket;

  const monthSpent = ENVELOPES.filter(e => e.type !== 'save' && e.type !== 'inv' && e.type !== 'perm')
                              .reduce((s, e) => s + e.spent, 0);
  const monthAllocated = ENVELOPES.filter(e => e.type !== 'save' && e.type !== 'inv' && e.type !== 'perm')
                                  .reduce((s, e) => s + e.allocated, 0);
  const monthRemaining = monthAllocated - monthSpent;

  // top-4 most active envelopes (highest spent, excluding fixes paid)
  const top4 = [...ENVELOPES]
    .filter(e => e.type !== 'save' && e.type !== 'inv' && e.type !== 'perm')
    .sort((a, b) => b.spent - a.spent)
    .slice(0, 4);

  return (
    <div style={{ flex: 1, overflow: 'auto', paddingBottom: 100 }}>
      {/* Greeting */}
      <div style={{ padding: '8px 20px 4px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim }}>Bonjour, Théo</div>
          <div style={{ fontFamily: 'DM Sans', fontSize: 18, fontWeight: 600, color: FOS.text }}>{monthLabel}</div>
        </div>
        <div style={{
          width: 40, height: 40, borderRadius: 100,
          background: FOS.surface, border: `1px solid ${FOS.outline}`,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M6 8a6 6 0 0112 0v4l1.5 3h-15L6 12V8z" stroke={FOS.text} strokeWidth="1.6" strokeLinejoin="round"/>
            <path d="M10 18a2 2 0 004 0" stroke={FOS.text} strokeWidth="1.6" strokeLinecap="round"/>
          </svg>
        </div>
      </div>

      {/* ───── Hero: Net Worth ───── */}
      <div style={{ padding: '12px 20px 0' }}>
        <Card style={{ padding: 20 }}>
          <SectionLabel>Patrimoine total</SectionLabel>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 10, marginTop: 8 }}>
            <Num size={40} weight={500} style={{ letterSpacing: -1.2 }}>
              {Math.round(nw).toLocaleString('fr-FR')}
            </Num>
            <Num size={20} weight={400} color={FOS.textDim}>€</Num>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 8 }}>
            <Pill color={FOS.primary} bg={`${FOS.primary}1F`}>
              <svg width="10" height="10" viewBox="0 0 10 10" style={{ marginRight: 4 }}>
                <path d="M5 1l4 5H1z" fill={FOS.primary}/>
              </svg>
              <Num size={12} color={FOS.primary} weight={600}>+{CURRENT.netWorthDelta.toLocaleString('fr-FR')} €</Num>
              <span style={{ marginLeft: 4, opacity: 0.7 }}>ce mois</span>
            </Pill>
            <span style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>
              Meilleur mois depuis 4 mois <span style={{ filter: 'grayscale(0)' }}>🔥</span>
            </span>
          </div>

          {/* Divider */}
          <div style={{ marginTop: 18, height: 1, background: FOS.outline }} />

          {/* Section: contribution du mois */}
          <div style={{ marginTop: 16 }}>
            <SectionLabel style={{ padding: 0 }}>Contribution du mois</SectionLabel>

            {/* Row 1: Épargne + Investissement, two columns, numbers only */}
            <div style={{ marginTop: 12, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
              <div>
                <div style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>Épargne</div>
                <Num size={16} weight={600} color={FOS.text} style={{ marginTop: 4, display: 'inline-block' }}>+{CURRENT.contribSavings} €</Num>
              </div>
              <div>
                <div style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>Investissement</div>
                <Num size={16} weight={600} color={FOS.text} style={{ marginTop: 4, display: 'inline-block' }}>+{CURRENT.contribInvest} €</Num>
              </div>
            </div>

          </div>
        </Card>
      </div>

      {/* ───── Insight ───── */}
      <div style={{ padding: '12px 20px 0' }}>
        <div style={{
          background: FOS.surface,
          border: `1px solid ${FOS.outline}`,
          borderLeft: `3px solid ${FOS.warn}`,
          borderRadius: 16, padding: '14px 16px',
          display: 'flex', alignItems: 'flex-start', gap: 12,
        }}>
          <div style={{
            width: 32, height: 32, borderRadius: 10,
            background: `${FOS.warn}1F`, color: FOS.warn,
            display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
          }}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M12 3l9 16H3L12 3z" stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round"/>
              <path d="M12 10v4M12 16.5v.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontFamily: 'DM Sans', fontSize: 14, color: FOS.text, fontWeight: 500 }}>
              Restos dépassé de <Num size={14} color={FOS.err} weight={600}>26 €</Num>
            </div>
            <div style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim, marginTop: 2 }}>
              5 sorties ce mois. Tu en es à 112% du budget.
            </div>
          </div>
        </div>
      </div>

      {/* ───── Budget du mois ───── */}
      <div style={{ padding: '20px 20px 0' }}>
        <SectionLabel right={<span>{eur(CURRENT.income)} de revenu</span>}>Budget {shortMonth}</SectionLabel>
        <Card style={{ marginTop: 8, padding: 18 }} onClick={() => onGoTo('budget')}>
          <div style={{ display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between' }}>
            <div>
              <div style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>Restant à dépenser</div>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: 6, marginTop: 4 }}>
                <Num size={28} weight={500}>{monthRemaining.toLocaleString('fr-FR')}</Num>
                <Num size={16} color={FOS.textDim}>€</Num>
              </div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>Dépensé</div>
              <Num size={14} color={FOS.textDim} style={{ marginTop: 4 }}>
                {monthSpent.toLocaleString('fr-FR')} / {monthAllocated.toLocaleString('fr-FR')} €
              </Num>
            </div>
          </div>
          <div style={{ marginTop: 14 }}>
            <Progress value={monthSpent} max={monthAllocated} color={FOS.primary} height={8}/>
          </div>
        </Card>
      </div>

      {/* ───── Enveloppes grid 2x2 ───── */}
      <div style={{ padding: '20px 20px 0' }}>
        <SectionLabel right={<span style={{ color: FOS.primary, fontWeight: 600 }} onClick={() => onGoTo('budget')}>Tout voir →</span>}>
          Enveloppes actives
        </SectionLabel>
        <div style={{ marginTop: 8, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {top4.map(e => <EnvelopeMini key={e.id} env={e} onClick={() => onOpenEnvelope(e.id)} />)}
        </div>
      </div>

      {/* ───── Graph 6 mois ───── */}
      <div style={{ padding: '20px 20px 0' }}>
        <Card>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <SectionLabel style={{ padding: 0 }}>Patrimoine 6 mois</SectionLabel>
            <Pill color={FOS.pos} bg={`${FOS.pos}1F`}>+11.8%</Pill>
          </div>
          <div style={{ marginTop: 8, marginLeft: -4, marginRight: -4 }}>
            <Sparkline data={NETWORTH_6M} width={340} height={92} stroke={FOS.primary}
                       fillFrom={`${FOS.primary}26`} fillTo={`${FOS.primary}00`} />
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 6, padding: '0 4px' }}>
            {['déc','jan','fév','mar','avr','mai'].map(m => (
              <span key={m} style={{ fontFamily: 'DM Sans', fontSize: 10, color: FOS.textDim }}>{m}</span>
            ))}
          </div>
        </Card>
      </div>

      {/* ───── 2 mois récents ───── */}
      <div style={{ padding: '16px 20px 0' }}>
        <SectionLabel>Mois récents</SectionLabel>
        <div style={{ marginTop: 8, display: 'flex', flexDirection: 'column', gap: 8 }}>
          {MONTH_HISTORY.slice(1, 3).map(m => (
            <Card key={m.month} variant="variant" style={{ padding: '12px 16px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}
                  onClick={() => onGoTo('history')}>
              <div>
                <div style={{ fontFamily: 'DM Sans', fontSize: 14, color: FOS.text, textTransform: 'capitalize', fontWeight: 500 }}>{m.month}</div>
                <div style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim, marginTop: 2 }}>
                  Revenu <Num size={11} color={FOS.textDim}>{m.income.toLocaleString('fr-FR')} €</Num>
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontFamily: 'DM Sans', fontSize: 10, color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 0.8 }}>Patrimoine</div>
                  <Num size={14} color={FOS.pos} weight={500}>+{m.contrib.toLocaleString('fr-FR')} €</Num>
                </div>
                <StatusDot status={m.status} />
              </div>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
}

function ContribRow({ label, value, total, color }) {
  const pct = (value / total) * 100;
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
      <div style={{ width: 92, fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>{label}</div>
      <div style={{ flex: 1 }}>
        <Progress value={value} max={total} color={color} height={5}/>
      </div>
      <Num size={12} weight={500} style={{ width: 50, textAlign: 'right' }}>+{value} €</Num>
    </div>
  );
}

function EnvelopeMini({ env, onClick }) {
  const pct = env.spent / env.allocated;
  const status = env.spent > env.allocated ? 'over' : pct > 0.8 ? 'warn' : env.type === 'fixe' ? 'fixed' : 'ok';
  const statusColor = status === 'over' ? FOS.err : status === 'warn' ? FOS.warn : status === 'fixed' ? FOS.textDim : FOS.save;
  return (
    <Card onClick={onClick} style={{ padding: 14 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div style={{
          width: 32, height: 32, borderRadius: 10,
          background: FOS.surfaceVar, display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: statusColor,
        }}><Icon name={iconFor(env)} size={16} /></div>
        {status === 'over' && <Pill color={FOS.err} bg={`${FOS.err}1F`} style={{ padding: '4px 7px', fontSize: 10 }}>↑</Pill>}
        {status === 'fixed' && <Pill color={FOS.textDim} bg="rgba(255,255,255,0.04)" style={{ padding: '4px 7px', fontSize: 10 }}>FIXE</Pill>}
      </div>
      <div style={{ marginTop: 12, fontFamily: 'DM Sans', fontSize: 13, color: FOS.text, fontWeight: 500 }}>
        {env.name}
      </div>
      <div style={{ marginTop: 4, display: 'flex', alignItems: 'baseline', gap: 4 }}>
        <Num size={16} weight={500} color={status === 'over' ? FOS.err : FOS.text}>
          {env.spent}
        </Num>
        <Num size={11} color={FOS.textDim}>/ {env.allocated} €</Num>
      </div>
      <div style={{ marginTop: 10 }}>
        <Progress value={env.spent} max={env.allocated} color={statusColor} height={4}/>
      </div>
    </Card>
  );
}

function StatusDot({ status }) {
  const map = { best: FOS.primary, good: FOS.pos, mid: FOS.warn, hard: FOS.err };
  return <div style={{ width: 8, height: 8, borderRadius: 100, background: map[status] }} />;
}

Object.assign(window, { HomeScreen, EnvelopeMini, StatusDot });
