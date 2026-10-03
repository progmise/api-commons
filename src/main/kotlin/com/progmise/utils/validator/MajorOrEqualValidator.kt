package com.progmise.utils.validator

import com.progmise.utils.exception.ExceptionCode
import com.progmise.utils.util.generateValueNotMajorOrEqualException

class MajorOrEqualValidator(
    private val bound: Int,
) : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()
        val value = data.toIntOrNull()

        if (value == null || value < bound) {
            exceptions.add(generateValueNotMajorOrEqualException(fieldNames, bound))
        }

        return exceptions
    }
}
