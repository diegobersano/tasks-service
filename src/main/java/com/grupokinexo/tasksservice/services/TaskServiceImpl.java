package com.grupokinexo.tasksservice.services;

import com.grupokinexo.tasksservice.models.domain.Task;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;
import com.grupokinexo.tasksservice.repositories.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskResponse create(TaskRequest task) {
        Task newTask = new Task();
        newTask.setName(task.getName());
        newTask.setDescription(task.getMessage());

        int createdId = taskRepository.add(newTask);

        return getById(createdId);
    }

    @Override
    public TaskResponse getById(int id) {
        Task createdTask = taskRepository.getById(id);

        if (createdTask == null) {
            return null;
        }

        return Map(createdTask);
    }

    @Override
    public List<TaskResponse> search() {
        List<Task> tasks = taskRepository.search();

        List<TaskResponse> result = new ArrayList<>();
        tasks.forEach(task -> result.add(Map(task)));

        return result;
    }

    @Override
    public TaskResponse edit(int id, TaskRequest task) {
        return null;
    }

    private TaskResponse Map(Task task) {

        TaskResponse result = new TaskResponse();
        result.setId(task.getId());
        result.setName(task.getName());
        result.setDescription(task.getDescription());
        result.setCreatedOn(task.getCreatedOn());

        return result;
    }
}