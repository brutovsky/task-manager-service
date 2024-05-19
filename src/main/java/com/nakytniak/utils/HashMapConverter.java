package com.nakytniak.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Convert
public class HashMapConverter implements AttributeConverter<Map<String, String>, String> {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        JavaTimeModule module = new JavaTimeModule();
        OBJECT_MAPPER = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true)
                .registerModule(module);
    }

    @Override
    public String convertToDatabaseColumn(final Map<String, String> customerInfo) {
        if (Objects.isNull(customerInfo)) {
            return null;
        }

        String customerInfoJson = null;
        try {
            customerInfoJson = OBJECT_MAPPER.writeValueAsString(customerInfo);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return customerInfoJson;
    }

    @Override
    public Map<String, String> convertToEntityAttribute(final String customerInfoJSON) {
        if (Objects.isNull(customerInfoJSON)) {
            return null;
        }

        Map<String, String> customerInfo = null;
        try {
            customerInfo = OBJECT_MAPPER.readValue(customerInfoJSON, new TypeReference<HashMap<String, String>>() {
            });
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return customerInfo;
    }
}