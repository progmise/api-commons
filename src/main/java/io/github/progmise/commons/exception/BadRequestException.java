package io.github.progmise.commons.exception;

import io.github.progmise.commons.enums.ErrorLevel;
import org.springframework.http.HttpStatus;

import java.util.List;

public class BadRequestException extends RequestException {

    private final List<ExceptionCode> exceptions;
    private final String errorCodeGeneral;

    public BadRequestException(List<ExceptionCode> exceptions, String errorCodeGeneral) {
        this(exceptions, errorCodeGeneral, ErrorLevel.ERROR);
    }

    public BadRequestException(List<ExceptionCode> exceptions, String errorCodeGeneral, ErrorLevel level) {
        super(
            HttpStatus.BAD_REQUEST,
            errorCodeGeneral,
            exceptions.isEmpty() ? errorCodeGeneral : exceptions.get(0).message(),
            level,
            null
        );
        this.exceptions = exceptions;
        this.errorCodeGeneral = errorCodeGeneral;
    }

    public BadRequestException(String errorMessage, String errorCode, String errorCodeGeneral) {
        this(List.of(new ExceptionCode(errorCode, errorMessage)), errorCodeGeneral);
    }

    public List<ExceptionCode> getExceptions() {
        return exceptions;
    }

    public String getErrorCodeGeneral() {
        return errorCodeGeneral;
    }
}
