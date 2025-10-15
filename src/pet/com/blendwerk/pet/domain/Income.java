package com.blendwerk.pet.domain;

import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.IncomeCategory;
import com.blendwerk.pet.domain.Money;

import java.time.Instant;

public final class Income extends Transaction {
    private IncomeCategory _category;

    Income(Budget budget) {
        super(budget);        
        _category = IncomeCategory.OTHER;
    }

    protected int sign() {
        return 1;
    }

    public IncomeCategory category() {
        return _category;
    }
    
    public void update(Money amount, IncomeCategory category) {        
        if (category == null) {
            throw new IllegalArgumentException("Income category cannot be null.");
        }
        _category = category;
        update(amount);
    }
    
}