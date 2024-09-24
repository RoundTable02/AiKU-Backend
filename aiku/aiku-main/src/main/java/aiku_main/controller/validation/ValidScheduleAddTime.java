package aiku_main.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidScheduleAddTimeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScheduleAddTime {
    String message() default "Invalid schedule time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
