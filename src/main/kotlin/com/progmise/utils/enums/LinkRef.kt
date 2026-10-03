package com.progmise.utils.enums

enum class LinkRef(
    val type: String,
) {
    FIRST("first"),
    PREVIOUS("previous"),
    NEXT("next"),
    LAST("last"),
    SELF("self"),
}
