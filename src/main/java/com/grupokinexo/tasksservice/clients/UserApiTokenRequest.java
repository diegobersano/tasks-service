package com.grupokinexo.tasksservice.clients;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserApiTokenRequest {
    @JsonProperty("username")
    private String userName;
    private String password;

    UserApiTokenRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
