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

public class EditTaskControllerTests extends BaseTaskControllerTest {
    private final int taskId = 9;
    private TaskResponse taskResponse;
    private TaskRequest taskRequest;

    @Before
    public void setup() {
        when(request.params(":id")).thenReturn(String.valueOf(taskId));
    }

    @Test
    public void editShouldReturnBadRequestResponseWhenIdInRouteIsNotInteger() {
        when(request.params(":id")).thenReturn("string");

        BadRequestApiException badRequestApiException = assertThrows(BadRequestApiException.class, () -> tasksController.editTask.handle(request, response));
        assertNotNull(badRequestApiException);
        assertNotNull(badRequestApiException.getMessage());
        assertNotNull(badRequestApiException.getErrorElements());
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getPropertyName().equals("id")));
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getDetail() != null && !x.getDetail().isEmpty()));

        verify(taskService, never()).edit(anyInt(), any(TaskRequest.class));
    }

    @Test
    public void editShouldReturnBadRequestWhenValidationsAreNotPassed() {
        final ErrorElement errorElement = new ErrorElement("prop", "detail");
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError(errorElement);
        when(validator.validate(any())).thenReturn(validationResult);

        BadRequestApiException badRequestApiException = assertThrows(BadRequestApiException.class, () -> tasksController.editTask.handle(request, response));
        assertNotNull(badRequestApiException);
        assertNotNull(badRequestApiException.getMessage());
        assertNotNull(badRequestApiException.getErrorElements());
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getPropertyName().equals(errorElement.getPropertyName())));
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getDetail().equals(errorElement.getDetail())));

        verify(taskService, never()).edit(anyInt(), any(TaskRequest.class));
    }

    @Test
    public void editShouldReturnBadRequestResponseWhenTheTaskToEditDoesNotExists() throws Exception {
        setupSuccessServiceExecution();
        when(taskService.getById(anyInt())).thenReturn(null);

        BadRequestApiException badRequestApiException = assertThrows(BadRequestApiException.class, () -> tasksController.editTask.handle(request, response));
        assertNotNull(badRequestApiException);
        assertNotNull(badRequestApiException.getMessage());
        assertNotNull(badRequestApiException.getErrorElements());
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getPropertyName().equals("id")));
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getDetail() != null && !x.getDetail().isEmpty()));
    }

    @Test
    public void editShouldReturnOkResponseWhenValidationsArePassed() throws Exception {
        setupSuccessServiceExecution();

        tasksController.editTask.handle(request, response);
        verify(response, times(1)).status(HttpStatus.SC_OK);
    }

    @Test
    public void editShouldCallServiceWhenValidationsArePassed() throws Exception {
        setupSuccessServiceExecution();

        when(taskService.edit(anyInt(), any(TaskRequest.class))).thenReturn(taskResponse);
        String parsedResponse = "parsedResponse";
        when(parser.parseToString(any(TaskResponse.class))).thenReturn(parsedResponse);

        String result = (String) tasksController.editTask.handle(request, response);
        assertNotNull(result);
        assertEquals(parsedResponse, result);

        verify(taskService, times(1)).edit(taskId, taskRequest);
        verify(parser, times(1)).parseToString(taskResponse);
    }

    private void setupSuccessServiceExecution() throws ParserException {
        taskResponse = new TaskResponse();
        taskResponse.setId(taskId);
        taskResponse.setName("Name");

        taskRequest = new TaskRequest();
        taskRequest.setName("Name");

        when(parser.parseToObject(any(), eq(TaskRequest.class))).thenReturn(taskRequest);
        when(validator.validate(any())).thenReturn(new ValidationResult());
        when(taskService.getById(anyInt())).thenReturn(taskResponse);
    }
}