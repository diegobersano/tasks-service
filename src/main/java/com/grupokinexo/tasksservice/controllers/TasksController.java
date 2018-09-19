package com.grupokinexo.tasksservice.controllers;

import com.grupokinexo.tasksservice.exceptions.BadRequestApiException;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.ErrorDetail;
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

import java.util.List;

public class TasksController {
    private final Parser parser;
    private final Validator validator;
    private final TaskService taskService;

    TasksController(Validator validator, Parser parser, TaskService taskService) {
        this.parser = parser;
        this.validator = validator;
        this.taskService = taskService;
    }

    public Route getTask = (request, response) -> getTask();
    public Route getById = this::getById;
    public Route createTask = this::createTask;
    public Route editTask = this::editTask;

    private String getTask() throws ParserException {
        List<TaskResponse> result = taskService.search();

        return parser.parseToString(result);
    }

    private String getById(Request request, Response response) throws ParserException, BadRequestApiException {
        int taskId = getTaskIdByRoute(request);

        TaskResponse task = taskService.getById(taskId);

        if (task == null) {
            response.status(HttpStatus.SC_NOT_FOUND);
            return parser.parseToString(new ErrorDetail());
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

        TaskResponse taskResponse = taskService.create(taskRequest);

        response.status(HttpStatus.SC_CREATED);
        response.header("Location", "/api/tasks/" + taskResponse.getId());

        return parser.parseToString(taskResponse);
    }

    private String editTask(Request request, Response response) throws ParserException, BadRequestApiException {
        int taskId = getTaskIdByRoute(request);

        TaskRequest taskRequest = parser.parseToObject(request.body(), TaskRequest.class);

        ValidationResult validationResult = validator.validate(taskRequest);
        if (!validationResult.isValid()) {
            throw new BadRequestApiException(validationResult);
        }

        TaskResponse existingTask = taskService.getById(taskId);

        if (existingTask == null) {
            throw new BadRequestApiException(new ErrorElement("id", "There is not a task with the identifier"));
        }

        TaskResponse taskResponse = taskService.edit(existingTask.getId(), taskRequest);
        response.status(HttpStatus.SC_OK);
        return parser.parseToString(taskResponse);
    }

    private int getTaskIdByRoute(Request request) throws BadRequestApiException {
        try {
            return Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            throw new BadRequestApiException(new ErrorElement("id", "The format of the task identifier is not valid"));
        }
    }
}