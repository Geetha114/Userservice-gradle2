package com.yap.young.exception.validators;

import com.yap.young.exception.annotation.ValidAge;
import com.yap.young.util.CommonUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    private String tooYoungMessage;

    private String tooOldMessage;

    @Override
    public void initialize(ValidAge constraintAnnotation) {
        this.tooYoungMessage = constraintAnnotation.tooYoungMessage();
        this.tooOldMessage = constraintAnnotation.tooOldMessage();
    }

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
        if (dob == null) {
            return true;
        }

        int age = CommonUtils.getAgeFromDob(dob);

        if (age < 8) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(tooYoungMessage).addConstraintViolation();
            return false;
        }

        if (age > 17) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(tooOldMessage).addConstraintViolation();
            return false;
        }

        return true;
    }
}


