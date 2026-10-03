package io.github.progmise.utils.dto;

import java.util.List;

public record ErrorsResponse(List<ApiError> errors) {
}
