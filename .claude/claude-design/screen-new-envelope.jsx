// screen-new-envelope.jsx — Finance OS — Bottom sheet for creating an envelope

function NewEnvelopeSheet({ open, type, onClose, onSave }) {
  const [name, setName]   = React.useState('');
  const [icon, setIcon]   = React.useState('◆');
  const [amount, setAmount] = React.useState('');
  const [goal, setGoal]   = React.useState('');

  React.useEffect(() => {
    if (!open) {
      const t = setTimeout(() => {
        setName(''); setIcon('◆'); setAmount(''); setGoal('');
      }, 250);
      return () => clearTimeout(t);
    }
  }, [open]);

  // Investissement / Épargne route to Patrimoine — different flow
  const isAccountFlow = type === 'inv' || type === 'save';

  const labels = {
    fixe:  { title: 'Nouvelle charge fixe',     placeholder: 'Mutuelle, Téléphone…',   amountLabel: 'Montant mensuel' },
    var:   { title: 'Nouvelle catégorie',       placeholder: 'Loisirs, Pharmacie…',    amountLabel: 'Budget mensuel' },
    month: { title: 'Nouvelle dépense du mois', placeholder: 'Vacances Lisbonne…',     amountLabel: 'Montant à allouer' },
    perm:  { title: 'Nouvel objectif',          placeholder: 'Voyage Japon, Vélo…',    amountLabel: 'Contribution mensuelle' },
  }[type] || { title: 'Nouvelle enveloppe', placeholder: '', amountLabel: 'Montant' };

  const ICONS = ['◆','◇','◢','◣','○','●','◐','◑','◉','◍','⌂','✈','∞','◈','◬','◤','♥','★','☂','☕','✓'];

  const canSave = name.trim() && amount && !isAccountFlow;

  const handleSave = () => {
    if (!canSave) return;
    onSave?.({ type, name: name.trim(), icon, amount: parseInt(amount), goal: parseInt(goal) || null });
    onClose?.();
  };

  return (
    <div style={{
      position: 'absolute', inset: 0,
      pointerEvents: open ? 'auto' : 'none',
      zIndex: 90,
    }}>
      <div onClick={onClose} style={{
        position: 'absolute', inset: 0,
        background: 'rgba(0,0,0,0.55)',
        opacity: open ? 1 : 0,
        transition: 'opacity 240ms ease',
      }} />

      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0,
        background: FOS.surface,
        borderTopLeftRadius: 28, borderTopRightRadius: 28,
        borderTop: `1px solid ${FOS.outlineSt}`,
        transform: open ? 'translateY(0)' : 'translateY(110%)',
        transition: 'transform 420ms cubic-bezier(0.32,0.72,0,1)',
        paddingBottom: 18, maxHeight: '88%', display: 'flex', flexDirection: 'column',
      }}>
        <div style={{ display: 'flex', justifyContent: 'center', padding: '10px 0 4px' }}>
          <div style={{ width: 38, height: 4, borderRadius: 2, background: 'rgba(255,255,255,0.18)' }} />
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '4px 20px 0' }}>
          <div>
            <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 11, color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 1.4, fontWeight: 600 }}>
              Créer
            </div>
            <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 18, fontWeight: 600, color: FOS.text, marginTop: 2 }}>
              {labels.title}
            </div>
          </div>
          <button onClick={onClose} style={{ background: 'transparent', border: 'none', color: FOS.textDim, cursor: 'pointer', padding: 4 }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M6 6l12 12M18 6L6 18" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
            </svg>
          </button>
        </div>

        {isAccountFlow ? (
          <div style={{ padding: '20px 20px 0', overflowY: 'auto' }}>
            <div style={{
              background: FOS.surfaceVar, border: `1px solid ${FOS.outline}`,
              borderRadius: 16, padding: 18,
              display: 'flex', alignItems: 'flex-start', gap: 12,
            }}>
              <div style={{
                width: 36, height: 36, borderRadius: 10,
                background: `${FOS.save}1F`, color: FOS.save,
                display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
              }}>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M3 7a2 2 0 012-2h13a1 1 0 011 1v2M3 7v11a2 2 0 002 2h14a1 1 0 001-1v-3M3 7h17a1 1 0 011 1v3h-5a2 2 0 100 4h5" stroke="currentColor" strokeWidth="1.6"/>
                </svg>
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 14, color: FOS.text, fontWeight: 500 }}>
                  Ouvrir un compte
                </div>
                <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 12, color: FOS.textDim, marginTop: 4, lineHeight: 1.5 }}>
                  Les comptes d'épargne et d'investissement se gèrent depuis Patrimoine. Le versement mensuel apparaîtra ensuite ici.
                </div>
                <button onClick={onClose} style={{
                  marginTop: 12, padding: '10px 14px', borderRadius: 100,
                  background: FOS.primary, color: FOS.onPrimary,
                  border: 'none', cursor: 'pointer',
                  fontFamily: 'DM Sans, sans-serif', fontSize: 13, fontWeight: 600,
                }}>Aller dans Patrimoine →</button>
              </div>
            </div>
          </div>
        ) : (
          <div style={{ padding: '20px 20px 0', overflowY: 'auto' }}>
            {/* Name */}
            <div>
              <label style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 11, color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 1.2, fontWeight: 600 }}>Nom</label>
              <input
                autoFocus
                value={name} onChange={(e) => setName(e.target.value)}
                placeholder={labels.placeholder}
                style={{
                  marginTop: 8, width: '100%', padding: '12px 14px',
                  background: FOS.surfaceVar, color: FOS.text,
                  border: `1px solid ${FOS.outline}`, borderRadius: 14,
                  fontFamily: 'DM Sans, sans-serif', fontSize: 15, outline: 'none', boxSizing: 'border-box',
                }}
              />
            </div>

            {/* Icon picker */}
            <div style={{ marginTop: 16 }}>
              <label style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 11, color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 1.2, fontWeight: 600 }}>Icône</label>
              <div style={{
                marginTop: 8, display: 'grid', gridTemplateColumns: 'repeat(8, 1fr)', gap: 6,
              }}>
                {ICONS.map(g => {
                  const active = g === icon;
                  return (
                    <button key={g} onClick={() => setIcon(g)} style={{
                      aspectRatio: '1 / 1', borderRadius: 10,
                      background: active ? `${FOS.primary}1F` : FOS.surfaceVar,
                      border: active ? `1px solid ${FOS.primary}` : `1px solid ${FOS.outline}`,
                      color: active ? FOS.primary : FOS.text,
                      fontSize: 16, cursor: 'pointer',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                    }}>{g}</button>
                  );
                })}
              </div>
            </div>

            {/* Amount */}
            <div style={{ marginTop: 16 }}>
              <label style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 11, color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 1.2, fontWeight: 600 }}>{labels.amountLabel}</label>
              <div style={{
                marginTop: 8, display: 'flex', alignItems: 'center',
                background: FOS.surfaceVar, border: `1px solid ${FOS.outline}`, borderRadius: 14,
                padding: '12px 14px',
              }}>
                <input
                  value={amount} inputMode="numeric"
                  onChange={(e) => setAmount(e.target.value.replace(/[^0-9]/g, ''))}
                  placeholder="0"
                  style={{
                    flex: 1, background: 'transparent', border: 'none', outline: 'none',
                    fontFamily: '"Geist Mono", ui-monospace, monospace',
                    fontSize: 18, color: FOS.text, padding: 0,
                  }}
                />
                <Num size={15} color={FOS.textDim}>€</Num>
              </div>
            </div>

            {/* Permanente goal (optional) */}
            {type === 'perm' && (
              <div style={{ marginTop: 16 }}>
                <label style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 11, color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 1.2, fontWeight: 600 }}>
                  Objectif total <span style={{ textTransform: 'none', letterSpacing: 0, fontWeight: 500 }}>(optionnel)</span>
                </label>
                <div style={{
                  marginTop: 8, display: 'flex', alignItems: 'center',
                  background: FOS.surfaceVar, border: `1px solid ${FOS.outline}`, borderRadius: 14,
                  padding: '12px 14px',
                }}>
                  <input
                    value={goal} inputMode="numeric"
                    onChange={(e) => setGoal(e.target.value.replace(/[^0-9]/g, ''))}
                    placeholder="Ex. 3 000"
                    style={{
                      flex: 1, background: 'transparent', border: 'none', outline: 'none',
                      fontFamily: '"Geist Mono", ui-monospace, monospace',
                      fontSize: 18, color: FOS.text, padding: 0,
                    }}
                  />
                  <Num size={15} color={FOS.textDim}>€</Num>
                </div>
              </div>
            )}

            {/* Footer */}
            <div style={{ marginTop: 22, display: 'flex', gap: 8 }}>
              <button onClick={onClose} style={{
                flex: 1, padding: '14px', borderRadius: 14,
                background: FOS.surfaceVar, color: FOS.text,
                border: `1px solid ${FOS.outlineSt}`,
                fontFamily: 'DM Sans, sans-serif', fontSize: 14, fontWeight: 600, cursor: 'pointer',
              }}>Annuler</button>
              <button onClick={handleSave} disabled={!canSave} style={{
                flex: 2, padding: '14px', borderRadius: 14,
                background: canSave ? FOS.primary : 'rgba(240,180,41,0.18)',
                color: canSave ? FOS.onPrimary : FOS.textDim,
                border: 'none', cursor: canSave ? 'pointer' : 'not-allowed',
                fontFamily: 'DM Sans, sans-serif', fontSize: 14, fontWeight: 700, letterSpacing: 0.3,
              }}>Créer l'enveloppe</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

Object.assign(window, { NewEnvelopeSheet });
