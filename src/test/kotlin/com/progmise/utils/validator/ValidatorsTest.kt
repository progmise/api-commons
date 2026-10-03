package com.progmise.utils.validator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidatorsTest {
    private val field = listOf("field")

    @Test
    fun `integer validator accepts integers and rejects decimals`() {
        val validator = IntegerValidator()
        assertTrue(validator.validate("42", field).isEmpty())
        assertTrue(validator.validate("-3", field).isEmpty())
        assertEquals("invalid.value.field", validator.validate("1.5", field).single().code)
        assertEquals("invalid.value.field", validator.validate("abc", field).single().code)
    }

    @Test
    fun `numeric validator accepts digits only`() {
        val validator = NumericValidator()
        assertTrue(validator.validate("123", field).isEmpty())
        assertEquals("invalid.value.field", validator.validate("-1", field).single().code)
        assertEquals("invalid.value.field", validator.validate("1.5", field).single().code)
    }

    @Test
    fun `decimal validator accepts up to two decimals`() {
        val validator = DecimalValidator()
        assertTrue(validator.validate("100", field).isEmpty())
        assertTrue(validator.validate("100.50", field).isEmpty())
        assertEquals("invalid.value.field", validator.validate("100.555", field).single().code)
        assertEquals("invalid.value.field", validator.validate("x", field).single().code)
    }

    @Test
    fun `date validator accepts ISO dates`() {
        val validator = DateValidator()
        assertTrue(validator.validate("2026-01-31", field).isEmpty())
        assertEquals("invalid.value.field", validator.validate("31/01/2026", field).single().code)
        assertEquals("invalid.value.field", validator.validate("not-a-date", field).single().code)
    }

    @Test
    fun `major or equal validator enforces bound`() {
        val validator = MajorOrEqualValidator(1)
        assertTrue(validator.validate("1", field).isEmpty())
        assertTrue(validator.validate("50", field).isEmpty())
        assertEquals("invalid.value.field", validator.validate("0", field).single().code)
        assertEquals("invalid.value.field", validator.validate("abc", field).single().code)
    }

    @Test
    fun `length validator enforces bounds`() {
        val validator = LengthValidator(2 to 4)
        assertTrue(validator.validate("abc", field).isEmpty())
        assertEquals("invalid.length.field", validator.validate("a", field).single().code)
        assertEquals("invalid.length.field", validator.validate("abcde", field).single().code)
    }

    @Test
    fun `composite validator aggregates all errors`() {
        val composite = CompositeValidator<String>(listOf(IntegerValidator(), DateValidator()))
        assertEquals(2, composite.validate("nope", field).size)
        assertTrue(composite.validate("2026-01-31", field).isNotEmpty())
    }
}
