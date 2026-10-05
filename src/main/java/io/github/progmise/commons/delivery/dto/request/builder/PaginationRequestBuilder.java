package io.github.progmise.commons.delivery.dto.request.builder;

import io.github.progmise.commons.exception.BadRequestException;
import io.github.progmise.commons.exception.ExceptionCode;
import io.github.progmise.commons.util.Constants;
import io.github.progmise.commons.util.ExceptionCodeGenerators;
import io.github.progmise.commons.validator.IntegerValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaginationRequestBuilder {

    public static final String OFFSET_PARAM = "_offset";
    public static final String LIMIT_PARAM = "_limit";

    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 20;
    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 100;

    private final IntegerValidator integerValidator = new IntegerValidator();

    public int[] build(Map<String, String> allParams) {
        List<ExceptionCode> exceptions = new ArrayList<>();
        String offset = allParams.getOrDefault(OFFSET_PARAM, String.valueOf(DEFAULT_OFFSET));
        String limit = allParams.getOrDefault(LIMIT_PARAM, String.valueOf(DEFAULT_LIMIT));

        exceptions.addAll(integerValidator.validate(offset, List.of(OFFSET_PARAM)));
        exceptions.addAll(integerValidator.validate(limit, List.of(LIMIT_PARAM)));

        if (exceptions.isEmpty() && Integer.parseInt(offset) < 0) {
            exceptions.add(ExceptionCodeGenerators.generateValueNotMajorOrEqualException(List.of(OFFSET_PARAM), 0));
        }

        if (exceptions.isEmpty() && Integer.parseInt(limit) < MIN_LIMIT) {
            exceptions.add(ExceptionCodeGenerators.generateValueNotMajorOrEqualException(List.of(LIMIT_PARAM), MIN_LIMIT));
        }

        if (exceptions.isEmpty() && Integer.parseInt(limit) > MAX_LIMIT) {
            exceptions.add(new ExceptionCode(
                "invalid.value." + LIMIT_PARAM,
                "The " + LIMIT_PARAM + " value must be minor or equal to " + MAX_LIMIT
            ));
        }

        if (!exceptions.isEmpty()) {
            throw new BadRequestException(exceptions, Constants.ERROR_PATH);
        }

        return new int[]{Integer.parseInt(offset), Integer.parseInt(limit)};
    }
}
