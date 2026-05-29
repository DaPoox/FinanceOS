// screen-envelope.jsx — Finance OS — Envelope detail

function EnvelopeDetailScreen({ envId, onBack }) {
  const env = ENVELOPES.find(e => e.id === envId);
  if (!env) return null;
  const txs = TRANSACTIONS[envId] || [];
  const over = env.spent > env.allocated;
  const pct = env.spent / env.allocated;
  const warn = !over && pct > 0.85;
  const status = over ? 'Dépassé' : warn ? 'Attention' : 'OK';
  const statusColor = over ? FOS.err : warn ? FOS.warn : FOS.pos;
  const isPerm = env.type === 'perm';

  return (
    <div style={{ flex: 1, overflow: 'auto', paddingBottom: 100 }}>
      <ScreenHeader title={env.name} onBack={onBack} subtitle={typeLabel(env.type)} right={<EnvelopeMenu env={env} />} />

      {/* Hero status card */}
      <div style={{ padding: '8px 20px 0' }}>
        <Card style={{ padding: 20 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <SectionLabel style={{ padding: 0 }}>
                {isPerm ? 'Accumulé' : 'Dépensé'}
              </SectionLabel>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: 6, marginTop: 8 }}>
                <Num size={36} weight={500} color={over ? FOS.err : FOS.text}>
                  {isPerm ? env.accumulated.toLocaleString('fr-FR') : env.spent.toLocaleString('fr-FR')}
                </Num>
                <Num size={18} color={FOS.textDim}>€</Num>
              </div>
              <Num size={13} color={FOS.textDim} style={{ marginTop: 4, display: 'inline-block' }}>
                {isPerm ? `+${env.allocated} €/mois` : `sur ${env.allocated} € alloués`}
              </Num>
            </div>
            <Pill color={statusColor} bg={`${statusColor}1F`}>{status}</Pill>
          </div>

          {!isPerm && (
            <div style={{ marginTop: 18 }}>
              <Progress value={env.spent} max={env.allocated} color={statusColor} height={10}/>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8 }}>
                <span style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim }}>
                  {Math.round(pct * 100)}% utilisé
                </span>
                <Num size={11} color={over ? FOS.err : FOS.textDim}>
                  {over ? `+${(env.spent - env.allocated)} € de dépassement` : `${(env.allocated - env.spent)} € restants`}
                </Num>
              </div>
            </div>
          )}

          <button style={{
            marginTop: 18, width: '100%', padding: '12px 14px',
            background: FOS.surfaceVar, color: FOS.text,
            border: `1px solid ${FOS.outlineSt}`, borderRadius: 14,
            fontFamily: 'DM Sans', fontWeight: 600, fontSize: 13, cursor: 'pointer',
            display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 8,
          }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
              <path d="M4 21h4l11-11-4-4L4 17v4z" stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round"/>
            </svg>
            Modifier l'allocation
          </button>
        </Card>
      </div>

      {/* Transactions */}
      <div style={{ padding: '22px 20px 0' }}>
        <SectionLabel right={<Num size={11} color={FOS.textDim}>{txs.length} transactions</Num>}>
          Ce mois
        </SectionLabel>
        <div style={{ marginTop: 10, background: FOS.surface, border: `1px solid ${FOS.outline}`, borderRadius: 18, overflow: 'hidden' }}>
          {txs.map((t, i) => (
            <div key={i} style={{
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
              padding: '14px 16px',
              borderBottom: i < txs.length - 1 ? `1px solid ${FOS.outline}` : 'none',
            }}>
              <div>
                <div style={{ fontFamily: 'DM Sans', fontSize: 14, color: FOS.text }}>{t.note}</div>
                <div style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim, marginTop: 2 }}>{t.date}</div>
              </div>
              <Num size={15} weight={500}>−{t.amount} €</Num>
            </div>
          ))}
          {!txs.length && (
            <div style={{ padding: '24px 16px', textAlign: 'center', color: FOS.textDim, fontFamily: 'DM Sans', fontSize: 13 }}>
              Pas encore de dépense ce mois
            </div>
          )}
        </div>
      </div>

      {/* Hint for permanent envelopes */}
      {isPerm && (
        <div style={{ padding: '22px 20px 0' }}>
          <SectionLabel>Historique mensuel</SectionLabel>
          <Card style={{ marginTop: 8 }}>
            <Sparkline data={[40, 60, 80, 80, 80, 80, 80, 80]} width={340} height={70}
                       stroke={FOS.primary} fillFrom={`${FOS.primary}26`} fillTo={`${FOS.primary}00`}/>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8 }}>
              <span style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim }}>Démarré il y a 8 mois</span>
              <Num size={11} color={FOS.pos}>+{env.accumulated.toLocaleString('fr-FR')} €</Num>
            </div>
          </Card>
        </div>
      )}
    </div>
  );
}

function EnvelopeMenu({ env }) {
  const [open, setOpen] = React.useState(false);
  const [confirming, setConfirming] = React.useState(false);

  return (
    <>
      <div style={{ position: 'relative' }}>
        <button onClick={() => setOpen(!open)} style={{
          width: 40, height: 40, borderRadius: 100,
          background: FOS.surface, border: `1px solid ${FOS.outline}`,
          color: FOS.text, cursor: 'pointer', display: 'flex',
          alignItems: 'center', justifyContent: 'center', padding: 0,
        }}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="5" r="1.6" fill="currentColor"/>
            <circle cx="12" cy="12" r="1.6" fill="currentColor"/>
            <circle cx="12" cy="19" r="1.6" fill="currentColor"/>
          </svg>
        </button>
        {open && (
          <>
            <div onClick={() => setOpen(false)} style={{ position: 'fixed', inset: 0, zIndex: 100 }} />
            <div style={{
              position: 'absolute', top: 48, right: 0,
              background: FOS.surfaceCt, border: `1px solid ${FOS.outlineSt}`,
              borderRadius: 14, padding: 4, minWidth: 210,
              boxShadow: '0 16px 40px rgba(0,0,0,0.5)',
              zIndex: 101,
            }}>
              <MenuItem
                label="Renommer"
                icon={
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <path d="M4 21h4l11-11-4-4L4 17v4z" stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round"/>
                  </svg>
                }
                onClick={() => setOpen(false)}
              />
              <MenuItem
                label="Archiver l'enveloppe"
                color={FOS.err}
                icon={
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <rect x="3" y="5" width="18" height="4" rx="1" stroke="currentColor" strokeWidth="1.8"/>
                    <path d="M5 9v10a1 1 0 001 1h12a1 1 0 001-1V9M10 13h4" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
                  </svg>
                }
                onClick={() => { setOpen(false); setConfirming(true); }}
              />
            </div>
          </>
        )}
      </div>
      {confirming && <ArchiveConfirm env={env} onCancel={() => setConfirming(false)} onConfirm={() => setConfirming(false)} />}
    </>
  );
}

function MenuItem({ label, icon, color, onClick }) {
  const [hover, setHover] = React.useState(false);
  return (
    <button
      onClick={onClick}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      style={{
        width: '100%', padding: '10px 12px', borderRadius: 10,
        background: hover ? 'rgba(255,255,255,0.04)' : 'transparent',
        border: 'none', cursor: 'pointer',
        display: 'flex', alignItems: 'center', gap: 10,
        color: color || FOS.text,
        fontFamily: 'DM Sans, sans-serif', fontSize: 13, fontWeight: 500,
        textAlign: 'left',
      }}>
      {icon}
      <span>{label}</span>
    </button>
  );
}

function ArchiveConfirm({ env, onCancel, onConfirm }) {
  return (
    <>
      <div onClick={onCancel} style={{
        position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.65)',
        zIndex: 200, animation: 'fos-fade-in 200ms ease',
      }} />
      <div style={{
        position: 'absolute', left: 20, right: 20, top: '32%',
        background: FOS.surface, border: `1px solid ${FOS.outlineSt}`,
        borderRadius: 20, padding: 20, zIndex: 201,
        boxShadow: '0 24px 60px rgba(0,0,0,0.6)',
        animation: 'fos-pop 280ms cubic-bezier(0.34,1.56,0.64,1)',
      }}>
        <div style={{
          width: 44, height: 44, borderRadius: 12,
          background: `${FOS.err}1F`, color: FOS.err,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <rect x="3" y="5" width="18" height="4" rx="1" stroke="currentColor" strokeWidth="1.8"/>
            <path d="M5 9v10a1 1 0 001 1h12a1 1 0 001-1V9M10 13h4" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
          </svg>
        </div>
        <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 17, fontWeight: 600, color: FOS.text, marginTop: 14 }}>
          Archiver « {env.name} » ?
        </div>
        <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 13, color: FOS.textDim, marginTop: 6, lineHeight: 1.5 }}>
          L'enveloppe n'apparaîtra plus à partir du mois prochain. L'historique des transactions reste consultable depuis Historique.
        </div>
        <div style={{ display: 'flex', gap: 8, marginTop: 18 }}>
          <button onClick={onCancel} style={{
            flex: 1, padding: '12px', borderRadius: 12,
            background: FOS.surfaceVar, color: FOS.text,
            border: `1px solid ${FOS.outlineSt}`,
            fontFamily: 'DM Sans, sans-serif', fontSize: 14, fontWeight: 600, cursor: 'pointer',
          }}>Annuler</button>
          <button onClick={onConfirm} style={{
            flex: 1, padding: '12px', borderRadius: 12,
            background: FOS.err, color: '#fff',
            border: 'none', cursor: 'pointer',
            fontFamily: 'DM Sans, sans-serif', fontSize: 14, fontWeight: 700,
          }}>Archiver</button>
        </div>
      </div>
    </>
  );
}

function typeLabel(t) {
  return { fixe: 'Enveloppe fixe', var: 'Variable standard', month: 'Enveloppe du mois',
           perm: 'Permanente • accumulée', save: 'Épargne', inv: 'Investissement' }[t] || '';
}

Object.assign(window, { EnvelopeDetailScreen, EnvelopeMenu });
