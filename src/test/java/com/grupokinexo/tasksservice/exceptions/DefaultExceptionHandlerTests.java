package com.grupokinexo.tasksservice.exceptions;

import com.grupokinexo.tasksservice.parsers.Parser;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DefaultExceptionHandlerTests {
    private final String parsedResponse = "Parsed response";
    private DefaultExceptionHandler exceptionHandler;
    private Request request;
    private Response response;

    @Before
    public void setup() throws ParserException {
        request = mock(Request.class);
        response = mock(Response.class);
        Parser parser = mock(Parser.class);
        when(parser.parseToString(any())).thenReturn(parsedResponse);

        exceptionHandler = new DefaultExceptionHandler(parser);
    }

    @Test
    public void manageExceptionShouldReturnBadRequestResponseWhenExceptionTypeIsBadRequestApiException() {
        Exception exception = new BadRequestApiException("Error");
        exceptionHandler.manageException(exception, request, response);

        verify(response, times(1)).status(HttpStatus.SC_BAD_REQUEST);
        verify(response, times(1)).body(parsedResponse);
    }
}
