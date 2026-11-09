package ru.naimix.vacancy.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Job vacancy published by a client company.
 */
@Entity
@Table(name = "vacancies")
public class Vacancy {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false, length = 120)
    private String city;

    @Column(name = "rate_min", precision = 12, scale = 2)
    private BigDecimal rateMin;

    @Column(name = "rate_max", precision = 12, scale = 2)
    private BigDecimal rateMax;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    private long version;

    /**
     * JPA constructor.
     */
    protected Vacancy() {
    }

    /**
     * Creates a published vacancy.
     *
     * @param title short title
     * @param description full description
     * @param city work city
     * @param rateMin lower hourly rate bound
     * @param rateMax upper hourly rate bound
     */
    public Vacancy(final String title, final String description, final String city,
                   final BigDecimal rateMin, final BigDecimal rateMax) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.city = city;
        this.rateMin = rateMin;
        this.rateMax = rateMax;
        this.status = "OPEN";
        this.createdAt = Instant.now();
    }

    /**
     * Moves the vacancy to another status.
     *
     * @param newStatus target status
     */
    public void changeStatus(final String newStatus) {
        this.status = newStatus;
    }

    /**
     * Returns the vacancy identifier.
     *
     * @return identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the vacancy title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the vacancy description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the work city.
     *
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns the lower rate bound.
     *
     * @return rate
     */
    public BigDecimal getRateMin() {
        return rateMin;
    }

    /**
     * Returns the upper rate bound.
     *
     * @return rate
     */
    public BigDecimal getRateMax() {
        return rateMax;
    }

    /**
     * Returns the vacancy status.
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the creation timestamp.
     *
     * @return timestamp
     */
    public Instant getCreatedAt() {
        return createdAt;
    }
}
