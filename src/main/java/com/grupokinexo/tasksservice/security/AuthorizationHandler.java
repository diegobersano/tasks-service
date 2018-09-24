package com.grupokinexo.tasksservice.security;

import spark.Request;
import spark.Response;

public interface AuthorizationHandler {
    Integer authorize(Request request, Response response);
}