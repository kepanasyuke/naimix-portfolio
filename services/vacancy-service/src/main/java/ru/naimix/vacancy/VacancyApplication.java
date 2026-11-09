package ru.naimix.vacancy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point of the vacancy service.
 */
@EnableCaching
@SpringBootApplication
public class VacancyApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(VacancyApplication.class, args);
    }
}
