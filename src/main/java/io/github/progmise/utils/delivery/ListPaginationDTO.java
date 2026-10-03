package io.github.progmise.utils.delivery;

import io.github.progmise.utils.enums.LinkRef;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListPaginationDTO<T> {

    private static final List<String> PAGINATION_PARAM_KEYS = List.of("_offset", "_limit");

    private final T list;
    private final long totalSize;
    private final int offset;
    private final int limit;

    public ListPaginationDTO(T list, long totalSize, int offset, int limit) {
        this.list = list;
        this.totalSize = totalSize;
        this.offset = offset;
        this.limit = limit;
    }

    public static <T> ListPaginationDTO<T> of(T list, long totalSize, int offset, int limit) {
        return new ListPaginationDTO<>(list, totalSize, offset, limit);
    }

    public T getList() {
        return list;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public EntityModel<T> toEntityModel(Map<String, String> allParams, String url) {
        return EntityModel.of(list, createPaginationLinks(allParams, offset, limit, url));
    }

    public Iterable<Link> createPaginationLinks(Map<String, String> allParams, int offset, int limit, String url) {
        List<Link> links = new ArrayList<>();

        links.add(createLink(LinkRef.FIRST, allParams, 0, limit, url));

        if (offset > 0) {
            links.add(createLink(LinkRef.PREVIOUS, allParams, Math.max(offset - limit, 0), limit, url));
        }

        int nextOffset = offset + limit;

        if (nextOffset < totalSize) {
            links.add(createLink(LinkRef.NEXT, allParams, nextOffset, limit, url));
        }

        long lastOffset = totalSize == 0L ? 0L : ((totalSize - 1) / limit) * limit;

        if (offset < lastOffset) {
            links.add(createLink(LinkRef.LAST, allParams, (int) lastOffset, limit, url));
        }

        return links;
    }

    private Link createLink(
        LinkRef linkRef,
        Map<String, String> allParams,
        int offset,
        int limit,
        String url
    ) {
        String searchParams = allParams.entrySet().stream()
            .filter(entry -> !PAGINATION_PARAM_KEYS.contains(entry.getKey()))
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&", "?", ""));

        String paginationParams = PAGINATION_PARAM_KEYS.get(0) + "=" + offset + "&" + PAGINATION_PARAM_KEYS.get(1) + "=" + limit;

        String separator = searchParams.length() > 1 ? "&" : "";

        return Link.of(url + searchParams + separator + paginationParams, linkRef.getType());
    }
}
