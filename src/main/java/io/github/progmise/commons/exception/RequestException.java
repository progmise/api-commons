package io.github.progmise.commons.exception;

import io.github.progmise.commons.enums.ErrorLevel;
import org.springframework.http.HttpStatus;

public class RequestException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final ErrorLevel level;
    private final String description;

    public RequestException(HttpStatus httpStatus, String errorCode, String errorMessage) {
        this(httpStatus, errorCode, errorMessage, ErrorLevel.ERROR, null);
    }

    public RequestException(
        HttpStatus httpStatus,
        String errorCode,
        String errorMessage,
        ErrorLevel level,
        String description
    ) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.level = level;
        this.description = description;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorLevel getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }
}
