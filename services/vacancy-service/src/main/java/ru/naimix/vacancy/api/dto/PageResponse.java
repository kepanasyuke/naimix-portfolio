package ru.naimix.vacancy.api.dto;

import java.util.List;

/**
 * Paged response payload.
 *
 * @param items page content
 * @param page zero based page number
 * @param size page size
 * @param total total elements
 */
public record PageResponse<T>(List<T> items, int page, int size, long total) {
}
