package com.blendwerk.pet.application.models;

import com.blendwerk.pet.domain.budgeting.Currency;

public record CreateBudgetInput(String name, String currency) implements Validatable {
    public ValidationResult validate() {
        if (name() == null || name().isBlank()) {
            return ValidationResult.invalid( "Budget name cannot be empty.");
        }
        if (currency == null || currency.isBlank()) {
            return ValidationResult.invalid("Budget currency cannot be empty.");
        }
        try {
            Currency.valueOf(currency);
        } catch (IllegalArgumentException ex) {
            return ValidationResult.invalid("Budget currency is not valid.");
        }

        return ValidationResult.valid();
    }

    public Currency asCurrency() {
        return Currency.valueOf(currency());
    }
}
