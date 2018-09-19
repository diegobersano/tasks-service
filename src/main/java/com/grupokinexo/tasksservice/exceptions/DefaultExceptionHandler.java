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
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setMessage(badRequestApiException.getMessage());
            errorDetail.addElements(badRequestApiException.getErrorElements());

            try {
                response.body(parser.parseToString(errorDetail));
            } catch (ParserException e) {
                response.body("{}");
            }

            response.status(HttpStatus.SC_BAD_REQUEST);
        }
    }
}