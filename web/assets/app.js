const MATCH_URL = "http://localhost:8083/api/match";
const ASSISTANT_URL = "http://localhost:8087/api/assistant/vacancy-description";
const WALLET_URL = "http://localhost:8085/api/billing/wallets";
const NOTIFICATION_URL = "http://localhost:8084/api/notifications";
const ANALYTICS_URL = "http://localhost:8086/api/analytics";
const BYTES_IN_MB = 1048576;

const DEMO_MATCHES = [
  { name: "Анна Ковалёва", city: "Москва", score: 0.82, skills: ["java", "spring", "postgresql", "kafka"] },
  { name: "Ольга Рябова", city: "Новосибирск", score: 0.71, skills: ["java", "spring", "rabbitmq", "postgresql"] },
  { name: "Игорь Плетнёв", city: "Санкт-Петербург", score: 0.64, skills: ["kotlin", "spring", "redis", "kubernetes"] }
];

const qs = (selector) => document.querySelector(selector);

const setText = (selector, value) => {
  qs(selector).textContent = value;
};

const setStatus = (value) => {
  qs("#backend-status").textContent = value;
};

const fetchJson = (url, options) =>
  fetch(url, options).then((response) =>
    response.ok ? response.json() : Promise.reject(new Error("HTTP " + response.status))
  );

const postJson = (url, payload, extraHeaders) => {
  const headers = Object.assign({ "Content-Type": "application/json" }, extraHeaders || {});
  return fetchJson(url, { method: "POST", headers: headers, body: JSON.stringify(payload) });
};

const percent = (score) => Math.round(score * 100) + "%";

const chipList = (skills) =>
  skills
    .map((skill) => '<span class="chip">' + skill + "</span>")
    .join(" ");

const matchRow = (match) =>
  '<div class="result__row"><div><div class="result__name">' + match.name + "</div>" +
  '<div class="result__meta">' + match.city + " · " + chipList(match.skills) + "</div></div>" +
  '<span class="score">' + percent(match.score) + "</span></div>";

const renderMatches = (matches) => {
  qs("#match-results").innerHTML = matches.map(matchRow).join("");
};

const showMatches = (matches, isDemo) => {
  renderMatches(matches);
  setStatus(isDemo ? "демо-данные матчинга" : "ответ matching-service");
};

const runMatch = () => {
  const text = qs("#match-text").value;
  postJson(MATCH_URL, { text: text, limit: 3 })
    .then((matches) => showMatches(matches, false))
    .catch(() => showMatches(DEMO_MATCHES, true));
};

const assistantPayload = () => {
  const skills = qs("#gen-skills").value.split(",");
  return {
    title: qs("#gen-title").value,
    city: qs("#gen-city").value,
    skills: skills,
    rateMin: 2500,
    rateMax: 3800,
    tone: "нейтральный"
  };
};

const showGenerated = (payload) => {
  setText("#gen-output", payload.text);
};

const demoDescription = (payload) =>
  "Ищем специалиста на роль «" + payload.title + "» в городе " + payload.city + ".\n\nОжидания по стеку:\n- " +
  payload.skills.join("\n- ") + "\n\nСтавка: " + payload.rateMin + "–" + payload.rateMax + " руб./час. (демо-ответ)";

const runAssistant = () => {
  const payload = assistantPayload();
  postJson(ASSISTANT_URL, payload, { "X-Client-Id": "dashboard" })
    .then(showGenerated)
    .catch(() => showGenerated({ text: demoDescription(payload) }));
};

const holdPayload = () => ({
  ownerId: "11111111-1111-1111-1111-111111111111",
  initialBalance: Number(qs("#hold-amount").value),
  currency: "RUB"
});

const showHold = (result) => {
  setText("#hold-output", JSON.stringify(result, null, 2));
};

const runHold = () => {
  postJson(WALLET_URL, holdPayload())
    .then(showHold)
    .catch(() => showHold({ status: "демо", amount: qs("#hold-amount").value, note: "billing-service не запущен" }));
};

const notificationPayload = () => ({
  recipient: "candidate@example.com",
  channel: "EMAIL",
  subject: "Задача назначена",
  body: "Проверьте детали задачи в личном кабинете."
});

const showNotification = (result) => {
  setText("#notification-output", JSON.stringify(result, null, 2));
};

const runNotification = () => {
  postJson(NOTIFICATION_URL, notificationPayload(), { "Idempotency-Key": qs("#notification-key").value })
    .then(showNotification)
    .catch(() => showNotification({ status: "демо", dedupKey: qs("#notification-key").value, attempts: 1 }));
};

const metricsRow = (label, value) => "<tr><td>" + label + "</td><td>" + value + "</td></tr>";

const countRow = (item) => metricsRow(item.name, item.value);

const totalRow = (summary) => metricsRow("Всего событий", summary.total);

const jvmRows = (jvm) => [
  metricsRow("Heap, МБ", Math.round(jvm.heapUsed / BYTES_IN_MB)),
  metricsRow("Потоков JVM", jvm.threads)
];

const renderAnalytics = (summary, jvm) => {
  const rows = summary.top.map(countRow);
  qs("#analytics-summary").innerHTML = [totalRow(summary)].concat(rows).concat(jvmRows(jvm)).join("");
};

const demoTotals = () => ({
  total: 1284,
  top: [
    { name: "vacancy_view", value: 612 },
    { name: "match_requested", value: 341 },
    { name: "hold_created", value: 96 }
  ]
});

const demoTotalsJvm = () => ({ heapUsed: 268435456, threads: 42 });

const loadAnalytics = () =>
  Promise.all([fetchJson(ANALYTICS_URL + "/summary"), fetchJson(ANALYTICS_URL + "/jvm")])
    .then((parts) => {
      renderAnalytics(parts[0], parts[1]);
      setStatus("analytics-service отвечает");
    })
    .catch(() => {
      renderAnalytics(demoTotals(), demoTotalsJvm());
      setStatus("демо-данные аналитики");
    });

const bind = (selector, handler) => {
  const node = qs(selector);
  attach(node, handler);
};

const attach = (node, handler) => {
  if (node) {
    node.addEventListener("click", handler);
  }
};

const BINDINGS = [
  ["#match-run", runMatch],
  ["#gen-run", runAssistant],
  ["#hold-run", runHold],
  ["#notification-run", runNotification]
];

const bindPair = (pair) => bind(pair[0], pair[1]);

const main = () => {
  BINDINGS.forEach(bindPair);
  loadAnalytics();
};

document.addEventListener("DOMContentLoaded", main);
