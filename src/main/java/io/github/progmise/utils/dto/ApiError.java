package io.github.progmise.utils.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.progmise.utils.enums.ErrorLevel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(String code, String message, ErrorLevel level, String description) {

    public ApiError(String code, String message, ErrorLevel level) {
        this(code, message, level, null);
    }
}
