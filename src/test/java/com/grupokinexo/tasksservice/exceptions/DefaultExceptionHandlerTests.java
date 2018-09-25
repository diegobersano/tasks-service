
package com.grupokinexo.tasksservice.exceptions;

import com.grupokinexo.tasksservice.parsers.Parser;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultExceptionHandlerTests {
    private final String parsedResponse = "Parsed response";
    private DefaultExceptionHandler exceptionHandler;
    private Request request;

    private Response response;

    @BeforeEach
    void setup() throws ParserException {
        request = mock(Request.class);
        response = mock(Response.class);
        Parser parser = mock(Parser.class);
        when(parser.parseToString(any())).thenReturn(parsedResponse);

        exceptionHandler = new DefaultExceptionHandler(parser);
    }

    @Test
    void manageExceptionShouldReturnBadRequestResponseWhenExceptionTypeIsBadRequestApiException() {
        Exception exception = new BadRequestApiException("Error");
        exceptionHandler.manageException(exception, request, response);

        verify(response, times(1)).status(HttpStatus.SC_BAD_REQUEST);
        verify(response, times(1)).body(parsedResponse);
    }

    @Test
    void manageExceptionShouldReturnForbiddenResponseWhenExceptionTypeIsUnauthorizedException() {
        UnauthorizedException exception = new UnauthorizedException("Error message");
        exceptionHandler.manageException(exception, request, response);

        verify(response, times(1)).status(HttpStatus.SC_FORBIDDEN);
        verify(response, times(1)).body(parsedResponse);
    }

    @Test
    void manageExceptionShouldReturnConflictResponseWhenExceptionTypeIsConflictException() {
        ConflictException exception = new ConflictException("Error message");
        exceptionHandler.manageException(exception, request, response);

        verify(response, times(1)).status(HttpStatus.SC_CONFLICT);
        verify(response, times(1)).body(parsedResponse);
    }

    @Test
    void manageExceptionShouldReturnNotFoundResponseWhenExceptionTypeIsNotFoundException() {
        NotFoundException exception = new NotFoundException("Error message");
        exceptionHandler.manageException(exception, request, response);

        verify(response, times(1)).status(HttpStatus.SC_NOT_FOUND);
        verify(response, times(1)).body(parsedResponse);
    }
}