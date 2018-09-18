package com.grupokinexo.tasksservice.controllers;

import com.grupokinexo.tasksservice.parsers.Parser;
import com.grupokinexo.tasksservice.services.TaskService;
import com.grupokinexo.tasksservice.validators.Validator;
import org.junit.Before;
import spark.Request;
import spark.Response;

import static org.mockito.Mockito.mock;

public abstract class BaseTaskControllerTest {
    TasksController tasksController;
    Parser parser;
    Validator validator;
    TaskService taskService;
    Request request;
    Response response;

    @Before
    public void baseSetup() {
        parser = mock(Parser.class);
        validator = mock(Validator.class);
        taskService = mock(TaskService.class);

        request = mock(Request.class);
        response = mock(Response.class);

        tasksController = new TasksController(validator, parser, taskService);
    }
}