package com.progmise.utils.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.progmise.utils.enums.ErrorLevel

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiError(
    val code: String,
    val message: String,
    val level: ErrorLevel,
    val description: String? = null,
)

data class ErrorsResponse(
    val errors: List<ApiError>,
)
