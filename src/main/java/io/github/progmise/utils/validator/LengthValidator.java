package io.github.progmise.utils.validator;

import io.github.progmise.utils.exception.ExceptionCode;
import io.github.progmise.utils.util.ExceptionCodeGenerators;

import java.util.ArrayList;
import java.util.List;

public class LengthValidator implements Validator<String> {

    private static final int DEFAULT_MIN = 1;
    private static final int DEFAULT_MAX = 255;

    private final int min;
    private final int max;

    public LengthValidator() {
        this(DEFAULT_MIN, DEFAULT_MAX);
    }

    public LengthValidator(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public List<ExceptionCode> validate(String data, List<String> fieldNames) {
        List<ExceptionCode> exceptions = new ArrayList<>();

        if (data.length() < min || data.length() > max) {
            exceptions.add(ExceptionCodeGenerators.generateLengthException(fieldNames, min, max));
        }

        return exceptions;
    }
}
