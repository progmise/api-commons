package io.github.progmise.commons.validator;

import io.github.progmise.commons.exception.ExceptionCode;
import io.github.progmise.commons.util.ExceptionCodeGenerators;

import java.util.ArrayList;
import java.util.List;

public class MajorOrEqualValidator implements Validator<String> {

    private final int bound;

    public MajorOrEqualValidator(int bound) {
        this.bound = bound;
    }

    @Override
    public List<ExceptionCode> validate(String data, List<String> fieldNames) {
        List<ExceptionCode> exceptions = new ArrayList<>();
        Integer value = parse(data);

        if (value == null || value < bound) {
            exceptions.add(ExceptionCodeGenerators.generateValueNotMajorOrEqualException(fieldNames, bound));
        }

        return exceptions;
    }

    private Integer parse(String data) {
        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
