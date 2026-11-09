package ru.naimix.vacancy.api;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.naimix.vacancy.api.dto.ChangeStatusRequest;
import ru.naimix.vacancy.api.dto.CreateVacancyRequest;
import ru.naimix.vacancy.api.dto.PageResponse;
import ru.naimix.vacancy.api.dto.VacancyView;
import ru.naimix.vacancy.service.VacancyService;

/**
 * Vacancy endpoints.
 */
@RestController
@RequestMapping("/api/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    /**
     * Creates the controller.
     *
     * @param vacancyService vacancy service
     */
    public VacancyController(final VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    /**
     * Publishes a vacancy.
     *
     * @param request creation payload
     * @return stored vacancy
     */
    @PostMapping
    public ResponseEntity<VacancyView> create(@Valid @RequestBody final CreateVacancyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyService.create(request));
    }

    /**
     * Returns one vacancy.
     *
     * @param id vacancy identifier
     * @return vacancy projection
     */
    @GetMapping("/{id}")
    public VacancyView byId(@PathVariable final UUID id) {
        return vacancyService.byId(id);
    }

    /**
     * Returns a page of vacancies.
     *
     * @param page zero based page number
     * @param size page size
     * @return paged projection
     */
    @GetMapping
    public PageResponse<VacancyView> page(@RequestParam(defaultValue = "0") final int page,
                                          @RequestParam(defaultValue = "20") final int size) {
        return vacancyService.page(page, size);
    }

    /**
     * Changes the vacancy status.
     *
     * @param id vacancy identifier
     * @param request status payload
     * @return updated vacancy
     */
    @PatchMapping("/{id}/status")
    public VacancyView changeStatus(@PathVariable final UUID id,
                                    @Valid @RequestBody final ChangeStatusRequest request) {
        return vacancyService.changeStatus(id, request.status());
    }

    /**
     * Removes a vacancy.
     *
     * @param id vacancy identifier
     * @return empty 204 response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        vacancyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
