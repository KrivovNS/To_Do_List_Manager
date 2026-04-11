package com.mipt.To_Do_List_Manager.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DueDateNotBeforeCreationValidator.class)
@Documented
public @interface DueDateNotBeforeCreation {
    String message() default "dueDate must not be earlier than task creation date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
