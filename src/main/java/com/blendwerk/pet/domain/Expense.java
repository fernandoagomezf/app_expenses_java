package com.blendwerk.pet.domain;

public final class Expense extends Transaction {
    public Expense(Budget budget) {
        super(budget);
    }

    private Expense() {
        super();
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

    static Expense of(Budget budget, Identifier id, Money amount, String category) {
        var expense = new Expense();
        Transaction.of(expense, budget, id, category, amount);
        return expense;
    }
}