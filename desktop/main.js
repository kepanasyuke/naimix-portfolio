const { app, BrowserWindow, Menu, ipcMain } = require("electron");
const path = require("path");
const fs = require("fs/promises");

const WORKSPACE = process.env.WORKSPACE_URL || "http://127.0.0.1:8090/api/workspace";
const CAPTURE_MODE = process.argv.includes("--capture");
const SHOTS_DIR = path.join(__dirname, "screenshots");

const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function callBackend(method, url, payload) {
  const response = await fetch(url, {
    method: method,
    headers: { "Content-Type": "application/json" },
    body: payload ? JSON.stringify(payload) : undefined
  });
  if (!response.ok) {
    throw new Error("HTTP " + response.status);
  }
  return response.json();
}

const draft = (payload) => callBackend("POST", WORKSPACE + "/projects", payload);
const assign = (vacancyId, payload) =>
  callBackend("POST", WORKSPACE + "/projects/" + vacancyId + "/assign", payload);
const complete = (vacancyId, holdId) =>
  callBackend("POST", WORKSPACE + "/projects/" + vacancyId + "/complete/" + holdId, {});
const cancel = (vacancyId, holdId) =>
  callBackend("POST", WORKSPACE + "/projects/" + vacancyId + "/cancel/" + holdId, {});
const overview = () => callBackend("GET", WORKSPACE + "/overview");

function registerHandlers() {
  ipcMain.handle("workspace:draft", (_event, payload) => draft(payload));
  ipcMain.handle("workspace:assign", (_event, vacancyId, payload) => assign(vacancyId, payload));
  ipcMain.handle("workspace:complete", (_event, vacancyId, holdId) => complete(vacancyId, holdId));
  ipcMain.handle("workspace:cancel", (_event, vacancyId, holdId) => cancel(vacancyId, holdId));
  ipcMain.handle("workspace:overview", () => overview());
}

function createWindow() {
  const win = new BrowserWindow({
    width: 1360,
    height: 900,
    minWidth: 1100,
    minHeight: 720,
    backgroundColor: "#000000",
    title: "Наймикс · Кабинет заказчика",
    autoHideMenuBar: true,
    webPreferences: {
      preload: path.join(__dirname, "preload.js"),
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: false
    }
  });
  win.loadFile(path.join(__dirname, "renderer", "index.html"));
  return win;
}

async function shot(win, name) {
  const image = await win.webContents.capturePage();
  await fs.mkdir(SHOTS_DIR, { recursive: true });
  const target = path.join(SHOTS_DIR, name);
  await fs.writeFile(target, image.toPNG());
  console.log("screenshot: " + target);
}

const DEMO_DRAFT = {
  vacancyId: "demo-vacancy",
  description:
    "Ищем специалиста на роль «Старший backend-разработчик» в городе Москва.\nОжидания по стеку:\n- Java 17\n- Spring Boot\n- PostgreSQL\n- Kafka",
  candidates: [
    { id: "f-1", name: "Анна Ковалёва", city: "Москва", score: 0.82, skills: ["java", "spring", "kafka"] },
    { id: "f-5", name: "Ольга Рябова", city: "Новосибирск", score: 0.71, skills: ["java", "spring", "rabbitmq"] },
    { id: "f-2", name: "Игорь Плетнёв", city: "Санкт-Петербург", score: 0.64, skills: ["kotlin", "redis"] }
  ]
};

const DEMO_ASSIGNMENT = {
  vacancyId: "demo-vacancy",
  holdId: "demo-hold",
  status: "HELD",
  amount: 12000,
  notificationId: "demo-notification"
};

const DEMO_OVERVIEW = {
  analytics: {
    total: 6,
    top: [
      { name: "project_assigned", value: 3 },
      { name: "project_completed", value: 2 },
      { name: "project_cancelled", value: 1 }
    ]
  },
  vacancies: { total: 6 }
};

const DRAFT_REQUEST = {
  ownerId: "11111111-1111-1111-1111-111111111111",
  title: "Старший backend-разработчик",
  city: "Москва",
  skills: ["Java 17", "Spring Boot", "PostgreSQL", "Kafka"],
  budget: 3000
};

async function inject(win, expression) {
  await win.webContents.executeJavaScript(expression);
}

async function captureScenario(win) {
  await delay(1500);

  const draftData = await draft(DRAFT_REQUEST).catch(() => DEMO_DRAFT);
  await inject(win, "window.__demo.showDraft(" + JSON.stringify(draftData) + ")");
  await delay(800);
  await shot(win, "01-project-draft.png");

  const assignment = await assign(draftData.vacancyId, {
    freelancerId: draftData.candidates[0].id,
    freelancerName: draftData.candidates[0].name,
    amount: 12000
  }).catch(() => DEMO_ASSIGNMENT);

  await inject(win, "window.__demo.showAssignment(" + JSON.stringify(assignment) + ")");
  await inject(win, "window.__demo.view('deal')");
  await delay(800);
  await shot(win, "02-escrow-held.png");

  const stats = await overview().catch(() => DEMO_OVERVIEW);
  await inject(win, "window.__demo.showKpi(" + JSON.stringify(stats) + ")");
  await inject(win, "window.__demo.view('kpi')");
  await delay(800);
  await shot(win, "03-kpi.png");

  app.quit();
}

app.whenReady().then(() => {
  Menu.setApplicationMenu(null);
  registerHandlers();
  const win = createWindow();
  if (CAPTURE_MODE) {
    win.webContents.once("did-finish-load", () => captureScenario(win));
  }
  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") {
    app.quit();
  }
});
