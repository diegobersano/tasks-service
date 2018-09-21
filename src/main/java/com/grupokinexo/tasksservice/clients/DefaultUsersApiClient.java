package com.grupokinexo.tasksservice.clients;

import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.helpers.HttpHelper;
import com.grupokinexo.tasksservice.models.external.User;
import com.grupokinexo.tasksservice.parsers.Parser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.Collection;

public class DefaultUsersApiClient implements UsersApiClient {
    private final Parser parser;
    private final HttpClient httpClient;
    private final String usersApiUrl;
    private final UserApiTokenRequest tokenRequest;
    private final HttpHelper httpHelper;

    DefaultUsersApiClient(Parser parser, HttpClient httpClient, HttpHelper httpHelper, String usersApiUrl, UserApiTokenRequest tokenRequest) {
        this.parser = parser;
        this.httpClient = httpClient;
        this.httpHelper = httpHelper;
        this.usersApiUrl = usersApiUrl;
        this.tokenRequest = tokenRequest;
    }

    public String getToken() throws ParserException, IOException {
        String url = usersApiUrl + "/login";

        HttpPost request = new HttpPost(url);

        String parsedRequest = parser.parseToString(tokenRequest);
        request.setEntity(new StringEntity(parsedRequest));
        HttpResponse response = httpClient.execute(request);
        String content = httpHelper.getBody(response);
        UserApiTokenResponse tokenResponse = parser.parseToObject(content, UserApiTokenResponse.class);

        return tokenResponse.getToken();
    }

    @Override
    public User getById(int id) throws IOException, ParserException {
        String url = usersApiUrl + "/users/" + id;

        HttpGet request = new HttpGet(url);
        HttpResponse response = execute(request);

        String content = httpHelper.getBody(response);
        return parser.parseToObject(content, User.class);
    }

    @Override
    public Collection<User> getAll() throws IOException, ParserException {
        String url = usersApiUrl + "/users";

        HttpGet request = new HttpGet(url);
        HttpResponse response = execute(request);

        String content = httpHelper.getBody(response);
        return parser.parseToListObject(content, User.class);
    }

    private HttpResponse execute(HttpRequestBase request) throws IOException, ParserException {
        String token = getToken();
        request.addHeader("Authorization", token);
        return httpClient.execute(request);
    }
}