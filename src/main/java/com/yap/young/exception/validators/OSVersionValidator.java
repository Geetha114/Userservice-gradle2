package com.yap.young.exception.validators;

import com.yap.young.exception.annotation.ValidOSVersion;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class OSVersionValidator implements ConstraintValidator<ValidOSVersion, String> {

    private final List<String> acceptedValues = Arrays.asList("ANDROID", "IOS");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        String upperValue = value.toUpperCase();

        for (String acceptedValue : acceptedValues) {
            if (upperValue.contains(acceptedValue)) {
                return true;
            }
        }
        return false;
    }
}
