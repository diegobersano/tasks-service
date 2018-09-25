package com.grupokinexo.tasksservice.services;

import com.grupokinexo.tasksservice.clients.UsersApiClient;
import com.grupokinexo.tasksservice.exceptions.BadRequestApiException;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.domain.Task;
import com.grupokinexo.tasksservice.models.domain.UserTask;
import com.grupokinexo.tasksservice.models.external.User;
import com.grupokinexo.tasksservice.models.requests.ShareTaskRequest;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.ErrorElement;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;
import com.grupokinexo.tasksservice.repositories.TaskRepository;
import com.grupokinexo.tasksservice.repositories.UserTaskRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserTaskRepository userTaskRepository;
    private final UsersApiClient usersApiClient;

    public TaskServiceImpl(TaskRepository taskRepository, UserTaskRepository userTaskRepository, UsersApiClient usersApiClient) {
        this.taskRepository = taskRepository;
        this.userTaskRepository = userTaskRepository;
        this.usersApiClient = usersApiClient;
    }

    @Override
    public TaskResponse create(TaskRequest task) {
        Task newTask = new Task();
        newTask.setName(task.getName());
        newTask.setDescription(task.getMessage());
        newTask.setCreatorId(task.getCurrentUserId());

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
        Task taskToEdit = taskRepository.getById(id);
        taskToEdit.setName(task.getName());
        taskToEdit.setDescription(task.getMessage());

        taskRepository.update(taskToEdit);

        return getById(id);
    }

    @Override
    public void shareTask(int id, ShareTaskRequest shareTaskRequest) throws IOException, ParserException, BadRequestApiException {
        Collection<Integer> userIds = new ArrayList<>();

        if (shareTaskRequest.getUserIds() != null) {
            for (int userId : shareTaskRequest.getUserIds()) {
                User validUser = usersApiClient.getById(userId);

                if (validUser == null) {
                    throw new BadRequestApiException(new ErrorElement("", ""));
                }

                userIds.add(userId);
            }
        }

        userTaskRepository.addToTask(id, userIds);
    }

    @Override
    public Collection<User> getUsersByTask(int id) {
        Collection<UserTask> usersByTask = userTaskRepository.getByTaskId(id);
        Collection<User> users = new ArrayList<>();

        usersByTask.forEach(userTask -> {
            User user = null;
            try {
                user = usersApiClient.getById(userTask.getUserId());
            } catch (IOException | ParserException e) {
                e.printStackTrace();
            }

            if (user != null) {
                users.add(user);
            }
        });

        return users;
    }

    private TaskResponse Map(Task task) {
        TaskResponse result = new TaskResponse();
        result.setId(task.getId());
        result.setName(task.getName());
        result.setDescription(task.getDescription());
        result.setCreatedOn(task.getCreatedOn());
        result.setCreatorId(task.getCreatorId());

        return result;
    }
}