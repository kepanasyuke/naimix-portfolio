const WORKSPACE = "http://127.0.0.1:8090/api/workspace";
const OWNER_ID = "11111111-1111-1111-1111-111111111111";

const state = { vacancyId: null, holdId: null, freelancer: null };

const qs = (selector) => document.querySelector(selector);

const setText = (selector, value) => {
  qs(selector).textContent = value;
};

const setStatus = (value, ok) => {
  setText("#stack-status", value);
  qs("#stack-dot").style.background = ok ? "#0c6565" : "#fe982d";
};

const fetchJson = (url, options) =>
  fetch(url, options).then((response) =>
    response.ok ? response.json() : Promise.reject(new Error("HTTP " + response.status))
  );

const postJson = (url, payload) =>
  fetchJson(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });

const percent = (score) => Math.round(score * 100) + "%";

const skillsLine = (skills) => (skills || []).join(" · ");

const candidateRow = (candidate) =>
  '<div class="result__row"><div><div class="result__name">' + candidate.name + "</div>" +
  '<div class="result__meta">' + candidate.city + " · " + skillsLine(candidate.skills) + "</div></div>" +
  '<span class="score">' + percent(candidate.score) + "</span>" +
  '<button class="btn btn--ghost" type="button" data-id="' + candidate.id + '" data-name="' +
  candidate.name + '">Нанять</button></div>';

const attachHire = (button) => {
  button.addEventListener("click", () => hire(button.dataset.id, button.dataset.name));
};

const bindHireButtons = (container) => {
  container.querySelectorAll("button").forEach(attachHire);
};

const fillCandidates = (container, candidates) => {
  container.innerHTML = candidates.map(candidateRow).join("");
  bindHireButtons(container);
};

const renderCandidates = (candidates) => {
  const container = qs("#candidates");
  fillCandidates(container, candidates);
};

const draftPayload = () => ({
  ownerId: OWNER_ID,
  title: qs("#title").value,
  city: qs("#city").value,
  skills: qs("#skills").value.split(",").map((skill) => skill.trim()),
  budget: Number(qs("#budget").value)
});

const rememberVacancy = (draft) => {
  state.vacancyId = draft.vacancyId;
};

const drawCandidates = (draft) => {
  renderCandidates(draft.candidates);
  setStatus("проект создан, подобрано " + draft.candidates.length, true);
};

const renderDraft = (draft) => {
  setText("#draft-text", draft.description);
  drawCandidates(draft);
};

const showDraft = (draft) => {
  rememberVacancy(draft);
  renderDraft(draft);
};

const fallbackDraft = () => ({
  vacancyId: "demo",
  description: "Демо-режим: workspace-service недоступен.\n\nЗапустите стек: scripts/start-stack.ps1",
  candidates: [
    { id: "f-1", name: "Анна Ковалёва", city: "Москва", score: 0.82, skills: ["java", "spring", "kafka"] },
    { id: "f-5", name: "Ольга Рябова", city: "Новосибирск", score: 0.71, skills: ["java", "spring", "rabbitmq"] }
  ]
});

const runDraft = () => {
  postJson(WORKSPACE + "/projects", draftPayload())
    .then(showDraft)
    .catch(() => showDraft(fallbackDraft()));
};

const hirePayload = (freelancerId, freelancerName) => ({
  freelancerId: freelancerId,
  freelancerName: freelancerName,
  amount: Number(qs("#amount").value)
});

const enableDeal = (enabled) => {
  qs("#accept-run").disabled = !enabled;
  qs("#cancel-run").disabled = !enabled;
};

const showAssignment = (assignment) => {
  rememberHold(assignment);
  renderAssignment(assignment);
};

const rememberHold = (assignment) => {
  state.holdId = assignment.holdId;
};

const renderAssignment = (assignment) => {
  setText("#deal-state", JSON.stringify(assignment, null, 2));
  enableDeal(true);
};

const showHireFailure = (error) => {
  setText("#deal-state", "Не удалось создать сделку: " + error.message);
  setStatus("ошибка сделки", false);
};

const rememberFreelancer = (name) => {
  state.freelancer = name;
  setText("#deal-hint", "Исполнитель: " + name);
};

const requestAssignment = (freelancerId, freelancerName) => {
  const url = WORKSPACE + "/projects/" + state.vacancyId + "/assign";
  postJson(url, hirePayload(freelancerId, freelancerName)).then(showAssignment).catch(showHireFailure);
};

const hire = (freelancerId, freelancerName) => {
  rememberFreelancer(freelancerName);
  requestAssignment(freelancerId, freelancerName);
};

const settle = (action) => {
  const url = WORKSPACE + "/projects/" + state.vacancyId + "/" + action + "/" + state.holdId;
  postJson(url, {}).then(showAssignment).catch(showHireFailure);
};

const runAccept = () => settle("complete");

const runCancel = () => settle("cancel");

const kpiRow = (label, value) => "<tr><td>" + label + "</td><td>" + value + "</td></tr>";

const eventRows = (overview) => overview.analytics.top.map((item) => kpiRow("Событие: " + item.name, item.value));

const kpiRows = (overview) => [kpiRow("Событий в аналитике", overview.analytics.total)]
  .concat(eventRows(overview))
  .concat([kpiRow("Всего вакансий", overview.vacancies.total)]);

const renderKpi = (overview) => {
  qs("#kpi-body").innerHTML = kpiRows(overview).join("");
};

const showOverview = (overview) => {
  renderKpi(overview);
  setStatus("стек отвечает: " + overview.vacancies.total + " вакансий", true);
};

const loadOverview = () =>
  fetchJson(WORKSPACE + "/overview")
    .then(showOverview)
    .catch(() => setStatus("workspace-service не запущен — демо-режим", false));

const bind = (selector, handler) => {
  qs(selector).addEventListener("click", handler);
};

const BINDINGS = [
  ["#draft-run", runDraft],
  ["#accept-run", runAccept],
  ["#cancel-run", runCancel]
];

const bindPair = (pair) => bind(pair[0], pair[1]);

const main = () => {
  BINDINGS.forEach(bindPair);
  loadOverview();
};

document.addEventListener("DOMContentLoaded", main);
