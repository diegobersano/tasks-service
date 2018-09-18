package com.grupokinexo.tasksservice.exceptions;

public class BadRequestApiException extends Exception {
    public BadRequestApiException(String message) {
        super(message);
    }
}