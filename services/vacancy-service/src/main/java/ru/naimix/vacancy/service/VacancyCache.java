package ru.naimix.vacancy.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import ru.naimix.vacancy.api.dto.VacancyView;

/**
 * Read-through cache of vacancy projections.
 */
@Component
public class VacancyCache {

    private static final String CACHE_NAME = "vacancies";

    private final Cache cache;

    /**
     * Creates the cache facade.
     *
     * @param cacheManager configured cache manager
     */
    public VacancyCache(final CacheManager cacheManager) {
        this.cache = cacheManager.getCache(CACHE_NAME);
    }

    /**
     * Reads a cached projection.
     *
     * @param id vacancy identifier
     * @return cached projection when present
     */
    public Optional<VacancyView> get(final UUID id) {
        return Optional.ofNullable(cache.get(id, VacancyView.class));
    }

    /**
     * Stores a projection.
     *
     * @param view vacancy projection
     */
    public void put(final VacancyView view) {
        cache.put(view.id(), view);
    }

    /**
     * Drops a cached projection.
     *
     * @param id vacancy identifier
     */
    public void evict(final UUID id) {
        cache.evict(id);
    }
}
