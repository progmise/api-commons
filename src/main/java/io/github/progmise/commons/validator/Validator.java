package io.github.progmise.commons.validator;

import io.github.progmise.commons.exception.ExceptionCode;

import java.util.List;

public interface Validator<T> {

    List<ExceptionCode> validate(T data, List<String> fieldNames);
}
