package com.grupokinexo.tasksservice.clients;

import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.external.User;
import com.grupokinexo.tasksservice.parsers.Parser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.IOException;
import java.util.Collection;

public class DefaultUsersApiClient implements UsersApiClient {
    private final Parser parser;
    private final CacheManager cacheManager;
    private final HttpClient httpClient;
    private final String usersApiUrl;
    private final String usersApiUserName;
    private final String usersApiPassword;

    DefaultUsersApiClient(Parser parser, CacheManager cacheManager, HttpClient httpClient, String usersApiUrl,
                          String usersApiUserName, String usersApiPassword) {
        this.parser = parser;
        this.cacheManager = cacheManager;
        this.httpClient = httpClient;
        this.usersApiUrl = usersApiUrl;
        this.usersApiUserName = usersApiUserName;
        this.usersApiPassword = usersApiPassword;
    }

    String getToken() throws ParserException, IOException {
        Cache cache = cacheManager.getCache("userToken");
        assert cache != null;

        UserApiTokenResponse tokenResponse = cache.get("token", UserApiTokenResponse.class);
        if (tokenResponse != null) {
            return tokenResponse.getToken();
        }

        String url = usersApiUrl + "/login";

        HttpPost request = new HttpPost(url);

        String parsedRequest = parser.parseToString(new UserApiTokenRequest(usersApiUserName, usersApiPassword));
        request.setEntity(new StringEntity(parsedRequest));
        HttpResponse response = httpClient.execute(request);
        String content = EntityUtils.toString(response.getEntity());
        tokenResponse = parser.parseToObject(content, UserApiTokenResponse.class);

        cache.put("token", tokenResponse);

        return tokenResponse.getToken();
    }

    @Override
    public User getById(int id) throws IOException, ParserException {
        String url = usersApiUrl + "/users/" + id;

        HttpGet request = new HttpGet(url);
        HttpResponse response = execute(request);

        String content = EntityUtils.toString(response.getEntity());
        return parser.parseToObject(content, User.class);
    }

    @Override
    public Collection<User> getAll() throws IOException, ParserException {
        String url = usersApiUrl + "/users";

        HttpGet request = new HttpGet(url);
        HttpResponse response = execute(request);

        String content = EntityUtils.toString(response.getEntity());
        return parser.parseToListObject(content, User.class);
    }

    private HttpResponse execute(HttpRequestBase request) throws IOException, ParserException {
        String token = getToken();
        request.addHeader("Authorization", token);
        return httpClient.execute(request);
    }
}