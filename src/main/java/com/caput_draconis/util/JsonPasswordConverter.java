package com.caput_draconis.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.Map;

@Converter
public class JsonPasswordConverter implements AttributeConverter<Map<String , Object> , String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    @Nullable
    public String convertToDatabaseColumn(Map<String, Object> details) {
        try {
            return mapper.writeValueAsString(details);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData , HashMap.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
