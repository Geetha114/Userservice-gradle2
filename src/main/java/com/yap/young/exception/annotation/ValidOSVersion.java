package com.yap.young.exception.annotation;

import com.yap.young.exception.validators.OSVersionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OSVersionValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOSVersion {

    String message() default "Invalid OS version value. Must be contains ANDROID or IOS.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
