package com.pmtool.backend.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

@Converter(autoApply = true)
public class DurationToPostgresIntervalConverter implements AttributeConverter<Duration, String> {

    @Override
    public String convertToDatabaseColumn(Duration duration) {
        if (duration == null) return null;
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        long hours = absSeconds / 3600;
        long minutes = (absSeconds % 3600) / 60;
        long secs = absSeconds % 60;
        // Store as HHH:MM:SS
        return String.format("%d:%02d:%02d", hours, minutes, secs);
    }

    @Override
    public Duration convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String[] parts = dbData.split(":");
        if (parts.length == 3) {
            long hours = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            long seconds = Long.parseLong(parts[2]);
            return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        }
        throw new IllegalArgumentException("Invalid duration format in DB: " + dbData);
    }
}