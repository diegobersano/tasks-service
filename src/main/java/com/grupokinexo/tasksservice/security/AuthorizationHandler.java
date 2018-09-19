package com.grupokinexo.tasksservice.security;

import spark.Request;
import spark.Response;

public interface AuthorizationHandler {
    void authorize(Request request, Response response);
}