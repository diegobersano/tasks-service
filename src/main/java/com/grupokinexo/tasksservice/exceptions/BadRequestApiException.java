package com.grupokinexo.tasksservice.exceptions;

import com.grupokinexo.tasksservice.models.responses.ErrorElement;
import com.grupokinexo.tasksservice.validators.ValidationResult;

import java.util.ArrayList;
import java.util.Collection;

public class BadRequestApiException extends Exception {
    private Collection<ErrorElement> errorElements;

    BadRequestApiException(String message) {
        super(message);
    }

    public BadRequestApiException(String message, Collection<ErrorElement> errorElements) {
        this(message);
        this.errorElements = errorElements;
    }

    public BadRequestApiException(ValidationResult validationResult) {
        this("The following fields showed error during validation");
        this.errorElements = validationResult.getErrors();
    }

    public BadRequestApiException(ErrorElement errorElement) {
        this("The following fields showed error during validation");
        this.errorElements = new ArrayList<>();
        this.errorElements.add(errorElement);
    }

    public Collection<ErrorElement> getErrorElements() {
        return errorElements;
    }
}