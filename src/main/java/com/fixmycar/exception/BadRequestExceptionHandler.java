package com.fixmycar.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

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
@Order(0) // Высокий приоритет для обработки ошибок валидации
public class BadRequestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BadRequestExceptionHandler.class);

    // Обработка ошибок валидации для параметров запроса и path variables
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();
        
        // Извлекаем и форматируем сообщения об ошибках
        Map<String, String> validationErrors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage(),
                        (error1, error2) -> error1 + "; " + error2)); // в случае дубликатов, объединяем
        
        errors.put("errors", validationErrors);
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("message", "Validation failed for request parameters");
        
        // Логируем ошибку с уровнем ERROR
        logger.error("Validation error for request parameters: {}", validationErrors);
        
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Обработка ошибок валидации для request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> errors = new HashMap<>();
        
        // Собираем все ошибки валидации
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                Map<String, Object> errorDetails = new HashMap<>();
                errorDetails.put("message", fieldError.getDefaultMessage());
                errorDetails.put("rejectedValue", fieldError.getRejectedValue());
                errors.put(fieldError.getField(), errorDetails);
            } else {
                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });
        
        response.put("errors", errors);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed for request body");
        
        // Логируем ошибку с уровнем ERROR
        logger.error("Validation error for request body: {}", errors);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    // Обработка BadRequestException
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());
        
        // Логируем ошибку с уровнем ERROR
        logger.error("Bad request error: {}", ex.getMessage());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
