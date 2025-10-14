package com.blendwerk.pet.domain;

import java.lang.String;
import java.time.Instant;
import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.ExpenseCategory;
import com.blendwerk.pet.domain.Identifier;
import com.blendwerk.pet.domain.Money;

public final class Expense extends BudgetTransaction {    
    private ExpenseCategory _category;

    Expense(Budget budget) {
        super(budget);
        _category = ExpenseCategory.OTHER;
    }

    public ExpenseCategory category() {
        return _category;
    }
    
    public void update(Money amount, ExpenseCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Expense category cannot be null.");
        }
        _category = category;
        update(amount);
    }
}