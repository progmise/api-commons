package io.github.progmise.commons.validator;

import io.github.progmise.commons.exception.ExceptionCode;

import java.util.List;

public class CompositeValidator<T> implements Validator<T> {

    private final List<Validator<T>> validators;

    public CompositeValidator(List<Validator<T>> validators) {
        this.validators = validators;
    }

    @Override
    public List<ExceptionCode> validate(T data, List<String> fieldNames) {
        return validators.stream()
            .flatMap(validator -> validator.validate(data, fieldNames).stream())
            .toList();
    }
}
