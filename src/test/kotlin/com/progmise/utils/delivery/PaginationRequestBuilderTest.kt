package com.progmise.utils.delivery

import com.progmise.utils.delivery.dto.request.builder.PaginationRequestBuilder
import com.progmise.utils.exception.BadRequestException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PaginationRequestBuilderTest {
    @Test
    fun `defaults when params are missing`() {
        val (offset, limit) = PaginationRequestBuilder().build(emptyMap())
        assertEquals(0, offset)
        assertEquals(20, limit)
    }

    @Test
    fun `parses valid offset and limit`() {
        val (offset, limit) = PaginationRequestBuilder().build(mapOf("_offset" to "10", "_limit" to "50"))
        assertEquals(10, offset)
        assertEquals(50, limit)
    }

    @Test
    fun `rejects non numeric values`() {
        val ex =
            assertFailsWith<BadRequestException> {
                PaginationRequestBuilder().build(mapOf("_limit" to "abc"))
            }
        assertEquals("invalid.value._limit", ex.exceptions.single().code)
    }

    @Test
    fun `rejects limit below minimum`() {
        assertFailsWith<BadRequestException> {
            PaginationRequestBuilder().build(mapOf("_limit" to "0"))
        }
    }

    @Test
    fun `rejects limit above maximum`() {
        val ex =
            assertFailsWith<BadRequestException> {
                PaginationRequestBuilder().build(mapOf("_limit" to "500"))
            }
        assertEquals("invalid.value._limit", ex.exceptions.single().code)
    }

    @Test
    fun `rejects negative offset`() {
        assertFailsWith<BadRequestException> {
            PaginationRequestBuilder().build(mapOf("_offset" to "-1"))
        }
    }
}
