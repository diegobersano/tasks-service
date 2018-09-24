package com.grupokinexo.tasksservice.services;

import com.grupokinexo.tasksservice.clients.UsersApiClient;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.external.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class UserServiceImplTests {
    private UsersApiClient apiClient;
    private UserServiceImpl service;

    @BeforeEach
    void setup() {
        apiClient = mock(UsersApiClient.class);
        service = new UserServiceImpl(apiClient);
    }

    @Test
    void getByIdShouldCallApiClient() throws IOException, ParserException {
        final int userId = 91;
        when(apiClient.getById(anyInt())).thenReturn(new User());

        service.getById(userId);

        verify(apiClient, times(1)).getById(userId);
    }

    @Test
    void getByIdShouldReturnApiClientResponse() throws IOException, ParserException {
        final User user = new User();
        user.setId(12);

        when(apiClient.getById(anyInt())).thenReturn(user);

        User result = service.getById(user.getId());
        assertNotNull(result);
        assertSame(user, result);
    }

    @Test
    void getByIdShouldReturnNullWhenApiClientThrowsException() throws IOException, ParserException {
        when(apiClient.getById(anyInt())).thenThrow(new IOException());

        User result = service.getById(1);
        assertNull(result);
    }

    @Test
    void searchShouldCallApiClient() throws IOException, ParserException {
        when(apiClient.getAll()).thenReturn(null);

        service.search();

        verify(apiClient, times(1)).getAll();
    }

    @Test
    void searchShouldReturnApiClientResponse() throws IOException, ParserException {
        final User user = new User();
        user.setId(12);
        Collection<User> users = new ArrayList<>();
        users.add(user);

        when(apiClient.getAll()).thenReturn(users);

        Collection result = service.search();
        assertNotNull(result);
        assertSame(users, result);
    }

    @Test
    void searchShouldReturnNullWhenApiClientThrowsException() throws IOException, ParserException {
        when(apiClient.getAll()).thenThrow(new IOException());

        Collection result = service.search();
        assertNull(result);
    }
}