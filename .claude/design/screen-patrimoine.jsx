// screen-patrimoine.jsx — Finance OS — Net worth + accounts

function PatrimoineScreen() {
  const liquid  = ACCOUNTS.filter(a => a.type === 'Compte courant').reduce((s,a) => s+a.balance, 0);
  const savings = ACCOUNTS.filter(a => a.type === 'Épargne').reduce((s,a) => s+a.balance, 0);
  const invest  = ACCOUNTS.filter(a => a.type === 'Investissement').reduce((s,a) => s+a.balance, 0);
  const total = liquid + savings + invest;

  const nw = useCountUp(total, { ms: 1100 });

  return (
    <div style={{ flex: 1, overflow: 'auto', paddingBottom: 100 }}>
      <div style={{ padding: '8px 20px 0' }}>
        <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim }}>Patrimoine</div>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 8, marginTop: 4 }}>
          <Num size={40} weight={500} style={{ letterSpacing: -1.2 }}>{Math.round(nw).toLocaleString('fr-FR')}</Num>
          <Num size={22} color={FOS.textDim}>€</Num>
        </div>
        <div style={{ display: 'flex', gap: 8, marginTop: 8 }}>
          <Pill color={FOS.pos} bg={`${FOS.pos}1F`}>
            <svg width="10" height="10" viewBox="0 0 10 10" style={{ marginRight: 4 }}><path d="M5 1l4 5H1z" fill={FOS.pos}/></svg>
            <Num size={12} color={FOS.pos} weight={600}>+12 380 €</Num>
            <span style={{ marginLeft: 4, opacity: 0.7 }}>6 mois</span>
          </Pill>
          <Pill color={FOS.pos} bg={`${FOS.pos}1F`}>+11.8%</Pill>
        </div>
      </div>

      {/* Donut */}
      <div style={{ padding: '22px 20px 0' }}>
        <Card style={{ padding: 20 }}>
          <SectionLabel>Répartition</SectionLabel>
          <div style={{ display: 'flex', alignItems: 'center', gap: 18, marginTop: 12 }}>
            <Donut
              size={150} stroke={14} gap={3}
              slices={[
                { value: savings, color: FOS.save },
                { value: invest,  color: FOS.invest },
                { value: liquid,  color: '#4a5568' },
              ]}
              center={
                <div style={{ textAlign: 'center' }}>
                  <Num size={20} weight={500}>{Math.round(total/1000)}k</Num>
                  <div style={{ fontFamily: 'DM Sans', fontSize: 10, color: FOS.textDim, marginTop: 2, letterSpacing: 0.8, textTransform: 'uppercase' }}>total</div>
                </div>
              }
            />
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 10 }}>
              <Legend color={FOS.save}   label="Épargne"        value={savings} total={total} />
              <Legend color={FOS.invest} label="Investissement" value={invest}  total={total} />
              <Legend color="#4a5568"    label="Liquidités"     value={liquid}  total={total} />
            </div>
          </div>
        </Card>
      </div>

      {/* Graph 12 mois */}
      <div style={{ padding: '14px 20px 0' }}>
        <Card>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <SectionLabel style={{ padding: 0 }}>Évolution 12 mois</SectionLabel>
            <div style={{ display: 'flex', gap: 4 }}>
              {['6M','12M','3A'].map((t, i) => (
                <div key={t} style={{
                  padding: '4px 10px', borderRadius: 100, fontFamily: 'DM Sans', fontSize: 11, fontWeight: 500,
                  background: i === 1 ? FOS.surfaceVar : 'transparent',
                  color: i === 1 ? FOS.text : FOS.textDim,
                }}>{t}</div>
              ))}
            </div>
          </div>
          <div style={{ marginTop: 12, marginLeft: -4 }}>
            <Sparkline data={NETWORTH_12M} width={340} height={120} stroke={FOS.primary}
                       fillFrom={`${FOS.primary}26`} fillTo={`${FOS.primary}00`} />
          </div>
        </Card>
      </div>

      {/* Comptes par catégorie */}
      <div style={{ padding: '22px 20px 0' }}>
        <SectionLabel right={<span style={{ color: FOS.primary, fontWeight: 600 }}>+ Ajouter</span>}>
          Comptes
        </SectionLabel>

        {['Compte courant', 'Épargne', 'Investissement'].map(cat => {
          const accs = ACCOUNTS.filter(a => a.type === cat);
          if (!accs.length) return null;
          return (
            <div key={cat} style={{ marginTop: 12 }}>
              <div style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim, marginBottom: 6, padding: '0 4px' }}>{cat}</div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
                {accs.map(a => <AccountRow key={a.name} acc={a} />)}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function Legend({ color, label, value, total }) {
  const pct = Math.round((value / total) * 100);
  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
        <div style={{ width: 8, height: 8, borderRadius: 100, background: color }} />
        <span style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim, flex: 1 }}>{label}</span>
        <Num size={11} color={FOS.textDim} weight={500}>{pct}%</Num>
      </div>
      <Num size={15} weight={500} style={{ marginLeft: 16, marginTop: 2 }}>
        {value.toLocaleString('fr-FR')} €
      </Num>
    </div>
  );
}

function AccountRow({ acc }) {
  const hasCap = acc.cap !== undefined;
  const pct = hasCap ? acc.balance / acc.cap : 0;
  return (
    <div style={{
      background: FOS.surface, border: `1px solid ${FOS.outline}`,
      borderRadius: 14, padding: '12px 14px',
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{
            width: 34, height: 34, borderRadius: 10,
            background: `${acc.color}1F`, color: acc.color,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontFamily: '"Geist Mono", monospace', fontSize: 13, fontWeight: 600,
          }}>{acc.name.slice(0,2).toUpperCase()}</div>
          <div>
            <div style={{ fontFamily: 'DM Sans', fontSize: 14, color: FOS.text, fontWeight: 500 }}>{acc.name}</div>
            {hasCap && (
              <div style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim, marginTop: 2 }}>
                Cap {acc.cap.toLocaleString('fr-FR')} €
              </div>
            )}
          </div>
        </div>
        <Num size={15} weight={500}>{acc.balance.toLocaleString('fr-FR')} €</Num>
      </div>
      {hasCap && (
        <div style={{ marginTop: 10 }}>
          <Progress value={acc.balance} max={acc.cap} color={acc.color} height={4} />
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 6 }}>
            <span style={{ fontFamily: 'DM Sans', fontSize: 10, color: FOS.textDim }}>{Math.round(pct * 100)}% de capacité</span>
            <Num size={10} color={FOS.textDim}>{(acc.cap - acc.balance).toLocaleString('fr-FR')} € disponibles</Num>
          </div>
        </div>
      )}
    </div>
  );
}

Object.assign(window, { PatrimoineScreen });
