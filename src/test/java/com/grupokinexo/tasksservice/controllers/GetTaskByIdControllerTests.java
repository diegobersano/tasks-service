package com.grupokinexo.tasksservice.controllers;

import com.grupokinexo.tasksservice.exceptions.BadRequestApiException;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.exceptions.UnauthorizedException;
import com.grupokinexo.tasksservice.models.responses.ErrorDetail;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class GetTaskByIdControllerTests extends BaseTaskControllerTest {
    private final int taskId = 11;
    private final String parsedResponse = "parsedTaskResponse";

    private TaskResponse taskResponse;

    @BeforeEach
    void setup() {
        taskResponse = new TaskResponse();
    }

    @Test
    void getShouldReturnOkResponseWhenTaskExists() throws Exception {
        setupSuccessServiceExecution();

        tasksController.getById.handle(request, response);
        verify(response, times(1)).status(HttpStatus.SC_OK);
    }

    @Test
    void getShouldReturnParsedTaskReturnedByService() throws Exception {
        setupSuccessServiceExecution();

        String result = (String) tasksController.getById.handle(request, response);
        assertNotNull(result);
        assertEquals(result, parsedResponse);

        verify(taskService, times(1)).getById(taskId);
        verify(parser, times(1)).parseToString(taskResponse);
    }

    @Test
    void getShouldReturnNotFoundResponseWhenTaskDoesNotExist() throws Exception {
        setupNotFoundServiceExecution();
        tasksController.getById.handle(request, response);

        verify(response, times(1)).status(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void getShouldReturnErrorResponseWhenTaskDoesNotExists() throws Exception {
        final String errorMessage = "errorMessage";

        setupNotFoundServiceExecution();
        when(parser.parseToString(any(ErrorDetail.class))).thenReturn(errorMessage);

        String result = (String) tasksController.getById.handle(request, response);

        assertEquals(result, errorMessage);

        verify(taskService, times(1)).getById(1);
        verify(parser, times(1)).parseToString(any(ErrorDetail.class));
    }

    @Test
    void getShouldThrowBadRequestExceptionWhenTheIdInRouteIsNotAnInteger() {
        when(request.params(":id")).thenReturn("string");

        BadRequestApiException badRequestApiException = assertThrows(BadRequestApiException.class, () -> tasksController.getById.handle(request, response));
        assertNotNull(badRequestApiException);
        assertNotNull(badRequestApiException.getMessage());
        assertNotNull(badRequestApiException.getErrorElements());
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getPropertyName().equals("id")));
        assertTrue(badRequestApiException.getErrorElements().stream().allMatch(x -> x.getDetail() != null && !x.getDetail().isEmpty()));

        verify(taskService, never()).getById(anyInt());
    }

    @Test
    void getShouldThrowUnauthorizedExceptionIfCurrentUserDoesNotMatchWithTheCreatorOfTheTask() throws ParserException {
        final int creatorId = 12;
        setupSuccessServiceExecution();
        tasksController.setCurrentUser(creatorId * 2);
        taskResponse.setCreatorId(creatorId);

        assertThrows(UnauthorizedException.class, () -> tasksController.getById.handle(request, response));
    }

    private void setupSuccessServiceExecution() throws ParserException {
        when(request.params(":id")).thenReturn(String.valueOf(taskId));
        taskResponse = new TaskResponse();
        taskResponse.setId(taskId);
        when(taskService.getById(anyInt())).thenReturn(taskResponse);

        when(parser.parseToString(any())).thenReturn(parsedResponse);
    }

    private void setupNotFoundServiceExecution() {
        when(request.params(":id")).thenReturn("1");
        when(taskService.getById(anyInt())).thenReturn(null);
    }
}