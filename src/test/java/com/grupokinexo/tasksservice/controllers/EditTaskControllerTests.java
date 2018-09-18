package com.grupokinexo.tasksservice.controllers;

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
    public void editShouldReturnOkResponseWhenValidationsArePassed() throws Exception {
        when(request.params(":id")).thenReturn("1");
        when(validator.validate(any())).thenReturn(new ValidationResult());

        tasksController.editTask.handle(request, response);
        verify(response, times(1)).status(HttpStatus.SC_OK);
    }

    @Test
    public void editShouldCallServiceWhenValidationsArePassed() throws Exception {
        final String parsedResponse = "parsedResponse";
        final int taskId = 9;
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(taskId);
        taskResponse.setName("Name");

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("Name");

        when(request.params(":id")).thenReturn(String.valueOf(taskId));

        when(validator.validate(any())).thenReturn(new ValidationResult());
        when(taskService.edit(anyInt(), any(TaskRequest.class))).thenReturn(taskResponse);
        when(parser.parseToString(any(TaskRequest.class))).thenReturn(parsedResponse);

        String result = (String) tasksController.editTask.handle(request, response);
        assertNotNull(result);
        assertEquals(parsedResponse, result);

        verify(taskService, times(1)).edit(taskId, taskRequest);
        verify(parser, times(1)).parseToString(taskResponse);
    }

    private  void  setupSuccessServiceExecution() {
        
    }
}