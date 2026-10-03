package com.progmise.utils.validator

import com.progmise.utils.exception.ExceptionCode
import com.progmise.utils.util.Constants.ISO_DATE_PATTERN
import com.progmise.utils.util.generateDateException
import com.progmise.utils.util.toLocalDate

class DateValidator : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        if (data.toLocalDate(ISO_DATE_PATTERN) == null) {
            exceptions.add(generateDateException(fieldNames))
        }

        return exceptions
    }
}
