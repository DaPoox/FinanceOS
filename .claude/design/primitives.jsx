// primitives.jsx — design tokens + shared components for Finance OS

const FOS = {
  bg:        '#090c12',
  surface:   '#0f1420',
  surfaceVar:'#161d2e',
  surfaceCt: '#1c263a',
  primary:   '#f0b429',
  onPrimary: '#1a1000',
  text:      '#e8eef5',
  textDim:   '#8ba4be',
  outline:   'rgba(255,255,255,0.06)',
  outlineSt: 'rgba(255,255,255,0.10)',
  err:       '#f87171',
  pos:       '#22c55e',
  warn:      '#fb923c',
  save:      '#7eb8f7',
  invest:    '#a78bfa',
};

// Format money EUR with thin spaces. Geist Mono assumed at use site.
function eur(n, { sign = false, decimals = 0 } = {}) {
  const v = Math.round(n * (10 ** decimals)) / (10 ** decimals);
  const s = Math.abs(v).toLocaleString('fr-FR', { minimumFractionDigits: decimals, maximumFractionDigits: decimals });
  const prefix = sign ? (v > 0 ? '+' : v < 0 ? '−' : '') : (v < 0 ? '−' : '');
  return `${prefix}${s} €`;
}

// ────────────────────────────────────────────────────────────
// Animated count-up
// ────────────────────────────────────────────────────────────
function useCountUp(target, { ms = 900, delay = 0, steps = 30 } = {}) {
  const [v, setV] = React.useState(0);
  React.useEffect(() => {
    let i = 0;
    const t0 = setTimeout(() => {
      const interval = setInterval(() => {
        i++;
        const p = i / steps;
        const eased = 1 - Math.pow(1 - p, 3);
        setV(target * eased);
        if (i >= steps) { setV(target); clearInterval(interval); }
      }, ms / steps);
      return () => clearInterval(interval);
    }, delay);
    return () => clearTimeout(t0);
  }, [target, ms, delay, steps]);
  return v;
}

// ────────────────────────────────────────────────────────────
// Card
// ────────────────────────────────────────────────────────────
function Card({ children, style, onClick, variant = 'surface' }) {
  const bg = variant === 'variant' ? FOS.surfaceVar : FOS.surface;
  return (
    <div onClick={onClick} style={{
      background: bg,
      border: `1px solid ${FOS.outline}`,
      borderRadius: 20,
      padding: 16,
      cursor: onClick ? 'pointer' : 'default',
      ...style,
    }}>{children}</div>
  );
}

// ────────────────────────────────────────────────────────────
// Progress bar
// ────────────────────────────────────────────────────────────
function Progress({ value, max, color = FOS.text, bg = 'rgba(255,255,255,0.06)', height = 6, animate = true, delay = 0 }) {
  const pct = Math.max(0, Math.min(1.2, value / max));
  const over = value > max;
  const [shown, setShown] = React.useState(animate ? 0 : pct);
  React.useEffect(() => {
    if (!animate) { setShown(pct); return; }
    const t = setTimeout(() => setShown(pct), 60 + delay);
    return () => clearTimeout(t);
  }, [pct, animate, delay]);
  return (
    <div style={{ position: 'relative', width: '100%', height, background: bg, borderRadius: height, overflow: 'hidden' }}>
      <div style={{
        position: 'absolute', inset: 0,
        width: `${Math.min(100, shown * 100)}%`,
        background: over ? FOS.err : color,
        borderRadius: height,
        transition: 'width 900ms cubic-bezier(0.22, 1, 0.36, 1)',
      }} />
      {over && (
        <div style={{
          position: 'absolute', right: 0, top: 0, bottom: 0,
          width: 3, background: FOS.err, borderRadius: height,
        }} />
      )}
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// Pill / chip
// ────────────────────────────────────────────────────────────
function Pill({ children, color = FOS.primary, bg, style }) {
  return (
    <span style={{
      display: 'inline-flex', alignItems: 'center',
      fontFamily: 'DM Sans, sans-serif', fontSize: 12, fontWeight: 600,
      lineHeight: 1, padding: '6px 10px', borderRadius: 100,
      background: bg ?? `${color}1F`, color, letterSpacing: 0.2,
      ...style,
    }}>{children}</span>
  );
}

// ────────────────────────────────────────────────────────────
// Numeric (Geist Mono)
// ────────────────────────────────────────────────────────────
function Num({ children, size = 16, weight = 500, color = FOS.text, style }) {
  return (
    <span style={{
      fontFamily: '"Geist Mono", ui-monospace, monospace',
      fontSize: size, fontWeight: weight, color, letterSpacing: -0.2,
      fontFeatureSettings: '"tnum" on, "lnum" on',
      ...style,
    }}>{children}</span>
  );
}

// ────────────────────────────────────────────────────────────
// Section header (small caps style label)
// ────────────────────────────────────────────────────────────
function SectionLabel({ children, right, style }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      padding: '0 4px', ...style,
    }}>
      <div style={{
        fontFamily: 'DM Sans, sans-serif', fontSize: 11, fontWeight: 600,
        color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 1.4,
      }}>{children}</div>
      {right && <div style={{ color: FOS.textDim, fontSize: 12 }}>{right}</div>}
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// Sparkline (smooth area)
// ────────────────────────────────────────────────────────────
function Sparkline({ data, width = 380, height = 80, stroke = FOS.primary, fillFrom = `${FOS.primary}33`, fillTo = `${FOS.primary}00`, showDot = true }) {
  if (!data?.length) return null;
  const min = Math.min(...data);
  const max = Math.max(...data);
  const range = max - min || 1;
  const stepX = width / (data.length - 1);
  const pts = data.map((v, i) => [i * stepX, height - ((v - min) / range) * (height - 12) - 6]);

  // smooth path via cardinal-ish curve
  const path = pts.reduce((acc, [x, y], i) => {
    if (i === 0) return `M ${x},${y}`;
    const [px, py] = pts[i - 1];
    const cx = (px + x) / 2;
    return `${acc} C ${cx},${py} ${cx},${y} ${x},${y}`;
  }, '');
  const fillPath = `${path} L ${width},${height} L 0,${height} Z`;

  const gradId = `sg-${Math.random().toString(36).slice(2, 8)}`;
  const last = pts[pts.length - 1];

  return (
    <svg width={width} height={height} viewBox={`0 0 ${width} ${height}`} style={{ display: 'block' }}>
      <defs>
        <linearGradient id={gradId} x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor={fillFrom} />
          <stop offset="100%" stopColor={fillTo} />
        </linearGradient>
      </defs>
      <path d={fillPath} fill={`url(#${gradId})`} />
      <path d={path} fill="none" stroke={stroke} strokeWidth={1.5} strokeLinecap="round" />
      {showDot && (
        <g>
          <circle cx={last[0]} cy={last[1]} r={7} fill={stroke} opacity={0.18} />
          <circle cx={last[0]} cy={last[1]} r={3.5} fill={stroke} />
          <circle cx={last[0]} cy={last[1]} r={1.6} fill={FOS.bg} />
        </g>
      )}
    </svg>
  );
}

// ────────────────────────────────────────────────────────────
// Donut (stroke-only) with center label
// ────────────────────────────────────────────────────────────
function Donut({ slices, size = 180, stroke = 14, gap = 4, center }) {
  const r = (size - stroke) / 2;
  const c = 2 * Math.PI * r;
  const total = slices.reduce((s, sl) => s + sl.value, 0);
  let acc = 0;
  return (
    <div style={{ position: 'relative', width: size, height: size }}>
      <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
        <circle cx={size/2} cy={size/2} r={r} fill="none" stroke="rgba(255,255,255,0.04)" strokeWidth={stroke} />
        {slices.map((sl, i) => {
          const len = (sl.value / total) * c - gap;
          const off = (acc / total) * c;
          acc += sl.value;
          return (
            <circle key={i}
              cx={size/2} cy={size/2} r={r} fill="none"
              stroke={sl.color} strokeWidth={stroke} strokeLinecap="round"
              strokeDasharray={`${Math.max(0, len)} ${c}`}
              strokeDashoffset={-off}
              style={{ transition: 'stroke-dasharray 1000ms cubic-bezier(0.22, 1, 0.36, 1)' }}
            />
          );
        })}
      </svg>
      {center && (
        <div style={{
          position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column',
          alignItems: 'center', justifyContent: 'center',
        }}>{center}</div>
      )}
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// Icons — inline Lucide subset. Monoline, currentColor.
// All paths use viewBox 0 0 24 24, stroke="currentColor", fill="none".
// ────────────────────────────────────────────────────────────
const LUCIDE_PATHS = {
  'house':           '<path d="M15 21v-8a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v8"/><path d="M3 10a2 2 0 0 1 .7-1.5l7-6a2 2 0 0 1 2.6 0l7 6A2 2 0 0 1 21 10v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>',
  'car':             '<path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"/><path d="M14 17H9"/><circle cx="6.5" cy="17.5" r="2.5"/><circle cx="16.5" cy="17.5" r="2.5"/>',
  'zap':             '<path d="M4 14a1 1 0 0 1-.78-1.63l9.9-10.2a.5.5 0 0 1 .86.46l-1.92 6.02A1 1 0 0 0 13 10h7a1 1 0 0 1 .78 1.63l-9.9 10.2a.5.5 0 0 1-.86-.46l1.92-6.02A1 1 0 0 0 11 14z"/>',
  'repeat':          '<path d="m17 2 4 4-4 4"/><path d="M3 11v-1a4 4 0 0 1 4-4h14"/><path d="m7 22-4-4 4-4"/><path d="M21 13v1a4 4 0 0 1-4 4H3"/>',
  'heart-pulse':     '<path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.29 1.51 4.04 3 5.5l7 7Z"/><path d="M3.22 12H9.5l.5-1 2 4.5 2-7 1.5 3.5h5.27"/>',
  'dumbbell':        '<path d="M6.5 6.5 17.5 17.5"/><path d="m21 21-1-1"/><path d="m3 3 1 1"/><path d="m18 22 4-4"/><path d="m2 6 4-4"/><path d="m3 10 7-7"/><path d="m14 21 7-7"/>',
  'smartphone':      '<rect width="14" height="20" x="5" y="2" rx="2" ry="2"/><path d="M12 18h.01"/>',
  'shield':          '<path d="M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z"/>',
  'shopping-basket': '<path d="m15 11-1 9"/><path d="m19 11-4-7"/><path d="M2 11h20"/><path d="m3.5 11 1.6 7.4a2 2 0 0 0 2 1.6h9.8a2 2 0 0 0 2-1.6l1.7-7.4"/><path d="m5 11 4-7"/><path d="m9 11 1 9"/>',
  'utensils':        '<path d="M3 2v7c0 1.1.9 2 2 2h0a2 2 0 0 0 2-2V2"/><path d="M7 2v20"/><path d="M21 15V2a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3Zm0 0v7"/>',
  'bus':             '<path d="M8 6v6"/><path d="M15 6v6"/><path d="M2 12h19.6"/><path d="M18 18h3s.5-1.7.8-2.8c.1-.4.2-.8.2-1.2 0-.4-.1-.8-.2-1.2l-1.4-5C20.1 6.8 19.1 6 18 6H4a2 2 0 0 0-2 2v10h3"/><circle cx="7" cy="18" r="2"/><circle cx="17" cy="18" r="2"/>',
  'shopping-bag':    '<path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/><path d="M3 6h18"/><path d="M16 10a4 4 0 0 1-8 0"/>',
  'plane':           '<path d="M17.8 19.2 16 11l3.5-3.5C21 6 21.5 4 21 3c-1-.5-3 0-4.5 1.5L13 8 4.8 6.2c-.5-.1-.9.1-1.1.5l-.3.5c-.2.5-.1 1 .3 1.3L9 12l-2 3H4l-1 1 3 2 2 3 1-1v-3l3-2 3.5 5.3c.3.4.8.5 1.3.3l.5-.2c.4-.3.6-.7.5-1.2z"/>',
  'umbrella':        '<path d="M22 12a10 10 0 0 0-20 0Z"/><path d="M12 12v8a2 2 0 0 0 4 0"/><path d="M12 2v1"/>',
  'gift':            '<rect x="3" y="8" width="18" height="4" rx="1"/><path d="M12 8v13"/><path d="M19 12v7a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-7"/><path d="M7.5 8a2.5 2.5 0 0 1 0-5C9 3 9 5 12 8c3-3 3-5 4.5-5a2.5 2.5 0 0 1 0 5"/>',
  'piggy-bank':      '<path d="M19 5c-1.5 0-2.8 1.4-3 2-2.5-1.5-7-1.5-9.5 0C6 6.4 4.7 5.5 3.5 5.5 2.5 5.5 2 6 2 7c0 1 1 2 1 3-1 0-1 1-1 1.5C2 12.3 3 13 4 13c0 2.5 1.5 4.5 4 5.5V21h3v-1.5h3V21h3v-3c1.5-1 2.5-2.5 2.5-4.5 0-3.5-2-7-4-7.5-.4-.6-1-1-1.5-1Z"/><circle cx="8" cy="10" r=".5" fill="currentColor"/>',
  'landmark':        '<path d="M3 22h18"/><path d="M6 18v-7"/><path d="M10 18v-7"/><path d="M14 18v-7"/><path d="M18 18v-7"/><path d="m2 8 10-5 10 5"/><path d="M3 11h18"/>',
  'trending-up':     '<polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/><polyline points="16 7 22 7 22 13"/>',
  'line-chart':      '<path d="M3 3v18h18"/><path d="m19 9-5 5-4-4-3 3"/>',
  'infinity':        '<path d="M12 12c-2-2.96-2.5-5-5.5-5a4 4 0 1 0 0 8c3 0 5.5-2 7-4s3.5-4 5.5-4a4 4 0 1 1 0 8c-3 0-3.5-2-5.5-5"/>',
  'wallet':          '<path d="M19 7V5a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-2"/><path d="M3 5v14"/><path d="M21 11h-7a2 2 0 0 0 0 4h7z"/>',
  'trash':           '<path d="M3 6h18"/><path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><path d="m19 6-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/>',
  'layout-grid':     '<rect width="7" height="7" x="3" y="3" rx="1"/><rect width="7" height="7" x="14" y="3" rx="1"/><rect width="7" height="7" x="14" y="14" rx="1"/><rect width="7" height="7" x="3" y="14" rx="1"/>',
  'building-2':      '<path d="M6 22V4a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v18Z"/><path d="M6 12H4a2 2 0 0 0-2 2v8h4"/><path d="M18 9h2a2 2 0 0 1 2 2v11h-4"/><path d="M10 6h4"/><path d="M10 10h4"/><path d="M10 14h4"/><path d="M10 18h4"/>',
  'calendar-days':   '<path d="M8 2v4"/><path d="M16 2v4"/><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/><path d="M8 14h.01"/><path d="M12 14h.01"/><path d="M16 14h.01"/><path d="M8 18h.01"/><path d="M12 18h.01"/><path d="M16 18h.01"/>',
  'calendar-plus':   '<path d="M8 2v4"/><path d="M16 2v4"/><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/><path d="M16 19h6"/><path d="M19 16v6"/>',
  'receipt':         '<path d="M4 2v20l2-1 2 1 2-1 2 1 2-1 2 1 2-1 2 1V2l-2 1-2-1-2 1-2-1-2 1-2-1-2 1Z"/><path d="M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8"/><path d="M12 17.5v-11"/>',
  'plus':            '<path d="M5 12h14"/><path d="M12 5v14"/>',
  'check':           '<path d="M20 6 9 17l-5-5"/>',
  'chevron-down':    '<path d="m6 9 6 6 6-6"/>',
  'chevron-left':    '<path d="m15 18-6-6 6-6"/>',
  'chevron-right':   '<path d="m9 18 6-6-6-6"/>',
  'arrow-right':     '<path d="M5 12h14"/><path d="m12 5 7 7-7 7"/>',
  'briefcase':       '<rect width="20" height="14" x="2" y="7" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>',
  'bell':            '<path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>',
  'sparkles':        '<path d="M9.937 15.5A2 2 0 0 0 8.5 14.063l-6.135-1.582a.5.5 0 0 1 0-.962L8.5 9.936A2 2 0 0 0 9.937 8.5l1.582-6.135a.5.5 0 0 1 .963 0L14.063 8.5A2 2 0 0 0 15.5 9.937l6.135 1.581a.5.5 0 0 1 0 .964L15.5 14.063a2 2 0 0 0-1.437 1.437l-1.582 6.135a.5.5 0 0 1-.963 0z"/><path d="M20 3v4"/><path d="M22 5h-4"/><path d="M4 17v2"/><path d="M5 18H3"/>',
};

function Icon({ name, size = 16, color, strokeWidth = 1.75, style }) {
  const inner = LUCIDE_PATHS[name];
  if (!inner) return null;
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none"
         stroke={color || 'currentColor'} strokeWidth={strokeWidth}
         strokeLinecap="round" strokeLinejoin="round"
         style={{ display: 'block', ...style }}
         dangerouslySetInnerHTML={{ __html: inner }} />
  );
}

// Map envelope id (or fallback type) → Lucide icon name.
const ENV_ICON_BY_ID = {
  rent: 'house', credit: 'car', utils: 'zap', subs: 'repeat',
  mutuelle: 'heart-pulse', sport: 'dumbbell', tel: 'smartphone', assur: 'shield',
  groceries: 'shopping-basket', restos: 'utensils', transport: 'bus', shopping: 'shopping-bag',
  ams: 'plane',
  vacances: 'umbrella', cadeaux: 'gift',
  livretA: 'piggy-bank', ldds: 'landmark',
  pea: 'trending-up', cto: 'line-chart',
};
const ENV_ICON_BY_TYPE = {
  fixe: 'wallet', var: 'wallet', month: 'plane', perm: 'infinity',
  save: 'piggy-bank', inv: 'trending-up',
};
function iconFor(env) {
  if (!env) return 'wallet';
  return ENV_ICON_BY_ID[env.id] || ENV_ICON_BY_TYPE[env.type] || 'wallet';
}

Object.assign(window, { FOS, eur, useCountUp, Card, Progress, Pill, Num, SectionLabel, Sparkline, Donut, Icon, iconFor });
