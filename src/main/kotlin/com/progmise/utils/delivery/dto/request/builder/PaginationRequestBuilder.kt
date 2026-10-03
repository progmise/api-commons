package com.progmise.utils.delivery.dto.request.builder

import com.progmise.utils.exception.BadRequestException
import com.progmise.utils.exception.ExceptionCode
import com.progmise.utils.util.Constants.ERROR_PATH
import com.progmise.utils.util.generateValueNotMajorOrEqualException
import com.progmise.utils.validator.IntegerValidator

class PaginationRequestBuilder {
    private val integerValidator = IntegerValidator()

    fun build(allParams: Map<String, String>): Pair<Int, Int> {
        val exceptions = ArrayList<ExceptionCode>()
        val offset = allParams[OFFSET_PARAM] ?: DEFAULT_OFFSET.toString()
        val limit = allParams[LIMIT_PARAM] ?: DEFAULT_LIMIT.toString()

        exceptions.addAll(integerValidator.validate(offset, listOf(OFFSET_PARAM)))
        exceptions.addAll(integerValidator.validate(limit, listOf(LIMIT_PARAM)))

        if (exceptions.isEmpty() && offset.toInt() < 0) {
            exceptions.add(generateValueNotMajorOrEqualException(listOf(OFFSET_PARAM), 0))
        }

        if (exceptions.isEmpty() && limit.toInt() < MIN_LIMIT) {
            exceptions.add(generateValueNotMajorOrEqualException(listOf(LIMIT_PARAM), MIN_LIMIT))
        }

        if (exceptions.isEmpty() && limit.toInt() > MAX_LIMIT) {
            exceptions.add(
                ExceptionCode(
                    code = "invalid.value.$LIMIT_PARAM",
                    message = "The $LIMIT_PARAM value must be minor or equal to $MAX_LIMIT",
                ),
            )
        }

        if (exceptions.isNotEmpty()) {
            throw BadRequestException(exceptions = exceptions, errorCodeGeneral = ERROR_PATH)
        }

        return offset.toInt() to limit.toInt()
    }

    companion object {
        const val OFFSET_PARAM = "_offset"
        const val LIMIT_PARAM = "_limit"
        private const val DEFAULT_OFFSET = 0
        private const val DEFAULT_LIMIT = 20
        private const val MIN_LIMIT = 1
        private const val MAX_LIMIT = 100
    }
}
