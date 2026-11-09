package ru.naimix.vacancy.domain;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Storage of vacancies.
 */
public interface VacancyRepository extends JpaRepository<Vacancy, UUID> {

    /**
     * Returns vacancies with the given status.
     *
     * @param status status filter
     * @param pageable paging request
     * @return page of vacancies
     */
    Page<Vacancy> findByStatus(String status, Pageable pageable);
}
