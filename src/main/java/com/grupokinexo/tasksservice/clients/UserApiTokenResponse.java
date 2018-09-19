package com.grupokinexo.tasksservice.clients;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserApiTokenResponse {
    private String token;

    @JsonProperty("expires_in")
    private int expiresIn;

    public String getToken() {
        return token;
    }

    public int getExpiresIn() {
        return expiresIn;
    }
}