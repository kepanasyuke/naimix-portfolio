package ru.naimix.workspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the business workspace service.
 */
@SpringBootApplication
public class WorkspaceApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(WorkspaceApplication.class, args);
    }
}
