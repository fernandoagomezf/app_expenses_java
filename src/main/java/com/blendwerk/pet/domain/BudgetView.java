package com.blendwerk.pet.domain;

public record BudgetView(
    Identifier id, 
    String name, 
    Currency currency, 
    int transactionCount, 
    double balance
) {
    public BudgetView {
        if (id == null) {
            throw new IllegalArgumentException("The id cannot be null.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The name cannot be null or blank.");
        }
        if (currency == null) {
            throw new IllegalArgumentException("The currency cannot be null.");
        }
        if (transactionCount < 0) {
            throw new IllegalArgumentException("The transaction count cannot be negative.");
        }
    }
}
