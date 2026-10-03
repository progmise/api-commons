package com.progmise.utils.exception

import com.progmise.utils.enums.ErrorLevel
import org.springframework.http.HttpStatus

open class RequestException(
    val httpStatus: HttpStatus,
    val errorCode: String,
    errorMessage: String,
    val level: ErrorLevel = ErrorLevel.ERROR,
    val description: String? = null,
) : RuntimeException(errorMessage)
