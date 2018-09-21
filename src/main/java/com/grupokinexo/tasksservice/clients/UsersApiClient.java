package com.grupokinexo.tasksservice.clients;

import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.external.User;

import java.io.IOException;
import java.util.Collection;

public interface UsersApiClient {
    User getById(int id) throws IOException, ParserException;

    Collection<User> getAll() throws IOException, ParserException;
}