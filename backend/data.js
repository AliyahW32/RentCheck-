const users = [
  {
    id: "u-renter",
    name: "Jordan Carter",
    role: "renter",
    city: "Charlotte, NC",
    finances: { income: 4600, debt: 350, savings: 450, cash: 5200, roommates: 0 },
    preferences: { beds: "1 bed", maxCommute: 35, priorities: ["budget", "walkability", "low fees"] }
  },
  {
    id: "u-agent",
    name: "Maya Singh",
    role: "agent",
    city: "Atlanta, GA",
    finances: { income: 6800, debt: 420, savings: 600, cash: 9000, roommates: 0 },
    preferences: { beds: "1 bed", maxCommute: 30, priorities: ["inventory visibility", "commute", "move-in cash"] }
  },
  {
    id: "u-admin",
    name: "Casey Brooks",
    role: "admin",
    city: "Durham, NC",
    finances: { income: 7200, debt: 250, savings: 800, cash: 12500, roommates: 1 },
    preferences: { beds: "2 bed", maxCommute: 25, priorities: ["operations", "coverage", "risk"] }
  }
];

const expenses = [
  { category: "Transportation", name: "Car payment", amount: 315 },
  { category: "Transportation", name: "Gas + maintenance", amount: 165 },
  { category: "Food", name: "Groceries", amount: 420 },
  { category: "Health", name: "Prescriptions", amount: 55 },
  { category: "Utilities", name: "Phone", amount: 85 },
  { category: "Lifestyle", name: "Subscriptions", amount: 42 },
  { category: "Safety", name: "Emergency buffer", amount: 200 }
];

const cityMaps = {
  "Charlotte, NC": {
    viewBox: "0 0 420 280",
    areas: [
      { id: "south-end", name: "South End", points: "32,178 125,148 156,228 74,252" },
      { id: "noda", name: "NoDa", points: "198,62 290,36 324,112 238,144" },
      { id: "dilworth", name: "Dilworth", points: "128,164 215,144 242,220 154,246" },
      { id: "uptown", name: "Uptown", points: "154,102 226,82 254,142 182,162" }
    ]
  },
  "Atlanta, GA": {
    viewBox: "0 0 420 280",
    areas: [
      { id: "midtown", name: "Midtown", points: "128,72 212,48 244,120 162,144" },
      { id: "grant-park", name: "Grant Park", points: "192,146 294,138 310,234 218,246" },
      { id: "old-fourth-ward", name: "Old Fourth Ward", points: "224,74 316,64 342,136 254,144" },
      { id: "west-midtown", name: "West Midtown", points: "76,92 156,74 174,152 92,164" }
    ]
  },
  "Durham, NC": {
    viewBox: "0 0 420 280",
    areas: [
      { id: "central-park", name: "Central Park", points: "164,80 250,62 274,142 188,156" },
      { id: "brightleaf", name: "Brightleaf", points: "100,110 174,92 192,166 116,182" },
      { id: "trinity-park", name: "Trinity Park", points: "136,42 212,30 234,88 158,100" },
      { id: "downtown", name: "Downtown", points: "194,146 282,136 300,212 216,224" }
    ]
  }
};

const listings = [
  { id: 1, title: "South End Studio", city: "Charlotte, NC", beds: "Studio", neighborhood: "South End", areaId: "south-end", rent: 1395, utilities: 145, internet: 65, parking: 95, insurance: 18, transit: 70, pet: 0, fees: { application: 75, amenity: 35 }, deposit: 1395 },
  { id: 2, title: "NoDa One-Bedroom", city: "Charlotte, NC", beds: "1 bed", neighborhood: "NoDa", areaId: "noda", rent: 1540, utilities: 165, internet: 65, parking: 85, insurance: 18, transit: 45, pet: 30, fees: { application: 100, amenity: 40 }, deposit: 1540 },
  { id: 3, title: "Dilworth Shared 2BR", city: "Charlotte, NC", beds: "2 bed", neighborhood: "Dilworth", areaId: "dilworth", rent: 1925, utilities: 210, internet: 70, parking: 60, insurance: 22, transit: 65, pet: 0, fees: { application: 60, amenity: 25 }, deposit: 1925 },
  { id: 4, title: "Midtown Garden Apartment", city: "Atlanta, GA", beds: "1 bed", neighborhood: "Midtown", areaId: "midtown", rent: 1710, utilities: 160, internet: 65, parking: 110, insurance: 19, transit: 80, pet: 35, fees: { application: 90, amenity: 38 }, deposit: 1710 },
  { id: 5, title: "Grant Park Shared Loft", city: "Atlanta, GA", beds: "2 bed", neighborhood: "Grant Park", areaId: "grant-park", rent: 2050, utilities: 225, internet: 70, parking: 45, insurance: 24, transit: 55, pet: 0, fees: { application: 75, amenity: 28 }, deposit: 2050 },
  { id: 6, title: "Old Fourth Ward Flex", city: "Atlanta, GA", beds: "1 bed", neighborhood: "Old Fourth Ward", areaId: "old-fourth-ward", rent: 1670, utilities: 150, internet: 60, parking: 80, insurance: 18, transit: 55, pet: 20, fees: { application: 85, amenity: 32 }, deposit: 1670 },
  { id: 7, title: "Downtown Durham One-Bedroom", city: "Durham, NC", beds: "1 bed", neighborhood: "Central Park", areaId: "central-park", rent: 1485, utilities: 145, internet: 60, parking: 85, insurance: 18, transit: 35, pet: 25, fees: { application: 85, amenity: 32 }, deposit: 1485 },
  { id: 8, title: "Brightleaf Split-Level", city: "Durham, NC", beds: "2 bed", neighborhood: "Brightleaf", areaId: "brightleaf", rent: 1820, utilities: 195, internet: 60, parking: 35, insurance: 22, transit: 40, pet: 0, fees: { application: 60, amenity: 20 }, deposit: 1820 }
];

module.exports = { cityMaps, expenses, listings, users };
