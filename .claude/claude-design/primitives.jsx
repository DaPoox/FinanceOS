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

Object.assign(window, { FOS, eur, useCountUp, Card, Progress, Pill, Num, SectionLabel, Sparkline, Donut });
