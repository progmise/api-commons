package io.github.progmise.commons.validator;

public abstract class BaseValidator {

    protected final IntegerValidator integerValidator = new IntegerValidator();
    protected final NumericValidator numericValidator = new NumericValidator();
    protected final DecimalValidator decimalValidator = new DecimalValidator();
    protected final DateValidator dateValidator = new DateValidator();
    protected final LengthValidator lengthValidator = new LengthValidator();
}
