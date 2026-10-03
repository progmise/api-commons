package io.github.progmise.utils.validator;

import io.github.progmise.utils.exception.ExceptionCode;
import io.github.progmise.utils.util.ExceptionCodeGenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DecimalValidator implements Validator<String> {

    private static final Pattern REGEX = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    public static boolean isValid(String data) {
        return REGEX.matcher(data).matches();
    }

    @Override
    public List<ExceptionCode> validate(String data, List<String> fieldNames) {
        List<ExceptionCode> exceptions = new ArrayList<>();

        if (!isValid(data)) {
            exceptions.add(ExceptionCodeGenerators.generateDecimalException(fieldNames));
        }

        return exceptions;
    }
}
