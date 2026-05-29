const defaultData = {
  dashboardKpis: [
    { label: "Total Inflow", value: "CAD 18.42M", trend: "+12.4%", tone: "positive" },
    { label: "Total Outflow", value: "CAD 15.08M", trend: "+8.1%", tone: "warning" },
    { label: "Net Movement", value: "CAD 3.34M", trend: "+5.7%", tone: "positive" },
    { label: "Transactions", value: "48,219", trend: "+18.2%", tone: "positive" },
    { label: "Failed / Reversed", value: "164", trend: "-2.3%", tone: "negative" },
    { label: "Pending Approvals", value: "6", trend: "Needs review", tone: "warning" }
  ],
  flowTrend: [
    { label: "12:00", inflow: 88, outflow: 64 },
    { label: "12:30", inflow: 72, outflow: 52 },
    { label: "13:00", inflow: 104, outflow: 81 },
    { label: "13:30", inflow: 90, outflow: 60 },
    { label: "14:00", inflow: 112, outflow: 94 },
    { label: "14:30", inflow: 96, outflow: 68 },
    { label: "15:00", inflow: 84, outflow: 58 },
    { label: "15:30", inflow: 120, outflow: 83 }
  ],
  topProducts: [
    { productCode: "DEMO_WALLET", totalValue: 920000, transactionCount: 92 },
    { productCode: "DEMO_LEDGER", totalValue: 740000, transactionCount: 74 },
    { productCode: "DEMO_PAY", totalValue: 560000, transactionCount: 56 },
    { productCode: "DEMO_INVEST", totalValue: 410000, transactionCount: 41 }
  ],
  services: [
    { name: "API Gateway", status: "Healthy", tone: "success" },
    { name: "General Ledger", status: "Healthy", tone: "success" },
    { name: "Session Manager", status: "Degraded", tone: "warning" },
    { name: "Approvals Engine", status: "Queue Busy", tone: "warning" },
    { name: "Profiling", status: "Healthy", tone: "success" }
  ],
  transactions: [
    {
      requestRef: "BATCH-20260423-001",
      productCode: "DEMO_WALLET",
      accountNumber: "DEMO-ACCT-001",
      counterparty: "DEMO-ACCT-002",
      transType: "Batch Post",
      amount: "CAD 17,070.00",
      statusCode: 200,
      status: "Successful",
      tone: "success",
      createdAt: "2026-04-23T15:28:00",
      details: {
        direction: "Mixed",
        groupRef: "BATCH-20260423-001",
        operator: "demo-admin@example.com",
        narration: "Split transfer across two destination accounts"
      }
    },
    {
      requestRef: "TRF-20260423-118",
      productCode: "DEMO_LEDGER",
      accountNumber: "DEMO-ACCT-004",
      counterparty: "DEMO-CONTACT-001",
      transType: "Settlement",
      amount: "CAD 2,460.00",
      statusCode: 102,
      status: "Pending",
      tone: "warning",
      createdAt: "2026-04-23T15:21:00",
      details: {
        direction: "Debit/Credit",
        groupRef: "FXP-SETTLE-20260423-118",
        operator: "settlement.bot",
        narration: "Buyer debit and seller credit settlement"
      }
    },
    {
      requestRef: "CR-20260423-992",
      productCode: "DEMO_PAY",
      accountNumber: "DEMO-ACCT-002",
      counterparty: "DEMO-ACCT-004",
      transType: "Credit",
      amount: "CAD 10,000.00",
      statusCode: 200,
      status: "Successful",
      tone: "success",
      createdAt: "2026-04-23T15:16:00",
      details: {
        direction: "Credit",
        groupRef: "CR-20260423-992",
        operator: "api-gateway",
        narration: "Wallet credit from staged clearing flow"
      }
    },
    {
      requestRef: "DB-20260423-551",
      productCode: "DEMO_WALLET",
      accountNumber: "DEMO-ACCT-003",
      counterparty: "DEMO-ACCT-004",
      transType: "Debit",
      amount: "CAD 7,070.00",
      statusCode: 409,
      status: "Reversed",
      tone: "danger",
      createdAt: "2026-04-23T15:04:00",
      details: {
        direction: "Debit",
        groupRef: "DB-20260423-551",
        operator: "demo-reviewer@example.com",
        narration: "Automatic compensation after failed fulfilment"
      }
    }
  ],
  approvals: [
    {
      title: "Manual Reversal Approval",
      product: "DEMO_WALLET",
      ref: "APR-20260423-17",
      waiting: "Checker pending",
      amount: "CAD 7,070.00"
    },
    {
      title: "Limit Override Review",
      product: "DEMO_PAY",
      ref: "APR-20260423-18",
      waiting: "Ops lead pending",
      amount: "CAD 25,000.00"
    },
    {
      title: "Operator Role Upgrade",
      product: "DEMO_LEDGER",
      ref: "APR-20260423-19",
      waiting: "Admin pending",
      amount: "Access change"
    }
  ],
  clients: [
    { productName: "Demo Wallet", emailAddress: "demo-wallet@example.com", productCode: "DEMO_WALLET", enabled: "1", transactionCount: 0, totalInflow: "0", totalOutflow: "0", netMovement: "0", failedCount: 0 },
    { productName: "Demo Ledger", emailAddress: "demo-ledger@example.com", productCode: "DEMO_LEDGER", enabled: "1", transactionCount: 0, totalInflow: "0", totalOutflow: "0", netMovement: "0", failedCount: 0 }
  ],
  operators: [
    { name: "Demo Admin", role: "Demo Admin", scope: "ALL", status: "Active" },
    { name: "Demo Checker", role: "Checker", scope: "DEMO_WALLET", status: "Active" },
    { name: "Demo Analyst", role: "Ops Analyst", scope: "DEMO_PAY", status: "On Duty" },
    { name: "Demo Reviewer", role: "Client Backoffice", scope: "DEMO_LEDGER", status: "Limited" }
  ],
  roles: [
    {
      name: "Demo Admin",
      desc: "Full platform visibility, cross-product operations, and configuration control.",
      perms: "Approvals, operators, transactions, reversals, health"
    },
    {
      name: "Checker",
      desc: "Review and approve queued operations within assigned product scope.",
      perms: "Approvals, transaction lookup, limited reversal review"
    },
    {
      name: "Ops Analyst",
      desc: "Monitor inflow, outflow, failures, retries, and service pressure.",
      perms: "Dashboard, transactions, health"
    },
    {
      name: "Client Backoffice",
      desc: "Scoped visibility to one productCode and its operational records.",
      perms: "Transactions, approvals, limited operator profile"
    }
  ],
  reversals: [
    { ref: "REV-20260423-12", reason: "Failed fulfilment compensation", product: "DEMO_WALLET", status: "Recovered" },
    { ref: "REV-20260423-13", reason: "Manual operator reversal", product: "DEMO_PAY", status: "Awaiting checker" },
    { ref: "REV-20260423-14", reason: "Retry-safe unwind", product: "DEMO_LEDGER", status: "Recovered" }
  ],
  approvalStats: [
    { label: "Queue Depth", value: "6 waiting" },
    { label: "Average Decision Time", value: "11 mins" },
    { label: "High-Risk Requests", value: "2 flagged" },
    { label: "Product Under Pressure", value: "DEMO_WALLET" }
  ],
  healthStats: [
    { label: "Pending Outbox Items", value: "14" },
    { label: "Retry Backlog", value: "3" },
    { label: "Degraded Services", value: "1" },
    { label: "Security Events Today", value: "4" }
  ]
};

const state = {
  session: {
    email: "",
    scope: "internal",
    productCode: "ALL",
    role: "",
    token: "",
    permissions: ""
  },
  config: {
    profilingBaseUrl: localStorage.getItem("smartcore.portal.profilingBaseUrl") || "http://localhost:8080/api/profilings",
    ledgerBaseUrl: localStorage.getItem("smartcore.portal.ledgerBaseUrl") || ""
  },
  activeView: "overview",
  activeWindow: "24h",
  filters: {
    product: "ALL",
    status: "ALL",
    type: "ALL",
    search: ""
  },
  data: JSON.parse(JSON.stringify(defaultData))
};

const loginScreen = document.getElementById("login-screen");
const portalShell = document.getElementById("portal-shell");
const loginForm = document.getElementById("login-form");
const loginError = document.getElementById("login-error");
const statusBanner = document.getElementById("status-banner");
const navButtons = Array.from(document.querySelectorAll(".nav-item"));
const switchButtons = Array.from(document.querySelectorAll("[data-switch-view]"));
const views = Array.from(document.querySelectorAll(".view"));
const kpiGrid = document.getElementById("kpi-grid");
const flowChart = document.getElementById("flow-chart");
const productChart = document.getElementById("product-chart");
const healthList = document.getElementById("health-list");
const healthServiceList = document.getElementById("health-service-list");
const transactionTable = document.getElementById("transaction-table");
const transactionViewTable = document.getElementById("transaction-view-table");
const approvalList = document.getElementById("approval-list");
const approvalQueue = document.getElementById("approval-queue");
const approvalStatsWrap = document.getElementById("approval-stats");
const clientCards = document.getElementById("client-cards");
const operatorCards = document.getElementById("operator-cards");
const roleCards = document.getElementById("role-cards");
const reversalCards = document.getElementById("reversal-cards");
const healthStatsWrap = document.getElementById("health-stats");
const topbarEyebrow = document.getElementById("topbar-eyebrow");
const viewTitle = document.getElementById("view-title");
const actionBtn = document.getElementById("action-btn");
const contextTitle = document.getElementById("context-title");
const transactionScopeLabel = document.getElementById("transaction-scope-label");
const approvalPill = document.getElementById("approval-pill");
const sessionUser = document.getElementById("session-user");
const sessionRole = document.getElementById("session-role");
const scopePill = document.getElementById("scope-pill");
const productPill = document.getElementById("product-pill");
const detailDrawer = document.getElementById("detail-drawer");
const drawerTitle = document.getElementById("drawer-title");
const drawerContent = document.getElementById("drawer-content");
const closeDrawer = document.getElementById("close-drawer");
const timeFilter = document.getElementById("time-filter");
const headerProductFilter = document.getElementById("header-product-filter");
const headerStatusFilter = document.getElementById("header-status-filter");
const headerTypeFilter = document.getElementById("header-type-filter");
const headerSearch = document.getElementById("header-search");
const refreshDataBtn = document.getElementById("refresh-data-btn");
const logoutBtn = document.getElementById("logout-btn");
const profilingBaseUrlInput = document.getElementById("profiling-base-url");

const viewMeta = {
  overview: { eyebrow: "Operations Command", title: "Transaction Command Center", action: "New Review" },
  transactions: { eyebrow: "Transactions Desk", title: "Ledger Activity and Search", action: "Export Queue" },
  approvals: { eyebrow: "Maker Checker", title: "Approval Operations", action: "Open Queue" },
  clients: { eyebrow: "Client Ledger", title: "Onboarded Clients and Product Performance", action: "Review Client" },
  operators: { eyebrow: "Access Control", title: "Operators and Ownership", action: "Invite Operator" },
  roles: { eyebrow: "Access Design", title: "Roles and Permissions", action: "Review Role Map" },
  reversals: { eyebrow: "Recovery Desk", title: "Reversals and Exceptions", action: "Flag Case" },
  health: { eyebrow: "Platform Watch", title: "Service Health and Pressure", action: "Refresh Status" }
};

function setStatus(message, tone) {
  statusBanner.textContent = message;
  statusBanner.className = `status-banner ${tone || "info"}`;
  statusBanner.classList.remove("hidden");
}

function clearStatus() {
  statusBanner.classList.add("hidden");
}

function setLoginError(message) {
  if (!message) {
    loginError.classList.add("hidden");
    loginError.textContent = "";
    return;
  }
  loginError.textContent = message;
  loginError.classList.remove("hidden");
}

function fmtNumber(value) {
  const num = Number(value || 0);
  return num.toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 0 });
}

function findClientByProductCode(productCode) {
  return (state.data.clients || []).find((item) => item.productCode === productCode) || null;
}

function clientLabel(productCode) {
  const client = findClientByProductCode(productCode);
  if (client && client.productName) {
    return `${client.productName} (${productCode})`;
  }
  return productCode || "-";
}

function computeDashboardFromTransactions(transactions) {
  const summary = {
    totalInflow: 0,
    totalOutflow: 0,
    transactionCount: transactions.length,
    failedCount: 0,
    reversedCount: 0
  };
  (transactions || []).forEach((txn) => {
    const amount = Number(String(txn.amount || 0).replace(/,/g, "")) || 0;
    if ((txn.details && txn.details.direction) === "DEBIT") {
      summary.totalOutflow += amount;
    } else {
      summary.totalInflow += amount;
    }
    if (txn.statusCode >= 400) {
      summary.failedCount += 1;
    }
    if (String(txn.status || "").toLowerCase() === "reversed") {
      summary.reversedCount += 1;
    }
  });
  return summary;
}

function normalizeStatus(code) {
  if (code === 200) return { text: "Successful", tone: "success" };
  if (code === 102) return { text: "Pending", tone: "warning" };
  if (code >= 400) return { text: "Failed", tone: "danger" };
  return { text: "Pending", tone: "warning" };
}

function mapLedgerTransaction(item) {
  const status = normalizeStatus(item.statusCode);
  return {
    requestRef: item.requestRef || item.transactionId,
    productCode: item.productCode,
    productName: item.productName || null,
    accountNumber: item.accountNumber,
    counterparty: item.batchRef || item.transactionId || "-",
    transType: item.transType || item.legType,
    amount: fmtNumber(item.legType === "DEBIT" ? item.finalCharges : item.amount),
    statusCode: item.statusCode,
    status: status.text,
    tone: status.tone,
    createdAt: item.createdAt,
    details: {
      direction: item.legType,
      groupRef: item.batchRef || "-",
      operator: "portal-read",
      narration: item.narration || item.description || "-",
      balanceBefore: item.balanceBefore,
      balanceAfter: item.balanceAfter,
      fees: item.fees,
      finalCharges: item.finalCharges,
      productName: item.productName || null
    }
  };
}


function buildFlowTrend(transactions) {
  if (!transactions.length) {
    return [];
  }
  const buckets = new Map();
  transactions.forEach((txn) => {
    const date = new Date(txn.createdAt || Date.now());
    const label = date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    const current = buckets.get(label) || { label, inflow: 0, outflow: 0 };
    const amount = Number(String(txn.amount || 0).replace(/,/g, "")) || 0;
    if ((txn.details && txn.details.direction) === "DEBIT") {
      current.outflow += amount;
    } else {
      current.inflow += amount;
    }
    buckets.set(label, current);
  });
  const rows = Array.from(buckets.values()).slice(0, 8);
  const max = rows.reduce((m, row) => Math.max(m, row.inflow, row.outflow), 0) || 1;
  return rows.map((row) => ({
    label: row.label,
    inflow: Math.round((row.inflow / max) * 100),
    outflow: Math.round((row.outflow / max) * 100)
  }));
}

function computeWindowLabel(dateString) {
  if (!dateString) return state.activeWindow;
  const diffMinutes = Math.max(0, Math.round((Date.now() - new Date(dateString).getTime()) / 60000));
  if (diffMinutes <= 30) return "30m";
  if (diffMinutes <= 60) return "1h";
  if (diffMinutes <= 1440) return "24h";
  return "7d";
}

function renderKpis() {
  kpiGrid.innerHTML = "";
  state.data.dashboardKpis.forEach((item) => {
    const card = document.createElement("article");
    card.className = "kpi-card";
    card.innerHTML = `
      <p class="kpi-label">${item.label}</p>
      <p class="kpi-value">${item.value}</p>
      <span class="kpi-trend ${item.tone}">${item.trend}</span>
    `;
    kpiGrid.appendChild(card);
  });
}

function renderFlow() {
  flowChart.innerHTML = "";
  if (!state.data.flowTrend.length) {
    flowChart.innerHTML = `<p style="margin:0; color: var(--muted);">No transaction trend data in this window.</p>`;
    return;
  }
  state.data.flowTrend.forEach((item) => {
    const group = document.createElement("div");
    group.className = "chart-group";
    group.innerHTML = `
      <div class="bar-stack">
        <div class="bar inflow" style="height:${item.inflow}%"></div>
        <div class="bar outflow" style="height:${item.outflow}%"></div>
      </div>
      <div class="bar-label">${item.label}</div>
    `;
    flowChart.appendChild(group);
  });
}

function buildTopProductsFromClients(clients) {
  return (clients || [])
    .map((item) => ({
      productCode: item.productCode,
      transactionCount: Number(item.transactionCount || 0),
      totalValue: Number(item.totalInflow || 0) + Number(item.totalOutflow || 0)
    }))
    .sort((a, b) => Number(b.totalValue || 0) - Number(a.totalValue || 0));
}

function syncProductFilterOptions() {
  const current = state.session.scope === "client" ? state.session.productCode : state.filters.product;
  const productCodes = Array.from(new Set((state.data.clients || [])
    .map((item) => item.productCode)
    .filter((item) => item && item !== "-")));
  headerProductFilter.innerHTML = "";
  const allOpt = document.createElement("option");
  allOpt.value = "ALL";
  allOpt.textContent = "All Products";
  headerProductFilter.appendChild(allOpt);
  productCodes.forEach((code) => {
    const opt = document.createElement("option");
    opt.value = code;
    opt.textContent = code;
    headerProductFilter.appendChild(opt);
  });
  headerProductFilter.value = productCodes.includes(current) || current === "ALL" ? current : "ALL";
}

function renderProducts() {
  productChart.innerHTML = "";
  if (!state.data.topProducts.length) {
    productChart.innerHTML = `<p style="margin:0; color: var(--muted);">No productCode activity in this window.</p>`;
    return;
  }
  state.data.topProducts.forEach((item) => {
    const pct = Math.max(10, Math.min(100, Number(item.transactionCount || item.value || 0)));
    const row = document.createElement("div");
    row.className = "mini-bar-row";
    row.innerHTML = `
      <div style="display:flex; justify-content:space-between; gap:12px;">
        <strong>${clientLabel(item.productCode || item.code)}</strong>
        <span>${item.totalValue != null ? fmtNumber(item.totalValue) : pct + "%"}</span>
      </div>
      <div class="mini-bar-track">
        <div class="mini-bar-fill" style="width:${pct}%"></div>
      </div>
    `;
    productChart.appendChild(row);
  });
}

function renderHealth(target) {
  target.innerHTML = "";
  state.data.services.forEach((item) => {
    const li = document.createElement("li");
    li.className = "health-item";
    li.innerHTML = `
      <strong>${item.name}</strong>
      <span class="status-pill ${item.tone}">${item.status}</span>
    `;
    target.appendChild(li);
  });
}

function filteredTransactions() {
  return state.data.transactions.filter((txn) => {
    const scopedProduct = state.session.scope === "client" ? state.session.productCode : state.filters.product;
    const productCode = txn.productCode || txn.product;
  const product = txn.productName ? `${txn.productName} (${productCode})` : clientLabel(productCode);
    const type = txn.transType || txn.type;
    const productMatch = scopedProduct === "ALL" || product === scopedProduct;
    const statusMatch = state.filters.status === "ALL" || txn.status === state.filters.status;
    const typeMatch = state.filters.type === "ALL" || type === state.filters.type;
    const text = state.filters.search.trim().toLowerCase();
    const searchMatch = !text || [txn.requestRef, product, txn.accountNumber, txn.counterparty].join(" ").toLowerCase().includes(text);
    const windowMatch = state.activeWindow === "custom" || computeWindowLabel(txn.createdAt) === state.activeWindow || state.activeWindow === "7d";
    return productMatch && statusMatch && typeMatch && searchMatch && windowMatch;
  });
}

function buildTransactionRow(txn, detailed) {
  const row = document.createElement("tr");
  row.className = "clickable-row";
  const productCode = txn.productCode || txn.product;
  const product = txn.productName ? `${txn.productName} (${productCode})` : clientLabel(productCode);
  const account = txn.accountNumber || txn.account;
  const type = txn.transType || txn.type;
  const ref = txn.requestRef || txn.ref;
  const amount = txn.amount;
  row.innerHTML = detailed
    ? `
      <td>${ref}</td>
      <td>${product}</td>
      <td>${account}</td>
      <td>${txn.counterparty || "-"}</td>
      <td>${type}</td>
      <td>${amount}</td>
      <td><span class="status-pill ${txn.tone}">${txn.status}</span></td>
      <td>${computeWindowLabel(txn.createdAt)}</td>
    `
    : `
      <td>${ref}</td>
      <td>${product}</td>
      <td>${account}</td>
      <td>${type}</td>
      <td>${amount}</td>
      <td><span class="status-pill ${txn.tone}">${txn.status}</span></td>
      <td>${new Date(txn.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</td>
    `;
  row.addEventListener("click", () => openDrawer(txn));
  return row;
}

function renderTransactions() {
  const txns = filteredTransactions();
  transactionTable.innerHTML = "";
  transactionViewTable.innerHTML = "";
  transactionScopeLabel.textContent = state.session.scope === "client" ? state.session.productCode : (state.filters.product === "ALL" ? "All Products" : state.filters.product);
  txns.slice(0, 4).forEach((txn) => transactionTable.appendChild(buildTransactionRow(txn, false)));
  txns.forEach((txn) => transactionViewTable.appendChild(buildTransactionRow(txn, true)));
}

function renderApprovals() {
  approvalList.innerHTML = "";
  approvalQueue.innerHTML = "";
  approvalPill.textContent = `${state.data.approvals.length} Waiting`;
  state.data.approvals.forEach((item) => {
    const markup = `
      <div>
        <strong>${item.title}</strong>
        <p style="margin:6px 0 0; color: var(--muted);">${item.product} • ${item.ref} • ${item.amount}</p>
      </div>
      <span class="status-pill warning">${item.waiting}</span>
    `;
    const card = document.createElement("article");
    card.className = "approval-item";
    card.innerHTML = markup;
    approvalList.appendChild(card);
    const queueCard = document.createElement("article");
    queueCard.className = "approval-item";
    queueCard.innerHTML = markup;
    approvalQueue.appendChild(queueCard);
  });
  approvalStatsWrap.innerHTML = "";
  state.data.approvalStats.forEach((item) => {
    const stat = document.createElement("article");
    stat.className = "stat-item";
    stat.innerHTML = `<div><strong>${item.label}</strong><p style="margin:6px 0 0; color: var(--muted);">${item.value}</p></div>`;
    approvalStatsWrap.appendChild(stat);
  });
}

function renderClients() {
  clientCards.innerHTML = "";
  state.data.clients.forEach((item) => {
    const card = document.createElement("article");
    card.className = "card-item";
    card.innerHTML = `
      <div>
        <strong>${item.productName || item.productCode}</strong>
        <p style="margin:6px 0 0; color: var(--muted);">${item.emailAddress || "-"} • ${item.productCode}</p>
        <small style="display:block; margin-top:8px; color: var(--muted);">Inflow ${fmtNumber(item.totalInflow)} • Outflow ${fmtNumber(item.totalOutflow)} • Net ${fmtNumber(item.netMovement)}</small>
        <small style="display:block; margin-top:6px; color: var(--muted);">Transactions ${Number(item.transactionCount || 0).toLocaleString()} • Failed ${Number(item.failedCount || 0).toLocaleString()}</small>
      </div>
      <span class="status-pill ${String(item.enabled) === "1" ? "success" : "warning"}">${String(item.enabled) === "1" ? "Enabled" : "Disabled"}</span>
    `;
    clientCards.appendChild(card);
  });
}

function renderOperators() {
  operatorCards.innerHTML = "";
  state.data.operators.forEach((item) => {
    const card = document.createElement("article");
    card.className = "card-item";
    card.innerHTML = `
      <div>
        <strong>${item.name}</strong>
        <p style="margin:6px 0 0; color: var(--muted);">${item.role} • Scope ${item.scope}</p>
      </div>
      <span class="status-pill ${item.status === "Active" ? "success" : "warning"}">${item.status}</span>
    `;
    operatorCards.appendChild(card);
  });
}

function renderRoles() {
  roleCards.innerHTML = "";
  state.data.roles.forEach((item) => {
    const card = document.createElement("article");
    card.className = "card-item";
    card.innerHTML = `
      <div>
        <strong>${item.name}</strong>
        <p style="margin:6px 0; color: var(--muted);">${item.desc}</p>
        <small style="color: var(--muted);">${item.perms}</small>
      </div>
    `;
    roleCards.appendChild(card);
  });
}

function renderReversals() {
  reversalCards.innerHTML = "";
  state.data.reversals.forEach((item) => {
    const card = document.createElement("article");
    card.className = "card-item";
    card.innerHTML = `
      <div>
        <strong>${item.ref}</strong>
        <p style="margin:6px 0 0; color: var(--muted);">${item.reason} • ${item.product}</p>
      </div>
      <span class="status-pill ${item.status === "Recovered" ? "success" : "warning"}">${item.status}</span>
    `;
    reversalCards.appendChild(card);
  });
}

function renderHealthStats() {
  healthStatsWrap.innerHTML = "";
  state.data.healthStats.forEach((item) => {
    const stat = document.createElement("article");
    stat.className = "stat-item";
    stat.innerHTML = `<div><strong>${item.label}</strong><p style="margin:6px 0 0; color: var(--muted);">${item.value}</p></div>`;
    healthStatsWrap.appendChild(stat);
  });
}

function openDrawer(txn) {
  const ref = txn.requestRef || txn.ref;
  const productCode = txn.productCode || txn.product;
  const product = txn.productName ? `${txn.productName} (${productCode})` : clientLabel(productCode);
  const account = txn.accountNumber || txn.account;
  drawerTitle.textContent = ref;
  drawerContent.innerHTML = "";
  const rows = {
    Product: product,
    Account: account,
    Counterparty: txn.counterparty || "-",
    Type: txn.transType || txn.type,
    Amount: txn.amount,
    Status: txn.status,
    Direction: txn.details.direction,
    GroupRef: txn.details.groupRef,
    Operator: txn.details.operator,
    Narration: txn.details.narration,
    "Balance Before": txn.details.balanceBefore || "-",
    "Balance After": txn.details.balanceAfter || "-"
  };
  Object.entries(rows).forEach(([label, value]) => {
    const detail = document.createElement("article");
    detail.className = "detail-row";
    detail.innerHTML = `<span>${label}</span><strong>${value}</strong>`;
    drawerContent.appendChild(detail);
  });
  detailDrawer.classList.remove("hidden");
}

function closeDetailDrawer() {
  detailDrawer.classList.add("hidden");
}

function setView(view) {
  state.activeView = view;
  views.forEach((section) => section.classList.toggle("active", section.id === `view-${view}`));
  navButtons.forEach((button) => button.classList.toggle("active", button.dataset.view === view));
  const meta = viewMeta[view];
  topbarEyebrow.textContent = meta.eyebrow;
  viewTitle.textContent = meta.title;
  actionBtn.textContent = meta.action;
}

function syncSessionUi() {
  sessionUser.textContent = state.session.email || "Community Demo Admin";
  sessionRole.textContent = state.session.role || "Ops Command";
  scopePill.textContent = state.session.scope === "internal" ? "Internal" : "Client";
  scopePill.className = `status-pill ${state.session.scope === "internal" ? "success" : "warning"}`;
  productPill.textContent = state.session.productCode || "ALL";
  headerProductFilter.value = state.session.scope === "client" ? state.session.productCode : state.filters.product;
  headerProductFilter.disabled = state.session.scope === "client";
  contextTitle.textContent = `${state.config.profilingBaseUrl} • portal API surface`;
}

function renderAll() {
  renderKpis();
  renderFlow();
  renderProducts();
  syncProductFilterOptions();
  renderHealth(healthList);
  renderHealth(healthServiceList);
  renderTransactions();
  renderApprovals();
  renderClients();
  renderOperators();
  renderRoles();
  renderReversals();
  renderHealthStats();
  syncSessionUi();
}

async function fetchJson(url, options) {
  const response = await fetch(url, options);
  const json = await response.json();
  if (!response.ok || (json && json.statusCode && json.statusCode >= 400)) {
    throw new Error((json && json.description) || `Request failed for ${url}`);
  }
  return json;
}

async function loginPortal(email, password) {
  const base = state.config.profilingBaseUrl.replace(/\/$/, "");
  return fetchJson(`${base}/backoffice/portal/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ emailAddress: email, password })
  });
}

async function fetchPortalMe() {
  const base = state.config.profilingBaseUrl.replace(/\/$/, "");
  return fetchJson(`${base}/backoffice/portal/auth/me`, {
    headers: { Authorization: `Bearer ${state.session.token}` }
  });
}

async function fetchApprovals() {
  const base = state.config.profilingBaseUrl.replace(/\/$/, "");
  const params = new URLSearchParams();
  const scopedProduct = state.session.scope === "client" ? state.session.productCode : state.filters.product;
  if (scopedProduct && scopedProduct !== "ALL") params.set("productCode", scopedProduct);
  const json = await fetchJson(`${base}/backoffice/portal/approvals${params.toString() ? `?${params.toString()}` : ""}`, {
    headers: { Authorization: `Bearer ${state.session.token}` }
  });
  const result = (((json || {}).data || {}).result || []);
  state.data.approvals = result.map((item) => ({
    title: item.operationType || "Approval Request",
    product: item.productCode,
    ref: item.approvalRef,
    waiting: item.status || "Pending",
    amount: item.referenceId || "Pending review"
  }));
}

async function fetchOperatorsAndRoles() {
  const base = state.config.profilingBaseUrl.replace(/\/$/, "");
  const headers = { Authorization: `Bearer ${state.session.token}` };
  const scopedProduct = state.session.scope === "client" ? state.session.productCode : state.filters.product;
  const operatorUrl = scopedProduct && scopedProduct !== "ALL"
    ? `${base}/backoffice/portal/operators?productCode=${encodeURIComponent(scopedProduct)}`
    : `${base}/backoffice/portal/operators`;
  const [usersJson, rolesJson] = await Promise.all([
    fetchJson(operatorUrl, { headers }),
    fetchJson(`${base}/backoffice/portal/roles`, { headers })
  ]);
  const users = (((usersJson || {}).data || {}).result || []);
  const roles = (((rolesJson || {}).data || {}).result || []);
  state.data.operators = users.map((item) => ({
    name: item.fullName,
    role: item.roleCode,
    scope: item.productCode,
    status: item.status
  }));
  state.data.roles = roles.map((item) => ({
    name: item.roleName,
    desc: `Role code ${item.roleCode}`,
    perms: item.permissions || ""
  }));
}

async function fetchClients() {
  const base = state.config.profilingBaseUrl.replace(/\/$/, "");
  const params = new URLSearchParams({
    window: state.activeWindow === "custom" ? "7d" : state.activeWindow,
    limit: "5000"
  });
  const scopedProduct = state.session.scope === "client" ? state.session.productCode : state.filters.product;
  if (scopedProduct && scopedProduct !== "ALL") params.set("productCode", scopedProduct);
  if (state.filters.search) params.set("search", state.filters.search);
  const json = await fetchJson(`${base}/backoffice/portal/clients?${params.toString()}`, {
    headers: { Authorization: `Bearer ${state.session.token}` }
  });
  state.data.clients = (((json || {}).data || {}).result || []);
  state.data.transactions = (state.data.transactions || []).map((txn) => {
    const client = findClientByProductCode(txn.productCode);
    if (client) {
      txn.productName = client.productName;
      if (txn.details) {
        txn.details.productName = client.productName;
      }
    }
    return txn;
  });
  const clientTopProducts = buildTopProductsFromClients(state.data.clients);
  if (clientTopProducts.length) {
    state.data.topProducts = clientTopProducts;
  }
}

async function fetchLedgerDashboard() {
  const base = state.config.profilingBaseUrl.replace(/\/$/, "");
  const params = new URLSearchParams({ window: state.activeWindow === "custom" ? "24h" : state.activeWindow });
  const scopedProduct = state.session.scope === "client" ? state.session.productCode : state.filters.product;
  if (scopedProduct && scopedProduct !== "ALL") params.set("productCode", scopedProduct);
  const json = await fetchJson(`${base}/backoffice/portal/dashboard?${params.toString()}`, {
    headers: { Authorization: `Bearer ${state.session.token}` }
  });
  const data = ((json.data || {}).dashboard || {});
  const txnFallback = computeDashboardFromTransactions(state.data.transactions || []);
  const inflow = Number(data.totalInflow || 0) || txnFallback.totalInflow;
  const outflow = Number(data.totalOutflow || 0) || txnFallback.totalOutflow;
  const transactionCount = Number(data.transactionCount || 0) || txnFallback.transactionCount;
  const failedCount = Number(data.failedCount || 0) || txnFallback.failedCount;
  const reversedCount = Number(data.reversedCount || 0) || txnFallback.reversedCount;
  state.data.dashboardKpis = [
    { label: "Total Inflow", value: fmtNumber(inflow), trend: `Window ${data.window || state.activeWindow}`, tone: "positive" },
    { label: "Total Outflow", value: fmtNumber(outflow), trend: `Scope ${data.productCode || "ALL"}`, tone: "warning" },
    { label: "Net Movement", value: fmtNumber(inflow - outflow), trend: "Computed", tone: "positive" },
    { label: "Transactions", value: Number(transactionCount || 0).toLocaleString(), trend: "Ledger read", tone: "positive" },
    { label: "Failed / Reversed", value: `${Number(failedCount || 0) + Number(reversedCount || 0)}`, trend: `${Number(reversedCount || 0)} reversed`, tone: "negative" },
    { label: "Pending Approvals", value: String(state.data.approvals.length || 0), trend: "Profiling queue", tone: "warning" }
  ];
  const clientTopProducts = buildTopProductsFromClients(state.data.clients);
  state.data.topProducts = clientTopProducts.length ? clientTopProducts : (data.topProducts || state.data.topProducts);
}

async function fetchLedgerTransactions() {
  const base = state.config.profilingBaseUrl.replace(/\/$/, "");
  const params = new URLSearchParams({
    window: state.activeWindow === "custom" ? "24h" : state.activeWindow,
    limit: "100"
  });
  const scopedProduct = state.session.scope === "client" ? state.session.productCode : state.filters.product;
  if (scopedProduct && scopedProduct !== "ALL") params.set("productCode", scopedProduct);
  if (state.filters.search) params.set("search", state.filters.search);
  if (state.filters.type === "Credit") params.set("legType", "CREDIT");
  if (state.filters.type === "Debit") params.set("legType", "DEBIT");
  if (state.filters.status === "Successful") params.set("statusCode", "200");
  const json = await fetchJson(`${base}/backoffice/portal/transactions?${params.toString()}`, {
    headers: { Authorization: `Bearer ${state.session.token}` }
  });
  const items = ((((json || {}).data || {}).result || {}).items || []);
  state.data.transactions = items.map((item) => {
    const mapped = mapLedgerTransaction(item);
    const client = findClientByProductCode(mapped.productCode);
    if (client) {
      mapped.productName = client.productName;
      if (mapped.details) {
        mapped.details.productName = client.productName;
      }
    }
    return mapped;
  });
}

async function fetchReversals() {
  const base = state.config.profilingBaseUrl.replace(/\/$/, "");
  const params = new URLSearchParams({
    window: state.activeWindow === "custom" ? "24h" : state.activeWindow,
    limit: "25"
  });
  const scopedProduct = state.session.scope === "client" ? state.session.productCode : state.filters.product;
  if (scopedProduct && scopedProduct !== "ALL") params.set("productCode", scopedProduct);
  const json = await fetchJson(`${base}/backoffice/portal/reversals?${params.toString()}`, {
    headers: { Authorization: `Bearer ${state.session.token}` }
  });
  state.data.reversals = (((json || {}).data || {}).result || []);
}

async function fetchHealth() {
  const base = state.config.profilingBaseUrl.replace(/\/$/, "");
  const params = new URLSearchParams({ window: state.activeWindow === "custom" ? "24h" : state.activeWindow });
  const scopedProduct = state.session.scope === "client" ? state.session.productCode : state.filters.product;
  if (scopedProduct && scopedProduct !== "ALL") params.set("productCode", scopedProduct);
  const json = await fetchJson(`${base}/backoffice/portal/health?${params.toString()}`, {
    headers: { Authorization: `Bearer ${state.session.token}` }
  });
  const result = (((json || {}).data || {}).result || {});
  state.data.services = result.services || state.data.services;
  state.data.healthStats = result.stats || state.data.healthStats;
}

async function refreshPortalData() {
  try {
    clearStatus();
    await Promise.allSettled([fetchApprovals(), fetchClients(), fetchOperatorsAndRoles(), fetchLedgerTransactions(), fetchReversals(), fetchHealth()]);
    await fetchLedgerDashboard();
    state.data.flowTrend = buildFlowTrend(state.data.transactions);
    state.data.approvalStats = [
      { label: "Queue Depth", value: `${state.data.approvals.length} waiting` },
      { label: "Average Decision Time", value: "Live integration next" },
      { label: "High-Risk Requests", value: state.data.approvals.filter((item) => /reversal|limit/i.test(item.title)).length + " flagged" },
      { label: "Product Under Pressure", value: state.session.productCode || "ALL" }
    ];
    renderAll();
    setStatus("Portal data refreshed from Smart-Core services.", "success");
  } catch (error) {
    renderAll();
    setStatus(`Using fallback data for some panels: ${error.message}`, "warning");
  }
}

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  setLoginError("");
  state.config.profilingBaseUrl = profilingBaseUrlInput.value.trim().replace(/\/$/, "");
  localStorage.setItem("smartcore.portal.profilingBaseUrl", state.config.profilingBaseUrl);

  try {
    const loginJson = await loginPortal(document.getElementById("email").value.trim(), document.getElementById("password").value);
    const result = ((loginJson.data || {}).result || {});
    state.session.email = result.emailAddress || document.getElementById("email").value.trim();
    state.session.productCode = result.productCode || "ALL";
    state.session.scope = result.scope === "CLIENT" ? "client" : "internal";
    state.session.role = result.roleCode || (state.session.scope === "internal" ? "Community Demo Admin" : "Client Backoffice");
    state.session.token = result.token || "";
    state.session.permissions = result.permissions || "";
    state.filters.product = state.session.scope === "client" ? state.session.productCode : "ALL";

    try {
      await fetchPortalMe();
    } catch (error) {
      console.warn("Portal /me lookup failed", error);
    }

    loginScreen.classList.add("hidden");
    portalShell.classList.remove("hidden");
    renderAll();
    setView("overview");
    await refreshPortalData();
  } catch (error) {
    setLoginError(error.message || "Unable to log into portal.");
  }
});

navButtons.forEach((button) => {
  button.addEventListener("click", () => setView(button.dataset.view));
});

switchButtons.forEach((button) => {
  button.addEventListener("click", () => setView(button.dataset.switchView));
});

Array.from(timeFilter.querySelectorAll(".segment")).forEach((button) => {
  button.addEventListener("click", async () => {
    Array.from(timeFilter.querySelectorAll(".segment")).forEach((item) => item.classList.remove("active"));
    button.classList.add("active");
    state.activeWindow = button.dataset.window;
    await refreshPortalData();
  });
});

headerProductFilter.addEventListener("change", async (event) => {
  state.filters.product = event.target.value;
  await refreshPortalData();
});

headerStatusFilter.addEventListener("change", () => {
  state.filters.status = event.target.value;
  renderTransactions();
});

headerTypeFilter.addEventListener("change", () => {
  state.filters.type = event.target.value;
  renderTransactions();
});

headerSearch.addEventListener("input", async (event) => {
  state.filters.search = event.target.value;
  await refreshPortalData();
});

refreshDataBtn.addEventListener("click", refreshPortalData);
logoutBtn.addEventListener("click", () => {
  state.session = { email: "", scope: "internal", productCode: "ALL", role: "", token: "", permissions: "" };
  loginScreen.classList.remove("hidden");
  portalShell.classList.add("hidden");
  closeDetailDrawer();
  clearStatus();
});

closeDrawer.addEventListener("click", closeDetailDrawer);

profilingBaseUrlInput.value = state.config.profilingBaseUrl;
renderAll();
