package com.grupokinexo.tasksservice.validators;

public interface Validator {
    ValidationResult validate(Object elementToValidate);
}