package com.blendwerk.pet.domain.budgeting;

public interface BudgetRebuilder {
    BudgetRebuilder withId(String id);
    BudgetRebuilder withProperties(String name, String currency);
    BudgetRebuilder withTransaction(String id, String category, String amount, String currency, int sign);
    Budget get();
}
