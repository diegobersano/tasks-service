package com.grupokinexo.tasksservice.security;

import com.grupokinexo.tasksservice.models.external.User;
import com.grupokinexo.tasksservice.parsers.Parser;
import com.grupokinexo.tasksservice.services.UserService;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import spark.HaltException;
import spark.Request;
import spark.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DefaultAuthorizationHandlerTests {
    private final String headerName = "X-Caller-Id";
    private UserService userService;
    private Parser parser;
    private CacheManager cacheManager;
    private DefaultAuthorizationHandler handler;
    private Response response;
    private Request request;

    @Before
    public void setup() {
        userService = mock(UserService.class);
        parser = mock(Parser.class);
        cacheManager = mock(CacheManager.class);

        handler = new DefaultAuthorizationHandler(userService, parser, cacheManager, headerName);

        request = mock(Request.class);
        response = mock(Response.class);
        Cache cache = mock(Cache.class);
        when(cache.get(any())).thenReturn(null);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
    }

    @Test
    public void authorizeShouldGetHeaderFromRequest() {
        when(request.headers(anyString())).thenReturn("1");
        when(userService.getById(anyInt())).thenReturn(new User());

        handler.authorize(request, response);

        verify(request, times(1)).headers(headerName);
    }

    @Test
    public void authorizeShouldThrowHaltExceptionWhenHeaderIsNotPresent() {
        when(request.headers(anyString())).thenReturn(null);
        HaltException e = assertThrows(HaltException.class, () -> handler.authorize(request, response));
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.statusCode());

        verify(userService, never()).getById(anyInt());
    }

    @Test
    public void authorizeShouldThrowHaltExceptionWhenHeaderDoesNotHaveNumberFormat() {
        when(request.headers(anyString())).thenReturn("string value");
        HaltException e = assertThrows(HaltException.class, () -> handler.authorize(request, response));
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.statusCode());

        verify(userService, never()).getById(anyInt());
    }

    @Test
    public void authorizeShouldThrowHaltExceptionIfUserDoesNotExists() {
        final int callerId = 1;
        when(request.headers(anyString())).thenReturn(String.valueOf(callerId));
        when(userService.getById(anyInt())).thenReturn(null);

        HaltException e = assertThrows(HaltException.class, () -> handler.authorize(request, response));
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.statusCode());

        verify(userService, times(1)).getById(callerId);
    }

    @Test
    public void authorizeShouldNotThrowExceptionIfUserExists() {
        final int callerId = 1;
        when(request.headers(anyString())).thenReturn(String.valueOf(callerId));
        when(userService.getById(anyInt())).thenReturn(new User());

        handler.authorize(request, response);
    }
}
