const state = {
  finances: {
    income: 4600,
    debt: 350,
    savings: 450,
    cash: 5200,
    city: "Charlotte, NC",
    roommates: 0
  },
  expenses: [
    { category: "Transportation", name: "Car payment", amount: 315 },
    { category: "Transportation", name: "Gas + maintenance", amount: 165 },
    { category: "Food", name: "Groceries", amount: 420 },
    { category: "Health", name: "Prescriptions", amount: 55 },
    { category: "Utilities", name: "Phone", amount: 85 },
    { category: "Lifestyle", name: "Subscriptions", amount: 42 },
    { category: "Family", name: "Child support / support", amount: 0 },
    { category: "Safety", name: "Emergency buffer", amount: 200 }
  ]
};

const listings = [
  {
    id: 1,
    title: "South End Studio",
    city: "Charlotte, NC",
    beds: "Studio",
    neighborhood: "South End",
    rent: 1395,
    utilities: 145,
    internet: 65,
    parking: 95,
    insurance: 18,
    transit: 70,
    pet: 0,
    fees: { application: 75, amenity: 35 },
    deposit: 1395
  },
  {
    id: 2,
    title: "NoDa One-Bedroom",
    city: "Charlotte, NC",
    beds: "1 bed",
    neighborhood: "NoDa",
    rent: 1540,
    utilities: 165,
    internet: 65,
    parking: 85,
    insurance: 18,
    transit: 45,
    pet: 30,
    fees: { application: 100, amenity: 40 },
    deposit: 1540
  },
  {
    id: 3,
    title: "Dilworth Shared 2BR",
    city: "Charlotte, NC",
    beds: "2 bed",
    neighborhood: "Dilworth",
    rent: 1925,
    utilities: 210,
    internet: 70,
    parking: 60,
    insurance: 22,
    transit: 65,
    pet: 0,
    fees: { application: 60, amenity: 25 },
    deposit: 1925
  },
  {
    id: 4,
    title: "Midtown Garden Apartment",
    city: "Atlanta, GA",
    beds: "1 bed",
    neighborhood: "Midtown",
    rent: 1710,
    utilities: 160,
    internet: 65,
    parking: 110,
    insurance: 19,
    transit: 80,
    pet: 35,
    fees: { application: 90, amenity: 38 },
    deposit: 1710
  },
  {
    id: 5,
    title: "Grant Park Shared Loft",
    city: "Atlanta, GA",
    beds: "2 bed",
    neighborhood: "Grant Park",
    rent: 2050,
    utilities: 225,
    internet: 70,
    parking: 45,
    insurance: 24,
    transit: 55,
    pet: 0,
    fees: { application: 75, amenity: 28 },
    deposit: 2050
  },
  {
    id: 6,
    title: "Downtown Durham One-Bedroom",
    city: "Durham, NC",
    beds: "1 bed",
    neighborhood: "Central Park",
    rent: 1485,
    utilities: 145,
    internet: 60,
    parking: 85,
    insurance: 18,
    transit: 35,
    pet: 25,
    fees: { application: 85, amenity: 32 },
    deposit: 1485
  }
];

const currency = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
  maximumFractionDigits: 0
});

const citySelect = document.querySelector("#city");
const roommatesSelect = document.querySelector("#roommates");
const financeForm = document.querySelector("#finance-form");
const expenseRows = document.querySelector("#expense-rows");
const headlineMetrics = document.querySelector("#headline-metrics");
const budgetBreakdown = document.querySelector("#budget-breakdown");
const listingGrid = document.querySelector("#listing-grid");

function init() {
  renderCities();
  populateFinanceInputs();
  renderExpenses();
  render();
  financeForm.addEventListener("input", handleFinanceChange);
  expenseRows.addEventListener("input", handleExpenseChange);
}

function renderCities() {
  const cities = [...new Set(listings.map((listing) => listing.city))];
  citySelect.innerHTML = cities
    .map((city) => `<option value="${city}">${city}</option>`)
    .join("");
}

function populateFinanceInputs() {
  document.querySelector("#income").value = state.finances.income;
  document.querySelector("#debt").value = state.finances.debt;
  document.querySelector("#savings").value = state.finances.savings;
  document.querySelector("#cash").value = state.finances.cash;
  citySelect.value = state.finances.city;
  roommatesSelect.value = String(state.finances.roommates);
}

function renderExpenses() {
  expenseRows.innerHTML = state.expenses
    .map(
      (expense, index) => `
        <tr>
          <td>${expense.category}</td>
          <td>${expense.name}</td>
          <td>
            <input
              type="number"
              min="0"
              step="5"
              value="${expense.amount}"
              data-index="${index}"
              aria-label="${expense.name}"
            >
          </td>
        </tr>
      `
    )
    .join("");
}

function handleFinanceChange(event) {
  const { id, value } = event.target;
  if (!id) {
    return;
  }

  if (id === "city") {
    state.finances.city = value;
  } else if (id === "roommates") {
    state.finances.roommates = Number(value);
  } else {
    state.finances[id] = Number(value) || 0;
  }

  render();
}

function handleExpenseChange(event) {
  const index = Number(event.target.dataset.index);
  if (Number.isNaN(index)) {
    return;
  }

  state.expenses[index].amount = Number(event.target.value) || 0;
  render();
}

function calculateBudget() {
  const monthlyExpenses = state.expenses.reduce((sum, expense) => sum + expense.amount, 0);
  const afterObligations =
    state.finances.income - monthlyExpenses - state.finances.debt - state.finances.savings;
  const incomeCap = state.finances.income * 0.35;
  const housingBudget = Math.max(0, Math.min(afterObligations, incomeCap));

  return {
    monthlyExpenses,
    afterObligations,
    incomeCap,
    housingBudget
  };
}

function getListingCost(listing) {
  const shareDivisor = state.finances.roommates > 0 && listing.beds === "2 bed"
    ? state.finances.roommates + 1
    : 1;
  const monthlyHidden =
    listing.utilities +
    listing.internet +
    listing.parking +
    listing.insurance +
    listing.transit +
    listing.pet +
    listing.fees.amenity;

  const monthlyTotal = (listing.rent + monthlyHidden) / shareDivisor;
  const moveInCash = (listing.deposit + listing.rent + listing.fees.application) / shareDivisor;

  return {
    shareDivisor,
    monthlyHidden: monthlyHidden / shareDivisor,
    monthlyTotal,
    moveInCash
  };
}

function getFit(listingTotal, housingBudget, moveInCash) {
  if (listingTotal <= housingBudget && moveInCash <= state.finances.cash) {
    return { label: "Strong fit", className: "fit-good" };
  }

  if (listingTotal <= housingBudget * 1.1 && moveInCash <= state.finances.cash * 1.05) {
    return { label: "Tight fit", className: "fit-close" };
  }

  return { label: "Not realistic", className: "fit-bad" };
}

function renderHeadlineMetrics(budget) {
  const metrics = [
    { label: "Monthly housing budget", value: currency.format(budget.housingBudget) },
    { label: "Non-housing monthly spend", value: currency.format(budget.monthlyExpenses + state.finances.debt + state.finances.savings) },
    { label: "Move-in cash available", value: currency.format(state.finances.cash) },
    { label: "Income-based ceiling", value: currency.format(budget.incomeCap) }
  ];

  headlineMetrics.innerHTML = metrics
    .map(
      (metric) => `
        <article class="metric-card">
          <p class="metric-label">${metric.label}</p>
          <p class="metric-value">${metric.value}</p>
        </article>
      `
    )
    .join("");
}

function renderBudgetBreakdown(budget) {
  const rows = [
    {
      title: "Take-home income",
      detail: "Monthly money actually landing in your account.",
      value: state.finances.income
    },
    {
      title: "Recurring living expenses",
      detail: "Built from your tracked monthly spending categories.",
      value: -budget.monthlyExpenses
    },
    {
      title: "Debt + savings goals",
      detail: "What must still happen before housing gets funded.",
      value: -(state.finances.debt + state.finances.savings)
    },
    {
      title: "Affordable housing budget",
      detail: "Lower of leftover cash or 35% of take-home pay.",
      value: budget.housingBudget
    }
  ];

  budgetBreakdown.innerHTML = rows
    .map(
      (row) => `
        <article class="budget-row">
          <div>
            <strong>${row.title}</strong>
            <p>${row.detail}</p>
          </div>
          <strong>${row.value < 0 ? "-" : ""}${currency.format(Math.abs(row.value))}</strong>
        </article>
      `
    )
    .join("");
}

function renderListings(budget) {
  const visibleListings = listings
    .filter((listing) => listing.city === state.finances.city)
    .map((listing) => {
      const costs = getListingCost(listing);
      const fit = getFit(costs.monthlyTotal, budget.housingBudget, costs.moveInCash);
      return { ...listing, ...costs, fit };
    })
    .sort((a, b) => a.monthlyTotal - b.monthlyTotal);

  listingGrid.innerHTML = visibleListings
    .map(
      (listing) => `
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
              <strong>${currency.format(Math.max(0, budget.housingBudget - listing.monthlyTotal))}</strong>
            </div>
            <div class="listing-cost-row">
              <span>Hidden cost stack</span>
              <strong>${currency.format(listing.monthlyHidden)}</strong>
            </div>
          </div>
          <p class="listing-note">
            Includes utilities, internet, parking, renter's insurance, commute estimate,
            amenity fees, and pet cost. Shared listings split rent and fees per roommate.
          </p>
        </article>
      `
    )
    .join("");
}

function render() {
  const budget = calculateBudget();
  renderHeadlineMetrics(budget);
  renderBudgetBreakdown(budget);
  renderListings(budget);
}

init();
