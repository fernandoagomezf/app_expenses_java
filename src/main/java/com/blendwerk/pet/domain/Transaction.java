package com.blendwerk.pet.domain;

import java.lang.String;

public abstract class Transaction implements Entity {
    private Identifier _id;
    private Budget _budget;
    private String _category;
    private Money _amount;

    protected Transaction(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be null.");
        }
        _budget = budget;
        _id = Identifier.create();
        _category = CATEGORY_GENERAL;
        _amount = Money.zero(_budget.currency());
        ensure();
    }

    protected Transaction() {        
        _id = null;
        _budget = null;
        _category = null;
        _amount = null;
    }

    static Transaction of(Transaction target, Budget budget, Identifier id, String category, Money amount) {
        target._budget = budget;
        target._id = id;
        target._category = category;
        target._amount = amount;
        return target;
    }

    public void ensure() {
        if (_budget == null) {
            throw new DomainException("A transaction must be associated with a budget.");
        }
        if (_amount == null) {
            throw new DomainException("A transaction must have a valid amount.");
        }
        if (_category == null) {
            throw new DomainException("A transaction must have a valid category.");
        }
        if (_amount.currency() != _budget.currency()) {
            throw new DomainException("A transaction amount currency must match the budget currency.");
        }
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
        _amount = amount;
        ensure();
    }

    public void categorize(String category) {
        _category = category;
        ensure();
    }

    public abstract int sign();

    public Money signedAmount() {
        return _amount.scale(sign());
    }

    public static final String CATEGORY_GENERAL = "General";
}
