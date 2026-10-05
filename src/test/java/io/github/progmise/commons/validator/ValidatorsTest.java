package io.github.progmise.commons.validator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorsTest {

    private final List<String> field = List.of("field");

    @Test
    void integerValidatorAcceptsIntegersAndRejectsDecimals() {
        IntegerValidator validator = new IntegerValidator();
        assertTrue(validator.validate("42", field).isEmpty());
        assertTrue(validator.validate("-3", field).isEmpty());
        assertEquals("invalid.value.field", validator.validate("1.5", field).get(0).code());
        assertEquals("invalid.value.field", validator.validate("abc", field).get(0).code());
    }

    @Test
    void numericValidatorAcceptsDigitsOnly() {
        NumericValidator validator = new NumericValidator();
        assertTrue(validator.validate("123", field).isEmpty());
        assertEquals("invalid.value.field", validator.validate("-1", field).get(0).code());
        assertEquals("invalid.value.field", validator.validate("1.5", field).get(0).code());
    }

    @Test
    void decimalValidatorAcceptsUpToTwoDecimals() {
        DecimalValidator validator = new DecimalValidator();
        assertTrue(validator.validate("100", field).isEmpty());
        assertTrue(validator.validate("100.50", field).isEmpty());
        assertEquals("invalid.value.field", validator.validate("100.555", field).get(0).code());
        assertEquals("invalid.value.field", validator.validate("x", field).get(0).code());
    }

    @Test
    void dateValidatorAcceptsIsoDates() {
        DateValidator validator = new DateValidator();
        assertTrue(validator.validate("2026-01-31", field).isEmpty());
        assertEquals("invalid.value.field", validator.validate("31/01/2026", field).get(0).code());
        assertEquals("invalid.value.field", validator.validate("not-a-date", field).get(0).code());
    }

    @Test
    void majorOrEqualValidatorEnforcesBound() {
        MajorOrEqualValidator validator = new MajorOrEqualValidator(1);
        assertTrue(validator.validate("1", field).isEmpty());
        assertTrue(validator.validate("50", field).isEmpty());
        assertEquals("invalid.value.field", validator.validate("0", field).get(0).code());
        assertEquals("invalid.value.field", validator.validate("abc", field).get(0).code());
    }

    @Test
    void lengthValidatorEnforcesBounds() {
        LengthValidator validator = new LengthValidator(2, 4);
        assertTrue(validator.validate("abc", field).isEmpty());
        assertEquals("invalid.length.field", validator.validate("a", field).get(0).code());
        assertEquals("invalid.length.field", validator.validate("abcde", field).get(0).code());
    }

    @Test
    void compositeValidatorAggregatesAllErrors() {
        CompositeValidator<String> composite = new CompositeValidator<>(
            List.of(new IntegerValidator(), new DateValidator())
        );
        assertEquals(2, composite.validate("nope", field).size());
        assertTrue(!composite.validate("2026-01-31", field).isEmpty());
    }
}
