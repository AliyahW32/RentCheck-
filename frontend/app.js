const API_BASE = "http://localhost:3001/api";

const state = {
  users: [],
  selectedAreaIds: [],
  dashboard: null
};

const currency = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
  maximumFractionDigits: 0
});

const userSelect = document.querySelector("#userId");
const citySelect = document.querySelector("#city");
const financeForm = document.querySelector("#finance-form");
const roommatesSelect = document.querySelector("#roommates");
const headlineMetrics = document.querySelector("#headline-metrics");
const budgetBreakdown = document.querySelector("#budget-breakdown");
const listingGrid = document.querySelector("#listing-grid");
const activeUserName = document.querySelector("#active-user-name");
const activeUserRole = document.querySelector("#active-user-role");
const recommendations = document.querySelector("#recommendations");
const mapCityLabel = document.querySelector("#map-city-label");
const cityMapSvg = document.querySelector("#city-map");
const selectedAreas = document.querySelector("#selected-areas");
const clearAreasButton = document.querySelector("#clear-areas");
const assistantForm = document.querySelector("#assistant-form");
const assistantInput = document.querySelector("#assistant-input");
const assistantResponse = document.querySelector("#assistant-response");

init();

async function init() {
  await loadUsers();
  bindEvents();
}

function bindEvents() {
  financeForm.addEventListener("input", handleFinanceChange);
  financeForm.addEventListener("change", handleFinanceChange);
  clearAreasButton.addEventListener("click", () => {
    state.selectedAreaIds = [];
    syncDashboard();
  });
  assistantForm.addEventListener("submit", handleAssistantSubmit);
}

async function loadUsers() {
  const response = await fetch(`${API_BASE}/users`);
  state.users = await response.json();
  userSelect.innerHTML = state.users
    .map((user) => `<option value="${user.id}">${user.name} · ${user.role}</option>`)
    .join("");
  userSelect.value = state.users[0].id;
  await loadDashboard(userSelect.value);
}

function collectPayload() {
  const selectedUser = state.dashboard?.user;
  if (!selectedUser) {
    return { userId: userSelect.value || state.users[0]?.id, areaIds: state.selectedAreaIds };
  }
  return {
    userId: userSelect.value || state.users[0]?.id,
    city: citySelect.value || selectedUser?.city,
    areaIds: state.selectedAreaIds,
    finances: {
      income: Number(document.querySelector("#income")?.value || selectedUser?.finances?.income || 0),
      debt: Number(document.querySelector("#debt")?.value || selectedUser?.finances?.debt || 0),
      savings: Number(document.querySelector("#savings")?.value || selectedUser?.finances?.savings || 0),
      cash: Number(document.querySelector("#cash")?.value || selectedUser?.finances?.cash || 0),
      roommates: Number(roommatesSelect.value || selectedUser?.finances?.roommates || 0)
    }
  };
}

async function loadDashboard(userId) {
  const response = await fetch(`${API_BASE}/dashboard?userId=${encodeURIComponent(userId)}`);
  state.dashboard = await response.json();
  render();
}

async function syncDashboard() {
  const response = await fetch(`${API_BASE}/dashboard/calculate`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(collectPayload())
  });
  state.dashboard = await response.json();
  render();
}

function render() {
  populateForm();
  renderHeader();
  renderMetrics();
  renderRecommendations();
  renderMap();
  renderBudget();
  renderListings();
}

function populateForm() {
  const { user, listings } = state.dashboard;
  const cities = [...new Set(state.users.map((entry) => entry.city).concat(listings.map((listing) => listing.city)))];
  userSelect.value = user.id;
  citySelect.innerHTML = cities.map((city) => `<option value="${city}">${city}</option>`).join("");
  citySelect.value = user.city;
  document.querySelector("#income").value = user.finances.income;
  document.querySelector("#debt").value = user.finances.debt;
  document.querySelector("#savings").value = user.finances.savings;
  document.querySelector("#cash").value = user.finances.cash;
  roommatesSelect.value = String(user.finances.roommates);
}

function renderHeader() {
  activeUserName.textContent = state.dashboard.user.name;
  activeUserRole.textContent = state.dashboard.user.role;
  activeUserRole.className = `role-pill role-${state.dashboard.user.role}`;
}

function renderMetrics() {
  const { budget, user, listings } = state.dashboard;
  const realisticCount = listings.filter((listing) => listing.fit.label !== "Not realistic").length;
  const metrics = [
    { label: "Monthly housing budget", value: currency.format(budget.housingBudget) },
    { label: "Move-in cash", value: currency.format(user.finances.cash) },
    { label: "Listings in filter", value: String(listings.length) },
    { label: "Realistic matches", value: String(realisticCount) }
  ];

  headlineMetrics.innerHTML = metrics
    .map((metric) => `
      <article class="metric-card">
        <p class="metric-label">${metric.label}</p>
        <p class="metric-value">${metric.value}</p>
      </article>
    `)
    .join("");
}

function renderRecommendations() {
  recommendations.innerHTML = state.dashboard.recommendations
    .map((item) => `<article class="recommendation-card">${item}</article>`)
    .join("");
}

function renderMap() {
  const { cityMap } = state.dashboard;
  mapCityLabel.textContent = `${state.dashboard.user.city} neighborhood map`;
  cityMapSvg.setAttribute("viewBox", cityMap.viewBox || "0 0 420 280");
  cityMapSvg.innerHTML = [
    `<rect x="0" y="0" width="420" height="280" rx="28" class="map-frame"></rect>`,
    ...cityMap.areas.map((area) => {
      const active = state.selectedAreaIds.includes(area.id);
      const point = labelPoint(area.points);
      return `
        <g class="map-group" data-area-id="${area.id}">
          <polygon class="map-area ${active ? "active" : ""}" points="${area.points}"></polygon>
          <text x="${point.x}" y="${point.y}" class="map-label">${area.name}</text>
        </g>
      `;
    })
  ].join("");

  cityMapSvg.querySelectorAll("[data-area-id]").forEach((node) => {
    node.addEventListener("click", () => toggleArea(node.dataset.areaId));
  });

  selectedAreas.innerHTML = state.selectedAreaIds.length
    ? state.selectedAreaIds
        .map((id) => {
          const area = cityMap.areas.find((entry) => entry.id === id);
          return `<span class="selected-chip">${area.name}</span>`;
        })
        .join("")
    : `<span class="selected-empty">No area selected. Showing the full city.</span>`;
}

function labelPoint(points) {
  const coords = points.split(" ").map((pair) => pair.split(",").map(Number));
  const sum = coords.reduce((acc, [x, y]) => ({ x: acc.x + x, y: acc.y + y }), { x: 0, y: 0 });
  return { x: sum.x / coords.length, y: sum.y / coords.length };
}

function toggleArea(areaId) {
  if (state.selectedAreaIds.includes(areaId)) {
    state.selectedAreaIds = state.selectedAreaIds.filter((id) => id !== areaId);
  } else {
    state.selectedAreaIds = [...state.selectedAreaIds, areaId];
  }
  syncDashboard();
}

function renderBudget() {
  const { budget, user } = state.dashboard;
  const rows = [
    { title: "Recurring living expenses", detail: "Costs tracked before housing is funded.", value: -budget.monthlyExpenses },
    { title: "Debt + savings goals", detail: "Existing obligations that stay in place.", value: -(user.finances.debt + user.finances.savings) },
    { title: "Income-based ceiling", detail: "35% cap to keep housing sustainable.", value: budget.incomeCap },
    { title: "Affordable housing budget", detail: "Lower of leftover cash or income ceiling.", value: budget.housingBudget }
  ];

  budgetBreakdown.innerHTML = rows
    .map((row) => `
      <article class="budget-row">
        <div>
          <strong>${row.title}</strong>
          <p>${row.detail}</p>
        </div>
        <strong>${row.value < 0 ? "-" : ""}${currency.format(Math.abs(row.value))}</strong>
      </article>
    `)
    .join("");
}

function renderListings() {
  if (!state.dashboard.listings.length) {
    listingGrid.innerHTML = `<article class="listing-card empty-card">No listings match this city and map selection.</article>`;
    return;
  }

  listingGrid.innerHTML = state.dashboard.listings
    .map((listing) => `
      <article class="listing-card">
        <div class="listing-top">
          <div>
            <p class="eyebrow">${listing.neighborhood}</p>
            <h3>${listing.title}</h3>
            <p class="listing-meta">${listing.beds} • ${listing.city}</p>
          </div>
          <span class="chip ${listing.fit.className}">${listing.fit.label}</span>
        </div>
        <div>
          <div class="listing-price">${currency.format(listing.monthlyTotal)}<span class="listing-meta"> / month</span></div>
          <p class="listing-meta">Base rent ${currency.format(listing.rent / listing.shareDivisor)} + hidden monthly costs ${currency.format(listing.monthlyHidden)}</p>
        </div>
        <div class="listing-costs">
          <div class="listing-cost-row">
            <span>Move-in cash needed</span>
            <strong>${currency.format(listing.moveInCash)}</strong>
          </div>
          <div class="listing-cost-row">
            <span>Monthly leftover after housing</span>
            <strong>${currency.format(Math.max(0, state.dashboard.budget.housingBudget - listing.monthlyTotal))}</strong>
          </div>
          <div class="listing-cost-row">
            <span>Hidden cost stack</span>
            <strong>${currency.format(listing.monthlyHidden)}</strong>
          </div>
        </div>
      </article>
    `)
    .join("");
}

async function handleFinanceChange(event) {
  if (event.target.id === "userId") {
    state.selectedAreaIds = [];
    await loadDashboard(userSelect.value);
    return;
  }
  if (event.target.id === "city") {
    state.selectedAreaIds = [];
  }
  await syncDashboard();
}

async function handleAssistantSubmit(event) {
  event.preventDefault();
  const message = assistantInput.value.trim();
  if (!message) {
    assistantResponse.textContent = "Enter a housing-related question first.";
    return;
  }

  assistantResponse.textContent = "Thinking...";
  const response = await fetch(`${API_BASE}/assistant/query`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      userId: state.dashboard.user.id,
      areaIds: state.selectedAreaIds,
      message
    })
  });
  const payload = await response.json();
  assistantResponse.textContent = payload.reply;
}
