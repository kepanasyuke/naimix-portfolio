package ru.naimix.workspace.api;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.naimix.workspace.api.dto.AssignRequest;
import ru.naimix.workspace.api.dto.Assignment;
import ru.naimix.workspace.api.dto.ProjectDraft;
import ru.naimix.workspace.api.dto.ProjectDraftRequest;
import ru.naimix.workspace.service.WorkspaceService;

/**
 * Business workspace endpoints: full project lifecycle.
 */
@RestController
@RequestMapping("/api/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    /**
     * Creates the controller.
     *
     * @param workspaceService workspace service
     */
    public WorkspaceController(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    /**
     * Creates a project draft with generated description and candidates.
     *
     * @param request draft request
     * @return drafted project
     */
    @PostMapping("/projects")
    public ResponseEntity<ProjectDraft> draft(@Valid @RequestBody final ProjectDraftRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceService.draft(request));
    }

    /**
     * Assigns a freelancer and reserves the escrow.
     *
     * @param vacancyId project reference
     * @param request assignment request
     * @return assignment with escrow status
     */
    @PostMapping("/projects/{vacancyId}/assign")
    public Assignment assign(@PathVariable final String vacancyId,
                             @Valid @RequestBody final AssignRequest request) {
        return workspaceService.assign(vacancyId, request);
    }

    /**
     * Captures the escrow after delivery.
     *
     * @param vacancyId project reference
     * @param holdId escrow hold
     * @return settled assignment
     */
    @PostMapping("/projects/{vacancyId}/complete/{holdId}")
    public Assignment complete(@PathVariable final String vacancyId, @PathVariable final String holdId) {
        return workspaceService.complete(vacancyId, holdId);
    }

    /**
     * Refunds the escrow when the project is cancelled.
     *
     * @param vacancyId project reference
     * @param holdId escrow hold
     * @return refunded assignment
     */
    @PostMapping("/projects/{vacancyId}/cancel/{holdId}")
    public Assignment cancel(@PathVariable final String vacancyId, @PathVariable final String holdId) {
        return workspaceService.cancel(vacancyId, holdId);
    }

    /**
     * Returns the business overview.
     *
     * @return counters and vacancy totals
     */
    @GetMapping("/overview")
    public Map<String, Object> overview() {
        return workspaceService.overview();
    }
}
