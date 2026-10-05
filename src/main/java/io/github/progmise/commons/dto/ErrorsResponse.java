package io.github.progmise.commons.dto;

import java.util.List;

public record ErrorsResponse(List<ApiError> errors) {
}
