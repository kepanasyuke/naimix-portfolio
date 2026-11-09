package ru.naimix.matching.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * Freelancer profile used for ranking.
 *
 * @param id profile identifier
 * @param name display name
 * @param city home city
 * @param rate hourly rate
 * @param skills declared skills
 * @param summary free form self description
 */
public record FreelancerProfile(String id, String name, String city, BigDecimal rate,
                                List<String> skills, String summary) {

    /**
     * Returns the text representation used for similarity.
     *
     * @return skills and summary joined into one document
     */
    public String document() {
        return String.join(" ", skills) + " " + summary;
    }
}
