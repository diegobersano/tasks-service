package com.grupokinexo.tasksservice.parsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grupokinexo.tasksservice.exceptions.ParserException;

import java.util.Collection;
import java.util.List;

public class JacksonJsonParser implements Parser {
    private final ObjectMapper jsonParser;

    public JacksonJsonParser(ObjectMapper objectMapper) {
        jsonParser = objectMapper.registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public <T> T parseToObject(String toParse, Class<T> type) throws ParserException {
        toParse = normalizeInput(toParse);

        try {
            return jsonParser.readValue(toParse, type);
        } catch (Exception e) {
            throw new ParserException(e.getMessage());
        }
    }

    @Override
    public <T> Collection<T> parseToListObject(String toParse, Class<T> type) throws ParserException {
        toParse = normalizeInput(toParse);

        try {
            CollectionType javaType = jsonParser.getTypeFactory().constructCollectionType(List.class, type);
            return jsonParser.readValue(toParse, javaType);
        } catch (Exception e) {
            throw new ParserException(e.getMessage());
        }
    }

    @Override
    public <T> String parseToString(T toParse) throws ParserException {
        try {
            return jsonParser.writeValueAsString(toParse);
        } catch (JsonProcessingException e) {
            throw new ParserException(e.getMessage());
        }
    }

    private String normalizeInput(String input) {
        if (input == null || input.isEmpty()) {
            input = "{}";
        }

        return input;
    }
}