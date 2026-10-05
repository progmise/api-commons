package io.github.progmise.commons.util;

import io.github.progmise.commons.exception.ExceptionCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionCodeGeneratorsTest {

    @Test
    void requiredFieldExceptionUsesDottedCodeAndLastFieldInMessage() {
        ExceptionCode code = ExceptionCodeGenerators.generateRequiredFieldException(List.of("body", "principal"));
        assertEquals("required.field.body.principal", code.code());
        assertEquals("The principal is required", code.message());
    }

    @Test
    void requiredQueryParamException() {
        ExceptionCode code = ExceptionCodeGenerators.generateRequiredQueryParamException(List.of("offset"));
        assertEquals("required.query.param.offset", code.code());
        assertEquals("The query param offset is required", code.message());
    }

    @Test
    void majorOrEqualExceptionIncludesTheBound() {
        ExceptionCode code = ExceptionCodeGenerators.generateValueNotMajorOrEqualException(List.of("limit"), 1);
        assertEquals("invalid.value.limit", code.code());
        assertEquals("The limit value must be major or equal to 1", code.message());
    }

    @Test
    void lengthExceptionIncludesBounds() {
        ExceptionCode code = ExceptionCodeGenerators.generateLengthException(List.of("name"), 2, 10);
        assertEquals("invalid.length.name", code.code());
        assertEquals("The name length is not between 2 and 10", code.message());
    }

    @Test
    void featureDisabledExceptionLowercasesTheCode() {
        ExceptionCode code = ExceptionCodeGenerators.generateFeatureDisabledException("GERMAN_AMORTIZATION_ON");
        assertEquals("feature.disabled.german_amortization_on", code.code());
    }
}
