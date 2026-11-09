package ru.naimix.matching.domain;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * In-memory catalogue of freelancer profiles used for demonstrations.
 */
@Component
public class FreelancerCatalog {

    private final List<FreelancerProfile> profiles = List.of(
            new FreelancerProfile("f-1", "Анна Ковалёва", "Москва", new BigDecimal("3200"),
                    List.of("java", "spring", "postgresql", "kafka"),
                    "Проектирую платёжные бэкенды, люблю доменные границы и надёжную доставку сообщений"),
            new FreelancerProfile("f-2", "Игорь Плетнёв", "Санкт-Петербург", new BigDecimal("2800"),
                    List.of("kotlin", "spring", "redis", "kubernetes"),
                    "Эксплуатация сервисов, отказоустойчивость, мониторинг и профилирование JVM"),
            new FreelancerProfile("f-3", "Мария Дубова", "Казань", new BigDecimal("2100"),
                    List.of("typescript", "react", "figma", "design-system"),
                    "Фронтенд для продуктовых команд, дизайн-системы и доступность интерфейсов"),
            new FreelancerProfile("f-4", "Пётр Соколов", "Екатеринбург", new BigDecimal("3600"),
                    List.of("python", "fastapi", "pandas", "ml"),
                    "Аналитика данных, модели рекомендаций и офлайн-оценка качества поиска"),
            new FreelancerProfile("f-5", "Ольга Рябова", "Новосибирск", new BigDecimal("2400"),
                    List.of("java", "spring", "rabbitmq", "postgresql"),
                    "Асинхронные процессы, идемпотентность и интеграции с внешними системами"),
            new FreelancerProfile("f-6", "Дмитрий Лапшин", "Москва", new BigDecimal("3100"),
                    List.of("golang", "kubernetes", "ci-cd", "observability"),
                    "Платформенная инженерия, пайплайны поставки и инфраструктура как код"),
            new FreelancerProfile("f-7", "Софья Миронова", "Краснодар", new BigDecimal("1900"),
                    List.of("java", "spring", "hibernate", "sql"),
                    "Разработка корпоративных сервисов, оптимизация запросов и миграции схемы"),
            new FreelancerProfile("f-8", "Артём Гусев", "Тюмень", new BigDecimal("2700"),
                    List.of("node", "typescript", "postgresql", "redis"),
                    "API высоких нагрузок, кэширование и очереди на стороне прикладного кода"));

    /**
     * Returns all known profiles.
     *
     * @return immutable profile list
     */
    public List<FreelancerProfile> all() {
        return profiles;
    }
}
