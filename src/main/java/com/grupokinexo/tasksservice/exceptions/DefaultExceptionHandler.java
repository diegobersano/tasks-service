package com.grupokinexo.tasksservice.exceptions;

import com.grupokinexo.tasksservice.models.responses.ErrorDetail;
import com.grupokinexo.tasksservice.parsers.Parser;
import org.apache.http.HttpStatus;
import spark.Request;
import spark.Response;

public class DefaultExceptionHandler implements ExceptionHandler {
    private final Parser parser;

    DefaultExceptionHandler(Parser parser) {
        this.parser = parser;
    }

    @Override
    public void manageException(Exception exception, Request request, Response response) {
        if (exception instanceof BadRequestApiException) {
            BadRequestApiException badRequestApiException = (BadRequestApiException) exception;
            ErrorDetail errorDetail = new ErrorDetail(badRequestApiException.getMessage());
            errorDetail.addElements(badRequestApiException.getErrorElements());

            setResponseBody(response, errorDetail);

            response.status(HttpStatus.SC_BAD_REQUEST);
        } else if (exception instanceof UnauthorizedException) {
            UnauthorizedException unauthorizedException = (UnauthorizedException) exception;
            ErrorDetail errorDetail = new ErrorDetail(unauthorizedException.getMessage());

            setResponseBody(response, errorDetail);

            response.status(HttpStatus.SC_FORBIDDEN);
        } else if (exception instanceof ConflictException) {
            ConflictException conflictException = (ConflictException) exception;
            ErrorDetail errorDetail = new ErrorDetail(conflictException.getMessage());

            setResponseBody(response, errorDetail);

            response.status(HttpStatus.SC_CONFLICT);
        }
    }

    private void setResponseBody(Response response, ErrorDetail errorDetail) {
        try {
            response.body(parser.parseToString(errorDetail));
        } catch (ParserException e) {
            response.body("{}");
        }
    }
}