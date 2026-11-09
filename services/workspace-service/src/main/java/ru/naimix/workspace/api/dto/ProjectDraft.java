package ru.naimix.workspace.api.dto;

import java.util.List;
import java.util.Map;

/**
 * Drafted project with generated description and ranked candidates.
 *
 * @param vacancyId created vacancy
 * @param description generated description
 * @param candidates ranked freelancers
 */
public record ProjectDraft(String vacancyId, String description, List<Map<String, Object>> candidates) {
}
