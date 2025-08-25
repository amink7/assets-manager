package com.example.assets.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global exception handler for REST controllers.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(RestExceptionHandler.class);


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class,
            ConversionFailedException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public void handleBadRequest(Exception ex) {
        log.warn("Bad request", ex);
        //respuesta con 400 Bad Request
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public void handleNotFound(NoHandlerFoundException ex) {
        log.warn("Route not found: {}", ex.getRequestURL());
    }
}