package io.github.progmise.utils.util;

import io.github.progmise.utils.exception.ExceptionCode;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

public final class ExceptionCodeGenerators {

    private static final String REQUIRED_FIELD_CODE_TEMPLATE = "required.field.{0}";
    private static final String REQUIRED_QUERY_PARAM_CODE_TEMPLATE = "required.query.param.{0}";
    private static final String INVALID_VALUE_CODE_TEMPLATE = "invalid.value.{0}";
    private static final String INVALID_LENGTH_CODE_TEMPLATE = "invalid.length.{0}";
    private static final String FEATURE_DISABLED_CODE_TEMPLATE = "feature.disabled.{0}";
    private static final String REQUIRED_FIELD_MESSAGE_TEMPLATE = "The {0} is required";
    private static final String REQUIRED_QUERY_PARAM_MESSAGE_TEMPLATE = "The query param {0} is required";
    private static final String INVALID_INTEGER_VALUE_MESSAGE_TEMPLATE = "The {0} provided must be an integer";
    private static final String INVALID_NUMERIC_VALUE_MESSAGE_TEMPLATE = "The {0} provided must be numeric";
    private static final String INVALID_DECIMAL_VALUE_MESSAGE_TEMPLATE = "The {0} provided must be a valid decimal number";
    private static final String INVALID_MAJOR_OR_EQUAL_MESSAGE_TEMPLATE = "The {0} value must be major or equal to {1}";
    private static final String INVALID_LENGTH_MESSAGE_TEMPLATE = "The {0} length is not between {1} and {2}";
    private static final String INVALID_DATE_MESSAGE_TEMPLATE = "The {0} does not have an ISO date format (yyyy-MM-dd)";
    private static final String INVALID_TYPE_MESSAGE_TEMPLATE = "The {0} provided does not match with any existing type";
    private static final String FEATURE_DISABLED_MESSAGE_TEMPLATE = "The feature {0} is currently disabled";

    private ExceptionCodeGenerators() {
    }

    public static ExceptionCode generateRequiredFieldException(List<String> fieldNames) {
        return new ExceptionCode(
            MessageFormat.format(REQUIRED_FIELD_CODE_TEMPLATE, String.join(".", fieldNames)),
            MessageFormat.format(REQUIRED_FIELD_MESSAGE_TEMPLATE, last(fieldNames))
        );
    }

    public static ExceptionCode generateRequiredQueryParamException(List<String> fieldNames) {
        return new ExceptionCode(
            MessageFormat.format(REQUIRED_QUERY_PARAM_CODE_TEMPLATE, String.join(".", fieldNames)),
            MessageFormat.format(REQUIRED_QUERY_PARAM_MESSAGE_TEMPLATE, last(fieldNames))
        );
    }

    public static ExceptionCode generateIntegerException(List<String> fieldNames) {
        return new ExceptionCode(
            MessageFormat.format(INVALID_VALUE_CODE_TEMPLATE, String.join(".", fieldNames)),
            MessageFormat.format(INVALID_INTEGER_VALUE_MESSAGE_TEMPLATE, last(fieldNames))
        );
    }

    public static ExceptionCode generateNumericException(List<String> fieldNames) {
        return new ExceptionCode(
            MessageFormat.format(INVALID_VALUE_CODE_TEMPLATE, String.join(".", fieldNames)),
            MessageFormat.format(INVALID_NUMERIC_VALUE_MESSAGE_TEMPLATE, last(fieldNames))
        );
    }

    public static ExceptionCode generateDecimalException(List<String> fieldNames) {
        return new ExceptionCode(
            MessageFormat.format(INVALID_VALUE_CODE_TEMPLATE, String.join(".", fieldNames)),
            MessageFormat.format(INVALID_DECIMAL_VALUE_MESSAGE_TEMPLATE, last(fieldNames))
        );
    }

    public static ExceptionCode generateValueNotMajorOrEqualException(List<String> fieldNames, Number bound) {
        return new ExceptionCode(
            MessageFormat.format(INVALID_VALUE_CODE_TEMPLATE, String.join(".", fieldNames)),
            MessageFormat.format(INVALID_MAJOR_OR_EQUAL_MESSAGE_TEMPLATE, last(fieldNames), bound)
        );
    }

    public static ExceptionCode generateLengthException(List<String> fieldNames, int min, int max) {
        return new ExceptionCode(
            MessageFormat.format(INVALID_LENGTH_CODE_TEMPLATE, String.join(".", fieldNames)),
            MessageFormat.format(INVALID_LENGTH_MESSAGE_TEMPLATE, last(fieldNames), min, max)
        );
    }

    public static ExceptionCode generateDateException(List<String> fieldNames) {
        return new ExceptionCode(
            MessageFormat.format(INVALID_VALUE_CODE_TEMPLATE, String.join(".", fieldNames)),
            MessageFormat.format(INVALID_DATE_MESSAGE_TEMPLATE, last(fieldNames))
        );
    }

    public static ExceptionCode generateTypeException(List<String> fieldNames) {
        return new ExceptionCode(
            MessageFormat.format(INVALID_VALUE_CODE_TEMPLATE, String.join(".", fieldNames)),
            MessageFormat.format(INVALID_TYPE_MESSAGE_TEMPLATE, last(fieldNames))
        );
    }

    public static ExceptionCode generateFeatureDisabledException(String feature) {
        return new ExceptionCode(
            MessageFormat.format(FEATURE_DISABLED_CODE_TEMPLATE, feature.toLowerCase(Locale.ROOT)),
            MessageFormat.format(FEATURE_DISABLED_MESSAGE_TEMPLATE, feature)
        );
    }

    private static String last(List<String> fieldNames) {
        return fieldNames.get(fieldNames.size() - 1);
    }
}
