package com.blendwerk.pet.domain.budgeting;

import com.blendwerk.pet.domain.core.Identifier;

public final class Income extends Transaction {
    public Income(Budget budget) {
        super(budget);
        ensure();
    }

    private Income() {
        super();
    }

    public int sign() {
        return 1;
    }

    public void categorize(IncomeCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Income category cannot be null.");
        }
        categorize(category.toString());
    }

    static Income of(Budget budget, Identifier id, Money amount, String category) {
        var income = new Income();
        Transaction.of(income, budget, id, category, amount);
        return income;
    }
}