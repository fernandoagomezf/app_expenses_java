package com.blendwerk.pet.domain;

import java.lang.IllegalArgumentException;
import java.lang.String;
import java.time.Instant;
import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.Entity;
import com.blendwerk.pet.domain.Identifier;
import com.blendwerk.pet.domain.Money;

public abstract class Transaction implements Entity {
    private final Identifier _id;
    private String _category;
    private Money _amount;

    protected Transaction(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Transaction must be associated with a budget.");
        }
        _id = Identifier.create();
        _category = CATEGORY_GENERAL;
        _amount = Money.zero(budget.currency());
    }

    protected Transaction(Identifier id, String category, Money amount) {
        if (id == null || category == null || category.isBlank() || amount == null) {
            throw new IllegalArgumentException("Cannot reconstruct transaction from invalid arguments.");
        }
        _id = id;
        _category = category;
        _amount = amount;
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
            throw new IllegalArgumentException("Transaction amount cannot be null.");
        }
        if (amount.currency() != _amount.currency()) {
            throw new IllegalArgumentException("Cannot replace an amount on another currency.");
        }
        _amount = amount;
    }

    public void categorize(String category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        _category = category;
    }

    public abstract int sign();

    public Money signedAmount() {
        return _amount.scale(sign());
    }

    public static final String CATEGORY_GENERAL = "General";
}
