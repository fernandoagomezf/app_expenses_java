package com.blendwerk.pet.domain;

import java.lang.IllegalArgumentException;
import java.lang.String;
import java.time.Instant;
import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.Identifier;
import com.blendwerk.pet.domain.Money;

public abstract class BudgetTransaction {
    private final Identifier _id;
    private final Budget _budget;
    private Money _amount;    
    private final Instant _createdAt;
    private Instant _updatedAt;

    protected BudgetTransaction(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Income must be associated with a budget.");
        }
        _id = Identifier.create();
        _budget = budget;
        _amount = Money.zero(budget.currency());
        _createdAt = Instant.now();
        _updatedAt = Instant.now();
    }

    public final Identifier id() {
        return _id;
    }

    public final Budget budget() {
        return _budget;
    }

    public final Money amount() {
        return _amount;
    }

    public final Instant createdAt() {
        return _createdAt;
    }

    public final Instant updatedAt() {
        return _updatedAt;
    }

    public void update(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Transaction amount cannot be null.");
        }
        _amount = amount;
        _updatedAt = Instant.now();
    }

    protected abstract int sign();

    public Money signedAmount() {
        return _amount.scale(sign());
    }
}
