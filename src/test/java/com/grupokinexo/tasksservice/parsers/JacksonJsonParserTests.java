package com.grupokinexo.tasksservice.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JacksonJsonParserTests {
    @Test
    public void parseToObjectShouldReturnParsedObject() throws ParserException {
        final int id = 2;
        final String name = "user name";

        JacksonJsonParser parser = new JacksonJsonParser(new ObjectMapper());
        String stringToParse = String.format("{ \"id\": %d, \"name\": \"%s\" }", id, name);

        EntityToParse entity = parser.parseToObject(stringToParse, EntityToParse.class);
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(name, entity.getName());
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void parseToObjectShouldThrowParserExceptionIfExceptionIsThrown() throws IOException, ParserException {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.registerModule(any())).thenReturn(mapper);
        when(mapper.disable(any(SerializationFeature.class))).thenReturn(mapper);
        IOException ioException = new IOException("User message");
        when(mapper.readValue(anyString(), any(Class.class))).thenThrow(ioException);

        JacksonJsonParser parser = new JacksonJsonParser(mapper);

        exception.expect(ParserException.class);
        exception.expectMessage(ioException.getMessage());

        parser.parseToObject("", EntityToParse.class);
    }

    @Test
    public void parseToStringShouldReturnParsedString() throws ParserException {
        final int id = 2;
        final String name = "user name";

        JacksonJsonParser parser = new JacksonJsonParser(new ObjectMapper());
        String parsedString = String.format("{\"id\":%d,\"name\":\"%s\"}", id, name);

        EntityToParse entity = new EntityToParse();
        entity.setId(id);
        entity.setName(name);

        String result = parser.parseToString(entity);
        assertNotNull(result);
        assertEquals(parsedString, result);
    }
}