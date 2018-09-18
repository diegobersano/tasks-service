package com.grupokinexo.tasksservice.validators;

import com.grupokinexo.tasksservice.models.responses.ErrorElement;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class DefaultValidator implements Validator {
    private final javax.validation.Validator validator;

    DefaultValidator(javax.validation.Validator validator) {
        this.validator = validator;
    }

    @Override
    public ValidationResult validate(Object elementToValidate) {
        ValidationResult result = new ValidationResult();

        Set<ConstraintViolation<Object>> validationErrors = validator.validate(elementToValidate);

        if (validationErrors.isEmpty()) {
            return result;
        }

        validationErrors.forEach(error -> result.addError(new ErrorElement(
                error.getPropertyPath().toString(),
                error.getMessage())));

        return result;
    }
}