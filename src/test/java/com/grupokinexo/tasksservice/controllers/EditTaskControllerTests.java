package com.grupokinexo.tasksservice.controllers;

import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.ErrorDetail;
import com.grupokinexo.tasksservice.models.responses.ErrorElement;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;
import com.grupokinexo.tasksservice.validators.ValidationResult;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class EditTaskControllerTests extends BaseTaskControllerTest {
    private final int taskId = 9;
    private TaskResponse taskResponse;
    private TaskRequest taskRequest;

    @Test
    public void editShouldReturnBadRequestResponseWhenIdInRouteIsNotInteger() throws Exception {
        when(request.params(":id")).thenReturn("string");
        tasksController.editTask.handle(request, response);

        verify(parser, times(1)).parseToString(any(ErrorDetail.class));
        verify(response, times(1)).status(HttpStatus.SC_BAD_REQUEST);
        verify(taskService, never()).edit(anyInt(), any(TaskRequest.class));
    }

    @Test
    public void editShouldReturnBadRequestWhenValidationsAreNotPassed() throws Exception {
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError(new ErrorElement("prop", "detail"));
        when(validator.validate(any())).thenReturn(validationResult);

        tasksController.editTask.handle(request, response);

        verify(parser, times(1)).parseToString(any(ErrorDetail.class));
        verify(response, times(1)).status(HttpStatus.SC_BAD_REQUEST);
        verify(taskService, never()).edit(anyInt(), any(TaskRequest.class));
    }

    @Test
    public void editShouldReturnBadRequestResponseWhenTheTaskToEditDoesNotExists() throws Exception {
        setupSuccessServiceExecution();
        when(taskService.getById(anyInt())).thenReturn(null);

        tasksController.editTask.handle(request, response);
        verify(response, times(1)).status(HttpStatus.SC_BAD_REQUEST);
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

        when(request.params(":id")).thenReturn(String.valueOf(taskId));
        when(parser.parseToObject(any(), eq(TaskRequest.class))).thenReturn(taskRequest);
        when(validator.validate(any())).thenReturn(new ValidationResult());
        when(taskService.getById(anyInt())).thenReturn(taskResponse);
    }
}