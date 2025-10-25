package com.blendwerk.pet.application.models;

import com.blendwerk.pet.domain.budgeting.Budget;

public interface BudgetModelListener {
    void onBudgetCreated(Budget budget);
    //void onBudgetUpdated(Budget budget);
    //void onBudgetDeleted(String budgetId);
    void onError(String message);
}