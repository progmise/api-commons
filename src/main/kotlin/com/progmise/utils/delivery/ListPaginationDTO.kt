package com.progmise.utils.delivery

import com.progmise.utils.enums.LinkRef
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import kotlin.math.max

data class ListPaginationDTO<T : Any>(
    val list: T,
    val totalSize: Long,
    val offset: Int,
    val limit: Int,
) {
    fun toEntityModel(
        allParams: Map<String, String>,
        url: String,
    ): EntityModel<T> =
        EntityModel.of(
            list,
            createPaginationLinks(allParams, offset, limit, url),
        )

    fun createPaginationLinks(
        allParams: Map<String, String>,
        offset: Int,
        limit: Int,
        url: String,
    ): Iterable<Link> {
        val links = mutableListOf<Link>()

        links.add(createLink(LinkRef.FIRST, allParams, 0, limit, url))

        if (offset > 0) {
            links.add(createLink(LinkRef.PREVIOUS, allParams, max(offset - limit, 0), limit, url))
        }

        val nextOffset = offset + limit

        if (nextOffset < totalSize) {
            links.add(createLink(LinkRef.NEXT, allParams, nextOffset, limit, url))
        }

        val lastOffset = if (totalSize == 0L) 0L else ((totalSize - 1) / limit) * limit

        if (offset < lastOffset) {
            links.add(createLink(LinkRef.LAST, allParams, lastOffset.toInt(), limit, url))
        }

        return links
    }

    private fun createLink(
        linkRef: LinkRef,
        allParams: Map<String, String>,
        offset: Int,
        limit: Int,
        url: String,
    ): Link {
        val uriSearchParams =
            allParams
                .filterKeys { it !in PAGINATION_PARAM_KEYS }
                .entries
                .joinToString(separator = "&", prefix = "?") { (key, value) -> "$key=$value" }

        val uriPaginationParams =
            listOf(
                "${PAGINATION_PARAM_KEYS.first()}=$offset",
                "${PAGINATION_PARAM_KEYS.last()}=$limit",
            ).joinToString("&", prefix = "&".takeIf { uriSearchParams.length > 1 } ?: String())

        return Link.of("$url$uriSearchParams$uriPaginationParams", linkRef.type)
    }

    companion object {
        private val PAGINATION_PARAM_KEYS = listOf("_offset", "_limit")

        fun <T : Any> of(
            list: T,
            totalSize: Long,
            offset: Int,
            limit: Int,
        ) = ListPaginationDTO(
            list = list,
            totalSize = totalSize,
            offset = offset,
            limit = limit,
        )
    }
}
