const http = require("http");
const { URL } = require("url");
const { buildAssistantReply, buildDashboard, users } = require("./logic");

const port = Number(process.env.PORT || 3001);

function sendJson(res, statusCode, payload) {
  res.writeHead(statusCode, {
    "Content-Type": "application/json; charset=utf-8",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET,POST,OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type"
  });
  res.end(JSON.stringify(payload));
}

function readBody(req) {
  return new Promise((resolve, reject) => {
    let raw = "";
    req.on("data", (chunk) => {
      raw += chunk;
    });
    req.on("end", () => {
      if (!raw) {
        resolve({});
        return;
      }
      try {
        resolve(JSON.parse(raw));
      } catch (error) {
        reject(error);
      }
    });
    req.on("error", reject);
  });
}

const server = http.createServer(async (req, res) => {
  if (!req.url) {
    sendJson(res, 404, { error: "Not found" });
    return;
  }

  if (req.method === "OPTIONS") {
    sendJson(res, 204, {});
    return;
  }

  const url = new URL(req.url, `http://${req.headers.host}`);

  if (req.method === "GET" && url.pathname === "/api/users") {
    sendJson(res, 200, users.map((user) => ({
      id: user.id,
      name: user.name,
      role: user.role,
      city: user.city
    })));
    return;
  }

  if (req.method === "GET" && url.pathname === "/api/dashboard") {
    const userId = url.searchParams.get("userId") || users[0].id;
    const areaIds = url.searchParams.getAll("areaId");
    sendJson(res, 200, buildDashboard(userId, { areaIds }));
    return;
  }

  if (req.method === "POST" && url.pathname === "/api/dashboard/calculate") {
    try {
      const body = await readBody(req);
      sendJson(res, 200, buildDashboard(body.userId || users[0].id, {
        city: body.city,
        areaIds: body.areaIds,
        expenses: body.expenses,
        finances: body.finances
      }));
    } catch (error) {
      sendJson(res, 400, { error: "Invalid request body" });
    }
    return;
  }

  if (req.method === "POST" && url.pathname === "/api/assistant/query") {
    try {
      const body = await readBody(req);
      sendJson(res, 200, buildAssistantReply({
        userId: body.userId || users[0].id,
        message: body.message || "",
        areaIds: body.areaIds || []
      }));
    } catch (error) {
      sendJson(res, 400, { error: "Invalid request body" });
    }
    return;
  }

  sendJson(res, 404, { error: "Not found" });
});

server.listen(port, () => {
  console.log(`RentCheck Me backend listening on http://localhost:${port}`);
});
