package ru.naimix.vacancy.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.naimix.vacancy.api.dto.CreateVacancyRequest;
import ru.naimix.vacancy.api.dto.PageResponse;
import ru.naimix.vacancy.api.dto.VacancyView;
import ru.naimix.vacancy.domain.Vacancy;
import ru.naimix.vacancy.domain.VacancyRepository;
import ru.naimix.vacancy.error.VacancyNotFoundException;

/**
 * Vacancy lifecycle with a read-through cache.
 */
@Service
public class VacancyService {

    private final VacancyRepository repository;
    private final VacancyCache cache;

    /**
     * Creates the service.
     *
     * @param repository vacancy storage
     * @param cache read-through cache
     */
    public VacancyService(final VacancyRepository repository, final VacancyCache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    /**
     * Publishes a new vacancy.
     *
     * @param request creation payload
     * @return stored vacancy
     */
    @Transactional
    public VacancyView create(final CreateVacancyRequest request) {
        return toView(repository.save(fromRequest(request)));
    }

    /**
     * Returns a vacancy by identifier, preferring the cache.
     *
     * @param id vacancy identifier
     * @return vacancy projection
     */
    @Transactional(readOnly = true)
    public VacancyView byId(final UUID id) {
        return cache.get(id).or(() -> fresh(id)).orElseThrow(() -> new VacancyNotFoundException(id));
    }

    /**
     * Returns a page of vacancies.
     *
     * @param page zero based page number
     * @param size page size
     * @return paged projection
     */
    @Transactional(readOnly = true)
    public PageResponse<VacancyView> page(final int page, final int size) {
        return toPage(repository.findAll(PageRequest.of(page, size)));
    }

    /**
     * Changes the status of a vacancy.
     *
     * @param id vacancy identifier
     * @param status target status
     * @return updated vacancy
     */
    @Transactional
    public VacancyView changeStatus(final UUID id, final String status) {
        return refreshed(save(require(id), status));
    }

    /**
     * Removes a vacancy.
     *
     * @param id vacancy identifier
     */
    @Transactional
    public void delete(final UUID id) {
        repository.delete(require(id));
        cache.evict(id);
    }

    private Optional<VacancyView> fresh(final UUID id) {
        return cached(repository.findById(id).map(this::toView));
    }

    private Optional<VacancyView> cached(final Optional<VacancyView> view) {
        view.ifPresent(cache::put);
        return view;
    }

    private Vacancy require(final UUID id) {
        return repository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
    }

    private Vacancy save(final Vacancy vacancy, final String status) {
        vacancy.changeStatus(status);
        return repository.save(vacancy);
    }

    private VacancyView refreshed(final Vacancy vacancy) {
        cache.evict(vacancy.getId());
        return toView(vacancy);
    }

    private Vacancy fromRequest(final CreateVacancyRequest request) {
        return new Vacancy(request.title(), request.description(), request.city(),
                request.rateMin(), request.rateMax());
    }

    private VacancyView toView(final Vacancy vacancy) {
        return new VacancyView(vacancy.getId(), vacancy.getTitle(), vacancy.getDescription(), vacancy.getCity(),
                vacancy.getRateMin(), vacancy.getRateMax(), vacancy.getStatus(), vacancy.getCreatedAt());
    }

    private PageResponse<VacancyView> toPage(final Page<Vacancy> found) {
        return new PageResponse<>(found.map(this::toView).getContent(), found.getNumber(), found.getSize(),
                found.getTotalElements());
    }
}
