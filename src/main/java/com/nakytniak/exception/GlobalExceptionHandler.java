package com.nakytniak.exception;

import com.nakytniak.dto.CoreResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<CoreResponse<Object>> handleAnyException(final RuntimeException e) {
        logger.log(Level.SEVERE, "Error: {0}", new Object[]{e.getMessage()});
        final CoreResponse<Object> errorResponse = CoreResponse.builder()
                .errorMessage(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<CoreResponse<Object>> handleAnyException(final Exception e) {
        logger.log(Level.SEVERE, "Error: {0}", new Object[]{e.getMessage()});
        final CoreResponse<Object> errorResponse = CoreResponse.builder()
                .errorMessage(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
