// screen-expense.jsx — Finance OS — Bottom sheet for expense entry

function ExpenseSheet({ open, onClose, onSave }) {
  const [amount, setAmount] = React.useState('');
  const [envelope, setEnvelope] = React.useState('groceries');
  const [note, setNote] = React.useState('');
  const [confirmed, setConfirmed] = React.useState(false);

  // when closed, reset after fade out
  React.useEffect(() => {
    if (!open) {
      const t = setTimeout(() => { setAmount(''); setNote(''); setConfirmed(false); }, 250);
      return () => clearTimeout(t);
    }
  }, [open]);

  const chips = [
    'groceries', 'restos', 'transport', 'shopping', 'ams', 'vacances',
  ].map(id => ENVELOPES.find(e => e.id === id)).filter(Boolean);

  const push = (k) => setAmount(prev => {
    if (k === '.') return prev.includes(',') || prev.includes('.') ? prev : (prev || '0') + ',';
    if (prev === '0' && k !== ',') return k;
    return prev + k;
  });
  const back = () => setAmount(prev => prev.slice(0, -1));
  const clear = () => setAmount('');

  const handleValidate = () => {
    if (!amount) return;
    setConfirmed(true);
    setTimeout(() => { onSave?.({ amount, envelope, note }); onClose?.(); }, 700);
  };

  return (
    <>
      {/* scrim */}
      <div onClick={onClose} style={{
        position: 'absolute', inset: 0,
        background: 'rgba(0,0,0,0.55)',
        opacity: open ? 1 : 0, pointerEvents: open ? 'auto' : 'none',
        transition: 'opacity 240ms ease',
        zIndex: 90,
      }} />

      {/* sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0,
        background: FOS.surface,
        borderTopLeftRadius: 28, borderTopRightRadius: 28,
        borderTop: `1px solid ${FOS.outlineSt}`,
        transform: open ? 'translateY(0)' : 'translateY(110%)',
        transition: 'transform 420ms cubic-bezier(0.32,0.72,0,1)',
        zIndex: 100,
        paddingBottom: 12,
      }}>
        {/* grabber */}
        <div style={{ display: 'flex', justifyContent: 'center', padding: '10px 0 4px' }}>
          <div style={{ width: 38, height: 4, borderRadius: 2, background: 'rgba(255,255,255,0.18)' }} />
        </div>

        {/* header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '4px 20px 0' }}>
          <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim }}>Nouvelle dépense</div>
          <button onClick={onClose} style={{
            background: 'transparent', border: 'none', color: FOS.textDim,
            cursor: 'pointer', padding: 4,
          }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M6 6l12 12M18 6L6 18" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
            </svg>
          </button>
        </div>

        {/* amount display */}
        <div style={{ padding: '12px 20px 0', textAlign: 'center', minHeight: 88 }}>
          {confirmed ? (
            <Confirmation amount={amount} envelope={ENVELOPES.find(e => e.id === envelope)} />
          ) : (
            <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'center', gap: 6 }}>
              <Num size={56} weight={500} style={{ letterSpacing: -2 }}
                   color={amount ? FOS.text : FOS.textDim}>
                {amount || '0'}
              </Num>
              <Num size={28} color={FOS.textDim}>€</Num>
            </div>
          )}
        </div>

        {!confirmed && (
          <>
            {/* envelope chips */}
            <div style={{
              display: 'flex', gap: 8, overflowX: 'auto',
              padding: '14px 20px 0', scrollbarWidth: 'none',
            }}>
              {chips.map(c => {
                const active = c.id === envelope;
                return (
                  <button key={c.id} onClick={() => setEnvelope(c.id)} style={{
                    flexShrink: 0, padding: '10px 14px', borderRadius: 100,
                    border: active ? `1px solid ${FOS.primary}` : `1px solid ${FOS.outlineSt}`,
                    background: active ? `${FOS.primary}1F` : FOS.surfaceVar,
                    color: active ? FOS.primary : FOS.text,
                    fontFamily: 'DM Sans', fontSize: 13, fontWeight: 500,
                    cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: 6,
                  }}>
                    <span style={{ fontSize: 14 }}>{c.icon}</span>
                    {c.name}
                  </button>
                );
              })}
            </div>

            {/* note */}
            <div style={{ padding: '14px 20px 0' }}>
              <input
                value={note} onChange={(e) => setNote(e.target.value)}
                placeholder="Note (optionnelle)"
                style={{
                  width: '100%', padding: '12px 14px',
                  background: FOS.surfaceVar, color: FOS.text,
                  border: `1px solid ${FOS.outline}`, borderRadius: 14,
                  fontFamily: 'DM Sans', fontSize: 14, outline: 'none',
                  boxSizing: 'border-box',
                }}
              />
            </div>

            {/* numpad */}
            <div style={{
              padding: '14px 20px 0',
              display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 1fr',
              gap: 8,
            }}>
              {['1','2','3'].map(k => <Key key={k} k={k} onPress={push} />)}
              <Key k="⌫" onPress={back} accent="dim" />
              {['4','5','6'].map(k => <Key key={k} k={k} onPress={push} />)}
              <KeyValidate disabled={!amount} onPress={handleValidate} />
              {['7','8','9'].map(k => <Key key={k} k={k} onPress={push} />)}
              <div style={{ gridRow: 'span 1' }} />
              <Key k="," onPress={() => push(',')} accent="dim" />
              <Key k="0" onPress={push} />
              <Key k="00" onPress={(v) => { push('0'); push('0'); }} accent="dim" />
              <Key k="C" onPress={clear} accent="dim" />
            </div>
          </>
        )}
      </div>
    </>
  );
}

function Key({ k, onPress, accent }) {
  const [pressed, setPressed] = React.useState(false);
  return (
    <button
      onClick={() => onPress(k)}
      onMouseDown={() => setPressed(true)}
      onMouseUp={() => setPressed(false)}
      onMouseLeave={() => setPressed(false)}
      style={{
        height: 52, borderRadius: 16,
        background: pressed ? FOS.surfaceCt : FOS.surfaceVar,
        border: `1px solid ${FOS.outline}`,
        color: accent === 'dim' ? FOS.textDim : FOS.text,
        fontFamily: '"Geist Mono", monospace',
        fontSize: 22, fontWeight: 500, cursor: 'pointer',
        transition: 'background 80ms ease, transform 80ms ease',
        transform: pressed ? 'scale(0.97)' : 'scale(1)',
      }}>
      {k}
    </button>
  );
}

function KeyValidate({ disabled, onPress }) {
  return (
    <button onClick={onPress} disabled={disabled} style={{
      gridRow: 'span 4',
      borderRadius: 20,
      background: disabled ? 'rgba(240,180,41,0.18)' : FOS.primary,
      color: disabled ? FOS.textDim : FOS.onPrimary,
      border: 'none', cursor: disabled ? 'not-allowed' : 'pointer',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      fontFamily: 'DM Sans', fontWeight: 700, fontSize: 14,
      letterSpacing: 0.4,
      transition: 'background 200ms ease, transform 120ms ease',
    }}>
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
        <path d="M5 12l5 5L19 7" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    </button>
  );
}

function Confirmation({ amount, envelope }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 10, animation: 'fos-pop 380ms cubic-bezier(0.34,1.56,0.64,1)' }}>
      <style>{`@keyframes fos-pop { 0% { transform: scale(0.4); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }`}</style>
      <div style={{
        width: 64, height: 64, borderRadius: 100,
        background: `${FOS.pos}1F`, color: FOS.pos,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
          <path d="M5 12l5 5L19 7" stroke="currentColor" strokeWidth="2.6" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
      </div>
      <div style={{ fontFamily: 'DM Sans', fontSize: 14, color: FOS.text }}>
        <Num size={14} color={FOS.text} weight={600}>{amount} €</Num> sur {envelope?.name}
      </div>
    </div>
  );
}

Object.assign(window, { ExpenseSheet });
