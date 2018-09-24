package com.grupokinexo.tasksservice.security;

import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.external.User;
import com.grupokinexo.tasksservice.models.responses.ErrorDetail;
import com.grupokinexo.tasksservice.parsers.Parser;
import com.grupokinexo.tasksservice.services.UserService;
import org.apache.http.HttpStatus;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import spark.Request;
import spark.Response;
import spark.Spark;

public class DefaultAuthorizationHandler implements AuthorizationHandler {
    private final UserService userService;
    private final Parser parser;
    private final CacheManager cacheManager;
    private final String callerHeader;

    public DefaultAuthorizationHandler(UserService userService, Parser parser, CacheManager cacheManager, String callerHeader) {
        this.userService = userService;
        this.parser = parser;
        this.cacheManager = cacheManager;
        this.callerHeader = callerHeader;
    }

    @Override
    public Integer authorize(Request request, Response response) {
        String callerValue = request.headers(callerHeader);

        if (callerValue == null || callerValue.isEmpty()) {
            setResponseError(String.format("The %s header is required", callerHeader));
            return null;
        }

        int callerId;
        try {
            callerId = Integer.parseInt(callerValue);
        } catch (NumberFormatException e) {
            setResponseError(String.format("The %s header must have a numeric value", callerHeader));
            return null;
        }

        User user = getUser(callerId);

        if (user == null) {
            setResponseError(String.format("The user with identifier %d doesn't have access to the application", callerId));
            return null;
        }

        return user.getId();
    }

    private User getUser(int id) {
        Cache cache = cacheManager.getCache("callerUsers");
        User user = cache.get(id, User.class);

        if (user != null) {
            return user;
        }

        user = userService.getById(id);

        if (user != null) {
            cache.put(id, user);
        }

        return user;
    }

    private void setResponseError(String message) {
        ErrorDetail errorDetail = new ErrorDetail(message);

        try {
            Spark.halt(HttpStatus.SC_UNAUTHORIZED, parser.parseToString(errorDetail));
        } catch (ParserException e) {
            Spark.halt(HttpStatus.SC_UNAUTHORIZED);
        }
    }
}