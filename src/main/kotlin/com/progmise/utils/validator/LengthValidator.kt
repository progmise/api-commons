package com.progmise.utils.validator

import com.progmise.utils.exception.ExceptionCode
import com.progmise.utils.util.generateLengthException

class LengthValidator(
    private val bounds: Pair<Int, Int> = DEFAULT_BOUNDS,
) : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        if (data.length < bounds.first || data.length > bounds.second) {
            exceptions.add(generateLengthException(fieldNames, bounds))
        }

        return exceptions
    }

    companion object {
        private val DEFAULT_BOUNDS = 1 to 255
    }
}
