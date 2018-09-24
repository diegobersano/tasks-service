package com.grupokinexo.tasksservice.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JacksonJsonParserTests {
    private final int id = 2;
    private final String name = "user name";
    private String parsedString = String.format("{\"id\":%d,\"name\":\"%s\"}", id, name);

    @Test
    void parseToObjectShouldReturnParsedObject() throws ParserException {
        JacksonJsonParser parser = new JacksonJsonParser(new ObjectMapper());

        EntityToParse entity = parser.parseToObject(parsedString, EntityToParse.class);

        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(name, entity.getName());
    }

    @Test
    void parseToObjectShouldThrowParserExceptionIfExceptionIsThrown() throws IOException {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.registerModule(any())).thenReturn(mapper);
        when(mapper.disable(any(SerializationFeature.class))).thenReturn(mapper);
        IOException ioException = new IOException("User message");
        when(mapper.readValue(anyString(), any(Class.class))).thenThrow(ioException);

        JacksonJsonParser parser = new JacksonJsonParser(mapper);

        ParserException exception = assertThrows(ParserException.class, () -> parser.parseToObject("", EntityToParse.class));
        assertEquals(ioException.getMessage(), exception.getMessage());
    }

    @Test
    void parseToObjectShouldUseEmptyJsonWhenEntityIsNull() throws IOException, ParserException {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.registerModule(any())).thenReturn(mapper);
        when(mapper.disable(any(SerializationFeature.class))).thenReturn(mapper);
        when(mapper.readValue(anyString(), any(Class.class))).thenReturn(null);

        JacksonJsonParser parser = new JacksonJsonParser(mapper);

        parser.parseToObject(null, EntityToParse.class);
        verify(mapper, times(1)).readValue("{}", EntityToParse.class);
    }

    @Test
    void parseToStringShouldReturnParsedString() throws ParserException {
        JacksonJsonParser parser = new JacksonJsonParser(new ObjectMapper());

        EntityToParse entity = new EntityToParse();
        entity.setId(id);
        entity.setName(name);

        String result = parser.parseToString(entity);

        assertNotNull(result);
        assertEquals(parsedString, result);
    }
}