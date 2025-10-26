package com.blendwerk.pet.application.models;

import java.math.BigDecimal;
import com.blendwerk.pet.domain.budgeting.Currency;

public record TransactionInput(String amount, String currency, String category, String type) implements Input {
    public ValidationResult validate() {
        if (type() == null || type().isBlank()) {
            return ValidationResult.invalid("Transaction type cannot be empty.");
        }
        if (amount() == null || amount().isBlank()) {
            return ValidationResult.invalid("Transaction amount cannot be empty.");
        }
        if (currency == null || currency.isBlank()) {
            return ValidationResult.invalid("Budget currency cannot be empty.");
        }
        if (category() == null || category().isBlank()) {
            return ValidationResult.invalid("Transaction category cannot be empty.");
        }
        try {
            new BigDecimal(amount());
            Currency.valueOf(currency);
        } catch (NumberFormatException ex) {
            return ValidationResult.invalid("Transaction amount is not valid.");
        } catch (IllegalArgumentException ex) {
            return ValidationResult.invalid("Transaction currency is not valid.");
        }
        return ValidationResult.valid();
    }
}
