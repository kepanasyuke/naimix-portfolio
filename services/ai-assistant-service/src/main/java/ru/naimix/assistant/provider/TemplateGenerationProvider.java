package ru.naimix.assistant.provider;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.naimix.assistant.api.dto.CoverLetterRequest;
import ru.naimix.assistant.api.dto.GenerationView;
import ru.naimix.assistant.api.dto.VacancyDescriptionRequest;
import ru.naimix.assistant.text.WordCounter;

/**
 * Deterministic template provider: no external calls, stable output.
 */
@Component
public class TemplateGenerationProvider implements GenerationProvider {

    private static final String PROVIDER = "template-v1";

    @Override
    public GenerationView describe(final VacancyDescriptionRequest request) {
        return view(description(request));
    }

    @Override
    public GenerationView coverLetter(final CoverLetterRequest request) {
        return view(letter(request));
    }

    private GenerationView view(final String text) {
        return new GenerationView(text, WordCounter.count(text), PROVIDER, 0);
    }

    private String description(final VacancyDescriptionRequest request) {
        return String.join("\n", intro(request), skills(request.skills()), terms(request));
    }

    private String intro(final VacancyDescriptionRequest request) {
        return "Ищем специалиста на роль «" + request.title() + "» в городе " + request.city() + ".";
    }

    private String skills(final List<String> skills) {
        return "Ожидания по стеку:\n" + skills.stream().map(skill -> "- " + skill)
                .collect(Collectors.joining("\n"));
    }

    private String terms(final VacancyDescriptionRequest request) {
        return "Ставка: " + request.rateMin() + "–" + request.rateMax() + " руб./час. Тон: " + request.tone() + ".";
    }

    private String letter(final CoverLetterRequest request) {
        return String.join("\n", greeting(request.name()), fit(request), closing());
    }

    private String greeting(final String name) {
        return "Здравствуйте! Меня зовут " + name + ".";
    }

    private String fit(final CoverLetterRequest request) {
        return "Откликаюсь на вакансию «" + request.vacancyTitle() + "». " + experience(request.experienceYears())
                + " Основа стека: " + String.join(", ", request.skills()) + ".";
    }

    private String experience(final Integer years) {
        return "Коммерческий опыт в разработке — " + years + " лет.";
    }

    private String closing() {
        return "Готов обсудить задачи команды и сроки на созвоне.";
    }
}
