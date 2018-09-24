package com.grupokinexo.tasksservice.integration;

import com.grupokinexo.tasksservice.clients.DefaultUsersApiClient;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.helpers.BeanHelper;
import com.grupokinexo.tasksservice.models.external.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ExternalUsersApiClientTests {
    private DefaultUsersApiClient client;

    @BeforeEach
    void setup() {
        client = BeanHelper.getBean("usersApiClient", DefaultUsersApiClient.class);
    }

    @Test
    void getTokenShouldReturnValidAuthenticationToken() throws IOException, ParserException {
        String token = client.getToken();
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer"));
    }

    @Test
    void getClientByIdShouldReturnValidUser() throws IOException, ParserException {
        final int userId = 1;
        User user = client.getById(userId);
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("Diego", user.getFirstName());
    }
}