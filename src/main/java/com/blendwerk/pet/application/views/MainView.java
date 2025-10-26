package com.blendwerk.pet.application.views;

import java.util.Optional;

import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.models.CreateBudgetInput;

public interface MainView {
    Optional<CreateBudgetInput> getNewBudget();
    void showError(String message);
    void showSuccess(String message);
    void showAbout();
    void addListener(MainViewListener listener);
    void removeListener(MainViewListener listener);
    void updateModel(BudgetModel model);
}
