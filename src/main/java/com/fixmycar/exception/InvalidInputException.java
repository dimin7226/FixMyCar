package com.fixmycar.exception;

import org.springframework.http.HttpStatus;

public class InvalidInputException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private final HttpStatus status;
    
    public InvalidInputException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
    
    public InvalidInputException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
}
