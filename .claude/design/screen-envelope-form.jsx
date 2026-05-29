// screen-envelope-form.jsx — Finance OS — Add/Edit envelope full-screen form.
// Modes:
//   'create'  → "Nouvelle enveloppe", no delete, CTA "Créer l'enveloppe"
//   'edit'    → header shows envelope name, destructive "Supprimer" visible, CTA "Enregistrer"

// Envelope types — full set with short description shown on the radio card.
const ENV_TYPES = [
  { id: 'fixe',  label: 'Fixe',          desc: 'Même montant chaque mois — loyer, abonnements.',                 swatch: '#4a5568' },
  { id: 'var',   label: 'Variable',      desc: 'Budget ajustable — courses, restos, transport.',                 swatch: '#e8eef5' },
  { id: 'month', label: 'Du mois',       desc: 'One-shot pour ce mois seulement — voyage, déménagement.',        swatch: '#fb923c' },
  { id: 'perm',  label: 'Permanente',    desc: 'Accumule mois après mois — vacances, cadeaux, projets.',         swatch: '#22c55e' },
  { id: 'save',  label: 'Épargne',       desc: 'Livret réglementé avec plafond — Livret A, LDDS.',               swatch: '#7eb8f7' },
  { id: 'inv',   label: 'Investissement', desc: 'Portefeuille marché — PEA, CTO.',                                swatch: '#a78bfa' },
];

// 16-icon palette from the existing primitives library.
const ENV_ICON_PALETTE = [
  'house', 'car', 'zap', 'shopping-basket',
  'utensils', 'shopping-bag', 'bus', 'plane',
  'heart-pulse', 'dumbbell', 'smartphone', 'shield',
  'umbrella', 'gift', 'piggy-bank', 'trending-up',
];

function EnvelopeFormScreen({ mode = 'create', initial }) {
  // Pre-fill for edit mode — Restos & cafés in the mock
  const defaults = mode === 'edit'
    ? { name: 'Restos & cafés', type: 'var', icon: 'utensils', amount: '180', accumulated: '', cap: '' }
    : { name: '', type: 'var', icon: 'shopping-basket', amount: '', accumulated: '', cap: '22950' };
  const d = { ...defaults, ...(initial || {}) };

  const [name, setName] = React.useState(d.name);
  const [type, setType] = React.useState(d.type);
  const [icon, setIcon] = React.useState(d.icon);
  const [amount, setAmount] = React.useState(d.amount);
  const [accumulated, setAccumulated] = React.useState(d.accumulated || '');
  const [cap, setCap] = React.useState(d.cap || '');

  const title = mode === 'edit' ? d.name : 'Nouvelle enveloppe';
  const subtitle = mode === 'edit' ? 'Modifier l\'enveloppe' : 'Crée une enveloppe de budget';

  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
      <ScreenHeader title={title} subtitle={subtitle} onBack={() => {}} />

      <div style={{ flex: 1, minHeight: 0, overflow: 'auto', paddingBottom: 110 }}>
        <div style={{ padding: '8px 20px 0', display: 'flex', flexDirection: 'column', gap: 22 }}>

          {/* ── 1. Name ─────────────────────────────────────── */}
          <FormField label="Nom de l'enveloppe">
            <TextInput
              value={name}
              onChange={setName}
              placeholder="Ex. Restos, Vacances Japon, Livret A…"
            />
          </FormField>

          {/* ── 2. Type ─────────────────────────────────────── */}
          <FormField label="Type">
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
              {ENV_TYPES.map(t => {
                const active = t.id === type;
                return (
                  <button key={t.id} onClick={() => setType(t.id)} style={{
                    display: 'flex', alignItems: 'flex-start', gap: 12,
                    padding: '12px 14px', borderRadius: 14,
                    background: active ? `${FOS.primary}14` : FOS.surface,
                    border: active ? `1px solid ${FOS.primary}` : `1px solid ${FOS.outline}`,
                    cursor: 'pointer', textAlign: 'left',
                    transition: 'background 160ms ease, border 160ms ease',
                  }}>
                    {/* Color swatch — uses the same hue family as the type's
                        in-app accent so users learn the visual code. */}
                    <div style={{
                      width: 10, height: 10, borderRadius: 100,
                      background: t.swatch, marginTop: 6, flexShrink: 0,
                    }} />
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{
                        fontFamily: 'DM Sans', fontSize: 14, fontWeight: 600,
                        color: active ? FOS.primary : FOS.text,
                      }}>{t.label}</div>
                      <div style={{
                        fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim,
                        marginTop: 2, lineHeight: 1.4,
                      }}>{t.desc}</div>
                    </div>
                    <div style={{
                      width: 20, height: 20, borderRadius: 100,
                      border: active ? `none` : `1.5px solid ${FOS.outlineSt}`,
                      background: active ? FOS.primary : 'transparent',
                      color: FOS.onPrimary, flexShrink: 0, marginTop: 2,
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                    }}>
                      {active && <Icon name="check" size={12} strokeWidth={3} />}
                    </div>
                  </button>
                );
              })}
            </div>
          </FormField>

          {/* ── 3. Icon picker ──────────────────────────────── */}
          <FormField label="Icône">
            <div style={{
              display: 'grid', gridTemplateColumns: 'repeat(8, 1fr)', gap: 8,
            }}>
              {ENV_ICON_PALETTE.map(n => {
                const active = n === icon;
                return (
                  <button key={n} onClick={() => setIcon(n)} style={{
                    aspectRatio: '1 / 1', borderRadius: 12,
                    background: active ? `${FOS.primary}1F` : FOS.surface,
                    border: active ? `1px solid ${FOS.primary}` : `1px solid ${FOS.outline}`,
                    color: active ? FOS.primary : FOS.text,
                    cursor: 'pointer',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    padding: 0,
                  }}>
                    <Icon name={n} size={18} strokeWidth={1.7} />
                  </button>
                );
              })}
            </div>
          </FormField>

          {/* ── 4. Monthly amount ───────────────────────────── */}
          <FormField label={amountLabelFor(type)}>
            <AmountInput value={amount} onChange={setAmount} />
          </FormField>

          {/* ── 5. Permanent: accumulated so far ────────────── */}
          {type === 'perm' && (
            <FormField
              label="Cumul actuel"
              hint="Combien tu as déjà mis de côté pour cette enveloppe (les mois précédents)."
            >
              <AmountInput value={accumulated} onChange={setAccumulated} placeholder="0" />
            </FormField>
          )}

          {/* ── 6. Savings: legal cap ───────────────────────── */}
          {type === 'save' && (
            <FormField
              label="Plafond du livret"
              hint={`Plafond légal Livret A : ${(22950).toLocaleString('fr-FR')} € · LDDS : ${(12000).toLocaleString('fr-FR')} €`}
            >
              <AmountInput value={cap} onChange={setCap} placeholder="22 950" />
            </FormField>
          )}

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
              Supprimer l'enveloppe
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
          {mode === 'edit' ? 'Enregistrer' : "Créer l'enveloppe"}
        </button>
      </div>
    </div>
  );
}

function amountLabelFor(type) {
  if (type === 'fixe')  return 'Montant mensuel';
  if (type === 'var')   return 'Budget mensuel';
  if (type === 'month') return 'Montant à allouer';
  if (type === 'perm')  return 'Contribution mensuelle';
  if (type === 'save')  return 'Versement mensuel';
  if (type === 'inv')   return 'Versement mensuel';
  return 'Montant';
}

// ────────────────────────────────────────────────────────────
// Reusable form primitives
// ────────────────────────────────────────────────────────────
function FormField({ label, hint, children }) {
  return (
    <div>
      <div style={{
        fontFamily: 'DM Sans', fontSize: 11, fontWeight: 600,
        color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 1.2,
        marginBottom: 8, padding: '0 2px',
      }}>{label}</div>
      {children}
      {hint && (
        <div style={{
          marginTop: 8, padding: '0 2px',
          fontFamily: 'DM Sans', fontSize: 11.5, color: FOS.textDim,
          lineHeight: 1.5,
        }}>{hint}</div>
      )}
    </div>
  );
}

function TextInput({ value, onChange, placeholder }) {
  return (
    <input
      value={value}
      onChange={(e) => onChange(e.target.value)}
      placeholder={placeholder}
      style={{
        width: '100%', boxSizing: 'border-box',
        padding: '14px 16px',
        background: FOS.surface, color: FOS.text,
        border: `1px solid ${FOS.outline}`, borderRadius: 14,
        fontFamily: 'DM Sans, sans-serif', fontSize: 15, outline: 'none',
        caretColor: FOS.primary,
      }}
    />
  );
}

function AmountInput({ value, onChange, placeholder = '0' }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'baseline', gap: 8,
      background: FOS.surface, border: `1px solid ${FOS.outline}`,
      borderRadius: 14, padding: '14px 16px',
    }}>
      <input
        value={value} inputMode="numeric"
        onChange={(e) => onChange(e.target.value.replace(/[^0-9]/g, ''))}
        placeholder={placeholder}
        style={{
          flex: 1, background: 'transparent', border: 'none', outline: 'none',
          fontFamily: '"Geist Mono", ui-monospace, monospace',
          fontSize: 22, fontWeight: 500, color: FOS.text, padding: 0,
          letterSpacing: -0.5, caretColor: FOS.primary,
        }}
      />
      <Num size={16} color={FOS.textDim}>€</Num>
    </div>
  );
}

Object.assign(window, { EnvelopeFormScreen, FormField, TextInput, AmountInput });
