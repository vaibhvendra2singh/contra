const state = { products: [], cart: new Map() };
const $ = (selector, root = document) => root.querySelector(selector);
const money = value => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(value || 0);
const dateTime = value => new Date(value).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' });

function loadCartFromStorage() {
  try {
    const raw = localStorage.getItem('contra_cart');
    if (raw) {
      const items = JSON.parse(raw);
      if (Array.isArray(items)) {
        state.cart = new Map(items);
      }
    }
  } catch (e) {
    console.warn('Could not read cart from localStorage', e);
  }
  updateCartCount();
}

function saveCartToStorage() {
  try {
    localStorage.setItem('contra_cart', JSON.stringify([...state.cart]));
  } catch (e) {
    console.warn('Could not save cart to localStorage', e);
  }
  updateCartCount();
}

function updateCartCount() {
  const count = [...state.cart.values()].reduce((sum, count) => sum + count, 0);
  const badge = $('#cart-count');
  if (badge) badge.textContent = count;
  const topbarClear = $('#topbar-clear-cart');
  if (topbarClear) {
    topbarClear.style.display = count > 0 ? 'inline-flex' : 'none';
  }
}

function clearCart() {
  if (!state.cart.size) return;
  state.cart.clear();
  saveCartToStorage();
  const dialog = $('#checkout-dialog');
  if (dialog && dialog.open) dialog.close();
  toast('Cart cleared.');
}

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

function go(page, updateHash = true) {
  const validPages = ['home', 'shop', 'track', 'admin', 'jira'];
  if (!validPages.includes(page)) page = 'home';
  document.querySelectorAll('.page').forEach(node => node.classList.toggle('active', node.id === `page-${page}`));
  document.querySelectorAll('.nav-link').forEach(node => node.classList.toggle('active', node.dataset.page === page));
  if (updateHash) {
    location.hash = page;
    try { localStorage.setItem('contra_last_page', page); } catch (_) {}
  }
  if (page === 'shop') loadCatalog();
  if (page === 'admin') loadAdmin();
  if (page === 'jira') loadJira();
  if (page === 'track') {
    const lastTrackId = localStorage.getItem('contra_last_track_id');
    if (lastTrackId && $('#track-id')) {
      $('#track-id').value = lastTrackId;
      if (!$('#tracking-result').hasChildNodes() || $('#tracking-result').textContent.trim() === '') {
        showOrder(lastTrackId);
      }
    }
  }
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

document.querySelectorAll('.nav-link').forEach(button => button.addEventListener('click', () => go(button.dataset.page)));
$('#cart-toggle').addEventListener('click', () => {
  if (!state.cart.size) return toast('Your cart is empty. Add something from the collection first.');
  openCheckout();
});
$('#topbar-clear-cart')?.addEventListener('click', clearCart);
$('#dialog-clear-cart')?.addEventListener('click', clearCart);
$('#refresh-admin')?.addEventListener('click', loadAdmin);
$('#refresh-jira')?.addEventListener('click', loadJira);

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
  saveCartToStorage();
  toast('Added to your bag.');
}

async function openCheckout() {
  if (!state.products.length) {
    try { state.products = await api('/api/products'); } catch (_) {}
  }
  const total = [...state.cart].reduce((sum, [id, quantity]) => sum + (state.products.find(p => p.id === id)?.price || 0) * quantity, 0);
  $('#checkout-total').textContent = money(total);

  const itemsContainer = $('#checkout-items-list');
  if (itemsContainer) {
    itemsContainer.innerHTML = [...state.cart].map(([id, quantity]) => {
      const p = state.products.find(prod => prod.id === id);
      return `<div class="checkout-item-row">
        <span><b>${quantity}×</b> ${escapeHtml(p ? p.name : id)}</span>
        <span>${money((p?.price || 0) * quantity)}</span>
      </div>`;
    }).join('') || '<div class="muted">No items in bag</div>';
  }

  try {
    const savedName = localStorage.getItem('contra_customer_name');
    const savedEmail = localStorage.getItem('contra_customer_email');
    if (savedName && $('[name="customerName"]')) $('[name="customerName"]').value = savedName;
    if (savedEmail && $('[name="customerEmail"]')) $('[name="customerEmail"]').value = savedEmail;
  } catch (_) {}
  $('#checkout-dialog').showModal();
}

$('#checkout-form').addEventListener('submit', async event => {
  event.preventDefault();
  const formElement = event.currentTarget || $('#checkout-form');
  const form = new FormData(formElement);
  const request = Object.fromEntries(form.entries());
  request.items = [...state.cart].map(([productId, quantity]) => ({ productId, quantity }));
  try {
    const order = await api('/api/orders', { method: 'POST', body: JSON.stringify(request) });
    try {
      if (request.customerName) localStorage.setItem('contra_customer_name', request.customerName);
      if (request.customerEmail) localStorage.setItem('contra_customer_email', request.customerEmail);
    } catch (_) {}
    state.cart.clear();
    saveCartToStorage();
    $('#checkout-dialog')?.close();
    formElement?.reset();
    toast(order.message || 'Order placed successfully.');
    if (order.orderId) {
      if ($('#track-id')) $('#track-id').value = order.orderId;
      go('track');
      await showOrder(order.orderId);
    }
    await loadCatalog();
  } catch (error) { toast(error.message); }
});

$('#track-form').addEventListener('submit', async event => {
  event.preventDefault(); await showOrder($('#track-id').value.trim());
});

async function showOrder(id) {
  if (!id) return;
  try { localStorage.setItem('contra_last_track_id', id); } catch (_) {}
  const target = $('#tracking-result'); target.innerHTML = '<div class="loading">Looking up your order…</div>';
  try {
    const order = await api(`/api/track/${encodeURIComponent(id)}`);
    target.innerHTML = renderOrder(order, true);
    const cancel = $('[data-cancel]', target);
    if (cancel) cancel.addEventListener('click', async () => {
      if (!confirm('Are you sure you want to cancel this order?')) return;
      try {
        await api(`/api/orders/${order.id}/cancel`, { method: 'POST' });
        toast('Order cancelled successfully.');
        await showOrder(order.id);
        await loadCatalog();
      } catch (e) { toast(e.message); }
    });
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
  if (customerView && ['CREATED', 'PAYMENT_PENDING', 'PAID', 'INVENTORY_ALLOCATED', 'FULFILLING'].includes(order.status)) {
    actions.push(`<button class="button button-signal" data-cancel>Cancel order</button>`);
  }
  if (customerView && order.status === 'PAYMENT_FAILED') actions.push('<button class="button button-emphasis" data-retry>Retry payment</button>');
  if (customerView && order.status === 'DELIVERED') actions.push('<button class="button button-light" data-return>Request a return</button>');
  return `<article class="order-card"><div class="order-header"><div><h2>Order ${escapeHtml(order.id.slice(0, 8).toUpperCase())}</h2><div class="muted">${escapeHtml(order.customerName)} · ${money(order.total)}</div></div><span class="status-pill status-${order.status.toLowerCase()}">${pretty(order.status)}</span></div>${order.trackingNumber ? `<p class="stock-label">TRACKING · ${escapeHtml(order.trackingNumber)}</p>` : ''}<div class="timeline">${order.timeline.map(item => `<div class="timeline-item"><b>${pretty(item.toStatus)}</b><small>${escapeHtml(item.actor)} · ${dateTime(item.occurredAt)}</small></div>`).join('')}</div>${actions.length ? `<div class="order-actions">${actions.join(' ')}</div>` : ''}</article>`;
}
async function loadAdmin() {
  const root = $('#admin-content');
  const statuses = ['PAYMENT_PENDING', 'PAYMENT_FAILED', 'INVENTORY_ALLOCATED', 'FULFILLING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  try {
    const [dashboard, orders, flags, notifications] = await Promise.all([
      api('/api/admin/dashboard'),
      api('/api/admin/orders'),
      api('/api/admin/flags'),
      api('/api/admin/notifications')
    ]);

    root.innerHTML = `
      <div class="dashboard-grid">
        <div class="metric-card"><span>Total orders</span><strong>${orders.length}</strong></div>
        <div class="metric-card"><span>Need attention</span><strong>${dashboard.stuckOrders}</strong></div>
        <div class="metric-card"><span>Failed payments</span><strong>${dashboard.failedPayments}</strong></div>
        <div class="metric-card"><span>In fulfillment</span><strong>${dashboard.fulfillmentQueue.length}</strong></div>
      </div>
      
      <div class="admin-two-col">
        <section class="admin-card">
          <div class="admin-section-heading"><h2>Orders by status</h2><span>LIVE ORDER FLOW</span></div>
          <div class="table-wrap">
            <table>
              <thead><tr><th>Status</th><th>Count</th></tr></thead>
              <tbody>${statuses.map(status => `<tr><td><span class="status-pill status-${status.toLowerCase()}">${pretty(status)}</span></td><td><b>${dashboard.ordersByStatus[status] || 0}</b></td></tr>`).join('')}</tbody>
            </table>
          </div>
        </section>

        <section class="admin-card">
          <div class="admin-section-heading"><h2>Fulfillment queue</h2><span>ALLOCATED → SHIPPED</span></div>
          ${dashboard.fulfillmentQueue.length ? dashboard.fulfillmentQueue.map(order => renderAdminOrder(order)).join('') : '<div class="empty">No orders are waiting in the fulfillment queue.</div>'}
        </section>
      </div>

      <section class="admin-section">
        <div class="admin-section-heading"><h2>Feature flags</h2><span>DEMO RELEASE GATING</span></div>
        <div class="feature-flags">${flags.map(flag => `
          <div class="flag-row">
            <div>
              <b>${escapeHtml(flag.key)}</b>
              <small>${escapeHtml(flag.description)} · Jira <code>${escapeHtml(flag.jiraIssueKey || 'unlinked')}</code> · QA <code>${escapeHtml(flag.qaIssueKey || 'unlinked')}</code></small>
            </div>
            <button class="switch ${flag.enabled ? 'on' : ''}" aria-label="Toggle ${escapeHtml(flag.key)}" data-flag="${escapeHtml(flag.key)}" data-enabled="${flag.enabled}"></button>
          </div>
        `).join('')}</div>
      </section>

      <section class="admin-section">
        <div class="admin-section-heading"><h2>Recent orders</h2><span>${orders.length} TOTAL PLACED</span></div>
        ${orders.length ? orders.map(order => renderAdminOrder(order)).join('') : '<div class="empty">Orders placed in the shop will appear here.</div>'}
      </section>
    `;

    root.querySelectorAll('[data-flag]').forEach(button => button.addEventListener('click', async () => {
      try { 
        await api(`/api/admin/flags/${encodeURIComponent(button.dataset.flag)}`, { method: 'PUT', body: JSON.stringify({ enabled: button.dataset.enabled !== 'true' }) }); 
        await loadAdmin(); 
      } catch (error) { toast(error.message); }
    }));
    root.querySelectorAll('[data-action]').forEach(button => button.addEventListener('click', () => adminAction(button)));

  } catch (error) { 
    root.innerHTML = `<div class="empty">${escapeHtml(error.message)}</div>`; 
  }
}

async function loadJira() {
  const root = $('#jira-content');
  root.innerHTML = '<div class="loading">Connecting to Jira Cloud (OM2)…</div>';

  try {
    const [jiraPanel, readiness] = await Promise.all([
      api('/api/admin/jira/dashboard').catch(() => ({ jiraAvailable: false })),
      api('/api/admin/jira/readiness?version=v1.0').catch(() => ({ ready: false, reasons: ['Jira unreachable'] }))
    ]);

    root.innerHTML = `
      <div class="jira-hero-banner ${jiraPanel.jiraAvailable ? 'connected' : 'offline'}">
        <div class="jira-hero-left">
          <div class="jira-status-pill">${jiraPanel.jiraAvailable ? '● JIRA CLOUD CONNECTED' : '○ JIRA OFFLINE'}</div>
          <h2>${escapeHtml(jiraPanel.sprintName || 'OM2 Agile Delivery')}</h2>
          <p class="jira-goal">${escapeHtml(jiraPanel.sprintGoal || 'Scrum Sprints · Epics · Quality Gates')}</p>
        </div>
        <div class="jira-hero-right">
          <div class="jira-stat-box">
            <span>SPRINT POINTS DONE</span>
            <strong>${jiraPanel.donePoints || 0} / ${jiraPanel.totalPoints || 60}</strong>
          </div>
          ${jiraPanel.boardUrl ? `<a href="${escapeHtml(jiraPanel.boardUrl)}" target="_blank" rel="noreferrer" class="button button-light">Open Jira Board ↗</a>` : ''}
        </div>
      </div>

      <div class="jira-sections-grid">
        <section class="admin-card">
          <div class="admin-section-heading">
            <h2>Release readiness</h2>
            <div class="version-select-wrap">
              <label>Release Version:
                <select id="jira-version" class="pill-select">
                  <option value="v1.0" ${readiness.version === 'v1.0' ? 'selected' : ''}>v1.0 (Sprint 3 · Current Release)</option>
                  <option value="v0.2" ${readiness.version === 'v0.2' ? 'selected' : ''}>v0.2 (Sprint 2 · Fulfillment)</option>
                  <option value="v0.1" ${readiness.version === 'v0.1' ? 'selected' : ''}>v0.1 (Sprint 1 · Order Core)</option>
                </select>
              </label>
            </div>
          </div>
          <div class="readiness-banner ${readiness.ready ? 'ready' : 'unready'}">
            <span class="status-pill">${readiness.ready ? 'READY TO SHIP' : 'CHECKS PENDING'}</span>
            <strong>${readiness.donePoints || 0} / ${readiness.totalPoints || 0} Story Points Complete</strong>
          </div>
          <div id="readiness-content" style="margin-top:16px;">${renderReadiness(readiness)}</div>
        </section>

        <section class="admin-card">
          <div class="admin-section-heading">
            <h2>Scrum work items</h2>
            <span>RECENT JIRA STORIES</span>
          </div>
          <div class="notice-list">
            ${(jiraPanel.recentIssues && jiraPanel.recentIssues.length) ? jiraPanel.recentIssues.map(i => `
              <div class="notice-row">
                <div>
                  <a href="${escapeHtml(i.url || '#')}" target="_blank" rel="noreferrer" class="jira-issue-link"><b>${escapeHtml(i.key)}</b></a>
                  <span> · ${escapeHtml(i.summary)}</span>
                </div>
                <span class="status-pill status-${(i.status || '').toLowerCase().replace(/\s+/g, '-')}">${escapeHtml(i.status)}</span>
              </div>
            `).join('') : '<div class="empty">No recent work items found in active sprint.</div>'}
          </div>
        </section>
      </div>
    `;

    $('#jira-version', root)?.addEventListener('change', async event => {
      try {
        const result = await api(`/api/admin/jira/readiness?version=${encodeURIComponent(event.target.value)}`);
        const banner = root.querySelector('.readiness-banner');
        if (banner) {
          banner.className = `readiness-banner ${result.ready ? 'ready' : 'unready'}`;
          banner.innerHTML = `<span class="status-pill">${result.ready ? 'READY TO SHIP' : 'CHECKS PENDING'}</span><strong>${result.donePoints || 0} / ${result.totalPoints || 0} Story Points Complete</strong>`;
        }
        $('#readiness-content', root).innerHTML = renderReadiness(result);
      } catch (error) { toast(error.message); }
    });

  } catch (error) {
    root.innerHTML = `<div class="empty">${escapeHtml(error.message)}</div>`;
  }
}
function renderReadiness(readiness) {
  return `<p>${readiness.donePoints}/${readiness.totalPoints} story points complete</p>${readiness.reasons.length ? `<ul>${readiness.reasons.map(reason => `<li>${escapeHtml(reason)}</li>`).join('')}</ul>` : '<p>All Jira readiness checks passed.</p>'}<div class="notice-list">${Object.entries(readiness.issuesByStatus).map(([status, issues]) => `<div class="notice-row"><span><b>${escapeHtml(status)}</b> · ${issues.map(i => `<a href="${escapeHtml(i.url)}" target="_blank" rel="noreferrer">${escapeHtml(i.key)}</a>`).join(', ')}</span></div>`).join('')}${readiness.blockers.map(i => `<div class="notice-row"><span>Blocker · ${escapeHtml(i.key)} ${escapeHtml(i.summary)}</span></div>`).join('')}</div>`;
}
function renderAdminOrder(order) {
  const actions = [];
  if (order.status === 'INVENTORY_ALLOCATED') actions.push(`<button class="button button-dark" data-action="fulfill" data-id="${order.id}">Start fulfillment</button>`);
  if (order.status === 'FULFILLING') actions.push(`<button class="button button-dark" data-action="ship" data-id="${order.id}">Add tracking & ship</button>`);
  if (['PAYMENT_PENDING', 'PAYMENT_FAILED', 'INVENTORY_ALLOCATED', 'FULFILLING'].includes(order.status)) {
    actions.push(`<button class="button button-signal" data-action="cancel" data-id="${order.id}">Cancel order</button>`);
  }
  if (order.status === 'SHIPPED') actions.push(`<button class="button button-emphasis" data-action="deliver" data-id="${order.id}">Mark delivered</button>`);
  if (order.status === 'RETURN_REQUESTED') actions.push(`<button class="button button-dark" data-action="returned" data-id="${order.id}">Receive return</button>`);
  if (order.status === 'RETURNED') actions.push(`<button class="button button-emphasis" data-action="refund" data-id="${order.id}">Issue refund</button>`);
  return `<article class="order-card"><div class="order-header"><div><h2>${escapeHtml(order.id.slice(0, 8).toUpperCase())} · ${money(order.total)}</h2><div class="muted">Order ID: <code>${escapeHtml(order.id)}</code></div><div class="muted">${escapeHtml(order.customerName)} · ${escapeHtml(order.customerEmail)}</div></div><span class="status-pill status-${order.status.toLowerCase()}">${pretty(order.status)}</span></div><p class="stock-label">${order.items.map(item => `${item.quantity} × ${escapeHtml(item.productName)}`).join(' · ')}</p>${actions.length ? `<div class="order-actions">${actions.join(' ')}</div>` : ''}</article>`;
}
async function adminAction(button) {
  const id = button.dataset.id, action = button.dataset.action;
  try {
    if (action === 'cancel') {
      if (!confirm('Are you sure you want to cancel this order? Allocated inventory will be returned to stock.')) return;
      await api(`/api/orders/${id}/cancel`, { method: 'POST' });
    } else if (action === 'ship') {
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

// Restore cart from storage
loadCartFromStorage();

// Listen for hash changes
window.addEventListener('hashchange', () => {
  const hash = location.hash.replace('#', '').trim();
  if (hash && ['home', 'shop', 'track', 'admin', 'jira'].includes(hash)) {
    go(hash, false);
  }
});

// Restore last viewed page or default to home
const initialPage = location.hash.replace('#', '').trim() || localStorage.getItem('contra_last_page') || 'home';
go(['home', 'shop', 'track', 'admin', 'jira'].includes(initialPage) ? initialPage : 'home', false);

