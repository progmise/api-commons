package io.github.progmise.utils.delivery;

import io.github.progmise.utils.delivery.dto.request.builder.PaginationRequestBuilder;
import io.github.progmise.utils.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaginationRequestBuilderTest {

    @Test
    void defaultsWhenParamsAreMissing() {
        int[] page = new PaginationRequestBuilder().build(Map.of());
        assertEquals(0, page[0]);
        assertEquals(20, page[1]);
    }

    @Test
    void parsesValidOffsetAndLimit() {
        int[] page = new PaginationRequestBuilder().build(Map.of("_offset", "10", "_limit", "50"));
        assertEquals(10, page[0]);
        assertEquals(50, page[1]);
    }

    @Test
    void rejectsNonNumericValues() {
        BadRequestException ex = assertThrows(
            BadRequestException.class,
            () -> new PaginationRequestBuilder().build(Map.of("_limit", "abc"))
        );
        assertEquals("invalid.value._limit", ex.getExceptions().get(0).code());
    }

    @Test
    void rejectsLimitBelowMinimum() {
        assertThrows(
            BadRequestException.class,
            () -> new PaginationRequestBuilder().build(Map.of("_limit", "0"))
        );
    }

    @Test
    void rejectsLimitAboveMaximum() {
        BadRequestException ex = assertThrows(
            BadRequestException.class,
            () -> new PaginationRequestBuilder().build(Map.of("_limit", "500"))
        );
        assertEquals("invalid.value._limit", ex.getExceptions().get(0).code());
    }

    @Test
    void rejectsNegativeOffset() {
        assertThrows(
            BadRequestException.class,
            () -> new PaginationRequestBuilder().build(Map.of("_offset", "-1"))
        );
    }
}
