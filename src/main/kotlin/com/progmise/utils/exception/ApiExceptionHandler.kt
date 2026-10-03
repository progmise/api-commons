package com.progmise.utils.exception

import com.progmise.utils.dto.ApiError
import com.progmise.utils.dto.ErrorsResponse
import com.progmise.utils.enums.ErrorLevel
import com.progmise.utils.util.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class ApiExceptionHandler {
    private val log by logger()

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(exception: BadRequestException): ResponseEntity<ErrorsResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorsResponse(
                exception.exceptions.map {
                    ApiError(
                        code = it.code,
                        message = it.message,
                        level = exception.level,
                        description = exception.errorCodeGeneral,
                    )
                },
            ),
        )

    @ExceptionHandler(RequestException::class)
    fun handleRequestException(exception: RequestException): ResponseEntity<ErrorsResponse> {
        log.error(
            "Exception thrown - CODE: {} MESSAGE: \"{}\" STATUS: {}",
            exception.errorCode,
            exception.message,
            exception.httpStatus,
        )

        return ResponseEntity.status(exception.httpStatus).body(
            ErrorsResponse(
                listOf(
                    ApiError(
                        code = exception.errorCode,
                        message = exception.message ?: exception.errorCode,
                        level = exception.level,
                        description = exception.description,
                    ),
                ),
            ),
        )
    }

    @ExceptionHandler(
        MissingServletRequestParameterException::class,
        MethodArgumentTypeMismatchException::class,
        HttpMessageNotReadableException::class,
    )
    fun handleMalformedRequest(exception: Exception): ResponseEntity<ErrorsResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorsResponse(
                listOf(
                    ApiError(
                        code = "invalid.request",
                        message = exception.message ?: "Malformed request",
                        level = ErrorLevel.ERROR,
                    ),
                ),
            ),
        )

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(exception: Exception): ResponseEntity<ErrorsResponse> {
        log.error("Unexpected exception - MESSAGE: \"{}\"", exception.message, exception)

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorsResponse(
                listOf(
                    ApiError(
                        code = "internal.error",
                        message = "An unexpected error occurred",
                        level = ErrorLevel.CRITICAL,
                    ),
                ),
            ),
        )
    }
}
