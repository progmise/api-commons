package com.progmise.utils.validator

import com.progmise.utils.exception.ExceptionCode
import com.progmise.utils.util.generateNumericException

class NumericValidator : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        if (isValid(data).not()) {
            exceptions.add(generateNumericException(fieldNames))
        }

        return exceptions
    }

    companion object {
        private val REGEX = """^\d+$""".toRegex()

        fun isValid(data: String): Boolean = REGEX.containsMatchIn(data)
    }
}
