// flow-canvas.jsx — Finance OS — interactive user-flow diagram.
// Pan/zoom canvas · phone-thumbnail nodes · labelled SVG arrows ·
// decision diamonds · legend · click-a-screen detail overlay.

const NODE_SCALE = 0.34;
const WORLD = { x: -160, y: -160, w: 2260, h: 3420 };

// ── geometry helpers ─────────────────────────────────────────
function boxOf(n) {
  if (n.kind === 'phone') return { x: n.x, y: n.y, w: NODE.w, h: NODE.h };
  const s = n.size || (n.kind === 'decision' ? DEC : ENT);
  return { x: n.x, y: n.y, w: s.w, h: s.h };
}
const DIR = { top: [0, -1], bottom: [0, 1], left: [-1, 0], right: [1, 0] };
function anchor(b, side) {
  switch (side) {
    case 'top': return [b.x + b.w / 2, b.y];
    case 'bottom': return [b.x + b.w / 2, b.y + b.h];
    case 'left': return [b.x, b.y + b.h / 2];
    case 'right': return [b.x + b.w, b.y + b.h / 2];
    default: return [b.x + b.w / 2, b.y + b.h / 2];
  }
}
function autoSides(a, b) {
  const ac = [a.x + a.w / 2, a.y + a.h / 2], bc = [b.x + b.w / 2, b.y + b.h / 2];
  const dx = bc[0] - ac[0], dy = bc[1] - ac[1];
  if (Math.abs(dx) > Math.abs(dy)) return dx > 0 ? ['right', 'left'] : ['left', 'right'];
  return dy > 0 ? ['bottom', 'top'] : ['top', 'bottom'];
}
function bezier(p0, c1, c2, p3, t) {
  const u = 1 - t;
  const x = u * u * u * p0[0] + 3 * u * u * t * c1[0] + 3 * u * t * t * c2[0] + t * t * t * p3[0];
  const y = u * u * u * p0[1] + 3 * u * u * t * c1[1] + 3 * u * t * t * c2[1] + t * t * t * p3[1];
  return [x, y];
}

function edgeGeometry(edge, nodeMap) {
  const fn = nodeMap[edge.from], tn = nodeMap[edge.to];
  if (!fn || !tn) return null;
  const fb = boxOf(fn), tb = boxOf(tn);
  let fs = edge.fromSide, ts = edge.toSide;
  if (!fs || !ts) { const [a, b] = autoSides(fb, tb); fs = fs || a; ts = ts || b; }
  const p0 = anchor(fb, fs), p3 = anchor(tb, ts);
  const dist = Math.hypot(p3[0] - p0[0], p3[1] - p0[1]);
  const k = Math.max(50, Math.min(190, dist * 0.45));
  const c1 = [p0[0] + DIR[fs][0] * k, p0[1] + DIR[fs][1] * k];
  const c2 = [p3[0] + DIR[ts][0] * k, p3[1] + DIR[ts][1] * k];
  const mid = bezier(p0, c1, c2, p3, 0.5);
  return { p0, c1, c2, p3, mid, fs, ts };
}
const toSvg = (p) => [p[0] - WORLD.x, p[1] - WORLD.y];

function arrowHead(tip, fromCtrl, color, key) {
  // triangle at `tip`, pointing along (tip - fromCtrl)
  const ang = Math.atan2(tip[1] - fromCtrl[1], tip[0] - fromCtrl[0]);
  const s = 9;
  const t = toSvg(tip);
  const pts = [
    [t[0], t[1]],
    [t[0] - s * Math.cos(ang - 0.42), t[1] - s * Math.sin(ang - 0.42)],
    [t[0] - s * Math.cos(ang + 0.42), t[1] - s * Math.sin(ang + 0.42)],
  ].map(p => p.join(',')).join(' ');
  return <polygon key={key} points={pts} fill={color} />;
}

// ── Arrow layer ──────────────────────────────────────────────
function Arrows({ nodeMap, hovered }) {
  return (
    <svg style={{ position: 'absolute', left: WORLD.x, top: WORLD.y, width: WORLD.w, height: WORLD.h, overflow: 'visible', pointerEvents: 'none' }}
         viewBox={`0 0 ${WORLD.w} ${WORLD.h}`}>
      {EDGES.map((e, i) => {
        const g = edgeGeometry(e, nodeMap);
        if (!g) return null;
        const color = CAT[e.cat] || CAT.nav;
        const active = !hovered || e.from === hovered || e.to === hovered;
        const op = active ? 1 : 0.12;
        const w = (e.from === hovered || e.to === hovered) ? 2.6 : 1.6;
        const P0 = toSvg(g.p0), C1 = toSvg(g.c1), C2 = toSvg(g.c2), P3 = toSvg(g.p3);
        const d = `M ${P0[0]} ${P0[1]} C ${C1[0]} ${C1[1]}, ${C2[0]} ${C2[1]}, ${P3[0]} ${P3[1]}`;
        return (
          <g key={i} style={{ opacity: op, transition: 'opacity .15s' }}>
            <path d={d} fill="none" stroke={color} strokeWidth={w}
                  strokeDasharray={e.dashed ? '5 6' : 'none'} strokeLinecap="round" />
            {arrowHead(g.p3, g.c2, color, 'h' + i)}
            {e.bidir && arrowHead(g.p0, g.c1, color, 'b' + i)}
          </g>
        );
      })}
    </svg>
  );
}

// edge labels are HTML (crisp text) positioned at the curve midpoint
function EdgeLabels({ nodeMap, hovered }) {
  return EDGES.map((e, i) => {
    if (!e.label) return null;
    const g = edgeGeometry(e, nodeMap);
    if (!g) return null;
    const color = CAT[e.cat] || CAT.nav;
    const active = !hovered || e.from === hovered || e.to === hovered;
    return (
      <div key={i} style={{
        position: 'absolute', left: g.mid[0], top: g.mid[1],
        transform: 'translate(-50%,-50%)', pointerEvents: 'none',
        fontFamily: 'DM Sans, sans-serif', fontSize: 12, fontWeight: 600,
        color: '#e8eef5', background: '#0e1320', padding: '3px 8px', borderRadius: 7,
        border: `1px solid ${color}66`, whiteSpace: 'nowrap',
        opacity: active ? 1 : 0.12, transition: 'opacity .15s',
        boxShadow: '0 2px 8px rgba(0,0,0,0.4)',
      }}>{e.label}</div>
    );
  });
}

// ── Node renderers ───────────────────────────────────────────
function DecisionNode({ node }) {
  const s = node.size || DEC;
  return (
    <div style={{ width: s.w, height: s.h, position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div style={{
        position: 'absolute', inset: 0, background: '#141b2b',
        border: `1.5px solid ${CAT.route}`, clipPath: 'polygon(50% 0, 100% 50%, 50% 100%, 0 50%)',
      }} />
      <div style={{
        position: 'relative', width: '62%', textAlign: 'center',
        fontFamily: 'DM Sans, sans-serif', fontSize: 13, fontWeight: 600,
        color: '#e8eef5', lineHeight: 1.25, whiteSpace: 'pre-line',
      }}>{node.title}</div>
    </div>
  );
}

function EntryNode({ node }) {
  const s = node.size || ENT;
  return (
    <div style={{
      width: s.w, height: s.h, borderRadius: 100,
      background: CAT.route, color: '#0a0f1a',
      display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
      fontFamily: 'DM Sans, sans-serif', fontSize: 13, fontWeight: 700,
      boxShadow: '0 6px 20px rgba(139,164,190,0.35)',
    }}>
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><path d="M5 3l14 9-14 9z" fill="#0a0f1a" /></svg>
      {node.title}
    </div>
  );
}

function PhoneNode({ node, onOpen, onHover }) {
  const color = CAT[node.cat] || CAT.nav;
  return (
    <div
      onMouseEnter={() => onHover(node.id)}
      onMouseLeave={() => onHover(null)}
      onClick={() => onOpen(node.id)}
      style={{ width: NODE.w, cursor: 'pointer' }}
    >
      {/* label chip above */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 6, height: 30 }}>
        <span style={{ width: 8, height: 8, borderRadius: 100, background: color, flexShrink: 0 }} />
        <div style={{ minWidth: 0 }}>
          <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 11, fontWeight: 600, color: '#e8eef5', lineHeight: 1.15, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: NODE.w - 16 }}>{node.title}</div>
          {node.state && <div style={{ fontFamily: '"Geist Mono", monospace', fontSize: 9, color: '#8ba4be', marginTop: 1, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: NODE.w - 16 }}>{node.state}</div>}
        </div>
      </div>
      <div style={{ borderRadius: 18, boxShadow: `0 0 0 1.5px ${color}, 0 10px 30px rgba(0,0,0,0.5)`, width: NODE.w, height: NODE.h, overflow: 'hidden', background: '#06080d' }}>
        <PhoneFrame scale={NODE_SCALE} tab={node.tab}>{node.render()}</PhoneFrame>
      </div>
    </div>
  );
}

function FlowNode({ node, onOpen, onHover, dim }) {
  const b = boxOf(node);
  let inner;
  if (node.kind === 'decision') inner = <DecisionNode node={node} />;
  else if (node.kind === 'entry') inner = <EntryNode node={node} />;
  else inner = <PhoneNode node={node} onOpen={onOpen} onHover={onHover} />;
  return (
    <div style={{
      position: 'absolute', left: node.x, top: node.kind === 'phone' ? node.y - 36 : node.y,
      opacity: dim ? 0.25 : 1, transition: 'opacity .15s',
    }}
    onMouseEnter={node.kind !== 'phone' ? () => onHover(node.id) : undefined}
    onMouseLeave={node.kind !== 'phone' ? () => onHover(null) : undefined}>
      {inner}
    </div>
  );
}

// ── Bands (grouping zones) ───────────────────────────────────
function Bands() {
  return BANDS.map(b => (
    <div key={b.id} style={{
      position: 'absolute', left: b.x, top: b.y, width: b.w, height: b.h,
      borderRadius: 24, background: `${b.tint}0c`, border: `1px solid ${b.tint}2e`,
      pointerEvents: 'none',
    }}>
      <div style={{ padding: '14px 22px' }}>
        <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 19, fontWeight: 700, color: b.tint, letterSpacing: -0.2 }}>{b.title}</div>
        {b.sub && <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 13, color: '#8ba4be', marginTop: 2 }}>{b.sub}</div>}
      </div>
    </div>
  ));
}

// ── Detail overlay ───────────────────────────────────────────
function DetailOverlay({ id, onClose, onNav }) {
  const node = NODES.find(n => n.id === id);
  React.useEffect(() => {
    const k = (e) => {
      if (e.key === 'Escape') onClose();
      if (e.key === 'ArrowRight') onNav(1);
      if (e.key === 'ArrowLeft') onNav(-1);
    };
    document.addEventListener('keydown', k);
    return () => document.removeEventListener('keydown', k);
  }, [onClose, onNav]);
  if (!node) return null;
  const color = CAT[node.cat] || CAT.nav;
  const incoming = EDGES.filter(e => e.to === id);
  const outgoing = EDGES.filter(e => e.from === id);
  const nm = Object.fromEntries(NODES.map(n => [n.id, n]));
  const catLabel = { happy: 'Happy path · données présentes', empty: 'État vide / onboarding', modal: 'Overlay / modale', gap: 'Manquant — à concevoir', happyflow: '' }[node.cat] || 'Routage';

  const vh = typeof window !== 'undefined' ? window.innerHeight : 900;
  const phoneScale = Math.min((vh - 140) / 892, 0.82);

  return ReactDOM.createPortal(
    <div onClick={onClose} style={{
      position: 'fixed', inset: 0, zIndex: 200, background: 'rgba(4,6,11,0.78)',
      backdropFilter: 'blur(12px)', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 40, padding: 40,
    }}>
      {/* phone */}
      <div onClick={(e) => e.stopPropagation()} style={{ position: 'relative', flexShrink: 0 }}>
        <div style={{ borderRadius: 44, boxShadow: `0 0 0 2px ${color}, 0 30px 80px rgba(0,0,0,0.6)`, width: 412 * phoneScale, height: 892 * phoneScale, overflow: 'hidden' }}>
          {node.kind === 'phone'
            ? <PhoneFrame scale={phoneScale} tab={node.tab} interactive>{node.render()}</PhoneFrame>
            : <div style={{ width: 412 * phoneScale, height: 892 * phoneScale, display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#0e1320' }}>
                {node.kind === 'decision' ? <DecisionNode node={node} /> : <EntryNode node={node} />}
              </div>}
        </div>
      </div>

      {/* info panel */}
      <div onClick={(e) => e.stopPropagation()} style={{
        width: 340, maxHeight: vh - 120, overflow: 'auto', color: '#e8eef5',
        fontFamily: 'DM Sans, sans-serif',
      }}>
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: 8, padding: '5px 12px', borderRadius: 100, background: `${color}1f`, border: `1px solid ${color}55`, marginBottom: 14 }}>
          <span style={{ width: 9, height: 9, borderRadius: 100, background: color }} />
          <span style={{ fontSize: 12, fontWeight: 600, color }}>{catLabel}</span>
        </div>
        <div style={{ fontSize: 24, fontWeight: 700, letterSpacing: -0.3, lineHeight: 1.2 }}>{node.title}</div>
        {node.state && <div style={{ fontFamily: '"Geist Mono", monospace', fontSize: 13, color: '#8ba4be', marginTop: 6 }}>{node.state}</div>}

        {node.entry && (
          <div style={{ marginTop: 18 }}>
            <SmallLabel>Déclencheur</SmallLabel>
            <div style={{ fontSize: 13, color: '#c7d6e6', marginTop: 6, lineHeight: 1.5 }}>{node.entry}</div>
          </div>
        )}

        <div style={{ marginTop: 18 }}>
          <SmallLabel>Arrive depuis</SmallLabel>
          {incoming.length ? incoming.map((e, i) => (
            <EdgeLine key={i} dir="in" name={nm[e.from]?.title} label={e.label} color={CAT[e.cat]} />
          )) : <Muted>Point d’entrée racine</Muted>}
        </div>

        <div style={{ marginTop: 16 }}>
          <SmallLabel>Mène vers</SmallLabel>
          {outgoing.length ? outgoing.map((e, i) => (
            <EdgeLine key={i} dir="out" name={nm[e.to]?.title} label={e.label} color={CAT[e.cat]} />
          )) : <Muted>Écran terminal</Muted>}
        </div>

        {node.cat === 'gap' && (
          <div style={{ marginTop: 18, padding: 14, borderRadius: 12, background: `${CAT.gap}12`, border: `1px solid ${CAT.gap}44` }}>
            <div style={{ fontSize: 13, fontWeight: 700, color: CAT.gap }}>⚠ À concevoir</div>
            <div style={{ fontSize: 12.5, color: '#c7d6e6', marginTop: 4, lineHeight: 1.5 }}>Cet écran / état n’existe pas dans le design actuel. Le « ? » marque l’emplacement à traiter.</div>
          </div>
        )}

        <div style={{ marginTop: 22, display: 'flex', gap: 8 }}>
          <button onClick={() => onNav(-1)} style={navBtn}>← Précédent</button>
          <button onClick={() => onNav(1)} style={navBtn}>Suivant →</button>
        </div>
      </div>

      <button onClick={onClose} style={{
        position: 'fixed', top: 22, right: 26, width: 40, height: 40, borderRadius: 100,
        background: 'rgba(255,255,255,0.08)', border: '1px solid rgba(255,255,255,0.15)',
        color: '#fff', fontSize: 22, cursor: 'pointer', lineHeight: 1,
      }}>×</button>
    </div>,
    document.body
  );
}
const navBtn = { flex: 1, padding: '10px', borderRadius: 10, background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.12)', color: '#e8eef5', fontFamily: 'DM Sans, sans-serif', fontSize: 13, fontWeight: 600, cursor: 'pointer' };
function SmallLabel({ children }) { return <div style={{ fontFamily: 'DM Sans, sans-serif', fontSize: 10.5, fontWeight: 700, letterSpacing: 1.2, textTransform: 'uppercase', color: '#8ba4be' }}>{children}</div>; }
function Muted({ children }) { return <div style={{ fontSize: 13, color: '#6c819a', marginTop: 6, fontStyle: 'italic' }}>{children}</div>; }
function EdgeLine({ dir, name, label, color }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 8 }}>
      <span style={{ color: color || '#8ba4be', fontFamily: '"Geist Mono", monospace', fontSize: 13, flexShrink: 0 }}>{dir === 'in' ? '→' : '↳'}</span>
      <div style={{ minWidth: 0 }}>
        <div style={{ fontSize: 13.5, fontWeight: 600, color: '#e8eef5' }}>{name || '—'}</div>
        {label && <div style={{ fontSize: 11.5, color: '#8ba4be' }}>{label}</div>}
      </div>
    </div>
  );
}

// ── Legend ───────────────────────────────────────────────────
function Legend() {
  const items = [
    ['happy', 'Happy path — données présentes'],
    ['empty', 'État vide / onboarding'],
    ['modal', 'Overlay / sheet / formulaire'],
    ['gap', 'Manquant — à concevoir (?)'],
    ['nav', 'Navigation par onglets'],
    ['route', 'Routage / décision'],
  ];
  return (
    <div style={{
      position: 'fixed', left: 20, bottom: 20, zIndex: 60,
      background: 'rgba(12,16,26,0.92)', border: '1px solid rgba(255,255,255,0.1)',
      borderRadius: 16, padding: '14px 16px', backdropFilter: 'blur(8px)',
      fontFamily: 'DM Sans, sans-serif', boxShadow: '0 12px 40px rgba(0,0,0,0.5)',
    }}>
      <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1.2, textTransform: 'uppercase', color: '#8ba4be', marginBottom: 10 }}>Légende</div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px 18px' }}>
        {items.map(([c, l]) => (
          <div key={c} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ width: 14, height: 3, borderRadius: 2, background: CAT[c], flexShrink: 0 }} />
            <span style={{ fontSize: 12, color: '#c7d6e6' }}>{l}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

// ── Canvas (pan/zoom) ────────────────────────────────────────
function FlowCanvas() {
  const vpRef = React.useRef(null);
  const worldRef = React.useRef(null);
  const tf = React.useRef({ x: 0, y: 0, scale: 1 });
  const [hovered, setHovered] = React.useState(null);
  const [selected, setSelected] = React.useState(null);
  const [zoomPct, setZoomPct] = React.useState(100);
  const nodeMap = React.useMemo(() => Object.fromEntries(NODES.map(n => [n.id, n])), []);

  const apply = React.useCallback(() => {
    const el = worldRef.current; if (!el) return;
    const { x, y, scale } = tf.current;
    el.style.transform = `translate3d(${x}px,${y}px,0) scale(${scale})`;
    setZoomPct(Math.round(scale * 100));
  }, []);

  const fit = React.useCallback(() => {
    const vp = vpRef.current; if (!vp) return;
    const pad = 120;
    const contentW = WORLD.w, contentH = WORLD.h;
    const s = Math.min((vp.clientWidth - pad) / contentW, (vp.clientHeight - pad) / contentH);
    tf.current.scale = Math.max(0.12, Math.min(1, s));
    tf.current.x = (vp.clientWidth - contentW * tf.current.scale) / 2 - WORLD.x * tf.current.scale;
    tf.current.y = 24 - WORLD.y * tf.current.scale;
    apply();
  }, [apply]);

  React.useEffect(() => { fit(); }, [fit]);

  React.useEffect(() => {
    const vp = vpRef.current; if (!vp) return;
    const zoomAt = (cx, cy, factor) => {
      const r = vp.getBoundingClientRect();
      const px = cx - r.left, py = cy - r.top, t = tf.current;
      const next = Math.min(2.2, Math.max(0.08, t.scale * factor));
      const k = next / t.scale;
      t.x = px - (px - t.x) * k; t.y = py - (py - t.y) * k; t.scale = next; apply();
    };
    const onWheel = (e) => {
      e.preventDefault();
      if (e.ctrlKey || e.metaKey) { zoomAt(e.clientX, e.clientY, Math.exp(-e.deltaY * 0.01)); }
      else { tf.current.x -= e.deltaX; tf.current.y -= e.deltaY; apply(); }
    };
    let drag = null;
    const onDown = (e) => {
      if (e.target.closest('[data-flow-node]')) return;
      drag = { x: e.clientX, y: e.clientY }; vp.setPointerCapture(e.pointerId); vp.style.cursor = 'grabbing';
    };
    const onMove = (e) => { if (!drag) return; tf.current.x += e.clientX - drag.x; tf.current.y += e.clientY - drag.y; drag = { x: e.clientX, y: e.clientY }; apply(); };
    const onUp = () => { drag = null; vp.style.cursor = ''; };
    vp.addEventListener('wheel', onWheel, { passive: false });
    vp.addEventListener('pointerdown', onDown);
    vp.addEventListener('pointermove', onMove);
    vp.addEventListener('pointerup', onUp);
    vp.addEventListener('pointercancel', onUp);
    return () => {
      vp.removeEventListener('wheel', onWheel);
      vp.removeEventListener('pointerdown', onDown);
      vp.removeEventListener('pointermove', onMove);
      vp.removeEventListener('pointerup', onUp);
      vp.removeEventListener('pointercancel', onUp);
    };
  }, [apply]);

  const zoomBtn = (factor) => {
    const vp = vpRef.current; const r = vp.getBoundingClientRect();
    const cx = r.left + r.width / 2, cy = r.top + r.height / 2, t = tf.current;
    const next = Math.min(2.2, Math.max(0.08, t.scale * factor)), k = next / t.scale;
    const px = cx - r.left, py = cy - r.top;
    t.x = px - (px - t.x) * k; t.y = py - (py - t.y) * k; t.scale = next; apply();
  };

  const navDetail = (dir) => {
    const idx = NODES.findIndex(n => n.id === selected);
    let next = idx;
    for (let i = 0; i < NODES.length; i++) {
      next = (next + dir + NODES.length) % NODES.length;
      if (NODES[next].kind === 'phone') break;
    }
    setSelected(NODES[next].id);
  };

  return (
    <div ref={vpRef} style={{ position: 'fixed', inset: 0, overflow: 'hidden', background: '#080b12', touchAction: 'none', cursor: 'grab' }}>
      {/* grid */}
      <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(rgba(255,255,255,0.05) 1px, transparent 1px)', backgroundSize: '34px 34px', pointerEvents: 'none' }} />

      <div ref={worldRef} style={{ position: 'absolute', top: 0, left: 0, transformOrigin: '0 0', willChange: 'transform' }}>
        <Bands />
        <Arrows nodeMap={nodeMap} hovered={hovered} />
        <EdgeLabels nodeMap={nodeMap} hovered={hovered} />
        {NODES.map(n => (
          <div key={n.id} data-flow-node="">
            <FlowNode node={n} onOpen={setSelected} onHover={setHovered}
              dim={hovered && n.kind === 'phone' && hovered !== n.id && !EDGES.some(e => (e.from === hovered && e.to === n.id) || (e.to === hovered && e.from === n.id))} />
          </div>
        ))}
      </div>

      {/* header */}
      <div style={{ position: 'fixed', top: 18, left: 20, zIndex: 60, fontFamily: 'DM Sans, sans-serif', pointerEvents: 'none' }}>
        <div style={{ fontSize: 21, fontWeight: 700, color: '#e8eef5', letterSpacing: -0.3 }}>Finance OS — Diagramme de flux</div>
        <div style={{ fontSize: 13, color: '#8ba4be', marginTop: 2 }}>{NODES.filter(n => n.kind === 'phone').length} écrans · cliquer pour le détail · molette = pan · ⌘/Ctrl+molette = zoom</div>
      </div>

      {/* zoom controls */}
      <div style={{ position: 'fixed', right: 20, bottom: 20, zIndex: 60, display: 'flex', alignItems: 'center', gap: 6, background: 'rgba(12,16,26,0.92)', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 12, padding: 6, fontFamily: 'DM Sans, sans-serif' }}>
        <button onClick={() => zoomBtn(1 / 1.25)} style={ctrlBtn}>−</button>
        <div style={{ width: 46, textAlign: 'center', fontSize: 12, color: '#c7d6e6', fontVariantNumeric: 'tabular-nums' }}>{zoomPct}%</div>
        <button onClick={() => zoomBtn(1.25)} style={ctrlBtn}>+</button>
        <button onClick={fit} style={{ ...ctrlBtn, width: 'auto', padding: '0 12px', fontSize: 12, fontWeight: 600 }}>Tout afficher</button>
      </div>

      <Legend />

      {selected && <DetailOverlay id={selected} onClose={() => setSelected(null)} onNav={navDetail} />}
    </div>
  );
}
const ctrlBtn = { width: 32, height: 32, borderRadius: 8, background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.12)', color: '#e8eef5', fontSize: 18, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', lineHeight: 1 };

ReactDOM.createRoot(document.getElementById('root')).render(<FlowCanvas />);
