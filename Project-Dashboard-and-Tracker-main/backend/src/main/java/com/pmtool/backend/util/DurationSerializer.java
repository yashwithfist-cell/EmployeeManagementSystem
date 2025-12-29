package com.pmtool.backend.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration> {
    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        long seconds = value.getSeconds();
        long absSeconds = Math.abs(seconds);
        long hours = absSeconds / 3600;
        long minutes = (absSeconds % 3600) / 60;
        long secs = absSeconds % 60;
        // Format as HHH:MM:SS
        String formatted = String.format("%d:%02d:%02d", hours, minutes, secs);
        gen.writeString(formatted);
    }
}