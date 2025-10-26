package com.blendwerk.pet.application.views;

public interface ViewListener {
    void requestAbout();
    void requestNewBudget();
    void requestSelectBudget(String budgetId);
    void requestNewTransaction();
    void requestSelectTransaction(String transactionId);
    void requestRefresh();
    void requestDeleteTransaction();
    void requestDeleteBudget();
}
