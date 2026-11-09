package ru.naimix.assistant.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Cover letter generation request.
 *
 * @param name freelancer name
 * @param vacancyTitle target vacancy
 * @param skills declared skills
 * @param experienceYears commercial experience
 */
public record CoverLetterRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 200) String vacancyTitle,
        @NotEmpty List<String> skills,
        @Min(0) Integer experienceYears) {
}
