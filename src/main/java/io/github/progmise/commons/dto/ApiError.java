package io.github.progmise.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.progmise.commons.enums.ErrorLevel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(String code, String message, ErrorLevel level, String description) {

    public ApiError(String code, String message, ErrorLevel level) {
        this(code, message, level, null);
    }
}
