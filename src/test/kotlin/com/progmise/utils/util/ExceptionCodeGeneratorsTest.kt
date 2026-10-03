package com.progmise.utils.util

import kotlin.test.Test
import kotlin.test.assertEquals

class ExceptionCodeGeneratorsTest {
    @Test
    fun `required field exception uses dotted code and last field in message`() {
        val code = generateRequiredFieldException(listOf("body", "principal"))
        assertEquals("required.field.body.principal", code.code)
        assertEquals("The principal is required", code.message)
    }

    @Test
    fun `required query param exception`() {
        val code = generateRequiredQueryParamException(listOf("offset"))
        assertEquals("required.query.param.offset", code.code)
        assertEquals("The query param offset is required", code.message)
    }

    @Test
    fun `major or equal exception includes the bound`() {
        val code = generateValueNotMajorOrEqualException(listOf("limit"), 1)
        assertEquals("invalid.value.limit", code.code)
        assertEquals("The limit value must be major or equal to 1", code.message)
    }

    @Test
    fun `length exception includes bounds`() {
        val code = generateLengthException(listOf("name"), 2 to 10)
        assertEquals("invalid.length.name", code.code)
        assertEquals("The name length is not between 2 and 10", code.message)
    }

    @Test
    fun `feature disabled exception lowercases the code`() {
        val code = generateFeatureDisabledException("GERMAN_AMORTIZATION_ON")
        assertEquals("feature.disabled.german_amortization_on", code.code)
    }
}
