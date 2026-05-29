// screen-account-form.jsx — Finance OS — Add/Edit account full-screen form.
// Manual balance tracking (no bank connection). Mirrors AccountRow visuals.

// Curated palette — picks from the existing chart/account hues so the
// avatar always feels at home next to the donut.
const ACCOUNT_COLORS = [
  '#e8eef5', // neutral (Boursorama-style)
  '#7eb8f7', // savings blue
  '#a78bfa', // investment purple
  '#22c55e', // green
  '#f0b429', // primary gold
  '#fb923c', // orange
  '#f87171', // red
  '#5eead4', // teal
];

const ACCOUNT_TYPES = [
  { id: 'Compte courant',    short: 'Courant',      hint: 'Compte du quotidien — Boursorama, BNP…' },
  { id: 'Épargne',           short: 'Épargne',      hint: 'Livret réglementé — Livret A, LDDS…' },
  { id: 'Investissement',    short: 'Invest.',      hint: 'Portefeuille marché — PEA, CTO…' },
];

function AccountFormScreen({ mode = 'create', initial }) {
  const defaults = mode === 'edit'
    ? { name: 'Livret A', type: 'Épargne', balance: '18400', cap: '22950', color: '#7eb8f7' }
    : { name: '', type: 'Compte courant', balance: '', cap: '', color: '#e8eef5' };
  const d = { ...defaults, ...(initial || {}) };

  const [name, setName] = React.useState(d.name);
  const [type, setType] = React.useState(d.type);
  const [balance, setBalance] = React.useState(d.balance);
  const [cap, setCap] = React.useState(d.cap);
  const [color, setColor] = React.useState(d.color);

  const showCap = type === 'Épargne';

  // Avatar initials — first 2 letters of the name, uppercase.
  const initials = name.trim()
    ? name.trim().replace(/[^A-Za-zÀ-ÿ0-9 ]/g, '').slice(0, 2).toUpperCase()
    : '··';

  // Default cap hint based on common French savings products
  const capHint = name.toLowerCase().includes('ldds')
    ? `Plafond légal LDDS : ${(12000).toLocaleString('fr-FR')} €`
    : `Plafond légal Livret A : ${(22950).toLocaleString('fr-FR')} €`;

  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
      <ScreenHeader
        title={mode === 'edit' ? (d.name || 'Compte') : 'Nouveau compte'}
        subtitle={mode === 'edit' ? 'Modifier le compte' : 'Suivre un compte manuellement'}
        onBack={() => {}}
      />

      <div style={{ flex: 1, minHeight: 0, overflow: 'auto', paddingBottom: 110 }}>
        <div style={{ padding: '8px 20px 0', display: 'flex', flexDirection: 'column', gap: 22 }}>

          {/* ── Live avatar preview ─────────────────────────── */}
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            padding: '4px 0 0',
          }}>
            <AvatarPreview initials={initials} color={color} typeLabel={type} />
          </div>

          {/* ── 1. Name ─────────────────────────────────────── */}
          <FormField label="Nom du compte">
            <TextInput
              value={name}
              onChange={setName}
              placeholder={mode === 'edit' ? '' : 'Ex. Livret A, Boursorama, PEA…'}
            />
          </FormField>

          {/* ── 2. Type segmented (3 options) ───────────────── */}
          <FormField label="Type" hint={ACCOUNT_TYPES.find(t => t.id === type)?.hint}>
            <div style={{
              display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 4,
              background: FOS.surface, border: `1px solid ${FOS.outline}`,
              borderRadius: 12, padding: 4,
            }}>
              {ACCOUNT_TYPES.map(t => {
                const active = t.id === type;
                return (
                  <button key={t.id} onClick={() => setType(t.id)} style={{
                    padding: '10px 8px', borderRadius: 8,
                    background: active ? FOS.surfaceCt : 'transparent',
                    border: 'none', cursor: 'pointer',
                    fontFamily: 'DM Sans, sans-serif', fontSize: 13,
                    fontWeight: active ? 600 : 500,
                    color: active ? FOS.text : FOS.textDim,
                    transition: 'background 160ms ease, color 160ms ease',
                  }}>{t.short}</button>
                );
              })}
            </div>
          </FormField>

          {/* ── 3. Balance — bigger, central numeric ────────── */}
          <FormField label="Solde actuel">
            <div style={{
              background: FOS.surface, border: `1px solid ${FOS.outline}`,
              borderRadius: 14, padding: '18px 18px 16px',
            }}>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: 8 }}>
                <input
                  value={balance} inputMode="numeric"
                  onChange={(e) => setBalance(e.target.value.replace(/[^0-9]/g, ''))}
                  placeholder="0"
                  style={{
                    flex: 1, background: 'transparent', border: 'none', outline: 'none',
                    fontFamily: '"Geist Mono", ui-monospace, monospace',
                    fontSize: 32, fontWeight: 500, color: FOS.text, padding: 0,
                    letterSpacing: -1, caretColor: FOS.primary,
                  }}
                />
                <Num size={20} color={FOS.textDim}>€</Num>
              </div>
            </div>
          </FormField>

          {/* ── 4. Cap (conditional, Épargne only) ──────────── */}
          {showCap && (
            <FormField label="Plafond du livret" hint={capHint}>
              <AmountInput value={cap} onChange={setCap} placeholder="22 950" />
              {balance && cap && parseInt(cap) > 0 && (
                <div style={{ marginTop: 12, padding: '0 2px' }}>
                  <Progress
                    value={parseInt(balance) || 0}
                    max={parseInt(cap)}
                    color={color}
                    height={5}
                    animate={false}
                  />
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8 }}>
                    <span style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim }}>
                      {Math.round(((parseInt(balance) || 0) / parseInt(cap)) * 100)}% de capacité
                    </span>
                    <Num size={11} color={FOS.textDim}>
                      {(parseInt(cap) - (parseInt(balance) || 0)).toLocaleString('fr-FR')} € disponibles
                    </Num>
                  </div>
                </div>
              )}
            </FormField>
          )}

          {/* ── 5. Color picker ─────────────────────────────── */}
          <FormField label="Couleur de l'avatar">
            <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
              {ACCOUNT_COLORS.map(c => {
                const active = c === color;
                return (
                  <button key={c} onClick={() => setColor(c)} style={{
                    width: 38, height: 38, borderRadius: 100,
                    background: c, padding: 0, cursor: 'pointer',
                    border: active ? `2px solid ${FOS.text}` : `2px solid transparent`,
                    boxShadow: active ? `0 0 0 4px ${FOS.bg}, 0 0 0 5px ${c}55` : 'none',
                    transition: 'box-shadow 160ms ease, border 160ms ease',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                  }}>
                    {active && (
                      <Icon name="check" size={14} strokeWidth={3} color={FOS.onPrimary} />
                    )}
                  </button>
                );
              })}
            </div>
          </FormField>

          {/* ── Edit-only: destructive ──────────────────────── */}
          {mode === 'edit' && (
            <button style={{
              alignSelf: 'flex-start',
              display: 'inline-flex', alignItems: 'center', gap: 8,
              padding: '12px 16px', borderRadius: 14,
              background: 'transparent', color: FOS.err,
              border: `1px solid ${FOS.err}33`, cursor: 'pointer',
              fontFamily: 'DM Sans', fontSize: 13, fontWeight: 600,
            }}>
              <Icon name="trash" size={14} />
              Supprimer le compte
            </button>
          )}
        </div>
      </div>

      {/* Sticky CTA */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0,
        background: FOS.surfaceCt,
        borderTop: `1px solid ${FOS.outline}`,
        padding: '14px 20px 18px',
      }}>
        <button style={{
          width: '100%', padding: '16px', borderRadius: 16,
          background: FOS.primary, color: FOS.onPrimary, border: 'none',
          fontFamily: 'DM Sans', fontWeight: 700, fontSize: 15,
          cursor: 'pointer', letterSpacing: 0.3,
        }}>
          {mode === 'edit' ? 'Enregistrer' : 'Ajouter le compte'}
        </button>
      </div>
    </div>
  );
}

function AvatarPreview({ initials, color, typeLabel }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8 }}>
      <div style={{
        width: 84, height: 84, borderRadius: 24,
        background: `${color}1F`, color: color,
        border: `1px solid ${color}33`,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        fontFamily: '"Geist Mono", ui-monospace, monospace',
        fontSize: 30, fontWeight: 600, letterSpacing: -0.5,
        transition: 'background 200ms ease, color 200ms ease, border 200ms ease',
      }}>{initials}</div>
      <div style={{
        fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim,
        textTransform: 'uppercase', letterSpacing: 1.2, fontWeight: 600,
      }}>{typeLabel}</div>
    </div>
  );
}

Object.assign(window, { AccountFormScreen });
