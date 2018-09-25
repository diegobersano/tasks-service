package com.grupokinexo.tasksservice.controllers;

import com.grupokinexo.tasksservice.exceptions.*;
import com.grupokinexo.tasksservice.models.requests.ShareTaskRequest;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.ErrorElement;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;
import com.grupokinexo.tasksservice.parsers.Parser;
import com.grupokinexo.tasksservice.services.TaskService;
import com.grupokinexo.tasksservice.validators.ValidationResult;
import com.grupokinexo.tasksservice.validators.Validator;
import org.apache.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TasksController implements BaseController {
    private final Parser parser;
    private final Validator validator;
    private final TaskService taskService;
    private int currentUserId;

    TasksController(Validator validator, Parser parser, TaskService taskService) {
        this.parser = parser;
        this.validator = validator;
        this.taskService = taskService;
    }

    public Route getTask = (request, response) -> getTask();
    public Route getById = this::getById;
    public Route createTask = this::createTask;
    public Route editTask = this::editTask;
    public Route shareTask = this::shareTask;
    public Route getUsersByTask = this::getUsersByTask;

    private String getUsersByTask(Request request, Response response) throws BadRequestApiException, ParserException {
        int taskId = getTaskIdByRoute(request);

        Collection users = taskService.getUsersByTask(taskId);
        response.status(HttpStatus.SC_OK);
        return parser.parseToString(users);
    }

    private String getTask() throws ParserException {
        List<TaskResponse> result = taskService.search();

        return parser.parseToString(result);
    }

    private String getById(Request request, Response response) throws ParserException, BadRequestApiException, UnauthorizedException, NotFoundException {
        int taskId = getTaskIdByRoute(request);

        TaskResponse task = taskService.getById(taskId);

        if (task == null) {
            throw new NotFoundException(String.format("The task with the identifier %d doesn't exists", taskId));
        }

        if (task.getCreatorId() != currentUserId) {
            throw new UnauthorizedException("The user is not the owner of the task");
        }

        response.status(HttpStatus.SC_OK);
        return parser.parseToString(task);
    }

    private String createTask(Request request, Response response) throws ParserException, BadRequestApiException {
        TaskRequest taskRequest = parser.parseToObject(request.body(), TaskRequest.class);

        ValidationResult validationResult = validator.validate(taskRequest);
        if (!validationResult.isValid()) {
            throw new BadRequestApiException(validationResult);
        }

        taskRequest.setCurrentUserId(currentUserId);
        TaskResponse taskResponse = taskService.create(taskRequest);

        response.status(HttpStatus.SC_CREATED);
        response.header("Location", "/api/tasks/" + taskResponse.getId());

        return parser.parseToString(taskResponse);
    }

    private String editTask(Request request, Response response) throws ParserException, BadRequestApiException, UnauthorizedException {
        int taskId = getTaskIdByRoute(request);

        TaskRequest taskRequest = parser.parseToObject(request.body(), TaskRequest.class);

        ValidationResult validationResult = validator.validate(taskRequest);
        if (!validationResult.isValid()) {
            throw new BadRequestApiException(validationResult);
        }

        TaskResponse existingTask = getAndValidateTask(taskId);

        taskRequest.setCurrentUserId(currentUserId);
        TaskResponse taskResponse = taskService.edit(existingTask.getId(), taskRequest);
        response.status(HttpStatus.SC_OK);
        return parser.parseToString(taskResponse);
    }

    private String shareTask(Request request, Response response) throws ParserException, BadRequestApiException, IOException, UnauthorizedException, ConflictException {
        int taskId = getTaskIdByRoute(request);
        ShareTaskRequest shareTaskRequest = parser.parseToObject(request.body(), ShareTaskRequest.class);

        ValidationResult validationResult = validator.validate(shareTaskRequest);
        if (!validationResult.isValid()) {
            throw new BadRequestApiException(validationResult);
        }

        TaskResponse existingTask = getAndValidateTask(taskId);

        if (Arrays.stream(shareTaskRequest.getUserIds()).anyMatch(x -> x == existingTask.getCreatorId())) {
            throw new ConflictException("The task can't be shared with its owner");
        }

        taskService.shareTask(taskId, shareTaskRequest);

        response.status(HttpStatus.SC_OK);

        Collection users = taskService.getUsersByTask(taskId);

        return parser.parseToString(users);
    }

    private int getTaskIdByRoute(Request request) throws BadRequestApiException {
        try {
            return Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            throw new BadRequestApiException(new ErrorElement("id", "The format of the task identifier is not valid"));
        }
    }

    private TaskResponse getAndValidateTask(int taskId) throws BadRequestApiException, UnauthorizedException {
        TaskResponse task = taskService.getById(taskId);

        if (task == null) {
            throw new BadRequestApiException(new ErrorElement("id", "There is not a task with the identifier"));
        }

        if (task.getCreatorId() != currentUserId) {
            throw new UnauthorizedException("The user is not the owner of the task");
        }

        return task;
    }

    @Override
    public void setCurrentUser(int id) {
        currentUserId = id;
    }
}