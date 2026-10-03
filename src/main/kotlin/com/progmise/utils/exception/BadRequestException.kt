package com.progmise.utils.exception

import com.progmise.utils.enums.ErrorLevel
import org.springframework.http.HttpStatus

class BadRequestException(
    val exceptions: List<ExceptionCode>,
    val errorCodeGeneral: String,
    level: ErrorLevel = ErrorLevel.ERROR,
) : RequestException(
        httpStatus = HttpStatus.BAD_REQUEST,
        errorCode = errorCodeGeneral,
        errorMessage = exceptions.firstOrNull()?.message ?: errorCodeGeneral,
        level = level,
    ) {
    constructor(
        errorMessage: String,
        errorCode: String,
        errorCodeGeneral: String,
    ) : this(
        exceptions = listOf(ExceptionCode(code = errorCode, message = errorMessage)),
        errorCodeGeneral = errorCodeGeneral,
    )
}
