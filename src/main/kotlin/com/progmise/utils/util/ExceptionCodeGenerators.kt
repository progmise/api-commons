package com.progmise.utils.util

import com.progmise.utils.exception.ExceptionCode
import java.text.MessageFormat.format

private const val REQUIRED_FIELD_CODE_TEMPLATE = "required.field.{0}"
private const val REQUIRED_QUERY_PARAM_CODE_TEMPLATE = "required.query.param.{0}"
private const val INVALID_VALUE_CODE_TEMPLATE = "invalid.value.{0}"
private const val INVALID_LENGTH_CODE_TEMPLATE = "invalid.length.{0}"
private const val FEATURE_DISABLED_CODE_TEMPLATE = "feature.disabled.{0}"
private const val REQUIRED_FIELD_MESSAGE_TEMPLATE = "The {0} is required"
private const val REQUIRED_QUERY_PARAM_MESSAGE_TEMPLATE = "The query param {0} is required"
private const val INVALID_INTEGER_VALUE_MESSAGE_TEMPLATE = "The {0} provided must be an integer"
private const val INVALID_NUMERIC_VALUE_MESSAGE_TEMPLATE = "The {0} provided must be numeric"
private const val INVALID_DECIMAL_VALUE_MESSAGE_TEMPLATE = "The {0} provided must be a valid decimal number"
private const val INVALID_MAJOR_OR_EQUAL_MESSAGE_TEMPLATE = "The {0} value must be major or equal to {1}"
private const val INVALID_LENGTH_MESSAGE_TEMPLATE = "The {0} length is not between {1} and {2}"
private const val INVALID_DATE_MESSAGE_TEMPLATE = "The {0} does not have an ISO date format (yyyy-MM-dd)"
private const val INVALID_TYPE_MESSAGE_TEMPLATE = "The {0} provided does not match with any existing type"
private const val FEATURE_DISABLED_MESSAGE_TEMPLATE = "The feature {0} is currently disabled"

fun generateRequiredFieldException(fieldNames: List<String>) =
    ExceptionCode(
        code = format(REQUIRED_FIELD_CODE_TEMPLATE, fieldNames.joinToString(".")),
        message = format(REQUIRED_FIELD_MESSAGE_TEMPLATE, fieldNames.last()),
    )

fun generateRequiredQueryParamException(fieldNames: List<String>) =
    ExceptionCode(
        code = format(REQUIRED_QUERY_PARAM_CODE_TEMPLATE, fieldNames.joinToString(".")),
        message = format(REQUIRED_QUERY_PARAM_MESSAGE_TEMPLATE, fieldNames.last()),
    )

fun generateIntegerException(fieldNames: List<String>) =
    ExceptionCode(
        code = format(INVALID_VALUE_CODE_TEMPLATE, fieldNames.joinToString(".")),
        message = format(INVALID_INTEGER_VALUE_MESSAGE_TEMPLATE, fieldNames.last()),
    )

fun generateNumericException(fieldNames: List<String>) =
    ExceptionCode(
        code = format(INVALID_VALUE_CODE_TEMPLATE, fieldNames.joinToString(".")),
        message = format(INVALID_NUMERIC_VALUE_MESSAGE_TEMPLATE, fieldNames.last()),
    )

fun generateDecimalException(fieldNames: List<String>) =
    ExceptionCode(
        code = format(INVALID_VALUE_CODE_TEMPLATE, fieldNames.joinToString(".")),
        message = format(INVALID_DECIMAL_VALUE_MESSAGE_TEMPLATE, fieldNames.last()),
    )

fun generateValueNotMajorOrEqualException(
    fieldNames: List<String>,
    bound: Number,
) = ExceptionCode(
    code = format(INVALID_VALUE_CODE_TEMPLATE, fieldNames.joinToString(".")),
    message = format(INVALID_MAJOR_OR_EQUAL_MESSAGE_TEMPLATE, fieldNames.last(), bound),
)

fun generateLengthException(
    fieldNames: List<String>,
    bounds: Pair<Int, Int>,
) = ExceptionCode(
    code = format(INVALID_LENGTH_CODE_TEMPLATE, fieldNames.joinToString(".")),
    message = format(INVALID_LENGTH_MESSAGE_TEMPLATE, fieldNames.last(), bounds.first, bounds.second),
)

fun generateDateException(fieldNames: List<String>) =
    ExceptionCode(
        code = format(INVALID_VALUE_CODE_TEMPLATE, fieldNames.joinToString(".")),
        message = format(INVALID_DATE_MESSAGE_TEMPLATE, fieldNames.last()),
    )

fun generateTypeException(fieldNames: List<String>) =
    ExceptionCode(
        code = format(INVALID_VALUE_CODE_TEMPLATE, fieldNames.joinToString(".")),
        message = format(INVALID_TYPE_MESSAGE_TEMPLATE, fieldNames.last()),
    )

fun generateFeatureDisabledException(feature: String) =
    ExceptionCode(
        code = format(FEATURE_DISABLED_CODE_TEMPLATE, feature.lowercase()),
        message = format(FEATURE_DISABLED_MESSAGE_TEMPLATE, feature),
    )
