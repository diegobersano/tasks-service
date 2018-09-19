package com.grupokinexo.tasksservice.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.external.User;
import com.grupokinexo.tasksservice.parsers.JacksonJsonParser;
import org.apache.http.client.HttpClient;
import org.junit.Test;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DefaultUsersApiClientTests {
    private static final String springConfigPath = "base-config.xml";

    @Test
    public void getTokenShouldCallExternalApi() throws IOException, ParserException {
        ApplicationContext context = new ClassPathXmlApplicationContext(springConfigPath);
        CacheManager cacheManager = context.getBean("cacheManager", CacheManager.class);
        HttpClient httpClient = context.getBean("httpClient", HttpClient.class);

        DefaultUsersApiClient client = new DefaultUsersApiClient(new JacksonJsonParser(new ObjectMapper()), cacheManager, httpClient, "", "", "");
        client.getToken();
    }

    @Test
    public void getClientById() throws IOException, ParserException {
        ApplicationContext context = new ClassPathXmlApplicationContext(springConfigPath);
        CacheManager cacheManager = context.getBean("cacheManager", CacheManager.class);
        HttpClient httpClient = context.getBean("httpClient", HttpClient.class);

        DefaultUsersApiClient client = new DefaultUsersApiClient(new JacksonJsonParser(new ObjectMapper()), cacheManager, httpClient, "", "", "");
        User user = client.getById(1);
        assertNotNull(user);
    }
}