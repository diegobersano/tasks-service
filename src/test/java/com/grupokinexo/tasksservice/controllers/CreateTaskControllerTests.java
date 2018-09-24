package com.grupokinexo.tasksservice.controllers;

import com.grupokinexo.tasksservice.exceptions.BadRequestApiException;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.ErrorElement;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;
import com.grupokinexo.tasksservice.validators.ValidationResult;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateTaskControllerTests extends BaseTaskControllerTest {
    private final int taskId = 98;
    private final int creatorId = 42;
    private final String parsedResponse = "parsedCreatedTaskResponse";

    private TaskRequest taskRequest;
    private TaskResponse taskResponse;

    @BeforeEach
    void setup() throws ParserException {
        taskRequest = new TaskRequest();
        taskRequest.setName("Name");

        when(parser.parseToObject(any(), eq(TaskRequest.class))).thenReturn(taskRequest);
        tasksController.setCurrentUser(creatorId);
    }

    @Test
    void createShouldThrowBadRequestExceptionWhenValidatorReturnsErrors() {
        setupValidationErrorsExecution();

        BadRequestApiException badRequestApiException = assertThrows(BadRequestApiException.class, () -> tasksController.createTask.handle(request, response));
        assertNotNull(badRequestApiException);
        assertNotNull(badRequestApiException.getMessage());
        assertNotNull(badRequestApiException.getErrorElements());
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getPropertyName().equals("propName")));
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getDetail() != null && !x.getDetail().isEmpty()));
    }

    @Test
    void createShouldNotCallCreateServiceWhenValidatorReturnsErrors() {
        setupValidationErrorsExecution();

        assertThrows(Exception.class, () -> tasksController.createTask.handle(request, response));

        verify(taskService, never()).create(any(TaskRequest.class));
    }

    @Test
    void createShouldReturnCreatedResponseWhenValidationsArePassed() throws Exception {
        setupSuccessServiceExecution();
        tasksController.createTask.handle(request, response);

        verify(response, times(1)).status(HttpStatus.SC_CREATED);
        verify(response, times(1)).header("Location", "/api/tasks/" + taskId);
    }

    @Test
    void createShouldCallServiceAndReturnCreatedEntity() throws Exception {
        setupSuccessServiceExecution();

        String result = (String) tasksController.createTask.handle(request, response);
        assertNotNull(result);
        assertEquals(parsedResponse, result);

        verify(taskService, times(1)).create(taskRequest);
        verify(parser, times(1)).parseToString(taskResponse);
    }

    @Test
    void createShouldCallServiceWithCurrentUserId() throws Exception {
        setupSuccessServiceExecution();

        ArgumentCaptor<TaskRequest> argumentCaptor = ArgumentCaptor.forClass(TaskRequest.class);
        tasksController.createTask.handle(request, response);
        verify(taskService).create(argumentCaptor.capture());

        assertEquals(creatorId, argumentCaptor.getValue().getCurrentUserId());
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