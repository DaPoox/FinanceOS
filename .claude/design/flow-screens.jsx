// flow-screens.jsx — Finance OS — helpers for the user-flow diagram.
// Wraps the real screen components in a device frame at any scale, and
// provides a "?" placeholder for screens / states that don't exist yet.

// ────────────────────────────────────────────────────────────
// PhoneFrame — the 412×892 device, scaled. Inner content gets the
// usual flex column + optional bottom nav. pointer-events are off by
// default so the whole node is clickable as one unit; the focus overlay
// turns them on so reviewers can scroll / tap inside.
// ────────────────────────────────────────────────────────────
function PhoneFrame({ scale = 1, tab = null, interactive = false, children }) {
  return (
    <div style={{ width: 412 * scale, height: 892 * scale, position: 'relative' }}>
      <div style={{
        width: 412, height: 892,
        transform: `scale(${scale})`, transformOrigin: 'top left',
        pointerEvents: interactive ? 'auto' : 'none',
      }}>
        <PhoneShell>
          {/* Mount exactly like the live app (ScreenSlide is absolute inset:0)
              so sheets / sticky footers / FABs anchor to the phone bottom
              instead of the bottom of the (tall) scroll content. */}
          <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column' }}>
            {children}
          </div>
          {tab && <BottomNav tab={tab} onChange={() => {}} />}
        </PhoneShell>
      </div>
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// MissingScreen — an honest, in-theme placeholder for a screen or
// state we have NOT designed. Giant "?" so it's impossible to miss in
// the flow. mode:
//   'screen'   → whole surface missing (just the ? + title)
//   'loading'  → faux skeleton with a ? badge ("loading not designed")
//   'error'    → error placeholder with a ? badge
// ────────────────────────────────────────────────────────────
function MissingHeader({ kicker, title }) {
  return (
    <div style={{ padding: '8px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      <div>
        {kicker && <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim }}>{kicker}</div>}
        <div style={{ fontFamily: 'DM Sans', fontSize: 22, fontWeight: 600, color: FOS.text }}>{title}</div>
      </div>
      <div style={{
        width: 40, height: 40, borderRadius: 100,
        background: FOS.surface, border: `1px solid ${FOS.outline}`,
        display: 'flex', alignItems: 'center', justifyContent: 'center', color: FOS.textDim,
        fontFamily: '"Geist Mono", monospace', fontSize: 20, fontWeight: 600,
      }}>?</div>
    </div>
  );
}

function QGlyph({ size = 120 }) {
  return (
    <div style={{
      width: size, height: size, borderRadius: size * 0.28,
      background: `${FOS.err}14`, border: `2px dashed ${FOS.err}66`,
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      color: FOS.err, fontFamily: '"Geist Mono", monospace',
      fontSize: size * 0.55, fontWeight: 700, flexShrink: 0,
    }}>?</div>
  );
}

function SkeletonBar({ w = '100%', h = 14, r = 8, mt = 0 }) {
  return (
    <div style={{
      width: w, height: h, borderRadius: r, marginTop: mt,
      background: 'linear-gradient(90deg, rgba(255,255,255,0.05) 0%, rgba(255,255,255,0.10) 50%, rgba(255,255,255,0.05) 100%)',
      backgroundSize: '200% 100%', animation: 'fos-shimmer 1.4s ease-in-out infinite',
    }} />
  );
}

function MissingScreen({ mode = 'screen', tab = null, kicker, title, caption }) {
  return (
    <>
      {mode === 'screen' ? (
        <div style={{
          flex: 1, display: 'flex', flexDirection: 'column',
          alignItems: 'center', justifyContent: 'center', gap: 22,
          padding: 32, textAlign: 'center', paddingBottom: tab ? 110 : 32,
        }}>
          <QGlyph size={132} />
          <div>
            <div style={{ fontFamily: 'DM Sans', fontSize: 20, fontWeight: 600, color: FOS.text }}>{title}</div>
            <div style={{ marginTop: 8, fontFamily: 'DM Sans', fontSize: 14, color: FOS.textDim, maxWidth: 260, lineHeight: 1.5 }}>{caption}</div>
          </div>
          <div style={{
            fontFamily: '"Geist Mono", monospace', fontSize: 11, letterSpacing: 1,
            color: FOS.err, textTransform: 'uppercase', padding: '6px 12px',
            border: `1px solid ${FOS.err}44`, borderRadius: 100, background: `${FOS.err}10`,
          }}>écran à concevoir</div>
        </div>
      ) : (
        <>
          <MissingHeader kicker={kicker} title={title} />
          {/* faux content, in the right place, with a ? laid over it */}
          <div style={{ flex: 1, position: 'relative', padding: '16px 20px 0', overflow: 'hidden' }}>
            {mode === 'loading' && (
              <div style={{ opacity: 0.5 }}>
                <div style={{ background: FOS.surface, border: `1px solid ${FOS.outline}`, borderRadius: 20, padding: 18 }}>
                  <SkeletonBar w="40%" h={10} />
                  <SkeletonBar w="70%" h={28} mt={14} />
                  <SkeletonBar w="55%" h={12} mt={14} />
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, marginTop: 14 }}>
                  {[0, 1, 2, 3].map(i => (
                    <div key={i} style={{ background: FOS.surface, border: `1px solid ${FOS.outline}`, borderRadius: 16, padding: 14 }}>
                      <SkeletonBar w={32} h={32} r={10} />
                      <SkeletonBar w="80%" h={12} mt={12} />
                      <SkeletonBar w="50%" h={12} mt={8} />
                    </div>
                  ))}
                </div>
              </div>
            )}
            {mode === 'error' && (
              <div style={{ opacity: 0.5 }}>
                <div style={{ background: FOS.surface, border: `1px solid ${FOS.outline}`, borderRadius: 20, padding: 18 }}>
                  <SkeletonBar w="50%" h={14} />
                  <SkeletonBar w="80%" h={24} mt={14} />
                </div>
              </div>
            )}
            {/* overlay ? */}
            <div style={{
              position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column',
              alignItems: 'center', justifyContent: 'center', gap: 18,
              background: `linear-gradient(180deg, ${FOS.bg}00 0%, ${FOS.bg}cc 38%, ${FOS.bg} 100%)`,
              padding: 24, textAlign: 'center',
            }}>
              <QGlyph size={104} />
              <div style={{ fontFamily: 'DM Sans', fontSize: 16, fontWeight: 600, color: FOS.text }}>{title}</div>
              <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim, maxWidth: 240, lineHeight: 1.5 }}>{caption}</div>
              <div style={{
                fontFamily: '"Geist Mono", monospace', fontSize: 11, letterSpacing: 1,
                color: FOS.err, textTransform: 'uppercase', padding: '6px 12px',
                border: `1px solid ${FOS.err}44`, borderRadius: 100, background: `${FOS.err}10`,
              }}>{mode === 'loading' ? 'skeleton à concevoir' : 'état d\'erreur à concevoir'}</div>
            </div>
          </div>
          {tab && null}
        </>
      )}
    </>
  );
}

// ────────────────────────────────────────────────────────────
// Composite renderers for states that aren't a single component call.
// ────────────────────────────────────────────────────────────

// Envelope detail with no transactions this month (uses the body-only
// EmptyEnvelopeTransactions slot under a real header + a FAB).
function EnvelopeDetailEmptyScreen() {
  return (
    <>
      <ScreenHeader title="Vacances" subtitle="Permanente • accumulée" onBack={() => {}} />
      <EmptyEnvelopeTransactions />
      <button style={{
        position: 'absolute', right: 20, bottom: 28,
        width: 60, height: 60, borderRadius: 22,
        background: FOS.primary, color: FOS.onPrimary, border: 'none',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        boxShadow: `0 12px 24px ${FOS.primary}55, 0 0 0 6px ${FOS.bg}`,
      }}>
        <Icon name="plus" size={26} strokeWidth={2.4} />
      </button>
    </>
  );
}

// A background screen with a sheet/dialog overlaid open — for modal nodes.
function WithOverlay({ background, children }) {
  return (
    <>
      {background}
      {children}
    </>
  );
}

Object.assign(window, {
  PhoneFrame, MissingScreen, EnvelopeDetailEmptyScreen, WithOverlay,
});
