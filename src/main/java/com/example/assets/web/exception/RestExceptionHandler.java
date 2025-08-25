package com.example.assets.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Global exception handler for REST controllers.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(RestExceptionHandler.class);


    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class,
            ConversionFailedException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            DateTimeParseException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        log.warn("Bad request", ex);
        //respuesta con 400 Bad Request
        String message = "Bad request";

        if (ex instanceof MethodArgumentTypeMismatchException mismatch &&
                "sortDirection".equals(mismatch.getName())) {
            message = "Sort direction must be ASC or DESC";
        } else if (hasDateTimeParseException(ex)) {
            message = "Fecha mal formateada";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        log.warn("{}", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ErrorResponse(ex.getReason()));
    }

    private boolean hasDateTimeParseException(Throwable ex) {
        while (ex != null) {
            if (ex instanceof DateTimeParseException) {
                return true;
            }
            ex = ex.getCause();
        }
        return false;
    }

    public record ErrorResponse(String message) { }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public Map<String, String> handleNotFound(NoHandlerFoundException ex) {
        log.warn("Route not found: {}", ex.getRequestURL());
        return Map.of("message", "Endpoint not found");
    }
}