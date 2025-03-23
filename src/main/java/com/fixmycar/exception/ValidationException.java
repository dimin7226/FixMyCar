package com.fixmycar.exception;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final List<ValidationErrorResponse.ValidationError> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }

    public ValidationException(String message, List<ValidationErrorResponse.ValidationError> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(String message, String field, Object rejectedValue, String errorMessage) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(new ValidationErrorResponse.ValidationError(field, rejectedValue, errorMessage));
    }

    public void addValidationError(String field, Object rejectedValue, String errorMessage) {
        this.errors.add(new ValidationErrorResponse.ValidationError(field, rejectedValue, errorMessage));
    }
}
