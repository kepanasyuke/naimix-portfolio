package ru.naimix.analytics.api.dto;

/**
 * Single counter value.
 *
 * @param name counter name
 * @param value counter value
 */
public record CountView(String name, long value) {
}
