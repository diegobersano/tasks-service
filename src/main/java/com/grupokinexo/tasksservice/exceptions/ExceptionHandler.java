package com.grupokinexo.tasksservice.exceptions;

import spark.Request;
import spark.Response;

public interface ExceptionHandler {
    void manageException(Exception exception, Request request, Response response);
}