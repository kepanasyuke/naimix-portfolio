package ru.naimix.workspace.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import ru.naimix.workspace.api.dto.AssignRequest;
import ru.naimix.workspace.api.dto.Assignment;
import ru.naimix.workspace.api.dto.ProjectDraft;
import ru.naimix.workspace.api.dto.ProjectDraftRequest;
import ru.naimix.workspace.gateway.ServiceGateway;

/**
 * Business flow: draft a project, assign a freelancer with escrow, settle it.
 */
@Service
public class WorkspaceService {

    private static final String OWNER = "11111111-1111-1111-1111-111111111111";
    private static final String CURRENCY = "RUB";
    private static final String TONE = "деловой";
    private static final BigDecimal WALLET_BALANCE = new BigDecimal("1000000.00");
    private static final int CANDIDATE_LIMIT = 5;

    private final ServiceGateway gateway;
    private final Map<String, String> wallets = new ConcurrentHashMap<>();

    /**
     * Creates the service.
     *
     * @param gateway platform gateway
     */
    public WorkspaceService(final ServiceGateway gateway) {
        this.gateway = gateway;
    }

    /**
     * Creates a vacancy with a generated description and ranked candidates.
     *
     * @param request draft request
     * @return drafted project
     */
    public ProjectDraft draft(final ProjectDraftRequest request) {
        final Map<String, Object> generated = gateway.describe(descriptionPayload(request));
        return new ProjectDraft(createdVacancy(request, generated), text(generated), candidates(request, generated));
    }

    /**
     * Assigns a freelancer and reserves the escrow amount.
     *
     * @param vacancyId project reference
     * @param request assignment request
     * @return assignment with escrow status
     */
    public Assignment assign(final String vacancyId, final AssignRequest request) {
        final Map<String, Object> hold = gateway.hold(wallet(), holdPayload(vacancyId, request));
        return assigned(vacancyId, request, hold);
    }

    /**
     * Captures the escrow after successful delivery.
     *
     * @param vacancyId project reference
     * @param holdId escrow hold
     * @return settled assignment
     */
    public Assignment complete(final String vacancyId, final String holdId) {
        final Map<String, Object> hold = gateway.capture(holdId);
        return settled(vacancyId, hold, "project_completed");
    }

    /**
     * Refunds the escrow when the project is cancelled.
     *
     * @param vacancyId project reference
     * @param holdId escrow hold
     * @return refunded assignment
     */
    public Assignment cancel(final String vacancyId, final String holdId) {
        final Map<String, Object> hold = gateway.release(holdId);
        return settled(vacancyId, hold, "project_cancelled");
    }

    /**
     * Returns the business overview of the workspace.
     *
     * @return counters and vacancy totals
     */
    public Map<String, Object> overview() {
        return Map.of("analytics", gateway.summary(), "vacancies", gateway.vacancies());
    }

    private Assignment assigned(final String vacancyId, final AssignRequest request, final Map<String, Object> hold) {
        gateway.recordEvent("project_assigned");
        return new Assignment(vacancyId, id(hold), status(hold), request.amount(), notified(vacancyId, request));
    }

    private Assignment settled(final String vacancyId, final Map<String, Object> hold, final String event) {
        gateway.recordEvent(event);
        return new Assignment(vacancyId, id(hold), status(hold), amount(hold), null);
    }

    private String createdVacancy(final ProjectDraftRequest request, final Map<String, Object> generated) {
        return id(gateway.createVacancy(vacancyPayload(request, generated)));
    }

    private List<Map<String, Object>> candidates(final ProjectDraftRequest request,
                                                 final Map<String, Object> generated) {
        return gateway.match(matchPayload(request, generated));
    }

    private String notified(final String vacancyId, final AssignRequest request) {
        final String key = "assign-" + vacancyId;
        return id(gateway.deliver(id(gateway.notify(notificationPayload(request), key))));
    }

    private String wallet() {
        return wallets.computeIfAbsent(OWNER, owner -> createdWallet());
    }

    private String createdWallet() {
        return id(gateway.createWallet(Map.of("ownerId", OWNER, "initialBalance", WALLET_BALANCE,
                "currency", CURRENCY)));
    }

    private Map<String, Object> descriptionPayload(final ProjectDraftRequest request) {
        return Map.of("title", request.title(), "city", request.city(), "skills", request.skills(),
                "rateMin", request.budget(), "rateMax", request.budget().multiply(BigDecimal.valueOf(2)),
                "tone", TONE);
    }

    private Map<String, Object> vacancyPayload(final ProjectDraftRequest request, final Map<String, Object> generated) {
        return Map.of("title", request.title(), "description", text(generated), "city", request.city(),
                "rateMin", request.budget(), "rateMax", request.budget().multiply(BigDecimal.valueOf(2)));
    }

    private Map<String, Object> matchPayload(final ProjectDraftRequest request, final Map<String, Object> generated) {
        return Map.of("text", text(generated) + " " + String.join(" ", request.skills()),
                "limit", CANDIDATE_LIMIT);
    }

    private Map<String, Object> holdPayload(final String vacancyId, final AssignRequest request) {
        return Map.of("amount", request.amount(), "idempotencyKey", "hold-" + vacancyId);
    }

    private Map<String, Object> notificationPayload(final AssignRequest request) {
        return Map.of("recipient", "freelancer@example.com", "channel", "EMAIL", "subject", "Проект назначен",
                "body", "Исполнитель " + request.freelancerName() + " назначен на проект.");
    }

    private String text(final Map<String, Object> generated) {
        return String.valueOf(generated.get("text"));
    }

    private String id(final Map<String, Object> body) {
        return String.valueOf(body.get("id"));
    }

    private String status(final Map<String, Object> body) {
        return String.valueOf(body.get("status"));
    }

    private BigDecimal amount(final Map<String, Object> body) {
        return new BigDecimal(String.valueOf(body.get("amount")));
    }
}
