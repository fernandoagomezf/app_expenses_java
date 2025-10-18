package com.blendwerk.pet.domain;

public final class Income extends Transaction {
    public Income(Budget budget) {
        super(budget);
    }

    public Income(Identifier id, String category, Money amount) {
        super(id, category, amount);
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
}