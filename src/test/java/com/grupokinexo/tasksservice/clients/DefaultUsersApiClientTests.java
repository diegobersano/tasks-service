package com.grupokinexo.tasksservice.clients;

import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.helpers.HttpHelper;
import com.grupokinexo.tasksservice.models.external.User;
import com.grupokinexo.tasksservice.parsers.Parser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static com.nitorcreations.Matchers.reflectEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

class DefaultUsersApiClientTests {
    private final String usersApiUrl = "http://url.com";

    private Parser parser;
    private HttpClient httpClient;
    private DefaultUsersApiClient apiClient;
    private HttpHelper httpHelper;
    private HttpResponse httpResponse;

    @BeforeEach
    void setup() {
        parser = mock(Parser.class);
        httpClient = mock(HttpClient.class);
        httpHelper = mock(HttpHelper.class);

        UserApiTokenRequest tokenRequest = new UserApiTokenRequest("user", "pass");
        apiClient = new DefaultUsersApiClient(parser, httpClient, httpHelper, usersApiUrl, tokenRequest);
    }

    @Test
    void getByIdShouldCallGetTokenEndpoint() throws IOException, ParserException {
        successSetup();

        apiClient.getById(1);

        tokenValidation();
    }

    @Test
    void getByIdShouldCallGetUserEndpoint() throws IOException, ParserException {
        final int userId = 12;
        successSetup();

        apiClient.getById(userId);

        String url = usersApiUrl + "/users/" + userId;
        ArgumentCaptor<HttpUriRequest> argument = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(httpClient, atLeastOnce()).execute(argument.capture());

        Optional<HttpUriRequest> httpGet = argument.getAllValues().stream().filter(x -> x instanceof HttpGet).findFirst();

        assertTrue(httpGet.isPresent());

        assertEquals(url, httpGet.get().getURI().toString());
        assertEquals("GET", httpGet.get().getMethod());
    }

    @Test
    void getAllShouldCallGetTokenEndpoint() throws IOException, ParserException {
        successSetup();

        apiClient.getAll();

        tokenValidation();
    }

    @Test
    void getAllShouldCallGetUsersEndpoint() throws IOException, ParserException {
        successSetup();

        apiClient.getAll();

        String url = usersApiUrl + "/users";
        ArgumentCaptor<HttpUriRequest> argument = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(httpClient, atLeastOnce()).execute(argument.capture());

        Optional<HttpUriRequest> httpGet = argument.getAllValues().stream().filter(x -> x instanceof HttpGet).findFirst();

        assertTrue(httpGet.isPresent());

        assertEquals(url, httpGet.get().getURI().toString());
        assertEquals("GET", httpGet.get().getMethod());
    }

    @Test
    void getByIdShouldReadBodyAndReturnParsedEntity() throws IOException, ParserException {
        successSetup();
        final String httpParsedResponse = "http parsed response";
        final User parsedUser = new User();
        parsedUser.setId(21);
        parsedUser.setFirstName("Name");

        when(httpHelper.getBody(any())).thenReturn(httpParsedResponse);
        when(parser.parseToObject(anyString(), eq(User.class))).thenReturn(parsedUser);

        User user = apiClient.getById(1);
        assertNotNull(user);

        verify(httpHelper, atLeastOnce()).getBody(httpResponse);
        verify(parser, times(1)).parseToObject(httpParsedResponse, User.class);
        assertThat(parsedUser, reflectEquals(user));
    }

    @Test
    void getAllShouldReadBodyAndReturnParsedEntity() throws IOException, ParserException {
        successSetup();
        final String httpParsedResponse = "http parsed response";
        final User parsedUser = new User();
        parsedUser.setId(21);
        parsedUser.setFirstName("Name");
        Collection<User> parsedUsers = new ArrayList<>();
        parsedUsers.add(parsedUser);

        when(httpHelper.getBody(any())).thenReturn(httpParsedResponse);
        when(parser.parseToListObject(anyString(), eq(User.class))).thenReturn(parsedUsers);

        Collection users = apiClient.getAll();
        assertNotNull(users);

        verify(httpHelper, atLeastOnce()).getBody(httpResponse);
        verify(parser, times(1)).parseToListObject(httpParsedResponse, User.class);
        assertThat(parsedUsers, reflectEquals(users));
    }

    private void successSetup() throws IOException, ParserException {
        httpResponse = mock(HttpResponse.class);
        when(httpResponse.getEntity()).thenReturn(mock(HttpEntity.class));
        when(httpClient.execute(any())).thenReturn(httpResponse);
        when(parser.parseToString(any())).thenReturn("{}");
        when(parser.parseToObject(any(), eq(UserApiTokenResponse.class))).thenReturn(new UserApiTokenResponse());
    }

    private void tokenValidation() throws IOException {
        String url = usersApiUrl + "/login";
        ArgumentCaptor<HttpUriRequest> argument = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(httpClient, atLeastOnce()).execute(argument.capture());

        Optional<HttpUriRequest> httpPost = argument.getAllValues().stream().filter(x -> x instanceof HttpPost).findFirst();

        assertTrue(httpPost.isPresent());

        assertEquals(url, httpPost.get().getURI().toString());
        assertEquals("POST", httpPost.get().getMethod());
    }
}