package com.fixmycar.exception;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Order(0) // Устанавливаем порядок обработки
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Обработка ошибок валидации (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Логируем ошибку с уровнем ERROR
        logger.error("Validation error: {}", errors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Обработка кастомного исключения ValidationException
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        // Логируем ошибку с уровнем ERROR
        logger.error("Validation error: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    // Обработка исключения ResourceNotFoundException (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        // Логируем ошибку с уровнем ERROR
        logger.error("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Обработка HandledRestException (400)
    @ExceptionHandler(HandledRestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleHandledRestException(HandledRestException ex) {
        // Логируем ошибку с уровнем ERROR
        logger.error("Handled REST exception: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());
        
        if (ex.getDetails() != null) {
            response.put("details", ex.getDetails());
        }
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Обработка других исключений (500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // Игнорируем ошибки, связанные с Swagger
        if (ex.getMessage() != null && ex.getMessage().contains("org.springdoc")) {
            return new ResponseEntity<>("Swagger error ignored", HttpStatus.OK);
        }

        // Логируем ошибку с уровнем ERROR
        logger.error("Internal server error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("An error occurred: "
                + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```# CODE

The next part of our plan is to introduce a new custom exception class, `HandledRestException`, which will encapsulate details about error messages for logging and client communication. This exception will represent handled, non-fatal errors that should result in a 400 Bad Request response. It will provide a more descriptive error response to clients and ensure consistent error handling across all endpoints.

<mermaid>
graph TD
    Start[Start: Create HandledRestException] --> A[Define class structure]
    A --> B[Create constructors with different parameter options]
    B --> C[Define method to get error details]
    C --> D[Implement toString method for logging]
    D --> End[HandledRestException ready for use in handlers]
</mermaid>

(added src/main/java/com/fixmycar/exception/HandledRestException.java)