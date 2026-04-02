const { cityMaps, expenses, listings, users } = require("./data");

function getUser(userId) {
  return users.find((user) => user.id === userId) || users[0];
}

function calculateBudget(finances, userExpenses = expenses) {
  const monthlyExpenses = userExpenses.reduce((sum, expense) => sum + Number(expense.amount || 0), 0);
  const afterObligations = finances.income - monthlyExpenses - finances.debt - finances.savings;
  const incomeCap = finances.income * 0.35;
  const housingBudget = Math.max(0, Math.min(afterObligations, incomeCap));
  return { monthlyExpenses, afterObligations, incomeCap, housingBudget };
}

function getListingCost(listing, roommates) {
  const shareDivisor = roommates > 0 && listing.beds === "2 bed" ? roommates + 1 : 1;
  const monthlyHidden = listing.utilities + listing.internet + listing.parking + listing.insurance + listing.transit + listing.pet + listing.fees.amenity;
  const monthlyTotal = (listing.rent + monthlyHidden) / shareDivisor;
  const moveInCash = (listing.deposit + listing.rent + listing.fees.application) / shareDivisor;
  return { shareDivisor, monthlyHidden: monthlyHidden / shareDivisor, monthlyTotal, moveInCash };
}

function getFit(listingTotal, housingBudget, moveInCash, cash) {
  if (listingTotal <= housingBudget && moveInCash <= cash) {
    return { label: "Strong fit", className: "fit-good" };
  }
  if (listingTotal <= housingBudget * 1.1 && moveInCash <= cash * 1.05) {
    return { label: "Tight fit", className: "fit-close" };
  }
  return { label: "Not realistic", className: "fit-bad" };
}

function buildRoleRecommendations(user) {
  if (user.role === "agent") {
    return [
      "Track neighborhoods where hidden costs push listings out of budget even when rent looks acceptable.",
      "Use the map to narrow inventory before discussing specific blocks with a renter."
    ];
  }
  if (user.role === "admin") {
    return [
      "Monitor which areas have no realistic listings so the team can flag coverage gaps.",
      "Review move-in cash friction separately from monthly affordability."
    ];
  }
  return [
    "Start broad by city, then click the map to keep only neighborhoods you would actually live in.",
    "If move-in cash is the blocker, compare shared 2-bedroom options before stretching monthly rent."
  ];
}

function buildDashboard(userId, overrides = {}) {
  const baseUser = getUser(userId);
  const user = {
    ...baseUser,
    city: overrides.city || baseUser.city,
    finances: { ...baseUser.finances, ...(overrides.finances || {}) }
  };
  const budget = calculateBudget(user.finances, overrides.expenses || expenses);
  const selectedAreaIds = Array.isArray(overrides.areaIds) ? overrides.areaIds : [];
  const decoratedListings = listings
    .filter((listing) => listing.city === user.city)
    .filter((listing) => selectedAreaIds.length === 0 || selectedAreaIds.includes(listing.areaId))
    .map((listing) => {
      const costs = getListingCost(listing, user.finances.roommates);
      return {
        ...listing,
        ...costs,
        fit: getFit(costs.monthlyTotal, budget.housingBudget, costs.moveInCash, user.finances.cash)
      };
    })
    .sort((a, b) => a.monthlyTotal - b.monthlyTotal);

  return {
    user,
    expenses: overrides.expenses || expenses,
    cityMap: cityMaps[user.city],
    budget,
    selectedAreaIds,
    listings: decoratedListings,
    recommendations: buildRoleRecommendations(user)
  };
}

const topicKeywords = [
  "rent", "budget", "housing", "apartment", "listing", "move-in", "deposit",
  "roommate", "commute", "area", "neighborhood", "fees", "afford", "lease", "city"
];

function isAllowedAssistantTopic(message) {
  const normalized = String(message || "").toLowerCase();
  return topicKeywords.some((keyword) => normalized.includes(keyword));
}

function buildAssistantReply({ userId, message, areaIds = [] }) {
  const dashboard = buildDashboard(userId, { areaIds });
  const { user, budget, listings: visibleListings } = dashboard;
  const normalized = String(message || "").toLowerCase();
  const selectedAreas = dashboard.cityMap.areas.filter((area) => areaIds.includes(area.id)).map((area) => area.name);
  const topListing = visibleListings[0];

  if (!isAllowedAssistantTopic(message)) {
    return {
      allowed: false,
      reply: "I can only answer questions about housing affordability, listings, neighborhoods, move-in costs, roommates, and budgeting inside RentCheck Me."
    };
  }

  if (normalized.includes("move-in") || normalized.includes("deposit")) {
    const sample = topListing ? `${topListing.title} needs about $${Math.round(topListing.moveInCash).toLocaleString()} upfront.` : "There are no listings in the selected area right now.";
    return {
      allowed: true,
      reply: `You currently have a housing budget of $${Math.round(budget.housingBudget).toLocaleString()} per month and $${Math.round(user.finances.cash).toLocaleString()} in move-in cash. ${sample} Focus on application fee plus first month plus deposit before stretching on monthly rent.`
    };
  }

  if (normalized.includes("roommate")) {
    const sharedListing = visibleListings.find((listing) => listing.shareDivisor > 1);
    return {
      allowed: true,
      reply: sharedListing ? `A roommate changes the math most on shared 2-bedroom listings. ${sharedListing.title} lands near $${Math.round(sharedListing.monthlyTotal).toLocaleString()} per person monthly in the current filter.` : "There is no shared listing in the current area filter. Clear the map selection or switch cities to compare roommate options."
    };
  }

  const areaSentence = selectedAreas.length > 0 ? `inside ${selectedAreas.join(", ")}` : `across ${user.city}`;
  const suggestion = topListing ? `${topListing.title} is the best current fit at about $${Math.round(topListing.monthlyTotal).toLocaleString()} per month.` : "No listing currently matches the selected area.";
  return {
    allowed: true,
    reply: `Your current affordability cap is $${Math.round(budget.housingBudget).toLocaleString()} per month ${areaSentence}. ${suggestion} Hidden monthly costs are already included, so compare total monthly cost instead of base rent alone.`
  };
}

module.exports = { buildAssistantReply, buildDashboard, users };
