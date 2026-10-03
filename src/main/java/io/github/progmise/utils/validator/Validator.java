package io.github.progmise.utils.validator;

import io.github.progmise.utils.exception.ExceptionCode;

import java.util.List;

public interface Validator<T> {

    List<ExceptionCode> validate(T data, List<String> fieldNames);
}
