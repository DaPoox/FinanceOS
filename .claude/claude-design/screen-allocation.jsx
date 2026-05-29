// screen-allocation.jsx — Finance OS — Monthly allocation flow
// Flow: 2 steps. (1) Revenu, (2) Ajuste — pré-rempli depuis le mois précédent.
// Groups are collapsible. Rows have a trash icon (→ confirm modal) AND can be
// swiped left to remove with an undo snackbar.

function AllocationScreen({ onBack, onComplete, onAddEnvelope }) {
  const [step, setStep] = React.useState(0);
  const [income, setIncome] = React.useState('4200');

  // edits: id -> string amount, or null if excluded from this month.
  // "Du mois" enveloppes start empty — they're month-specific by nature
  // (trips, one-offs) and shouldn't carry over from the previous allocation.
  const [edits, setEdits] = React.useState(() =>
    Object.fromEntries(ENVELOPES.map(e => [
      e.id,
      e.type === 'month' ? null : String(e.allocated),
    ]))
  );

  // Confirmation dialog: { id } when asking, null when closed
  const [confirmRemove, setConfirmRemove] = React.useState(null);

  // Undo snackbar
  const [pendingUndo, setPendingUndo] = React.useState(null);
  const undoTimerRef = React.useRef(null);

  const doRemove = (id) => {
    const prevValue = edits[id];
    setEdits(prev => ({ ...prev, [id]: null }));
    if (undoTimerRef.current) clearTimeout(undoTimerRef.current);
    setPendingUndo({ id, prevValue, expiresAt: Date.now() + 4000 });
    undoTimerRef.current = setTimeout(() => setPendingUndo(null), 4000);
  };

  const undoRemove = () => {
    if (!pendingUndo) return;
    setEdits(prev => ({ ...prev, [pendingUndo.id]: pendingUndo.prevValue }));
    if (undoTimerRef.current) clearTimeout(undoTimerRef.current);
    setPendingUndo(null);
  };

  React.useEffect(() => () => { if (undoTimerRef.current) clearTimeout(undoTimerRef.current); }, []);

  const totalAlloc = Object.values(edits).reduce((s, v) => s + (parseInt(v) || 0), 0);
  const remaining  = (parseInt(income) || 0) - totalAlloc;

  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
      <ScreenHeader
        title="Allouer mai 2026"
        onBack={step === 0 ? onBack : () => setStep(step - 1)}
        right={
          <div style={{ display: 'flex', gap: 4 }}>
            {[0,1].map(i => (
              <div key={i} style={{
                width: i === step ? 20 : 6, height: 6, borderRadius: 100,
                background: i <= step ? FOS.primary : FOS.surfaceVar,
                transition: 'width 240ms ease',
              }} />
            ))}
          </div>
        }
      />

      {/* Scroll area — minHeight:0 is required for flex children to scroll */}
      <div style={{ flex: 1, minHeight: 0, overflow: 'auto', paddingBottom: 130 }}>
        {step === 0 && <StepIncome value={income} onChange={setIncome} />}
        {step === 1 && (
          <StepAdjust
            edits={edits}
            setEdits={setEdits}
            onAddEnvelope={onAddEnvelope}
            onRequestRemove={(id) => setConfirmRemove({ id })}
            onSwipeRemove={doRemove}
          />
        )}
      </div>

      {/* Undo snackbar */}
      {step === 1 && pendingUndo && (
        <UndoSnackbar
          key={pendingUndo.id + pendingUndo.expiresAt}
          envelopeName={ENVELOPES.find(e => e.id === pendingUndo.id)?.name || 'Enveloppe'}
          onUndo={undoRemove}
        />
      )}

      {/* Confirm dialog */}
      {confirmRemove && (
        <ConfirmRemoveDialog
          envelope={ENVELOPES.find(e => e.id === confirmRemove.id)}
          onCancel={() => setConfirmRemove(null)}
          onConfirm={() => { doRemove(confirmRemove.id); setConfirmRemove(null); }}
        />
      )}

      {/* Sticky footer */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0,
        background: FOS.surfaceCt,
        borderTop: `1px solid ${FOS.outline}`,
        padding: '14px 20px 18px',
      }}>
        {step === 1 && (
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
            <span style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>Non alloué</span>
            <Num size={15} weight={600} color={remaining < 0 ? FOS.err : remaining < 50 ? FOS.warn : FOS.pos}>
              {remaining >= 0 ? '' : '−'}{Math.abs(remaining).toLocaleString('fr-FR')} €
            </Num>
          </div>
        )}
        <button onClick={() => step < 1 ? setStep(step + 1) : onComplete?.()} style={{
          width: '100%', padding: '16px', borderRadius: 16,
          background: FOS.primary, color: FOS.onPrimary, border: 'none',
          fontFamily: 'DM Sans', fontWeight: 700, fontSize: 15,
          cursor: 'pointer', letterSpacing: 0.3,
        }}>
          {step < 1 ? 'Continuer' : 'Valider l\'allocation'}
        </button>
      </div>
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// Step 1 — Revenu
// ────────────────────────────────────────────────────────────
function StepIncome({ value, onChange }) {
  return (
    <div style={{ padding: '8px 20px 0' }}>
      <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim, marginTop: 8 }}>
        Étape 1 sur 2
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

      <div style={{
        margin: '40px 0 0',
        fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim,
        textAlign: 'center', lineHeight: 1.5,
      }}>
        Ton allocation est pré-remplie depuis <span style={{ color: FOS.text, fontWeight: 500 }}>avril 2026</span>.<br/>
        Tu pourras ajuster ou retirer des enveloppes à l'étape suivante.
      </div>
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// Step 2 — Ajuste (collapsible groups)
// ────────────────────────────────────────────────────────────
function StepAdjust({ edits, setEdits, onAddEnvelope, onRequestRemove, onSwipeRemove }) {
  const groups = [
    { label: 'Fixes',           type: 'fixe' },
    { label: 'Variables',       type: 'var' },
    { label: 'Du mois',         type: 'month' },
    { label: 'Permanentes',     type: 'perm' },
    { label: 'Épargne',         type: 'save' },
    { label: 'Investissement',  type: 'inv' },
  ];

  // Open state per group — all closed by default. "Du mois" gets a hint of being
  // the most likely to change so we open it (typical use case: add a trip).
  const [openMap, setOpenMap] = React.useState({ month: true });
  const setOpen = (type, val) => setOpenMap(m => ({ ...m, [type]: val }));

  const allOpen = groups.every(g => openMap[g.type]);
  const toggleAll = () => {
    const next = !allOpen;
    setOpenMap(Object.fromEntries(groups.map(g => [g.type, next])));
  };

  return (
    <div style={{ padding: '8px 20px 0' }}>
      <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim, marginTop: 8 }}>
        Étape 2 sur 2
      </div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginTop: 4 }}>
        <div style={{ fontFamily: 'DM Sans', fontSize: 22, fontWeight: 600, color: FOS.text, lineHeight: 1.3 }}>
          Ajuste tes enveloppes
        </div>
        <button onClick={toggleAll} style={{
          background: 'transparent', border: 'none', cursor: 'pointer',
          color: FOS.textDim, fontFamily: 'DM Sans', fontSize: 12, fontWeight: 500,
          padding: 0,
        }}>
          {allOpen ? 'Tout replier' : 'Tout déplier'}
        </button>
      </div>
      <div style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim, marginTop: 6 }}>
        Glisse une ligne ou tape <Icon name="trash" size={11} style={{ display: 'inline', verticalAlign: '-2px', margin: '0 2px' }} /> pour retirer une enveloppe de ce mois.
      </div>

      {groups.map(g => {
        const items = ENVELOPES.filter(e => e.type === g.type && edits[e.id] !== null);
        const groupTotal = items.reduce((s, e) => s + (parseInt(edits[e.id]) || 0), 0);
        const isOpen = !!openMap[g.type];

        return (
          <CollapsibleGroup
            key={g.type}
            label={g.label}
            total={groupTotal}
            count={items.length}
            open={isOpen}
            onToggle={() => setOpen(g.type, !isOpen)}
          >
            {items.map(e => (
              <AdjustRow
                key={e.id}
                env={e}
                value={edits[e.id]}
                onChange={(v) => setEdits({ ...edits, [e.id]: v })}
                onRequestRemove={() => onRequestRemove(e.id)}
                onSwipeRemove={() => onSwipeRemove(e.id)}
              />
            ))}
            {onAddEnvelope && (
              <AddEnvelopeRow
                label={addLabelFor(g.type)}
                prominent={g.type === 'month'}
                onClick={() => onAddEnvelope(g.type)}
              />
            )}
          </CollapsibleGroup>
        );
      })}
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// Collapsible group — header always visible, body conditional
// ────────────────────────────────────────────────────────────
function CollapsibleGroup({ label, total, count, open, onToggle, children }) {
  return (
    <div style={{ marginTop: 16 }}>
      <button onClick={onToggle} style={{
        width: '100%', display: 'flex', alignItems: 'center', gap: 10,
        background: 'transparent', border: 'none', cursor: 'pointer',
        padding: '6px 2px', textAlign: 'left',
      }}>
        <Chevron open={open} />
        <span style={{
          fontFamily: 'DM Sans', fontSize: 11, fontWeight: 600,
          letterSpacing: 0.9, textTransform: 'uppercase', color: FOS.textDim,
        }}>{label}</span>
        <span style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim, opacity: 0.6 }}>
          · {count}
        </span>
        <span style={{ flex: 1 }} />
        <Num size={12} color={FOS.textDim}>{total.toLocaleString('fr-FR')} €</Num>
      </button>
      <div style={{
        overflow: 'hidden',
        maxHeight: open ? 9999 : 0,
        opacity: open ? 1 : 0,
        transition: 'max-height 320ms ease, opacity 200ms ease',
      }}>
        <div style={{ marginTop: 8, display: 'flex', flexDirection: 'column', gap: 6, paddingBottom: 4 }}>
          {children}
        </div>
      </div>
    </div>
  );
}

function Chevron({ open }) {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
         style={{ transform: open ? 'rotate(90deg)' : 'rotate(0deg)', transition: 'transform 200ms ease' }}>
      <path d="M9 6l6 6-6 6" stroke={FOS.textDim} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  );
}

// ────────────────────────────────────────────────────────────
// AdjustRow — the row that lives inside the group
// ────────────────────────────────────────────────────────────
function AdjustRow({ env, value, onChange, onRequestRemove, onSwipeRemove }) {
  const accent = env.type === 'save' ? FOS.save : env.type === 'inv' ? FOS.invest : FOS.textDim;
  return (
    <SwipeableRow onRemove={onSwipeRemove}>
      {/* Inner row: NO border-radius — outer wrapper provides the rounded shape */}
      <div style={{
        display: 'flex', alignItems: 'center', gap: 10,
        background: FOS.surface, border: `1px solid ${FOS.outline}`,
        padding: '10px 12px',
      }}>
        <div style={{
          width: 30, height: 30, borderRadius: 8,
          background: FOS.surfaceVar, color: accent,
          display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
        }}><Icon name={iconFor(env)} size={15} /></div>
        <div style={{ flex: 1, minWidth: 0, fontFamily: 'DM Sans', fontSize: 14, color: FOS.text,
                      whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
          {env.name}
        </div>
        <input
          value={value || ''}
          onChange={(ev) => onChange(ev.target.value.replace(/[^0-9]/g, ''))}
          inputMode="numeric"
          style={{
            width: 64, padding: '6px 8px',
            background: FOS.surfaceVar, border: `1px solid ${FOS.outline}`,
            borderRadius: 8, textAlign: 'right',
            fontFamily: '"Geist Mono", monospace', fontSize: 14, color: FOS.text, outline: 'none',
          }}
        />
        <span style={{ fontFamily: '"Geist Mono", monospace', fontSize: 13, color: FOS.textDim }}>€</span>
        <button
          onClick={(e) => { e.stopPropagation(); onRequestRemove(); }}
          aria-label={`Retirer ${env.name}`}
          style={{
            width: 30, height: 30, borderRadius: 8,
            background: 'transparent', border: 'none', cursor: 'pointer',
            color: FOS.textDim, display: 'flex', alignItems: 'center', justifyContent: 'center',
            flexShrink: 0, transition: 'color 160ms ease, background 160ms ease',
          }}
          onMouseEnter={(e) => { e.currentTarget.style.color = FOS.err; e.currentTarget.style.background = `${FOS.err}14`; }}
          onMouseLeave={(e) => { e.currentTarget.style.color = FOS.textDim; e.currentTarget.style.background = 'transparent'; }}
        >
          <Icon name="trash" size={14} />
        </button>
      </div>
    </SwipeableRow>
  );
}

// ────────────────────────────────────────────────────────────
// SwipeableRow — left-swipe reveals a red "Retirer" action
// ────────────────────────────────────────────────────────────
const REVEAL_WIDTH = 96;

function SwipeableRow({ children, onRemove }) {
  const [dx, setDx] = React.useState(0);
  const [open, setOpen] = React.useState(false);
  const [removing, setRemoving] = React.useState(false);
  const startX = React.useRef(null);
  const startY = React.useRef(null);
  const dragging = React.useRef(false);
  const horizontal = React.useRef(false);

  const onPointerDown = (e) => {
    if (e.target.tagName === 'INPUT' || e.target.tagName === 'BUTTON' || e.target.closest('button')) return;
    startX.current = e.clientX;
    startY.current = e.clientY;
    dragging.current = true;
    horizontal.current = false;
    e.currentTarget.setPointerCapture?.(e.pointerId);
  };

  const onPointerMove = (e) => {
    if (!dragging.current || startX.current == null) return;
    const dxRaw = e.clientX - startX.current;
    const dyRaw = e.clientY - startY.current;
    // Lock direction once intent is clear (8px threshold)
    if (!horizontal.current && (Math.abs(dxRaw) > 8 || Math.abs(dyRaw) > 8)) {
      horizontal.current = Math.abs(dxRaw) > Math.abs(dyRaw);
      if (!horizontal.current) { dragging.current = false; return; }
    }
    if (!horizontal.current) return;
    const base = open ? -REVEAL_WIDTH : 0;
    const next = Math.min(0, Math.max(-REVEAL_WIDTH * 1.25, base + dxRaw));
    setDx(next);
  };

  const finishDrag = () => {
    if (!dragging.current) return;
    dragging.current = false;
    startX.current = null;
    if (dx <= -REVEAL_WIDTH * 0.7) {
      setRemoving(true);
      setDx(-REVEAL_WIDTH * 2);
      setTimeout(() => onRemove(), 220);
      return;
    }
    if (dx <= -REVEAL_WIDTH * 0.4) { setOpen(true);  setDx(-REVEAL_WIDTH); }
    else                            { setOpen(false); setDx(0); }
  };

  const handleRemoveClick = () => {
    setRemoving(true);
    setDx(-REVEAL_WIDTH * 2);
    setTimeout(() => onRemove(), 220);
  };

  return (
    <div style={{
      position: 'relative',
      borderRadius: 14, overflow: 'hidden',
      background: FOS.err, // ← red is the wrapper bg, fully clipped by the rounded shape
      maxHeight: removing ? 0 : 200,
      opacity: removing ? 0 : 1,
      marginTop: removing ? -6 : 0,
      transition: 'max-height 220ms ease, opacity 180ms ease, margin-top 220ms ease',
    }}>
      {/* Reveal action (sits on the wrapper's red bg) */}
      <button
        onClick={handleRemoveClick}
        style={{
          position: 'absolute', top: 0, right: 0, bottom: 0,
          width: REVEAL_WIDTH,
          background: 'transparent', border: 'none', cursor: 'pointer',
          display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
          gap: 4, color: '#fff', padding: 0,
        }}
      >
        <Icon name="trash" size={16} color="#fff" />
        <span style={{ fontFamily: 'DM Sans', fontSize: 11, fontWeight: 600, letterSpacing: 0.3 }}>Retirer</span>
      </button>

      {/* Foreground — opaque, rectangular; outer clipper rounds it */}
      <div
        onPointerDown={onPointerDown}
        onPointerMove={onPointerMove}
        onPointerUp={finishDrag}
        onPointerCancel={finishDrag}
        style={{
          position: 'relative',
          transform: `translateX(${dx}px)`,
          transition: dragging.current ? 'none' : 'transform 240ms cubic-bezier(0.22, 1, 0.36, 1)',
          touchAction: 'pan-y',
          userSelect: 'none',
        }}
      >
        {children}
      </div>
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// Undo snackbar — auto-dismisses after 4 s
// ────────────────────────────────────────────────────────────
function UndoSnackbar({ envelopeName, onUndo }) {
  return (
    <div style={{
      position: 'absolute', left: 16, right: 16, bottom: 110,
      background: FOS.surfaceCt,
      border: `1px solid ${FOS.outlineSt}`,
      borderRadius: 14, padding: '12px 14px',
      display: 'flex', alignItems: 'center', gap: 12,
      boxShadow: '0 12px 32px rgba(0,0,0,0.4)',
      animation: 'fos-slide-up 240ms cubic-bezier(0.22, 1, 0.36, 1)',
      zIndex: 5,
    }}>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.text, fontWeight: 500 }}>
          {envelopeName} retirée
        </div>
        <div style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim, marginTop: 2 }}>
          Plus dans l'allocation de ce mois
        </div>
      </div>
      <button onClick={onUndo} style={{
        background: 'transparent', border: 'none', cursor: 'pointer',
        color: FOS.primary, fontFamily: 'DM Sans', fontSize: 13, fontWeight: 700,
        letterSpacing: 0.5, padding: '6px 8px', textTransform: 'uppercase',
      }}>Annuler</button>
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// Confirmation dialog for explicit (trash-tap) removal
// ────────────────────────────────────────────────────────────
function ConfirmRemoveDialog({ envelope, onCancel, onConfirm }) {
  if (!envelope) return null;
  return (
    <div style={{
      position: 'absolute', inset: 0, zIndex: 50,
      background: 'rgba(6,8,13,0.7)', backdropFilter: 'blur(4px)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      padding: 24, animation: 'fos-fade-in 180ms ease',
    }}
    onClick={onCancel}>
      <div onClick={(e) => e.stopPropagation()} style={{
        width: '100%', background: FOS.surfaceCt,
        border: `1px solid ${FOS.outlineSt}`, borderRadius: 20,
        padding: 22, animation: 'fos-pop 220ms cubic-bezier(0.22, 1, 0.36, 1)',
      }}>
        <div style={{
          width: 44, height: 44, borderRadius: 12,
          background: `${FOS.err}1F`, color: FOS.err,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          marginBottom: 14,
        }}>
          <Icon name="trash" size={20} />
        </div>
        <div style={{
          fontFamily: 'DM Sans', fontSize: 17, fontWeight: 600,
          color: FOS.text, lineHeight: 1.3,
        }}>
          Retirer « {envelope.name} » de ce mois ?
        </div>
        <div style={{
          fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim,
          marginTop: 8, lineHeight: 1.5,
        }}>
          L'enveloppe ne sera pas allouée ce mois-ci. Elle reste disponible pour les mois suivants.
        </div>
        <div style={{ display: 'flex', gap: 8, marginTop: 18 }}>
          <button onClick={onCancel} style={{
            flex: 1, padding: '12px', borderRadius: 12,
            background: FOS.surfaceVar, color: FOS.text,
            border: `1px solid ${FOS.outline}`,
            fontFamily: 'DM Sans', fontSize: 14, fontWeight: 600, cursor: 'pointer',
          }}>Annuler</button>
          <button onClick={onConfirm} style={{
            flex: 1, padding: '12px', borderRadius: 12,
            background: FOS.err, color: '#fff', border: 'none',
            fontFamily: 'DM Sans', fontSize: 14, fontWeight: 700, cursor: 'pointer',
          }}>Retirer</button>
        </div>
      </div>
    </div>
  );
}

Object.assign(window, { AllocationScreen });
