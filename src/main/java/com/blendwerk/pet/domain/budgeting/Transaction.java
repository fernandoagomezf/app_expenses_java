package com.blendwerk.pet.domain.budgeting;

import java.lang.String;

import com.blendwerk.pet.domain.core.DomainException;
import com.blendwerk.pet.domain.core.Entity;
import com.blendwerk.pet.domain.core.Identifier;

public abstract class Transaction implements Entity {
    private Identifier _id;
    private Budget _budget;
    private String _category;
    private Money _amount;
    private boolean _validated;

    protected Transaction(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be null.");
        }
        _budget = budget;
        _id = Identifier.create();
        _category = CATEGORY_GENERAL;
        _amount = Money.zero(_budget.currency());
        
        _validated = false;
        ensure();
    }

    protected Transaction() {        
        _budget = null;
        _id = null;
        _category = null;
        _amount = null;

        _validated = false;
    }

    static Transaction of(Transaction target, Budget budget, Identifier id, String category, Money amount) {
        target._budget = budget;
        target._id = id;
        target._category = category;
        target._amount = amount;
        target._validated = false;
        return target;
    }

    public final void ensure() {
        if (_validated) {
            return;
        }

        if (_budget == null) {
            throw new IllegalStateException("A transaction must be associated with a budget.");
        }
        if (_id == null || _id.isEmpty()) {
            throw new IllegalStateException("A transaction must have a valid ID.");
        }
        if (_amount == null || _category == null) {
            throw new IllegalStateException("A transaction must have a valid amount.");
        }
        if (_amount.currency() != _budget.currency()) {
            throw new DomainException("A transaction amount currency must match the budget currency.");
        }

        _validated = true;
    }

    public final Budget budget() {
        return _budget;
    }

    public final Identifier id() {
        return _id;
    }

    public final Money amount() {
        return _amount;
    }

    public final String category() {
        return _category;
    }

    public void update(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null.");
        }
        _amount = amount;
        _validated = false;
        ensure();
    }

    public void categorize(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank.");
        }
        _category = category;
        _validated = false;
        ensure();
    }

    public abstract int sign();

    public Money signedAmount() {
        return _amount.scale(sign());
    }

    public static final String CATEGORY_GENERAL = "General";
}
