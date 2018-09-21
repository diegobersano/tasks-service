package com.grupokinexo.tasksservice.services;

import com.grupokinexo.tasksservice.models.external.User;

import java.util.Collection;

public interface UserService {
    User getById(int id);

    Collection<User> search();
}