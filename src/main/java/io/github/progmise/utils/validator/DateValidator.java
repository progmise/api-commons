package io.github.progmise.utils.validator;

import io.github.progmise.utils.exception.ExceptionCode;
import io.github.progmise.utils.util.Constants;
import io.github.progmise.utils.util.ExceptionCodeGenerators;
import io.github.progmise.utils.util.Extensions;

import java.util.ArrayList;
import java.util.List;

public class DateValidator implements Validator<String> {

    @Override
    public List<ExceptionCode> validate(String data, List<String> fieldNames) {
        List<ExceptionCode> exceptions = new ArrayList<>();

        if (Extensions.toLocalDate(data, Constants.ISO_DATE_PATTERN) == null) {
            exceptions.add(ExceptionCodeGenerators.generateDateException(fieldNames));
        }

        return exceptions;
    }
}
