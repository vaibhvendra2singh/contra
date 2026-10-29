const state = { products: [], cart: new Map() };
const $ = (selector, root = document) => root.querySelector(selector);
const money = value => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value || 0);
const dateTime = value => new Date(value).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' });

async function api(path, options = {}) {
  const response = await fetch(path, { headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }, ...options });
  const body = response.status === 204 ? null : await response.json().catch(() => null);
  if (!response.ok) throw new Error(body?.message || `Request failed (${response.status})`);
  return body;
}
function toast(message) {
  const node = $('#toast'); node.textContent = message; node.classList.add('show');
  clearTimeout(toast.timer); toast.timer = setTimeout(() => node.classList.remove('show'), 3200);
}
function go(page) {
  document.querySelectorAll('.page').forEach(node => node.classList.toggle('active', node.id === `page-${page}`));
  document.querySelectorAll('.nav-link').forEach(node => node.classList.toggle('active', node.dataset.page === page));
  if (page === 'shop') loadCatalog();
  if (page === 'admin') loadAdmin();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}
document.querySelectorAll('.nav-link').forEach(button => button.addEventListener('click', () => go(button.dataset.page)));
$('#cart-toggle').addEventListener('click', () => {
  if (!state.cart.size) return toast('Your cart is empty. Add something from the collection first.');
  openCheckout();
});
$('#refresh-admin').addEventListener('click', loadAdmin);

async function loadCatalog() {
  const grid = $('#product-grid');
  try {
    state.products = await api('/api/products');
    grid.innerHTML = state.products.map((p, index) => {
      const art = ['mug', 'tote', 'tee'][index % 3];
      return `<article class="product-card"><div class="product-image ${art}"><span>${['C', '↗', 'Co'][index % 3]}</span></div><div class="product-info"><h3>${escapeHtml(p.name)}</h3><p>${escapeHtml(p.description || '')}</p><div class="product-meta"><strong>${money(p.price)}</strong><button data-add="${p.id}" ${p.availableQuantity < 1 ? 'disabled' : ''}>${p.availableQuantity < 1 ? 'Sold out' : 'Add to bag +'}</button></div><div class="stock-label">${p.availableQuantity} available · ${p.allocatedQuantity} allocated</div></div></article>`;
    }).join('') || '<div class="empty">The catalog is empty right now.</div>';
    grid.querySelectorAll('[data-add]').forEach(button => button.addEventListener('click', () => addToCart(button.dataset.add)));
  } catch (error) { grid.innerHTML = `<div class="empty">${escapeHtml(error.message)}</div>`; }
}
function addToCart(id) {
  state.cart.set(id, (state.cart.get(id) || 0) + 1);
  $('#cart-count').textContent = [...state.cart.values()].reduce((sum, count) => sum + count, 0);
  toast('Added to your bag.');
}
function openCheckout() {
  const total = [...state.cart].reduce((sum, [id, quantity]) => sum + (state.products.find(p => p.id === id)?.price || 0) * quantity, 0);
  $('#checkout-total').textContent = money(total);
  $('#checkout-dialog').showModal();
}
$('#checkout-form').addEventListener('submit', async event => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  const request = Object.fromEntries(form.entries());
  request.items = [...state.cart].map(([productId, quantity]) => ({ productId, quantity }));
  try {
    const order = await api('/api/orders', { method: 'POST', body: JSON.stringify(request) });
    state.cart.clear(); $('#cart-count').textContent = '0'; $('#checkout-dialog').close(); event.currentTarget.reset();
    toast(order.message);
    $('#track-id').value = order.orderId; go('track'); await showOrder(order.orderId);
    await loadCatalog();
  } catch (error) { toast(error.message); }
});
$('#track-form').addEventListener('submit', async event => {
  event.preventDefault(); await showOrder($('#track-id').value.trim());
});
async function showOrder(id) {
  const target = $('#tracking-result'); target.innerHTML = '<div class="loading">Looking up your order…</div>';
  try {
    const order = await api(`/api/track/${encodeURIComponent(id)}`);
    target.innerHTML = renderOrder(order, true);
    const cancel = $('[data-cancel]', target);
    if (cancel) cancel.addEventListener('click', async () => { try { await api(`/api/orders/${order.id}/cancel`, { method: 'POST' }); await showOrder(order.id); } catch (e) { toast(e.message); } });
    const ret = $('[data-return]', target);
    if (ret) ret.addEventListener('click', async () => { try { await api(`/api/orders/${order.id}/returns`, { method: 'POST' }); await showOrder(order.id); } catch (e) { toast(e.message); } });
    const retry = $('[data-retry]', target);
    if (retry) retry.addEventListener('click', async () => {
      const token = prompt('Payment token (leave blank for success; use fail-demo to simulate another decline):', '');
      if (token === null) return;
      try { await api(`/api/orders/${order.id}/retry-payment`, { method: 'POST', body: JSON.stringify({ paymentToken: token }) }); await showOrder(order.id); } catch (e) { toast(e.message); }
    });
  } catch (error) { target.innerHTML = `<div class="empty">${escapeHtml(error.message)}</div>`; }
}
function renderOrder(order, customerView = false) {
  const actions = [];
  if (customerView && !['SHIPPED', 'DELIVERED', 'CANCELLED', 'RETURN_REQUESTED', 'RETURNED', 'REFUNDED'].includes(order.status)) actions.push(`<button data-cancel>Cancel order</button>`);
  if (customerView && order.status === 'PAYMENT_FAILED') actions.push('<button data-retry>Retry payment</button>');
  if (customerView && order.status === 'DELIVERED') actions.push('<button data-return>Request a return</button>');
  return `<article class="order-card"><div class="order-header"><div><h2>Order ${escapeHtml(order.id.slice(0, 8).toUpperCase())}</h2><div class="muted">${escapeHtml(order.customerName)} · ${money(order.total)}</div></div><span class="status-pill">${pretty(order.status)}</span></div>${order.trackingNumber ? `<p class="stock-label">TRACKING · ${escapeHtml(order.trackingNumber)}</p>` : ''}<div class="timeline">${order.timeline.map(item => `<div class="timeline-item"><b>${pretty(item.toStatus)}</b><small>${escapeHtml(item.actor)} · ${dateTime(item.occurredAt)}</small></div>`).join('')}</div>${actions.length ? `<div class="order-actions">${actions.join('')}</div>` : ''}</article>`;
}
async function loadAdmin() {
  const root = $('#admin-content'); root.innerHTML = '<div class="loading">Loading operations…</div>';
  try {
    const [dashboard, orders, flags, notifications, jiraPanel, readiness] = await Promise.all([
      api('/api/admin/dashboard'), api('/api/admin/orders'), api('/api/admin/flags'), api('/api/admin/notifications'),
      api('/api/admin/jira/dashboard'), api('/api/admin/jira/readiness?version=v1.0')
    ]);
    const statuses = ['PAYMENT_PENDING', 'PAYMENT_FAILED', 'INVENTORY_ALLOCATED', 'FULFILLING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];
    root.innerHTML = `<div class="dashboard-grid"><div class="metric-card"><span>Total orders</span><strong>${orders.length}</strong></div><div class="metric-card"><span>Need attention</span><strong>${dashboard.stuckOrders}</strong></div><div class="metric-card"><span>Failed payments</span><strong>${dashboard.failedPayments}</strong></div><div class="metric-card"><span>In fulfillment</span><strong>${dashboard.fulfillmentQueue.length}</strong></div></div>
      <section class="admin-section"><div class="admin-section-heading"><h2>Orders by status</h2><span>LIVE ORDER FLOW</span></div><div class="table-wrap"><table><thead><tr><th>Status</th><th>Orders</th></tr></thead><tbody>${statuses.map(status => `<tr><td>${pretty(status)}</td><td>${dashboard.ordersByStatus[status] || 0}</td></tr>`).join('')}</tbody></table></div></section>
      <section class="admin-section"><div class="admin-section-heading"><h2>Fulfillment queue</h2><span>ALLOCATED → SHIPPED</span></div>${dashboard.fulfillmentQueue.length ? dashboard.fulfillmentQueue.map(order => renderAdminOrder(order)).join('') : '<div class="empty">No orders are waiting in the fulfillment queue.</div>'}</section>
      <section class="admin-section"><div class="admin-section-heading"><h2>Feature flags</h2><span>DEMO CONTROL</span></div><div class="feature-flags">${flags.map(flag => `<div class="flag-row"><div><b>${escapeHtml(flag.key)}</b><small>${escapeHtml(flag.description)} · Jira ${escapeHtml(flag.jiraIssueKey || 'unlinked')} · QA ${escapeHtml(flag.qaIssueKey || 'unlinked')}</small></div><button class="switch ${flag.enabled ? 'on' : ''}" aria-label="Toggle ${escapeHtml(flag.key)}" data-flag="${escapeHtml(flag.key)}" data-enabled="${flag.enabled}"></button></div>`).join('')}</div></section>
      <section class="admin-section"><div class="admin-section-heading"><h2>Jira delivery</h2><span>${jiraPanel.jiraAvailable ? 'CONNECTED' : 'OFFLINE'}</span></div>${jiraPanel.jiraAvailable ? `<p>${escapeHtml(jiraPanel.sprintName)} · ${jiraPanel.donePoints}/${jiraPanel.totalPoints} points done · ${escapeHtml(jiraPanel.sprintGoal)}</p><p><a href="${escapeHtml(jiraPanel.boardUrl)}" target="_blank" rel="noreferrer">Open Jira board ↗</a></p><div class="notice-list">${jiraPanel.recentIssues.map(i => `<div class="notice-row"><span><a href="${escapeHtml(i.url)}" target="_blank" rel="noreferrer">${escapeHtml(i.key)}</a> · ${escapeHtml(i.summary)}</span><small>${escapeHtml(i.status)}</small></div>`).join('') || '<div class="empty">No recent work items.</div>'}</div>` : '<div class="empty">Jira is not configured or cannot be reached. Order operations continue normally.</div>'}</section>
      <section class="admin-section"><div class="admin-section-heading"><h2>Release readiness · v1.0</h2><span class="status-pill">${readiness.ready ? 'READY' : 'NOT READY'}</span></div><p>${readiness.donePoints}/${readiness.totalPoints} story points complete</p>${readiness.reasons.length ? `<ul>${readiness.reasons.map(reason => `<li>${escapeHtml(reason)}</li>`).join('')}</ul>` : '<p>All Jira readiness checks passed.</p>'}<div class="notice-list">${Object.entries(readiness.issuesByStatus).map(([status, issues]) => `<div class="notice-row"><span><b>${escapeHtml(status)}</b> · ${issues.map(i => `<a href="${escapeHtml(i.url)}" target="_blank" rel="noreferrer">${escapeHtml(i.key)}</a>`).join(', ')}</span></div>`).join('')}${readiness.blockers.map(i => `<div class="notice-row"><span>Blocker · ${escapeHtml(i.key)} ${escapeHtml(i.summary)}</span></div>`).join('')}</div></section>
      <section class="admin-section"><div class="admin-section-heading"><h2>Recent orders</h2><span>${orders.length} TOTAL</span></div>${orders.length ? orders.map(order => renderAdminOrder(order)).join('') : '<div class="empty">Orders placed in the shop will appear here.</div>'}</section>
      <section class="admin-section"><div class="admin-section-heading"><h2>Notification log</h2><span>SIMULATED EMAIL + SMS</span></div><div class="notice-list">${notifications.slice(0, 8).map(n => `<div class="notice-row"><span><b>${escapeHtml(n.subject)}</b> · ${escapeHtml(n.recipient)}</span><small>${dateTime(n.createdAt)}</small></div>`).join('') || '<div class="empty">No notifications logged yet.</div>'}</div></section>`;
    root.querySelectorAll('[data-flag]').forEach(button => button.addEventListener('click', async () => {
      try { await api(`/api/admin/flags/${encodeURIComponent(button.dataset.flag)}`, { method: 'PUT', body: JSON.stringify({ enabled: button.dataset.enabled !== 'true' }) }); await loadAdmin(); }
      catch (error) { toast(error.message); }
    }));
    root.querySelectorAll('[data-action]').forEach(button => button.addEventListener('click', () => adminAction(button)));
  } catch (error) { root.innerHTML = `<div class="empty">${escapeHtml(error.message)}</div>`; }
}
function renderAdminOrder(order) {
  let action = '';
  if (order.status === 'INVENTORY_ALLOCATED') action = `<button data-action="fulfill" data-id="${order.id}">Start fulfillment</button>`;
  if (order.status === 'FULFILLING') action = `<button data-action="ship" data-id="${order.id}">Add tracking & ship</button>`;
  if (order.status === 'SHIPPED') action = `<button data-action="deliver" data-id="${order.id}">Mark delivered</button>`;
  if (order.status === 'RETURN_REQUESTED') action = `<button data-action="returned" data-id="${order.id}">Receive return</button>`;
  if (order.status === 'RETURNED') action = `<button data-action="refund" data-id="${order.id}">Issue refund</button>`;
  return `<article class="order-card"><div class="order-header"><div><h2>${escapeHtml(order.id.slice(0, 8).toUpperCase())} · ${money(order.total)}</h2><div class="muted">${escapeHtml(order.customerName)} · ${escapeHtml(order.customerEmail)}</div></div><span class="status-pill">${pretty(order.status)}</span></div><p class="stock-label">${order.items.map(item => `${item.quantity} × ${escapeHtml(item.productName)}`).join(' · ')}</p>${action ? `<div class="order-actions">${action}</div>` : ''}</article>`;
}
async function adminAction(button) {
  const id = button.dataset.id, action = button.dataset.action;
  try {
    if (action === 'ship') {
      const trackingNumber = prompt('Tracking number:', `CT-${Math.random().toString(36).slice(2, 9).toUpperCase()}`);
      if (!trackingNumber) return;
      await api(`/api/admin/orders/${id}/ship`, { method: 'POST', body: JSON.stringify({ trackingNumber }) });
    } else if (action === 'fulfill') await api(`/api/admin/orders/${id}/fulfill`, { method: 'POST' });
    else if (action === 'deliver') await api(`/api/admin/orders/${id}/deliver`, { method: 'POST' });
    else if (action === 'returned') await api(`/api/admin/orders/${id}/returned`, { method: 'POST' });
    else if (action === 'refund') await api(`/api/orders/${id}/refund`, { method: 'POST' });
    toast('Order updated.'); await loadAdmin();
  } catch (error) { toast(error.message); }
}
function pretty(value) { return (value || '').toLowerCase().replaceAll('_', ' ').replace(/\b\w/g, char => char.toUpperCase()); }
function escapeHtml(value) { return String(value ?? '').replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]); }
loadCatalog();
