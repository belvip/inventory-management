package com.belvinard.inventory_management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FutureOrPresentValidator implements ConstraintValidator<FutureOrPresent, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are handled by @NotNull if needed
        }
        return !value.isBefore(LocalDate.now());
    }
}