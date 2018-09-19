package com.grupokinexo.tasksservice.controllers;

import com.grupokinexo.tasksservice.exceptions.BadRequestApiException;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.ErrorElement;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;
import com.grupokinexo.tasksservice.validators.ValidationResult;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateTaskControllerTests extends BaseTaskControllerTest {
    private final int taskId = 98;
    private final String parsedResponse = "parsedCreatedTaskResponse";

    private TaskRequest taskRequest;
    private TaskResponse taskResponse;

    @Before
    public void setup() throws ParserException {
        taskRequest = new TaskRequest();
        taskRequest.setName("Name");

        when(parser.parseToObject(any(), eq(TaskRequest.class))).thenReturn(taskRequest);
    }

    @Test
    public void createShouldThrowBadRequestExceptionWhenValidatorReturnsErrors() {
        setupValidationErrorsExecution();

        BadRequestApiException badRequestApiException = assertThrows(BadRequestApiException.class, () -> tasksController.createTask.handle(request, response));
        assertNotNull(badRequestApiException);
        assertNotNull(badRequestApiException.getMessage());
        assertNotNull(badRequestApiException.getErrorElements());
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getPropertyName().equals("propName")));
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getDetail() != null && !x.getDetail().isEmpty()));
    }

    @Test
    public void createShouldNotCallCreateServiceWhenValidatorReturnsErrors() {
        setupValidationErrorsExecution();

        assertThrows(Exception.class, () -> tasksController.createTask.handle(request, response));

        verify(taskService, never()).create(any(TaskRequest.class));
    }

    @Test
    public void createShouldReturnCreatedResponseWhenValidationsArePassed() throws Exception {
        setupSuccessServiceExecution();
        tasksController.createTask.handle(request, response);

        verify(response, times(1)).status(HttpStatus.SC_CREATED);
        verify(response, times(1)).header("Location", "/api/tasks/" + taskId);
    }

    @Test
    public void createShouldCallServiceAndReturnCreatedEntity() throws Exception {
        setupSuccessServiceExecution();

        String result = (String) tasksController.createTask.handle(request, response);
        assertNotNull(result);
        assertEquals(parsedResponse, result);

        verify(taskService, times(1)).create(taskRequest);
        verify(parser, times(1)).parseToString(taskResponse);
    }

    private void setupSuccessServiceExecution() throws ParserException {
        taskResponse = new TaskResponse();
        taskResponse.setId(taskId);
        when(taskService.create(any(TaskRequest.class))).thenReturn(taskResponse);
        when(parser.parseToString(any())).thenReturn(parsedResponse);

        when(validator.validate(any(TaskRequest.class))).thenReturn(new ValidationResult());
    }

    private void setupValidationErrorsExecution() {
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError(new ErrorElement("propName", "error message"));

        when(validator.validate(any(TaskRequest.class))).thenReturn(validationResult);
    }
}