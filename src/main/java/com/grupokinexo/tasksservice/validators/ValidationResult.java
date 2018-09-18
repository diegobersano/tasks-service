package com.grupokinexo.tasksservice.validators;

import com.grupokinexo.tasksservice.models.responses.ErrorElement;

import java.util.ArrayList;
import java.util.Collection;

public class ValidationResult {
    public ValidationResult() {
        errorDetails = new ArrayList<>();
    }

    private Collection<ErrorElement> errorDetails;

    public boolean isValid() {
        return errorDetails.isEmpty();
    }

    public void addError(ErrorElement errorDetail) {
        errorDetails.add(errorDetail);
    }

    public Collection<ErrorElement> getErrors() {
        return errorDetails;
    }
}