package com.grupokinexo.tasksservice.integration;

import com.grupokinexo.tasksservice.clients.DefaultUsersApiClient;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.helpers.BeanHelper;
import com.grupokinexo.tasksservice.models.external.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ExternalUsersApiClientTests {
    private DefaultUsersApiClient client;

    @Before
    public void setup() {
        client = BeanHelper.getBean("usersApiClient", DefaultUsersApiClient.class);
    }

    @Test
    public void getTokenShouldReturnValidAuthenticationToken() throws IOException, ParserException {
        String token = client.getToken();
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer"));
    }

    @Test
    public void getClientByIdShouldReturnValidUser() throws IOException, ParserException {
        final int userId = 1;
        User user = client.getById(userId);
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("Diego", user.getFirstName());
    }
}