package io.github.progmise.commons.exception;

import io.github.progmise.commons.dto.ApiError;
import io.github.progmise.commons.dto.ErrorsResponse;
import io.github.progmise.commons.enums.ErrorLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorsResponse> handleBadRequest(BadRequestException exception) {
        List<ApiError> errors = exception.getExceptions().stream()
            .map(code -> new ApiError(
                code.code(),
                code.message(),
                exception.getLevel(),
                exception.getErrorCodeGeneral()
            ))
            .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorsResponse(errors));
    }

    @ExceptionHandler(RequestException.class)
    public ResponseEntity<ErrorsResponse> handleRequestException(RequestException exception) {
        log.error(
            "Exception thrown - CODE: {} MESSAGE: \"{}\" STATUS: {}",
            exception.getErrorCode(),
            exception.getMessage(),
            exception.getHttpStatus()
        );

        return ResponseEntity.status(exception.getHttpStatus()).body(
            new ErrorsResponse(List.of(new ApiError(
                exception.getErrorCode(),
                exception.getMessage() != null ? exception.getMessage() : exception.getErrorCode(),
                exception.getLevel(),
                exception.getDescription()
            )))
        );
    }

    @ExceptionHandler({
        MissingServletRequestParameterException.class,
        MethodArgumentTypeMismatchException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorsResponse> handleMalformedRequest(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorsResponse(List.of(new ApiError(
                "invalid.request",
                exception.getMessage() != null ? exception.getMessage() : "Malformed request",
                ErrorLevel.ERROR
            )))
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorsResponse> handleUnexpected(Exception exception) {
        log.error("Unexpected exception - MESSAGE: \"{}\"", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ErrorsResponse(List.of(new ApiError(
                "internal.error",
                "An unexpected error occurred",
                ErrorLevel.CRITICAL
            )))
        );
    }
}
