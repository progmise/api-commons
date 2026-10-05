package io.github.progmise.commons.enums;

public enum LinkRef {
    FIRST("first"),
    PREVIOUS("previous"),
    NEXT("next"),
    LAST("last"),
    SELF("self");

    private final String type;

    LinkRef(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
