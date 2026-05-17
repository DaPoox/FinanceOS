// screen-history.jsx — Finance OS — Annual / monthly history

function HistoryScreen() {
  const totalIncome  = MONTH_HISTORY.reduce((s, m) => s + m.income, 0);
  const totalContrib = MONTH_HISTORY.reduce((s, m) => s + m.contrib, 0);
  const savingRate   = Math.round((totalContrib / totalIncome) * 100);

  return (
    <div style={{ flex: 1, overflow: 'auto', paddingBottom: 100 }}>
      <div style={{ padding: '8px 20px 0' }}>
        <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim }}>Historique</div>
        <div style={{ fontFamily: 'DM Sans', fontSize: 22, fontWeight: 600, color: FOS.text, marginTop: 4 }}>
          12 derniers mois
        </div>
      </div>

      {/* Annual summary */}
      <div style={{ padding: '14px 20px 0' }}>
        <Card style={{ padding: 18, background: `linear-gradient(135deg, ${FOS.surface} 0%, ${FOS.surfaceVar} 100%)` }}>
          <SectionLabel>Bilan annuel</SectionLabel>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginTop: 14 }}>
            <Stat label="Revenu total"     value={totalIncome}   color={FOS.text} />
            <Stat label="Patrimoine ajouté" value={totalContrib} color={FOS.pos} sign />
          </div>
          <div style={{ marginTop: 18, paddingTop: 16, borderTop: `1px solid ${FOS.outline}` }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <div style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>Taux d'épargne moyen</div>
                <Num size={26} weight={500} color={FOS.primary} style={{ marginTop: 4, display: 'inline-block' }}>
                  {savingRate}%
                </Num>
              </div>
              <BarsMini data={MONTH_HISTORY.slice().reverse().map(m => m.contrib)} />
            </div>
          </div>
        </Card>
      </div>

      {/* Month list */}
      <div style={{ padding: '20px 20px 0' }}>
        <SectionLabel>Tous les mois</SectionLabel>
        <div style={{ marginTop: 10, display: 'flex', flexDirection: 'column', gap: 6 }}>
          {MONTH_HISTORY.map((m, i) => <MonthRow key={m.month} m={m} delay={i * 30} />)}
        </div>
      </div>
    </div>
  );
}

function Stat({ label, value, color, sign }) {
  return (
    <div>
      <div style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 0.8 }}>
        {label}
      </div>
      <div style={{ display: 'flex', alignItems: 'baseline', gap: 4, marginTop: 6 }}>
        <Num size={20} weight={500} color={color}>{sign ? '+' : ''}{value.toLocaleString('fr-FR')}</Num>
        <Num size={12} color={FOS.textDim}>€</Num>
      </div>
    </div>
  );
}

function BarsMini({ data }) {
  const max = Math.max(...data);
  return (
    <div style={{ display: 'flex', alignItems: 'flex-end', gap: 3, height: 36 }}>
      {data.map((v, i) => (
        <div key={i} style={{
          width: 6, height: `${(v / max) * 100}%`,
          background: FOS.primary, borderRadius: 2, opacity: 0.4 + (i / data.length) * 0.6,
        }} />
      ))}
    </div>
  );
}

function MonthRow({ m, delay }) {
  const map = {
    best: { color: FOS.primary, label: 'RECORD' },
    good: { color: FOS.pos,     label: 'Bon' },
    mid:  { color: FOS.warn,    label: 'Moyen' },
    hard: { color: FOS.err,     label: 'Dur' },
  }[m.status];

  return (
    <div style={{
      background: FOS.surface, border: `1px solid ${FOS.outline}`,
      borderRadius: 14, padding: '12px 14px',
      display: 'flex', alignItems: 'center', gap: 12,
    }}>
      <div style={{
        width: 4, height: 36, borderRadius: 4, background: map.color, opacity: 0.9, flexShrink: 0,
      }} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
          <div style={{ fontFamily: 'DM Sans', fontSize: 14, color: FOS.text, fontWeight: 500, textTransform: 'capitalize' }}>{m.month}</div>
          {m.status === 'best' && <Pill color={FOS.primary} bg={`${FOS.primary}1F`} style={{ padding: '3px 7px', fontSize: 9 }}>{map.label}</Pill>}
        </div>
        <div style={{ display: 'flex', gap: 12, marginTop: 4 }}>
          <span style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim }}>
            <Num size={11} color={FOS.text}>{m.income.toLocaleString('fr-FR')}</Num> revenu
          </span>
          <span style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim }}>
            <Num size={11} color={FOS.text}>{m.spent.toLocaleString('fr-FR')}</Num> dépensé
          </span>
        </div>
      </div>
      <div style={{ textAlign: 'right' }}>
        <Num size={15} weight={500} color={FOS.pos}>+{m.contrib.toLocaleString('fr-FR')}</Num>
        <Num size={11} color={FOS.textDim}> €</Num>
      </div>
    </div>
  );
}

Object.assign(window, { HistoryScreen });
