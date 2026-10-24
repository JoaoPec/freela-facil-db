const NS = 'http://www.w3.org/2000/svg';
const FONT = 'Arial, Helvetica, sans-serif';
const L = {};

function el(tag, attrs, parent) {
  const e = document.createElementNS(NS, tag);
  for (const k in attrs) e.setAttribute(k, attrs[k]);
  (parent || L.shapes).appendChild(e);
  return e;
}

function setup(w, h) {
  document.body.style.cssText = 'margin:0;overflow:hidden;background:#fff';
  const svg = el('svg', { width: w, height: h, viewBox: `0 0 ${w} ${h}` }, document.body);
  svg.style.display = 'block';
  svg.innerHTML = `<defs>
    <pattern id="grid" width="13" height="13" patternUnits="userSpaceOnUse">
      <path d="M13 0.5H0.5V13" fill="none" stroke="#e4e8ef" stroke-width="1"/>
    </pattern>
    <linearGradient id="hd" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0" stop-color="#cfcfcf"/><stop offset="1" stop-color="#b2b2b2"/>
    </linearGradient>
    <linearGradient id="ft" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0" stop-color="#f3f3f3"/><stop offset="1" stop-color="#e2e2e2"/>
    </linearGradient>
    <marker id="arr" viewBox="0 0 10 10" refX="9.5" refY="5" markerWidth="10" markerHeight="10" markerUnits="userSpaceOnUse" orient="auto">
      <path d="M1 1L9.5 5L1 9" fill="none" stroke="#000" stroke-width="1.2"/>
    </marker>
    <filter id="sh" x="-10%" y="-10%" width="125%" height="125%">
      <feDropShadow dx="1" dy="2" stdDeviation="1.6" flood-color="#000" flood-opacity=".28"/>
    </filter>
  </defs>
  <rect width="100%" height="100%" fill="#fff"/>
  <rect width="100%" height="100%" fill="url(#grid)"/>`;
  L.lines = el('g', {}, svg);
  L.shapes = el('g', {}, svg);
  L.labels = el('g', {}, svg);
}

function text(x, y, t, o = {}, parent) {
  const e = el('text', {
    x, y, 'font-family': FONT, 'font-size': o.size || 14,
    'font-weight': o.bold ? 'bold' : 'normal', 'text-anchor': o.anchor || 'middle',
    'dominant-baseline': 'central', fill: o.fill || '#000',
    'letter-spacing': o.ls || 0,
  }, parent || L.shapes);
  e.textContent = t;
  return e;
}

// ---------- Modelo conceitual ----------
function entity(x, y, w, h, name) {
  el('rect', { x: x + 3, y: y + 3, width: w, height: h, fill: '#4a4a4a' });
  el('rect', { x: x + .5, y: y + .5, width: w, height: h, fill: '#fff', stroke: '#000', 'stroke-width': 1 });
  text(x + w / 2, y + h / 2, name, { bold: true, size: 14 });
}

function rel(cx, cy, name, w = 220, h = 74) {
  el('polygon', {
    points: `${cx + .5},${cy - h / 2 + .5} ${cx + w / 2 + .5},${cy + .5} ${cx + .5},${cy + h / 2 + .5} ${cx - w / 2 + .5},${cy + .5}`,
    fill: '#fff', stroke: '#000', 'stroke-width': 1,
  });
  text(cx, cy, name, { bold: true, size: 14 });
}

function wire(pts, arrow) {
  const a = {
    points: pts.map(p => `${p[0] + .5},${p[1] + .5}`).join(' '),
    fill: 'none', stroke: '#000', 'stroke-width': 1,
  };
  if (arrow) a['marker-end'] = 'url(#arr)';
  el('polyline', a, L.lines);
}

function card(x, y, t, size = 14) {
  text(x, y, t, { bold: true, size, anchor: 'start' }, L.labels);
}

// ---------- Modelo lógico ----------
function pkIcon(cx, cy, color = '#111') {
  el('circle', { cx, cy: cy - 4, r: 3.2, fill: color });
  el('rect', { x: cx - 1, y: cy - 2, width: 2, height: 9, fill: color });
  el('rect', { x: cx + 1, y: cy + 3, width: 2.2, height: 1.6, fill: color });
  el('rect', { x: cx + 1, y: cy + 5.4, width: 1.6, height: 1.4, fill: color });
}

function fkIcon(cx, cy) {
  el('rect', { x: cx - 7.5, y: cy - 9.5, width: 15, height: 19, rx: 2, fill: '#eef5ee', stroke: '#6f8f6f', 'stroke-width': 1 });
  pkIcon(cx, cy, '#2f9a2f');
}

function footKey(x, cy) {
  el('circle', { cx: x + 3, cy, r: 3, fill: '#111' });
  el('rect', { x: x + 4, y: cy - 1, width: 11, height: 2, fill: '#d08a00' });
  el('rect', { x: x + 11, y: cy + 1, width: 1.5, height: 2.5, fill: '#d08a00' });
  el('rect', { x: x + 13.5, y: cy + 1, width: 1.5, height: 2, fill: '#d08a00' });
}

function table(x, y, w, name, fields) {
  const HH = 22, RH = 22, PAD = 4, FH = 28;
  const bodyH = fields.length * RH + PAD * 2;
  const h = HH + bodyH + FH;
  const g = el('g', { filter: 'url(#sh)' });
  el('rect', { x: x + .5, y: y + .5, width: w, height: h, rx: 8, fill: '#f6f6f6', stroke: '#a3a3a3' }, g);
  // cabeçalho
  el('path', {
    d: `M${x + .5} ${y + HH + .5} V${y + 8.5} Q${x + .5} ${y + .5} ${x + 8.5} ${y + .5} H${x + w - 7.5} Q${x + w + .5} ${y + .5} ${x + w + .5} ${y + 8.5} V${y + HH + .5} Z`,
    fill: 'url(#hd)', stroke: '#a3a3a3',
  });
  text(x + w / 2, y + HH / 2 + 1, name, { bold: true, size: 11.5 });
  // campos
  el('rect', { x: x + 4.5, y: y + HH + PAD - 1.5, width: w - 8, height: fields.length * RH + 2, fill: '#fbfbfb' });
  fields.forEach((f, i) => {
    const cy = y + HH + PAD + i * RH + RH / 2;
    if (f.k === 'pk') pkIcon(x + 15, cy);
    if (f.k === 'fk') fkIcon(x + 15, cy);
    text(x + 28, cy, f.n, { size: 10, anchor: 'start', fill: '#1a1a1a', ls: .4 });
  });
  // rodapé
  const fy = y + HH + bodyH;
  el('path', {
    d: `M${x + .5} ${fy + .5} H${x + w + .5} V${y + h - 7.5} Q${x + w + .5} ${y + h + .5} ${x + w - 7.5} ${y + h + .5} H${x + 8.5} Q${x + .5} ${y + h + .5} ${x + .5} ${y + h - 7.5} Z`,
    fill: 'url(#ft)', stroke: '#a3a3a3',
  });
  footKey(x + 6, fy + 11);
  el('rect', { x: x + 27.5, y: fy + 3.5, width: w - 34, height: 17, fill: '#fff', stroke: '#d4d4d4' });
  return {
    x, y, w, h, r: x + w, b: y + h, cx: x + w / 2,
    row: i => y + HH + PAD + i * RH + RH / 2,
  };
}

const PK = n => ({ n, k: 'pk' });
const FK = n => ({ n, k: 'fk' });
const F = n => ({ n });
