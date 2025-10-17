package com.blendwerk.pet.domain;

import java.lang.String;

public final class Expense extends Transaction {
    public Expense(Budget budget) {
        super(budget);
    }

    public Expense(Identifier id, String category, Money amount) {
        super(id, category, amount);
    }

    public int sign() {
        return -1;
    }
    
    public void categorize(ExpenseCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Expense category cannot be null.");
        }
        categorize(category.toString());
    }
}