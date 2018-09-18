package com.grupokinexo.tasksservice.controllers;

import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.ErrorDetail;
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

    public Route getTask = this::getTask;
    public Route getById = this::getById;
    public Route createTask = this::createTask;
    public Route editTask = this::editTask;

    private String getTask(Request request, Response response) throws ParserException {
        List<TaskResponse> result = taskService.search();

        return parser.parseToString(result);
    }

    private String getById(Request request, Response response) throws ParserException {
        int taskId;
        try {
            taskId = Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.SC_BAD_REQUEST);

            return parser.parseToString(new ErrorDetail());
        }

        TaskResponse task = taskService.getById(taskId);

        if (task == null) {
            response.status(HttpStatus.SC_NOT_FOUND);
            return parser.parseToString(new ErrorDetail());
        }

        response.status(HttpStatus.SC_OK);
        return parser.parseToString(task);
    }

    private String createTask(Request request, Response response) throws ParserException {
        String body = request.body();

        if (body == null || body.isEmpty()) {
            body = "{}";
        }

        TaskRequest taskRequest = parser.parseToObject(body, TaskRequest.class);

        ValidationResult validationResult = validator.validate(taskRequest);
        if (!validationResult.isValid()) {
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setMessage("The following fields (values) showed error during validation:");
            errorDetail.addElements(validationResult.getErrors());

            response.status(HttpStatus.SC_BAD_REQUEST);
            return parser.parseToString(errorDetail);
        }

        TaskResponse taskResponse = taskService.create(taskRequest);

        response.status(HttpStatus.SC_CREATED);
        response.header("Location", "/api/tasks/" + taskResponse.getId());

        return parser.parseToString(taskResponse);
    }

    private String editTask(Request request, Response response) throws ParserException {
        String body = request.body();

        if (body == null || body.isEmpty()) {
            body = "{}";
        }

        TaskRequest taskRequest = parser.parseToObject(body, TaskRequest.class);

        ValidationResult validationResult = validator.validate(taskRequest);
        if (!validationResult.isValid()) {
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setMessage("The following fields (values) showed error during validation:");
            errorDetail.addElements(validationResult.getErrors());

            response.status(HttpStatus.SC_BAD_REQUEST);
            return parser.parseToString(errorDetail);
        }

        int taskId;
        try {
            taskId = Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.SC_BAD_REQUEST);

            return parser.parseToString(new ErrorDetail());
        }

        TaskResponse existingTask = taskService.getById(taskId);

        if (existingTask == null) {
            response.status(HttpStatus.SC_BAD_REQUEST);

            return parser.parseToString(new ErrorDetail());
        }

        TaskResponse taskResponse = taskService.edit(existingTask.getId(), taskRequest);

        response.status(HttpStatus.SC_OK);

        return parser.parseToString(taskResponse);
    }
}