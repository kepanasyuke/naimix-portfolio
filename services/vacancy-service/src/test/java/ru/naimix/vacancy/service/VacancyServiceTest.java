package ru.naimix.vacancy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.naimix.vacancy.api.dto.VacancyView;
import ru.naimix.vacancy.domain.Vacancy;
import ru.naimix.vacancy.domain.VacancyRepository;

class VacancyServiceTest {

    private static final UUID ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String CACHED_TITLE = "Java-разработчик";
    private static final String STORED_TITLE = "Вакансия из базы";

    private final VacancyRepository repository = mock(VacancyRepository.class);
    private final VacancyCache cache = mock(VacancyCache.class);
    private final VacancyService service = new VacancyService(repository, cache);

    @Test
    void returnsCachedProjectionWithoutDatabase() {
        when(cache.get(ID)).thenReturn(Optional.of(cached()));
        assertEquals(CACHED_TITLE, service.byId(ID).title());
    }

    @Test
    void fallsBackToDatabaseWhenCacheIsEmpty() {
        givenStoredVacancy();
        assertEquals(STORED_TITLE, service.byId(ID).title());
    }

    private void givenStoredVacancy() {
        when(cache.get(ID)).thenReturn(Optional.empty());
        when(repository.findById(ID)).thenReturn(Optional.of(stored()));
    }

    private VacancyView cached() {
        return new VacancyView(ID, CACHED_TITLE, "Описание вакансии", "Москва",
                new BigDecimal("2500"), new BigDecimal("3800"), "OPEN", Instant.EPOCH);
    }

    private Vacancy stored() {
        return new Vacancy(STORED_TITLE, "Описание вакансии из базы", "Москва",
                new BigDecimal("2500"), new BigDecimal("3800"));
    }
}
