package com.grupokinexo.tasksservice.services;

import com.grupokinexo.tasksservice.clients.UsersApiClient;
import com.grupokinexo.tasksservice.models.external.User;

import java.util.Collection;

public class UserServiceImpl implements UserService {
    private final UsersApiClient usersApiClient;

    public UserServiceImpl(UsersApiClient usersApiClient) {
        this.usersApiClient = usersApiClient;
    }

    @Override
    public User getById(int id) {
        try {
            return usersApiClient.getById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<User> search() {
        try {
            return usersApiClient.getAll();
        } catch (Exception e) {
            return null;
        }
    }
}