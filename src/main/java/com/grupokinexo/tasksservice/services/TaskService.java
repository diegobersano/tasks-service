package com.grupokinexo.tasksservice.services;

import com.grupokinexo.tasksservice.exceptions.BadRequestApiException;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.external.User;
import com.grupokinexo.tasksservice.models.requests.ShareTaskRequest;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface TaskService {
    TaskResponse create(TaskRequest task);

    TaskResponse getById(int id);

    List<TaskResponse> search();

    TaskResponse edit(int id, TaskRequest task);

    void shareTask(int id, ShareTaskRequest shareTaskRequest) throws IOException, ParserException, BadRequestApiException;

    Collection<User> getUsersByTask(int id);
}