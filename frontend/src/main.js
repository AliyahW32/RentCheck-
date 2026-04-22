import React, { createElement, useEffect, useState } from "https://esm.sh/react@18.3.1";
import { createRoot } from "https://esm.sh/react-dom@18.3.1/client";
import htm from "https://esm.sh/htm@3.1.1";

const html = htm.bind(createElement);
const API_BASE = "http://localhost:3001/api";
const STORAGE_TOKEN = "rentcheck.token";
const STORAGE_USER_ID = "rentcheck.userId";
const SUPPORTED_CITIES = ["Charlotte, NC", "Atlanta, GA", "Durham, NC"];
const currency = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
  maximumFractionDigits: 0
});

const ROUTES = {
  welcome: "/",
  onboarding: "/onboarding",
  dashboard: "/dashboard",
  living: "/living",
  vehicles: "/vehicles",
  analytics: "/analytics"
};

function App() {
  const [path, setPath] = useState(window.location.pathname || ROUTES.welcome);
  const [status, setStatus] = useState("loading");
  const [error, setError] = useState("");
  const [info, setInfo] = useState("");
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [showLocationModal, setShowLocationModal] = useState(false);
  const [locationStatus, setLocationStatus] = useState("idle");
  const [authMode, setAuthMode] = useState("register");
  const [authToken, setAuthToken] = useState(localStorage.getItem(STORAGE_TOKEN) || "");
  const [currentUser, setCurrentUser] = useState(null);
  const [dashboard, setDashboard] = useState(null);
  const [selectedAreaIds, setSelectedAreaIds] = useState([]);
  const [spendingDraft, setSpendingDraft] = useState([]);
  const [vehicleQuery, setVehicleQuery] = useState("");
  const [vehicleSuggestion, setVehicleSuggestion] = useState(null);
  const [onboarding, setOnboarding] = useState(getDefaultOnboarding());

  useEffect(() => {
    const handlePopState = () => setPath(window.location.pathname || ROUTES.welcome);
    window.addEventListener("popstate", handlePopState);
    return () => window.removeEventListener("popstate", handlePopState);
  }, []);

  useEffect(() => {
    const storedUserId = localStorage.getItem(STORAGE_USER_ID);

    if (!authToken || !storedUserId) {
      setStatus("ready");
      return;
    }

    loadAuthenticatedState(authToken, storedUserId).catch((loadError) => {
      clearSession();
      setStatus("ready");
      setError(loadError.message || "Could not restore your session.");
    });
  }, []);

  const cityOptions = dashboard?.supportedCities?.length > 0 ? dashboard.supportedCities : SUPPORTED_CITIES;
  const model = dashboard ? normalizeDashboard(dashboard) : null;

  function navigate(to) {
    if (window.location.pathname !== to) {
      window.history.pushState({}, "", to);
      setPath(to);
    }
  }

  function setSession(token, userId) {
    localStorage.setItem(STORAGE_TOKEN, token);
    localStorage.setItem(STORAGE_USER_ID, userId);
    setAuthToken(token);
  }

  function clearSession() {
    localStorage.removeItem(STORAGE_TOKEN);
    localStorage.removeItem(STORAGE_USER_ID);
    setAuthToken("");
    setCurrentUser(null);
    setDashboard(null);
    setSelectedAreaIds([]);
    setSpendingDraft([]);
    setVehicleQuery("");
    setVehicleSuggestion(null);
    setOnboarding(getDefaultOnboarding());
  }

  async function request(pathname, options = {}, tokenOverride = authToken) {
    const headers = { ...(options.headers || {}) };

    if (tokenOverride) {
      headers.Authorization = `Bearer ${tokenOverride}`;
    }

    if (options.body && !headers["Content-Type"]) {
      headers["Content-Type"] = "application/json";
    }

    const response = await fetch(`${API_BASE}${pathname}`, {
      ...options,
      headers,
      body: options.body ? JSON.stringify(options.body) : undefined
    });

    if (!response.ok) {
      let message = `Request failed with ${response.status}`;
      try {
        const payload = await response.json();
        if (payload.message) {
          message = payload.message;
        }
      } catch (_error) {
        const text = await response.text();
        if (text) {
          message = text;
        }
      }
      throw new Error(message);
    }

    if (response.status === 204) {
      return null;
    }

    return response.json();
  }

  async function loadAuthenticatedState(token, userId, areaIds = []) {
    setStatus("loading");
    setError("");
    const user = await request(`/users/${encodeURIComponent(userId)}`, {}, token);
    const dashboardResponse = await request("/dashboard/calculate", {
      method: "POST",
      body: buildDashboardPayload(user, hydrateOnboarding(getDefaultOnboarding(), user), areaIds)
    }, token);

    setCurrentUser(user);
    setDashboard(dashboardResponse);
    setSelectedAreaIds(dashboardResponse.selectedAreaIds || areaIds);
    setOnboarding(hydrateOnboarding(getDefaultOnboarding(), user, dashboardResponse));
    setSpendingDraft(normalizeSpendingEntries(dashboardResponse.analytics?.spendingEntries || []));
    setVehicleSuggestion(null);
    setStatus("ready");
  }

  async function handleRegister() {
    try {
      setStatus("loading");
      setError("");
      setInfo("");
      const payload = buildOnboardingRequest(onboarding);
      const response = await request("/auth/register", { method: "POST", body: payload }, "");

      setSession(response.token, response.user.id);
      await loadAuthenticatedState(response.token, response.user.id);
      setInfo("Account created.");
      setShowLocationModal(true);
      navigate(ROUTES.dashboard);
    } catch (registerError) {
      setStatus("ready");
      setError(registerError.message || "Could not create the account.");
    }
  }

  async function handleLogin() {
    try {
      setStatus("loading");
      setError("");
      setInfo("");
      const response = await request("/auth/login", {
        method: "POST",
        body: {
          email: onboarding.account.email,
          password: onboarding.account.password
        }
      }, "");

      setSession(response.token, response.user.id);
      await loadAuthenticatedState(response.token, response.user.id);
      setInfo("Signed in.");
      navigate(ROUTES.dashboard);
    } catch (loginError) {
      setStatus("ready");
      setError(loginError.message || "Could not sign in.");
    }
  }

  async function refreshDashboard(areaIds = selectedAreaIds, nextOnboarding = onboarding) {
    if (!currentUser || !authToken) {
      return;
    }

    const response = await request("/dashboard/calculate", {
      method: "POST",
      body: buildDashboardPayload(currentUser, nextOnboarding, areaIds)
    });

    setDashboard(response);
    setSelectedAreaIds(response.selectedAreaIds || areaIds);
    setSpendingDraft(normalizeSpendingEntries(response.analytics?.spendingEntries || []));
  }

  async function saveProfile() {
    if (!currentUser || !authToken) {
      return;
    }

    try {
      setStatus("loading");
      setError("");
      setInfo("");
      const updatedUser = await request(`/users/${encodeURIComponent(currentUser.id)}/onboarding`, {
        method: "PUT",
        body: buildOnboardingRequest(onboarding, currentUser.role)
      });
      setCurrentUser(updatedUser);
      await refreshDashboard(selectedAreaIds, onboarding);
      setOnboarding((current) => ({ ...current, accountReady: true, account: { ...current.account, password: "" } }));
      setStatus("ready");
      setInfo("Profile saved.");
    } catch (saveError) {
      setStatus("ready");
      setError(saveError.message || "Could not save the profile.");
    }
  }

  async function handleAreaToggle(areaId) {
    const nextAreaIds = selectedAreaIds.includes(areaId)
      ? selectedAreaIds.filter((id) => id !== areaId)
      : [...selectedAreaIds, areaId];

    setSelectedAreaIds(nextAreaIds);

    try {
      await refreshDashboard(nextAreaIds);
    } catch (areaError) {
      setError(areaError.message || "Could not refresh housing options.");
    }
  }

  async function handleCityChange(city) {
    const nextOnboarding = setIn(onboarding, "location.city", city);
    setOnboarding(nextOnboarding);

    if (!currentUser) {
      return;
    }

    try {
      await refreshDashboard([], nextOnboarding);
      setSelectedAreaIds([]);
    } catch (cityError) {
      setError(cityError.message || "Could not refresh the selected city.");
    }
  }

  function updateOnboarding(field, value) {
    setOnboarding((current) => setIn(current, field, value));
  }

  function updateSpendingEntry(index, field, value) {
    setSpendingDraft((current) =>
      current.map((entry, entryIndex) =>
        entryIndex === index
          ? { ...entry, [field]: field === "planned" || field === "actual" ? Number(value) || 0 : value }
          : entry
      )
    );
  }

  function addSpendingEntry() {
    setSpendingDraft((current) => [
      ...current,
      { month: getCurrentMonth(), category: "General", note: "", planned: 0, actual: 0 }
    ]);
  }

  function removeSpendingEntry(index) {
    setSpendingDraft((current) => current.filter((_, entryIndex) => entryIndex !== index));
  }

  async function saveSpendingSheet() {
    if (!currentUser) {
      return;
    }

    try {
      setStatus("loading");
      setError("");
      const analytics = await request(`/planning/${encodeURIComponent(currentUser.id)}/analytics/spending`, {
        method: "PUT",
        body: { entries: spendingDraft }
      });
      setDashboard((current) => ({ ...current, analytics }));
      setSpendingDraft(normalizeSpendingEntries(analytics.spendingEntries || []));
      setStatus("ready");
      setInfo("Monthly spending sheet saved.");
    } catch (saveError) {
      setStatus("ready");
      setError(saveError.message || "Could not save monthly spending.");
    }
  }

  async function fetchVehicleSuggestion() {
    if (!currentUser) {
      return;
    }

    try {
      setError("");
      const suggestion = await request(`/planning/${encodeURIComponent(currentUser.id)}/vehicles/suggest`, {
        method: "POST",
        body: { desiredType: vehicleQuery }
      });
      setVehicleSuggestion(suggestion);
    } catch (suggestError) {
      setError(suggestError.message || "Could not load vehicle suggestions.");
    }
  }

  async function completeOnboarding() {
    if (currentUser) {
      await saveProfile();
      setShowLocationModal(true);
      navigate(ROUTES.dashboard);
      return;
    }

    await handleRegister();
  }

  function handleBudgetFocus(target) {
    updateOnboarding("budgetingFor", target);
    navigate(target === "vehicle" ? ROUTES.vehicles : ROUTES.living);
  }

  async function handleUseCurrentLocation() {
    if (!navigator.geolocation) {
      setLocationStatus("unsupported");
      return;
    }

    setLocationStatus("requesting");

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        const { latitude, longitude } = position.coords;
        const label = `Current location enabled (${latitude.toFixed(3)}, ${longitude.toFixed(3)})`;
        const nextOnboarding = setIn(
          setIn(onboarding, "location.currentLocationLabel", label),
          "location.useCurrentLocation",
          true
        );
        setOnboarding(nextOnboarding);
        setLocationStatus("granted");
        if (currentUser) {
          await saveProfile();
        }
      },
      () => setLocationStatus("denied"),
      { enableHighAccuracy: true, timeout: 10000 }
    );
  }

  function handleLogout() {
    clearSession();
    setAuthMode("register");
    setInfo("Signed out.");
    navigate(ROUTES.welcome);
  }

  if (status === "loading" && !model && !currentUser) {
    return html`<div className="loading-state">Loading RentCheck...</div>`;
  }

  return html`
    <div className="app-shell ${sidebarCollapsed ? "sidebar-collapsed" : ""}">
      <${Sidebar}
        path=${path}
        collapsed=${sidebarCollapsed}
        accountReady=${Boolean(currentUser)}
        onToggle=${() => setSidebarCollapsed((current) => !current)}
        onNavigate=${navigate}
      />
      <main className="app-main">
        <${AppHeader}
          path=${path}
          accountName=${currentUser?.name || "Guest"}
          isAuthenticated=${Boolean(currentUser)}
          onOpenLocation=${() => setShowLocationModal(true)}
          onLogout=${handleLogout}
        />
        ${error ? html`<div className="banner banner-warn">${error}</div>` : null}
        ${info ? html`<div className="banner">${info}</div>` : null}
        ${renderRoute(path, {
          model,
          onboarding,
          cityOptions,
          authMode,
          isAuthenticated: Boolean(currentUser),
          selectedAreaIds,
          onNavigate: navigate,
          onSetAuthMode: setAuthMode,
          onUpdateOnboarding: updateOnboarding,
          onCompleteOnboarding: completeOnboarding,
          onLogin: handleLogin,
          onSaveProfile: saveProfile,
          onBudgetFocus: handleBudgetFocus,
          onAreaToggle: handleAreaToggle,
          onCityChange: handleCityChange,
          onClearAreas: () => refreshDashboard([], onboarding).then(() => setSelectedAreaIds([])),
          onOpenLocation: () => setShowLocationModal(true),
          spendingDraft,
          onUpdateSpendingEntry: updateSpendingEntry,
          onAddSpendingEntry: addSpendingEntry,
          onRemoveSpendingEntry: removeSpendingEntry,
          onSaveSpendingSheet: saveSpendingSheet,
          vehicleQuery,
          onVehicleQueryChange: setVehicleQuery,
          onFetchVehicleSuggestion: fetchVehicleSuggestion,
          vehicleSuggestion
        })}
      </main>
      ${showLocationModal
        ? html`
            <${LocationModal}
              onboarding=${onboarding}
              cityOptions=${cityOptions}
              status=${locationStatus}
              onClose=${() => setShowLocationModal(false)}
              onUseCurrentLocation=${handleUseCurrentLocation}
              onChooseCity=${handleCityChange}
            />
          `
        : null}
    </div>
  `;
}

function renderRoute(path, props) {
  switch (path) {
    case ROUTES.onboarding:
      return createElement(OnboardingPage, props);
    case ROUTES.dashboard:
      return createElement(DashboardPage, props);
    case ROUTES.living:
      return createElement(LivingOptionsPage, props);
    case ROUTES.vehicles:
      return createElement(VehicleOptionsPage, props);
    case ROUTES.analytics:
      return createElement(AnalyticsPage, props);
    default:
      return createElement(WelcomePage, props);
  }
}

function Sidebar({ path, collapsed, accountReady, onToggle, onNavigate }) {
  const items = [
    { to: ROUTES.welcome, label: "Welcome", short: "W" },
    { to: ROUTES.onboarding, label: accountReady ? "Profile" : "Get Started", short: "G" },
    { to: ROUTES.dashboard, label: "Budget Hub", short: "B", disabled: !accountReady },
    { to: ROUTES.living, label: "Living Options", short: "L", disabled: !accountReady },
    { to: ROUTES.vehicles, label: "Vehicle Options", short: "V", disabled: !accountReady },
    { to: ROUTES.analytics, label: "Analytics", short: "A", disabled: !accountReady }
  ];

  return html`
    <aside className="sidebar">
      <div className="sidebar-top">
        <button type="button" className="sidebar-toggle" onClick=${onToggle}>${collapsed ? "Expand" : "Collapse"}</button>
        <div className="brand-lockup">
          <div className="brand-mark">RC</div>
          ${collapsed ? null : html`<div><p className="eyebrow">RentCheck</p><h2>Planning Suite</h2></div>`}
        </div>
      </div>
      <nav className="sidebar-nav">
        ${items.map((item) => html`
          <button
            key=${item.to}
            type="button"
            disabled=${item.disabled}
            className=${path === item.to ? "sidebar-link active" : "sidebar-link"}
            onClick=${() => onNavigate(item.disabled ? ROUTES.onboarding : item.to)}
            title=${item.label}
          >
            <span className="sidebar-icon">${item.short}</span>
            ${collapsed ? null : html`<span>${item.label}</span>`}
          </button>
        `)}
      </nav>
    </aside>
  `;
}

function AppHeader({ path, accountName, isAuthenticated, onOpenLocation, onLogout }) {
  const titles = {
    [ROUTES.welcome]: "Welcome",
    [ROUTES.onboarding]: isAuthenticated ? "Profile" : "Account Setup",
    [ROUTES.dashboard]: "Budget Dashboard",
    [ROUTES.living]: "Living Options",
    [ROUTES.vehicles]: "Vehicle Options",
    [ROUTES.analytics]: "Profile Analytics"
  };

  return html`
    <header className="topbar">
      <div>
        <p className="eyebrow">Budgeting and planning</p>
        <h1>${titles[path] || "RentCheck"}</h1>
      </div>
      <div className="topbar-actions">
        ${isAuthenticated ? html`<button type="button" className="ghost-button" onClick=${onOpenLocation}>Location options</button>` : null}
        <div className="user-chip">
          <strong>${accountName}</strong>
          <span>${isAuthenticated ? "Signed in" : "Guest mode"}</span>
        </div>
        ${isAuthenticated ? html`<button type="button" className="ghost-button" onClick=${onLogout}>Log out</button>` : null}
      </div>
    </header>
  `;
}

function WelcomePage({ model, onboarding, isAuthenticated, onNavigate, onSetAuthMode }) {
  const cards = model
    ? [
        { label: "Estimated housing budget", value: currency.format(model.budget.housingBudget) },
        { label: "Monthly free cash", value: currency.format(model.budget.moneyToWorkWith) },
        { label: "Living matches", value: String(model.listings.length) },
        { label: "Vehicle budget target", value: currency.format(model.budget.vehicleBudget) }
      ]
    : [
        { label: "Welcome flow", value: "Onboarding" },
        { label: "Secure auth", value: "Enabled" },
        { label: "Persistent accounts", value: "Enabled" },
        { label: "Profile analytics", value: "Ready" }
      ];

  return html`
    <section className="route-stack">
      <section className="hero-panel welcome-panel">
        <div className="hero-copy">
          <p className="eyebrow">Initial page</p>
          <h2>Build a plan before you commit to rent, transportation, or both.</h2>
          <p className="hero-text">
            The welcome flow now signs users into a persistent account, saves their profile to the backend,
            and uses that account to drive budgeting, housing options, vehicle options, and analytics.
          </p>
          <div className="cta-row">
            <button type="button" className="primary-button" onClick=${() => {
              onSetAuthMode("register");
              onNavigate(ROUTES.onboarding);
            }}>
              ${isAuthenticated ? "Open profile" : "Create account"}
            </button>
            <button type="button" className="ghost-button" onClick=${() => {
              onSetAuthMode("login");
              onNavigate(ROUTES.onboarding);
            }}>
              ${isAuthenticated ? "Open dashboard" : "Sign in"}
            </button>
          </div>
        </div>
        <div className="panel accent-panel">
          <p className="eyebrow">What this flow includes</p>
          <div className="metric-grid">
            ${cards.map((card) => html`
              <article className="metric-card" key=${card.label}>
                <p className="metric-label">${card.label}</p>
                <p className="metric-value">${card.value}</p>
              </article>
            `)}
          </div>
        </div>
      </section>
    </section>
  `;
}

function OnboardingPage({
  model,
  onboarding,
  cityOptions,
  authMode,
  isAuthenticated,
  onSetAuthMode,
  onUpdateOnboarding,
  onCompleteOnboarding,
  onLogin,
  onSaveProfile
}) {
  const budgetPurposeOptions = ["living", "vehicle", "both", "casual budgeting"];
  const useCaseOptions = ["first apartment", "moving with roommates", "relocation", "student planning"];

  function handleChange(event) {
    const { name, value } = event.target;
    const numericFields = new Set([
      "finances.income",
      "finances.debt",
      "finances.savingsGoal",
      "finances.cash",
      "finances.roommates",
      "finances.monthlyEssentials"
    ]);
    onUpdateOnboarding(name, numericFields.has(name) ? Number(value) || 0 : value);
  }

  if (!isAuthenticated && authMode === "login") {
    return html`
      <section className="route-stack">
        <section className="panel">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Sign in</p>
              <h2>Access your saved account</h2>
            </div>
            <button type="button" className="ghost-button" onClick=${() => onSetAuthMode("register")}>Need an account?</button>
          </div>
          <form className="form-grid">
            <label>
              <span>Email</span>
              <input name="account.email" type="email" value=${onboarding.account.email} onInput=${handleChange} />
            </label>
            <label>
              <span>Password</span>
              <input name="account.password" type="password" value=${onboarding.account.password} onInput=${handleChange} />
            </label>
          </form>
          <div className="cta-row">
            <button type="button" className="primary-button" onClick=${onLogin}>Sign in</button>
          </div>
        </section>
      </section>
    `;
  }

  return html`
    <section className="route-stack">
      <section className="panel">
        <div className="section-heading">
          <div>
            <p className="eyebrow">${isAuthenticated ? "Profile" : "Account page"}</p>
            <h2>${isAuthenticated ? "Update account and budgeting details" : "Make an account and add standard information"}</h2>
          </div>
          ${!isAuthenticated ? html`<button type="button" className="ghost-button" onClick=${() => onSetAuthMode("login")}>Already have an account?</button>` : null}
        </div>
        <form className="form-grid">
          <label>
            <span>Full name</span>
            <input name="account.fullName" value=${onboarding.account.fullName} onInput=${handleChange} />
          </label>
          <label>
            <span>Email</span>
            <input name="account.email" type="email" value=${onboarding.account.email} onInput=${handleChange} />
          </label>
          <label>
            <span>${isAuthenticated ? "New password" : "Password"}</span>
            <input name="account.password" type="password" value=${onboarding.account.password} onInput=${handleChange} />
          </label>
          <label>
            <span>Phone</span>
            <input name="account.phone" value=${onboarding.account.phone} onInput=${handleChange} />
          </label>
          <label>
            <span>What are you using this for?</span>
            <select name="useCase" value=${onboarding.useCase} onChange=${handleChange}>
              ${useCaseOptions.map((option) => html`<option key=${option} value=${option}>${titleCase(option)}</option>`)}
            </select>
          </label>
          <label>
            <span>What are you budgeting for?</span>
            <select name="budgetingFor" value=${onboarding.budgetingFor} onChange=${handleChange}>
              ${budgetPurposeOptions.map((option) => html`<option key=${option} value=${option}>${titleCase(option)}</option>`)}
            </select>
          </label>
          <label>
            <span>Preferred city</span>
            <select name="location.city" value=${onboarding.location.city} onChange=${handleChange}>
              ${cityOptions.map((city) => html`<option key=${city} value=${city}>${city}</option>`)}
            </select>
          </label>
          <label>
            <span>ZIP code</span>
            <input name="location.zip" value=${onboarding.location.zip} onInput=${handleChange} />
          </label>
        </form>
      </section>

      <section className="layout-grid">
        <section className="panel">
          <div className="section-heading compact">
            <div>
              <p className="eyebrow">Money profile</p>
              <h3>Budget calculator inputs</h3>
            </div>
          </div>
          <form className="form-grid compact-grid">
            <label><span>Monthly income</span><input name="finances.income" type="number" value=${onboarding.finances.income} onInput=${handleChange} /></label>
            <label><span>Monthly debt</span><input name="finances.debt" type="number" value=${onboarding.finances.debt} onInput=${handleChange} /></label>
            <label><span>Monthly savings goal</span><input name="finances.savingsGoal" type="number" value=${onboarding.finances.savingsGoal} onInput=${handleChange} /></label>
            <label><span>Cash available</span><input name="finances.cash" type="number" value=${onboarding.finances.cash} onInput=${handleChange} /></label>
            <label>
              <span>Roommates</span>
              <select name="finances.roommates" value=${onboarding.finances.roommates} onChange=${handleChange}>
                <option value="0">0</option>
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
              </select>
            </label>
            <label><span>Other monthly essentials</span><input name="finances.monthlyEssentials" type="number" value=${onboarding.finances.monthlyEssentials} onInput=${handleChange} /></label>
          </form>
        </section>

        <section className="panel">
          <div className="section-heading compact">
            <div>
              <p className="eyebrow">Live preview</p>
              <h3>Account-linked planning</h3>
            </div>
          </div>
          <div className="recommendation-list">
            <article className="recommendation-card"><strong>Primary goal</strong><p>${titleCase(onboarding.budgetingFor)}</p></article>
            <article className="recommendation-card"><strong>Use case</strong><p>${titleCase(onboarding.useCase)}</p></article>
            <article className="recommendation-card"><strong>City</strong><p>${onboarding.location.city}</p></article>
            ${model ? html`<article className="recommendation-card"><strong>Current housing budget</strong><p>${currency.format(model.budget.housingBudget)}</p></article>` : null}
          </div>
          <div className="cta-row">
            <button type="button" className="primary-button" onClick=${isAuthenticated ? onSaveProfile : onCompleteOnboarding}>
              ${isAuthenticated ? "Save profile" : "Create account"}
            </button>
          </div>
        </section>
      </section>
    </section>
  `;
}

function DashboardPage({ model, onboarding, cityOptions, isAuthenticated, onUpdateOnboarding, onBudgetFocus, onOpenLocation, onCityChange, onSaveProfile }) {
  if (!isAuthenticated || !model) {
    return html`<section className="panel"><h2>Sign in to open the budget dashboard.</h2></section>`;
  }

  function handleChange(event) {
    const { name, value } = event.target;
    onUpdateOnboarding(name, Number(value) || 0);
  }

  return html`
    <section className="route-stack">
      <section className="hero-panel dashboard-panel">
        <div className="hero-copy">
          <p className="eyebrow">Main page</p>
          <h2>Set the budget, confirm location, then branch into the next planning path.</h2>
          <p className="hero-text">
            This dashboard is now tied to the signed-in account. Save the profile to persist budgeting,
            location, and onboarding choices to the backend.
          </p>
          <div className="cta-row">
            <button type="button" className="primary-button" onClick=${() => onBudgetFocus("living")}>Find living options</button>
            <button type="button" className="ghost-button" onClick=${() => onBudgetFocus("vehicle")}>Find vehicle options</button>
            <button type="button" className="ghost-button" onClick=${onSaveProfile}>Save profile</button>
          </div>
        </div>
        <div className="panel accent-panel">
          <p className="eyebrow">Location and purpose</p>
          <div className="stack-list">
            <article className="stack-item"><strong>Budgeting for</strong><span>${titleCase(onboarding.budgetingFor)}</span></article>
            <article className="stack-item"><strong>Use case</strong><span>${titleCase(onboarding.useCase)}</span></article>
            <article className="stack-item">
              <strong>Search city</strong>
              <select value=${onboarding.location.city} onChange=${(event) => onCityChange(event.target.value)}>
                ${cityOptions.map((city) => html`<option key=${city} value=${city}>${city}</option>`)}
              </select>
            </article>
            <article className="stack-item"><strong>Current location</strong><span>${onboarding.location.currentLocationLabel || "Not enabled yet"}</span></article>
          </div>
          <button type="button" className="ghost-button full-width" onClick=${onOpenLocation}>Use current location</button>
        </div>
      </section>

      <section className="layout-grid">
        <section className="panel">
          <div className="section-heading compact">
            <div><p className="eyebrow">Budget calculator</p><h3>Monthly inputs</h3></div>
          </div>
          <form className="form-grid compact-grid">
            <label><span>Income</span><input name="finances.income" type="number" value=${onboarding.finances.income} onInput=${handleChange} /></label>
            <label><span>Debt</span><input name="finances.debt" type="number" value=${onboarding.finances.debt} onInput=${handleChange} /></label>
            <label><span>Savings goal</span><input name="finances.savingsGoal" type="number" value=${onboarding.finances.savingsGoal} onInput=${handleChange} /></label>
            <label><span>Essentials</span><input name="finances.monthlyEssentials" type="number" value=${onboarding.finances.monthlyEssentials} onInput=${handleChange} /></label>
          </form>
        </section>
        <section className="panel">
          <div className="section-heading compact">
            <div><p className="eyebrow">Results</p><h3>How much money they have to work with</h3></div>
          </div>
          <div className="budget-breakdown">
            <article className="budget-row"><div><strong>Housing budget</strong><p>Max monthly housing target.</p></div><strong>${currency.format(model.budget.housingBudget)}</strong></article>
            <article className="budget-row"><div><strong>Vehicle budget</strong><p>Suggested transportation allocation.</p></div><strong>${currency.format(model.budget.vehicleBudget)}</strong></article>
            <article className="budget-row"><div><strong>Money to work with</strong><p>Remaining amount after planned commitments.</p></div><strong>${currency.format(model.budget.moneyToWorkWith)}</strong></article>
          </div>
        </section>
      </section>
    </section>
  `;
}

function LivingOptionsPage({ model, isAuthenticated, selectedAreaIds, onAreaToggle, onClearAreas }) {
  if (!isAuthenticated || !model) {
    return html`<section className="panel"><h2>Sign in to browse living options.</h2></section>`;
  }

  return html`
    <section className="route-stack">
      <section className="layout-grid">
        <section className="panel">
          <div className="section-heading">
            <div><p className="eyebrow">Living options</p><h2>Browse housing options by location and budget fit</h2></div>
            <button type="button" className="ghost-button" onClick=${onClearAreas}>Clear areas</button>
          </div>
          <p className="hero-text">Select neighborhoods to narrow the housing list without leaving the signed-in dashboard context.</p>
          <${CityMapView} cityMap=${model.cityMap} selectedAreaIds=${selectedAreaIds} onToggleArea=${onAreaToggle} />
          <div className="selected-areas">
            ${selectedAreaIds.length > 0
              ? model.cityMap.areas.filter((area) => selectedAreaIds.includes(area.id)).map((area) => html`<span key=${area.id} className="selected-chip">${area.name}</span>`)
              : html`<span className="selected-empty">No neighborhood filter selected.</span>`}
          </div>
        </section>
        <section className="panel">
          <div className="section-heading compact">
            <div><p className="eyebrow">Budget fit</p><h3>${model.cityMap.city}</h3></div>
          </div>
          <div className="metric-grid metric-grid-single">
            <article className="metric-card"><p className="metric-label">Housing budget</p><p className="metric-value">${currency.format(model.budget.housingBudget)}</p></article>
            <article className="metric-card"><p className="metric-label">Available move-in cash</p><p className="metric-value">${currency.format(model.user.finances.cash)}</p></article>
            <article className="metric-card"><p className="metric-label">Matches shown</p><p className="metric-value">${model.listings.length}</p></article>
          </div>
        </section>
      </section>

      <section className="panel">
        <div className="section-heading"><div><p className="eyebrow">Housing results</p><h2>Living choices aligned to the current budget</h2></div></div>
        <div className="listing-grid">
          ${model.listings.length > 0
            ? model.listings.map((listing) => html`<${ListingCard} key=${listing.id} listing=${listing} />`)
            : html`<article className="listing-card empty-card">No housing options match the current location filter.</article>`}
        </div>
      </section>
    </section>
  `;
}

function VehicleOptionsPage({ model, isAuthenticated, vehicleQuery, onVehicleQueryChange, onFetchVehicleSuggestion, vehicleSuggestion }) {
  if (!isAuthenticated || !model) {
    return html`<section className="panel"><h2>Sign in to compare vehicle options.</h2></section>`;
  }

  return html`
    <section className="route-stack">
      <section className="panel">
        <div className="section-heading"><div><p className="eyebrow">Vehicle options</p><h2>Compare transportation choices against the remaining budget</h2></div></div>
        <div className="vehicle-input-row">
          <label>
            <span>Type of car you want</span>
            <input value=${vehicleQuery} onInput=${(event) => onVehicleQueryChange(event.target.value)} placeholder="SUV, sedan, used truck, hybrid..." />
          </label>
          <button type="button" className="primary-button" onClick=${onFetchVehicleSuggestion}>Get car approach</button>
        </div>
        ${vehicleSuggestion
          ? html`
              <article className="panel suggestion-panel">
                <p className="eyebrow">Custom guidance</p>
                <h3>${vehicleSuggestion.desiredType}</h3>
                <p className="hero-text">${vehicleSuggestion.guidance}</p>
                <div className="vehicle-grid">
                  ${vehicleSuggestion.suggestions.map((option) => html`
                    <article key=${option.id} className="feature-card">
                      <p className="eyebrow">${option.label}</p>
                      <h3>${currency.format(option.monthlyTarget)} / month</h3>
                      <p>${option.description}</p>
                      <div className="stack-list compact-stack">
                        <article className="stack-item"><strong>Estimated payment</strong><span>${currency.format(option.monthlyTarget)}</span></article>
                        <article className="stack-item"><strong>Upfront target</strong><span>${currency.format(option.cashTarget)}</span></article>
                      </div>
                    </article>
                  `)}
                </div>
              </article>
            `
          : null}
        <div className="vehicle-grid">
          ${model.vehicleOptions.map((option) => html`
            <article key=${option.id} className="feature-card">
              <p className="eyebrow">${option.label}</p>
              <h3>${currency.format(option.monthlyTarget)} / month</h3>
              <p>${option.description}</p>
              <div className="stack-list compact-stack">
                <article className="stack-item"><strong>Estimated payment</strong><span>${currency.format(option.monthlyTarget)}</span></article>
                <article className="stack-item"><strong>Upfront target</strong><span>${currency.format(option.cashTarget)}</span></article>
              </div>
            </article>
          `)}
        </div>
      </section>
    </section>
  `;
}

function AnalyticsPage({ model, onboarding, isAuthenticated, spendingDraft, onUpdateSpendingEntry, onAddSpendingEntry, onRemoveSpendingEntry, onSaveSpendingSheet }) {
  if (!isAuthenticated || !model) {
    return html`<section className="panel"><h2>Sign in to view analytics.</h2></section>`;
  }

  return html`
    <section className="route-stack">
      <section className="layout-grid">
        <section className="panel">
          <div className="section-heading"><div><p className="eyebrow">Profile analytics</p><h2>Saving, spending, and available cash</h2></div></div>
          <div className="metric-grid">
            <article className="metric-card"><p className="metric-label">Income</p><p className="metric-value">${currency.format(model.analytics.income)}</p></article>
            <article className="metric-card"><p className="metric-label">Committed spending</p><p className="metric-value">${currency.format(model.analytics.committedSpending)}</p></article>
            <article className="metric-card"><p className="metric-label">Savings goal</p><p className="metric-value">${currency.format(model.analytics.savingsGoal)}</p></article>
            <article className="metric-card"><p className="metric-label">Money to work with</p><p className="metric-value">${currency.format(model.analytics.moneyToWorkWith)}</p></article>
          </div>
          <article className="recommendation-card">
            <strong>Feedback</strong>
            <p>${model.analytics.feedback || "Add monthly data to compare trends."}</p>
          </article>
        </section>
        <section className="panel">
          <div className="section-heading compact"><div><p className="eyebrow">Profile summary</p><h3>${onboarding.account.fullName || model.user.name}</h3></div></div>
          <div className="recommendation-list">
            <article className="recommendation-card"><strong>Budgeting for</strong><p>${titleCase(onboarding.budgetingFor)}</p></article>
            <article className="recommendation-card"><strong>Using the app for</strong><p>${titleCase(onboarding.useCase)}</p></article>
            <article className="recommendation-card"><strong>Location focus</strong><p>${onboarding.location.currentLocationLabel || onboarding.location.city}</p></article>
          </div>
        </section>
      </section>
      <section className="panel">
        <div className="section-heading"><div><p className="eyebrow">Analytical view</p><h2>Spending mix</h2></div></div>
        <div className="analytics-list">
          ${model.analytics.breakdown.map((item) => html`
            <article key=${item.label} className="budget-row">
              <div><strong>${item.label}</strong><p>${item.description}</p></div>
              <strong>${currency.format(item.amount)}</strong>
            </article>
          `)}
        </div>
      </section>
      <section className="panel">
        <div className="section-heading">
          <div><p className="eyebrow">Monthly tracker</p><h2>Track spending in a spreadsheet-style sheet</h2></div>
          <div className="cta-row no-top-margin">
            <button type="button" className="ghost-button" onClick=${onAddSpendingEntry}>Add row</button>
            <button type="button" className="primary-button" onClick=${onSaveSpendingSheet}>Save sheet</button>
          </div>
        </div>
        <div className="sheet-table">
          <div className="sheet-header">Month</div>
          <div className="sheet-header">Category</div>
          <div className="sheet-header">Note</div>
          <div className="sheet-header">Planned</div>
          <div className="sheet-header">Actual</div>
          <div className="sheet-header">Action</div>
          ${spendingDraft.map((entry, index) => html`
            <input value=${entry.month} onInput=${(event) => onUpdateSpendingEntry(index, "month", event.target.value)} placeholder="2026-04" />
            <input value=${entry.category} onInput=${(event) => onUpdateSpendingEntry(index, "category", event.target.value)} placeholder="Food" />
            <input value=${entry.note} onInput=${(event) => onUpdateSpendingEntry(index, "note", event.target.value)} placeholder="Groceries and dining" />
            <input type="number" value=${entry.planned} onInput=${(event) => onUpdateSpendingEntry(index, "planned", event.target.value)} />
            <input type="number" value=${entry.actual} onInput=${(event) => onUpdateSpendingEntry(index, "actual", event.target.value)} />
            <button type="button" className="ghost-button table-button" onClick=${() => onRemoveSpendingEntry(index)}>Remove</button>
          `)}
        </div>
        <div className="summary-grid">
          ${(model.analytics.monthlySummaries || []).map((summary) => html`
            <article key=${summary.month} className="recommendation-card">
              <strong>${summary.month}</strong>
              <p>Planned ${currency.format(summary.plannedTotal)} · Actual ${currency.format(summary.actualTotal)}</p>
              <p>${summary.status} (${currency.format(summary.variance)})</p>
            </article>
          `)}
        </div>
      </section>
    </section>
  `;
}

function LocationModal({ onboarding, cityOptions, status, onClose, onUseCurrentLocation, onChooseCity }) {
  const statusMessage = {
    idle: "Choose a supported city or let the browser use the current device location.",
    requesting: "Requesting device location...",
    granted: "Current location enabled and ready to save to your profile.",
    denied: "Location permission was denied.",
    unsupported: "This browser does not support geolocation."
  }[status];

  return html`
    <div className="modal-backdrop" onClick=${onClose}>
      <div className="modal-card" onClick=${(event) => event.stopPropagation()}>
        <div className="section-heading compact">
          <div><p className="eyebrow">Location popup</p><h3>Use current location?</h3></div>
          <button type="button" className="ghost-button" onClick=${onClose}>Close</button>
        </div>
        <p className="hero-text">${statusMessage}</p>
        <div className="cta-row">
          <button type="button" className="primary-button" onClick=${onUseCurrentLocation}>Allow current location</button>
        </div>
        <label>
          <span>Supported city</span>
          <select value=${onboarding.location.city} onChange=${(event) => onChooseCity(event.target.value)}>
            ${cityOptions.map((city) => html`<option key=${city} value=${city}>${city}</option>`)}
          </select>
        </label>
      </div>
    </div>
  `;
}

function CityMapView({ cityMap, selectedAreaIds, onToggleArea }) {
  return html`
    <svg className="city-map" viewBox=${cityMap.viewBox} role="img" aria-label=${`${cityMap.city} neighborhood map`}>
      <rect x="0" y="0" width="420" height="280" rx="28" className="map-frame"></rect>
      ${cityMap.areas.map((area) => {
        const point = getLabelPoint(area.points);
        return html`
          <g key=${area.id} className="map-group" onClick=${() => onToggleArea(area.id)}>
            <polygon className=${selectedAreaIds.includes(area.id) ? "map-area active" : "map-area"} points=${area.points}></polygon>
            <text x=${point.x} y=${point.y} className="map-label">${area.name}</text>
          </g>
        `;
      })}
    </svg>
  `;
}

function ListingCard({ listing }) {
  return html`
    <article className="listing-card">
      <img className="listing-image" src=${listing.imageUrl} alt=${listing.title} />
      <div className="listing-top">
        <div>
          <p className="eyebrow">${listing.neighborhood}</p>
          <h3>${listing.title}</h3>
          <p className="listing-meta">${listing.beds} - ${listing.city}</p>
        </div>
        <span className=${`chip ${listing.fitClassName}`}>${listing.fitLabel}</span>
      </div>
      <div>
        <div className="listing-price">${currency.format(listing.monthlyTotal)}<span className="listing-meta"> / month</span></div>
        <p className="listing-meta">Base rent ${currency.format(listing.sharedRent)} + hidden costs ${currency.format(listing.monthlyHidden)}</p>
      </div>
      <div className="listing-costs">
        <div className="listing-cost-row"><span>Move-in cash needed</span><strong>${currency.format(listing.moveInCash)}</strong></div>
        <div className="listing-cost-row"><span>Left after housing</span><strong>${currency.format(listing.leftoverAfterHousing)}</strong></div>
      </div>
    </article>
  `;
}

function buildDashboardPayload(user, onboarding, areaIds) {
  return {
    userId: user.id,
    city: onboarding.location.city,
    areaIds,
    finances: {
      income: onboarding.finances.income,
      debt: onboarding.finances.debt,
      savings: onboarding.finances.savingsGoal,
      cash: onboarding.finances.cash,
      roommates: onboarding.finances.roommates,
      monthlyEssentials: onboarding.finances.monthlyEssentials
    }
  };
}

function buildOnboardingRequest(onboarding, role = "RENTER") {
  return {
    fullName: onboarding.account.fullName,
    email: onboarding.account.email,
    password: onboarding.account.password,
    phone: onboarding.account.phone,
    role: String(role || "RENTER").toUpperCase(),
    city: onboarding.location.city,
    zipCode: onboarding.location.zip,
    useCurrentLocation: onboarding.location.useCurrentLocation,
    currentLocationLabel: onboarding.location.currentLocationLabel,
    useCase: onboarding.useCase,
    budgetingFor: onboarding.budgetingFor,
    beds: "1 bed",
    maxCommute: 30,
    priorities: [onboarding.budgetingFor, onboarding.useCase],
    finances: {
      income: onboarding.finances.income,
      debt: onboarding.finances.debt,
      savings: onboarding.finances.savingsGoal,
      cash: onboarding.finances.cash,
      roommates: onboarding.finances.roommates,
      monthlyEssentials: onboarding.finances.monthlyEssentials
    }
  };
}

function getDefaultOnboarding() {
  return {
    accountReady: false,
    account: {
      fullName: "",
      email: "",
      password: "",
      phone: ""
    },
    useCase: "first apartment",
    budgetingFor: "living",
    location: {
      city: SUPPORTED_CITIES[0],
      zip: "",
      currentLocationLabel: "",
      useCurrentLocation: false
    },
    finances: {
      income: 4200,
      debt: 320,
      savingsGoal: 450,
      cash: 6800,
      roommates: 1,
      monthlyEssentials: 920
    }
  };
}

function hydrateOnboarding(current, user, dashboard = null) {
  return {
    ...current,
    accountReady: true,
    account: {
      ...current.account,
      fullName: user.name || current.account.fullName,
      email: user.email || current.account.email,
      phone: user.phone || current.account.phone,
      password: ""
    },
    useCase: user.preferences?.useCase || current.useCase,
    budgetingFor: user.preferences?.budgetingFor || current.budgetingFor,
    location: {
      city: user.city || current.location.city,
      zip: user.locationPreference?.zipCode || current.location.zip,
      currentLocationLabel: user.locationPreference?.currentLocationLabel || current.location.currentLocationLabel,
      useCurrentLocation: Boolean(user.locationPreference?.useCurrentLocation)
    },
    finances: {
      income: user.finances?.income ?? current.finances.income,
      debt: user.finances?.debt ?? current.finances.debt,
      savingsGoal: user.finances?.savings ?? current.finances.savingsGoal,
      cash: user.finances?.cash ?? current.finances.cash,
      roommates: user.finances?.roommates ?? current.finances.roommates,
      monthlyEssentials: user.finances?.monthlyEssentials ?? dashboard?.budget?.monthlyExpenses ?? current.finances.monthlyEssentials
    }
  };
}

function normalizeDashboard(payload) {
  return {
    ...payload,
    user: {
      ...payload.user,
      role: String(payload.user?.role || "").toLowerCase()
    },
    listings: (payload.listings || []).map((listing) => ({
      ...listing,
      fitLabel: listing.fitLabel || "",
      fitClassName: listing.fitClassName || ""
    })),
    analytics: payload.analytics || {
      income: payload.user?.finances?.income || 0,
      committedSpending: 0,
      savingsGoal: payload.user?.finances?.savings || 0,
      moneyToWorkWith: payload.budget?.moneyToWorkWith || 0,
      breakdown: [],
      spendingEntries: [],
      monthlySummaries: [],
      feedback: ""
    }
  };
}

function normalizeSpendingEntries(entries) {
  return (entries || []).map((entry) => ({
    month: entry.month || getCurrentMonth(),
    category: entry.category || "General",
    note: entry.note || "",
    planned: Number(entry.planned) || 0,
    actual: Number(entry.actual) || 0
  }));
}

function getCurrentMonth() {
  return new Date().toISOString().slice(0, 7);
}

function getLabelPoint(points) {
  const coords = points.split(" ").map((pair) => pair.split(",").map(Number));
  const total = coords.reduce((acc, [x, y]) => ({ x: acc.x + x, y: acc.y + y }), { x: 0, y: 0 });
  return { x: total.x / coords.length, y: total.y / coords.length };
}

function setIn(object, path, value) {
  const parts = path.split(".");
  const clone = { ...object };
  let current = clone;

  parts.forEach((part, index) => {
    if (index === parts.length - 1) {
      current[part] = value;
      return;
    }

    current[part] = { ...current[part] };
    current = current[part];
  });

  return clone;
}

function titleCase(value) {
  return String(value || "")
    .split(" ")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

createRoot(document.getElementById("root")).render(createElement(App));
