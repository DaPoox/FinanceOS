// phone.jsx — Dark Android-style device shell for Finance OS

function FOSStatusBar() {
  return (
    <div style={{
      height: 36, padding: '0 22px', display: 'flex',
      alignItems: 'center', justifyContent: 'space-between',
      color: FOS.text, fontFamily: 'DM Sans, sans-serif', fontSize: 13, fontWeight: 500,
      position: 'relative', flexShrink: 0,
    }}>
      <div>9:41</div>
      <div style={{
        position: 'absolute', left: '50%', top: 8, transform: 'translateX(-50%)',
        width: 22, height: 22, borderRadius: 100, background: '#000',
      }} />
      <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
        {/* signal */}
        <svg width="14" height="11" viewBox="0 0 14 11"><path d="M0 8h2v3H0zM4 5h2v6H4zM8 2h2v9H8zM12 0h2v11h-2z" fill={FOS.text}/></svg>
        {/* wifi */}
        <svg width="14" height="10" viewBox="0 0 14 10"><path d="M7 0a12 12 0 017 2.3l-1.3 1.6A10 10 0 007 2 10 10 0 001.3 3.9L0 2.3A12 12 0 017 0zm0 3.5a8 8 0 014.7 1.6L10.4 6.7A6 6 0 007 5.5 6 6 0 003.6 6.7L2.3 5.1A8 8 0 017 3.5zm0 3.5a4 4 0 012.5.9L7 10 4.5 7.9A4 4 0 017 7z" fill={FOS.text}/></svg>
        {/* battery */}
        <div style={{
          width: 22, height: 11, border: `1px solid ${FOS.text}`, borderRadius: 3,
          position: 'relative', padding: 1, boxSizing: 'border-box',
        }}>
          <div style={{ width: '78%', height: '100%', background: FOS.text, borderRadius: 1 }} />
          <div style={{
            position: 'absolute', right: -3, top: 3, width: 2, height: 5,
            background: FOS.text, borderRadius: 1,
          }} />
        </div>
      </div>
    </div>
  );
}

function FOSGestureBar() {
  return (
    <div style={{ height: 22, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
      <div style={{ width: 128, height: 4, borderRadius: 2, background: 'rgba(255,255,255,0.4)' }} />
    </div>
  );
}

// Phone shell — provides status bar + content area + gesture bar.
// Children get the full content viewport; children manage their own scroll.
function PhoneShell({ children, width = 412, height = 892 }) {
  return (
    <div style={{
      width, height,
      borderRadius: 44, padding: 8, boxSizing: 'border-box',
      background: 'linear-gradient(180deg, #1a1f2c 0%, #0a0d14 100%)',
      boxShadow: '0 40px 80px rgba(0,0,0,0.55), 0 0 0 1px rgba(255,255,255,0.06) inset',
    }}>
      <div style={{
        width: '100%', height: '100%',
        background: FOS.bg, borderRadius: 36, overflow: 'hidden',
        display: 'flex', flexDirection: 'column',
        position: 'relative',
      }}>
        <FOSStatusBar />
        <div style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>
          {children}
        </div>
        <FOSGestureBar />
      </div>
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// Bottom navigation (4 pillars)
// ────────────────────────────────────────────────────────────
function BottomNav({ tab, onChange }) {
  const items = [
    { id: 'home',       label: 'Accueil',     icon: <NavIconHome /> },
    { id: 'budget',     label: 'Budget',      icon: <NavIconBudget /> },
    { id: 'patrimoine', label: 'Patrimoine',  icon: <NavIconWallet /> },
    { id: 'history',    label: 'Historique',  icon: <NavIconHistory /> },
  ];
  return (
    <div style={{
      position: 'absolute', left: 0, right: 0, bottom: 0,
      background: FOS.surfaceCt,
      borderTop: `1px solid ${FOS.outline}`,
      padding: '8px 12px 12px',
      display: 'flex', justifyContent: 'space-around',
    }}>
      {items.map(it => {
        const active = tab === it.id;
        return (
          <button key={it.id} onClick={() => onChange(it.id)} style={{
            border: 'none', background: 'transparent', cursor: 'pointer',
            display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4,
            flex: 1, padding: '6px 0',
            color: active ? FOS.primary : FOS.textDim,
          }}>
            <div style={{
              width: 56, height: 26, borderRadius: 100,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              background: active ? `${FOS.primary}1F` : 'transparent',
              transition: 'background 220ms ease',
            }}>{it.icon}</div>
            <div style={{
              fontFamily: 'DM Sans, sans-serif', fontSize: 11,
              fontWeight: active ? 600 : 500,
            }}>{it.label}</div>
          </button>
        );
      })}
    </div>
  );
}

// Simple line icons drawn as SVG paths (no emoji)
const NavIconHome = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
    <path d="M3 11L12 4l9 7v9a1 1 0 01-1 1h-5v-6h-6v6H4a1 1 0 01-1-1v-9z" stroke="currentColor" strokeWidth="1.6" strokeLinejoin="round"/>
  </svg>
);
const NavIconBudget = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
    <rect x="3" y="6" width="18" height="14" rx="2" stroke="currentColor" strokeWidth="1.6"/>
    <path d="M3 10h18M8 6V3m8 3V3" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round"/>
  </svg>
);
const NavIconWallet = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
    <path d="M3 7a2 2 0 012-2h13a1 1 0 011 1v2M3 7v11a2 2 0 002 2h14a1 1 0 001-1v-3M3 7h17a1 1 0 011 1v3h-5a2 2 0 100 4h5" stroke="currentColor" strokeWidth="1.6" strokeLinejoin="round"/>
  </svg>
);
const NavIconHistory = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
    <circle cx="12" cy="12" r="9" stroke="currentColor" strokeWidth="1.6"/>
    <path d="M12 7v5l3 2" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round"/>
  </svg>
);

// ────────────────────────────────────────────────────────────
// Floating Action Button
// ────────────────────────────────────────────────────────────
function FAB({ onClick, bottom = 92 }) {
  return (
    <button onClick={onClick} style={{
      position: 'absolute', right: 20, bottom,
      width: 60, height: 60, borderRadius: 22,
      background: FOS.primary, color: FOS.onPrimary,
      border: 'none', cursor: 'pointer',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      boxShadow: `0 12px 24px ${FOS.primary}55, 0 0 0 6px ${FOS.bg}`,
      transition: 'transform 180ms cubic-bezier(0.34,1.56,0.64,1)',
      zIndex: 50,
    }}
    onMouseDown={(e) => e.currentTarget.style.transform = 'scale(0.94)'}
    onMouseUp={(e) => e.currentTarget.style.transform = 'scale(1)'}
    onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}>
      <svg width="26" height="26" viewBox="0 0 24 24" fill="none">
        <path d="M12 5v14M5 12h14" stroke={FOS.onPrimary} strokeWidth="2.5" strokeLinecap="round"/>
      </svg>
    </button>
  );
}

// ────────────────────────────────────────────────────────────
// Screen header (back, title, action)
// ────────────────────────────────────────────────────────────
function ScreenHeader({ title, onBack, right, subtitle }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 8,
      padding: '12px 16px 8px',
    }}>
      {onBack && (
        <button onClick={onBack} style={{
          width: 40, height: 40, borderRadius: 100,
          background: FOS.surface, border: `1px solid ${FOS.outline}`,
          color: FOS.text, cursor: 'pointer', display: 'flex',
          alignItems: 'center', justifyContent: 'center', padding: 0,
        }}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M15 6l-6 6 6 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </button>
      )}
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{
          fontFamily: 'DM Sans, sans-serif', fontSize: 18, fontWeight: 600,
          color: FOS.text, lineHeight: 1.2,
        }}>{title}</div>
        {subtitle && (
          <div style={{
            fontFamily: 'DM Sans, sans-serif', fontSize: 12,
            color: FOS.textDim, marginTop: 2,
          }}>{subtitle}</div>
        )}
      </div>
      {right}
    </div>
  );
}

// Slide-in animation wrapper for screen transitions — pure CSS keyframes
const _slideStyleInjected = (() => {
  if (typeof document === 'undefined') return false;
  if (document.getElementById('fos-slides')) return true;
  const s = document.createElement('style');
  s.id = 'fos-slides';
  s.textContent = `
    @keyframes fos-slide-right { from { transform: translate3d(36px,0,0); opacity: 0; } to { transform: none; opacity: 1; } }
    @keyframes fos-slide-left  { from { transform: translate3d(-36px,0,0); opacity: 0; } to { transform: none; opacity: 1; } }
    @keyframes fos-slide-up    { from { transform: translate3d(0,36px,0); opacity: 0; } to { transform: none; opacity: 1; } }
  `;
  document.head.appendChild(s);
  return true;
})();

function ScreenSlide({ children, dir = 'right', keyId }) {
  // Entry animation disabled — was unreliable in preview iframes.
  return (
    <div key={keyId} style={{
      position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column',
    }}>{children}</div>
  );
}

Object.assign(window, { PhoneShell, BottomNav, FAB, ScreenHeader, ScreenSlide });
