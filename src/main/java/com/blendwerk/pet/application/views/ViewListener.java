package com.blendwerk.pet.application.views;

public interface ViewListener {
    void requestAbout();
    void requestNewBudget();
    void requestSelectBudget(String budgetId);
}
