package com.progmise.utils.delivery

import com.progmise.utils.enums.LinkRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListPaginationDTOTest {
    private val url = "http://localhost/schedules"

    @Test
    fun `first page contains first next and last links`() {
        val dto = ListPaginationDTO.of(listOf("a"), totalSize = 50, offset = 0, limit = 10)
        val links = dto.createPaginationLinks(emptyMap(), 0, 10, url).toList()
        val rels = links.map { it.rel.value() }

        assertTrue(LinkRef.FIRST.type in rels)
        assertTrue(LinkRef.NEXT.type in rels)
        assertTrue(LinkRef.LAST.type in rels)
        assertTrue(LinkRef.PREVIOUS.type !in rels)
    }

    @Test
    fun `middle page contains all navigation links`() {
        val dto = ListPaginationDTO.of(listOf("a"), totalSize = 50, offset = 20, limit = 10)
        val links = dto.createPaginationLinks(emptyMap(), 20, 10, url).toList()
        val rels = links.map { it.rel.value() }

        assertEquals(4, links.size)
        assertTrue(LinkRef.PREVIOUS.type in rels)
        assertTrue(LinkRef.NEXT.type in rels)
    }

    @Test
    fun `last page has no next link`() {
        val dto = ListPaginationDTO.of(listOf("a"), totalSize = 50, offset = 40, limit = 10)
        val links = dto.createPaginationLinks(emptyMap(), 40, 10, url).toList()
        val rels = links.map { it.rel.value() }

        assertTrue(LinkRef.NEXT.type !in rels)
        assertTrue(LinkRef.PREVIOUS.type in rels)
    }

    @Test
    fun `links preserve non pagination query params`() {
        val params = mapOf("_offset" to "0", "_limit" to "10", "system" to "FRENCH")
        val dto = ListPaginationDTO.of(listOf("a"), totalSize = 50, offset = 0, limit = 10)
        val link = dto.createPaginationLinks(params, 0, 10, url).first()

        assertTrue(link.href.contains("system=FRENCH"))
        assertTrue(link.href.contains("_offset="))
        assertTrue(link.href.contains("_limit="))
    }
}
