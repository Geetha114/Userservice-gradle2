package com.yap.young.exception.annotation;

import com.yap.young.exception.validators.AgeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {

    String message() default "Invalid age";

    String tooYoungMessage() default "Age must be at least 8 years old.";

    String tooOldMessage() default "Age must be 17 years old or younger.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}