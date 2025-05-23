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

    @ExceptionHandler(TooQuicklyException.class)
    public ResponseEntity<String> handleTooQuicklyException(TooQuicklyException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.TOO_EARLY);
    }
    
    // Обработка исключения ResourceNotFoundException (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        // Логируем ошибку с уровнем ERROR
        logger.error("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
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
