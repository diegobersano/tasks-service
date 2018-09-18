package com.grupokinexo.tasksservice.services;

import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse create(TaskRequest task);

    TaskResponse getById(int id);

    List<TaskResponse> search();

    TaskResponse edit(int id, TaskRequest task);
}