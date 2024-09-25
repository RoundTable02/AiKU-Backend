package aiku_main.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;

public class ValidPointAmountValidator implements ConstraintValidator<ValidPointAmount, Integer> {

    @Override
    public void initialize(ValidPointAmount constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if(integer % 10 != 0 || integer < 0){
            return false;
        }
        return true;
    }
}
