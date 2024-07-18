package com.yap.young.exception.annotation;

import com.yap.young.exception.validators.GenderValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = GenderValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGender {

    String message() default "Invalid gender value. Must be in [\"MALE\", \"FEMALE\", \"OTHER\"].";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

