package com.progmise.utils.validator

import com.progmise.utils.exception.ExceptionCode

interface Validator<T> {
    fun validate(
        data: T,
        fieldNames: List<String>,
    ): List<ExceptionCode>
}
