package io.github.progmise.commons.delivery;

import io.github.progmise.commons.enums.LinkRef;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListPaginationDTOTest {

    private static final String URL = "http://localhost/schedules";

    @Test
    void firstPageContainsFirstNextAndLastLinks() {
        ListPaginationDTO<List<String>> dto = ListPaginationDTO.of(List.of("a"), 50, 0, 10);
        List<String> rels = rels(dto.createPaginationLinks(Map.of(), 0, 10, URL));

        assertTrue(rels.contains(LinkRef.FIRST.getType()));
        assertTrue(rels.contains(LinkRef.NEXT.getType()));
        assertTrue(rels.contains(LinkRef.LAST.getType()));
        assertFalse(rels.contains(LinkRef.PREVIOUS.getType()));
    }

    @Test
    void middlePageContainsAllNavigationLinks() {
        ListPaginationDTO<List<String>> dto = ListPaginationDTO.of(List.of("a"), 50, 20, 10);
        List<Link> links = toList(dto.createPaginationLinks(Map.of(), 20, 10, URL));
        List<String> rels = links.stream().map(link -> link.getRel().value()).toList();

        assertEquals(4, links.size());
        assertTrue(rels.contains(LinkRef.PREVIOUS.getType()));
        assertTrue(rels.contains(LinkRef.NEXT.getType()));
    }

    @Test
    void lastPageHasNoNextLink() {
        ListPaginationDTO<List<String>> dto = ListPaginationDTO.of(List.of("a"), 50, 40, 10);
        List<String> rels = rels(dto.createPaginationLinks(Map.of(), 40, 10, URL));

        assertFalse(rels.contains(LinkRef.NEXT.getType()));
        assertTrue(rels.contains(LinkRef.PREVIOUS.getType()));
    }

    @Test
    void linksPreserveNonPaginationQueryParams() {
        Map<String, String> params = Map.of("_offset", "0", "_limit", "10", "system", "FRENCH");
        ListPaginationDTO<List<String>> dto = ListPaginationDTO.of(List.of("a"), 50, 0, 10);
        Link link = dto.createPaginationLinks(params, 0, 10, URL).iterator().next();

        assertTrue(link.getHref().contains("system=FRENCH"));
        assertTrue(link.getHref().contains("_offset="));
        assertTrue(link.getHref().contains("_limit="));
    }

    private List<String> rels(Iterable<Link> links) {
        return StreamSupport.stream(links.spliterator(), false)
            .map(link -> link.getRel().value())
            .toList();
    }

    private List<Link> toList(Iterable<Link> links) {
        return StreamSupport.stream(links.spliterator(), false).toList();
    }
}
