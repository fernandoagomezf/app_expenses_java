package com.blendwerk.pet.domain;

public record BudgetView(
    Identifier id, 
    String name, 
    Money balance
) {
    public BudgetView {
        if (id == null) {
            throw new IllegalArgumentException("The id cannot be null.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The name cannot be null or blank.");
        }
        if (balance == null) {
            throw new IllegalArgumentException("The balance cannot be null.");
        }
    }
}
