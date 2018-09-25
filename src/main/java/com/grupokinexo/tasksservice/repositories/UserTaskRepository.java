package com.grupokinexo.tasksservice.repositories;

import com.grupokinexo.tasksservice.models.domain.UserTask;

import java.util.Collection;

public interface UserTaskRepository {
    Collection<UserTask> getByTaskId(int taskId);

    void addToTask(int taskId, Collection<Integer> users);
}