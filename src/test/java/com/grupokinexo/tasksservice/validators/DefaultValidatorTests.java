package com.grupokinexo.tasksservice.validators;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultValidatorTests {
    @Test
    public void validateShouldReturnErrorsWhenValidationFail() {
        final String errorMessage = "error message";
        final String propertyName = "prop name";

        Set<ConstraintViolation<Object>> errors = new HashSet<>();
        ConstraintViolation error = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn(propertyName);
        when(error.getPropertyPath()).thenReturn(path);
        when(error.getMessage()).thenReturn(errorMessage);
        errors.add(error);

        Validator validator = mock(Validator.class);
        when(validator.validate(any())).thenReturn(errors);

        DefaultValidator defaultValidator = new DefaultValidator(validator);

        ValidationResult validationResult = defaultValidator.validate(new Object());
        assertNotNull(validationResult);
        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getErrors().size());

        assertAll("Validation error elements",
                () -> assertTrue(validationResult.getErrors().stream().anyMatch(x -> x.getDetail().equals(errorMessage))),
                () -> assertTrue(validationResult.getErrors().stream().anyMatch(x -> x.getPropertyName().equals(propertyName)))
        );
    }

    @Test
    public void validateShouldReturnEmptyErrorWhenValidationPass() {
        Set<ConstraintViolation<Object>> errors = new HashSet<>();
        Validator validator = mock(Validator.class);
        when(validator.validate(any())).thenReturn(errors);

        DefaultValidator defaultValidator = new DefaultValidator(validator);

        ValidationResult validationResult = defaultValidator.validate(1);
        assertNotNull(validationResult);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getErrors().size());
    }
}
