const OWNER_ID = "11111111-1111-1111-1111-111111111111";

const state = { vacancyId: null, holdId: null, candidates: [] };

const qs = (selector) => document.querySelector(selector);

const setText = (selector, value) => {
  qs(selector).textContent = value;
};

const setDot = (ok) => {
  qs("#stack-dot").style.background = ok ? "#0c6565" : "#fe982d";
};

const setSideStatus = (ok) => {
  setText("#side-status", ok ? "стек на связи" : "демо-режим");
  setDot(ok);
};

const setStatus = (value, ok) => {
  setText("#stack-status", value);
  setSideStatus(ok);
};

const percent = (score) => Math.round(score * 100) + "%";

const money = (value) => Number(value).toLocaleString("ru-RU") + " ₽";

const skillsLine = (skills) => (skills || []).join(" · ");

const candidateCard = (candidate) =>
  '<div class="candidate"><div><div class="candidate__name">' + candidate.name + "</div>" +
  '<div class="candidate__meta">' + candidate.city + " · " + skillsLine(candidate.skills) + "</div></div>" +
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
  container.innerHTML = candidates.map(candidateCard).join("");
  bindHireButtons(container);
};

const drawCandidates = (candidates) => {
  fillCandidates(qs("#candidates"), candidates);
};

const draftPayload = () => ({
  ownerId: OWNER_ID,
  title: qs("#title").value,
  city: qs("#city").value,
  skills: qs("#skills").value.split(",").map((skill) => skill.trim()),
  budget: Number(qs("#budget").value)
});

const renderText = (draft) => {
  setText("#draft-text", draft.description);
};

const renderPick = (draft) => {
  state.candidates = draft.candidates;
  drawCandidates(draft.candidates);
};

const announceDraft = (draft) => {
  setStatus("проект создан · " + draft.candidates.length + " кандидатов", true);
};

const paintDraft = (draft) => {
  renderText(draft);
  renderPick(draft);
};

const renderDraft = (draft) => {
  paintDraft(draft);
  announceDraft(draft);
};

const rememberVacancy = (draft) => {
  state.vacancyId = draft.vacancyId;
};

const showDraft = (draft) => {
  rememberVacancy(draft);
  renderDraft(draft);
};

const hirePayload = (freelancerId, freelancerName) => ({
  freelancerId: freelancerId,
  freelancerName: freelancerName,
  amount: Number(qs("#amount").value)
});

const enableDeal = () => {
  qs("#accept-run").disabled = false;
  qs("#cancel-run").disabled = false;
};

const dealCard = (assignment) =>
  '<div class="candidate"><div><div class="candidate__name">' + assignment.status + "</div>" +
  '<div class="candidate__meta">hold ' + assignment.holdId + "</div></div>" +
  '<span class="badge badge--live">' + money(assignment.amount) + "</span></div>";

const paintNotification = (assignment) => {
  qs("#notification-state").textContent = "Уведомление: " + assignment.notificationId;
};

const paintDeal = (assignment) => {
  qs("#deal-cards").innerHTML = dealCard(assignment);
  enableDeal();
};

const renderAssignment = (assignment) => {
  paintDeal(assignment);
  paintNotification(assignment);
};

const rememberHold = (assignment) => {
  state.holdId = assignment.holdId;
};

const showAssignment = (assignment) => {
  rememberHold(assignment);
  renderAssignment(assignment);
};

const failAssignment = (error) => {
  setText("#notification-state", "Ошибка: " + error.message);
  setStatus("сервис недоступен", false);
};

const requestAssignment = (freelancerId, freelancerName) => {
  window.workspaceApi.assign(state.vacancyId, hirePayload(freelancerId, freelancerName))
    .then(showAssignment)
    .catch(failAssignment);
};

const rememberFreelancer = (name) => {
  setText("#deal-hint", "Исполнитель: " + name);
};

const hire = (freelancerId, freelancerName) => {
  rememberFreelancer(freelancerName);
  requestAssignment(freelancerId, freelancerName);
};

const runDraft = () => {
  window.workspaceApi.draft(draftPayload()).then(showDraft).catch(failAssignment);
};

const settle = (action) => {
  const call = action === "complete" ? "complete" : "cancel";
  window.workspaceApi[call](state.vacancyId, state.holdId).then(showAssignment).catch(failAssignment);
};

const runAccept = () => settle("complete");

const runCancel = () => settle("cancel");

const moneyCard = (label, value) =>
  '<div class="kpi__card"><div class="kpi__value">' + value + '</div><div class="kpi__label">' + label + "</div></div>";

const kpiRow = (label, value) => "<tr><td>" + label + "</td><td>" + value + "</td></tr>";

const eventRows = (overview) => overview.analytics.top.map((item) => kpiRow(item.name, item.value));

const kpiCards = (overview) => [
  moneyCard("событий", overview.analytics.total),
  moneyCard("вакансий", overview.vacancies.total),
  moneyCard("типов событий", overview.analytics.top.length)
].join("");

const kpiRows = (overview) => [kpiRow("Всего событий", overview.analytics.total)]
  .concat(eventRows(overview))
  .concat([kpiRow("Всего вакансий", overview.vacancies.total)]);

const showKpi = (overview) => {
  qs("#kpi-cards").innerHTML = kpiCards(overview);
  qs("#kpi-body").innerHTML = kpiRows(overview).join("");
};

const TITLES = { project: "Новый проект", deal: "Сделка и эскроу", kpi: "Показатели" };

const hideOthers = (name) => {
  document.querySelectorAll(".view").forEach((view) => {
    view.hidden = view.dataset.view !== name;
  });
};

const markActive = (name) => {
  document.querySelectorAll(".side__link").forEach((link) => {
    link.classList.toggle("side__link--active", link.dataset.view === name);
  });
};

const focusSection = (name) => {
  markActive(name);
  setText("#view-title", TITLES[name]);
};

const showView = (name) => {
  hideOthers(name);
  focusSection(name);
};

const bindNav = (link) => {
  link.addEventListener("click", (event) => {
    event.preventDefault();
    showView(link.dataset.view);
  });
};

const loadOverview = () => {
  window.workspaceApi.overview().then(showKpi).catch(() => setStatus("workspace недоступен", false));
};

const bind = (selector, handler) => {
  qs(selector).addEventListener("click", handler);
};

const BINDINGS = [["#draft-run", runDraft], ["#accept-run", runAccept], ["#cancel-run", runCancel]];

const bindPair = (pair) => bind(pair[0], pair[1]);

const wireNav = () => {
  document.querySelectorAll(".side__link").forEach(bindNav);
};

const start = () => {
  wireNav();
  loadOverview();
};

const main = () => {
  BINDINGS.forEach(bindPair);
  start();
};

window.__demo = { showDraft: showDraft, showAssignment: showAssignment, showKpi: showKpi, view: showView };

document.addEventListener("DOMContentLoaded", main);
