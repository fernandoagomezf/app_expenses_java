package com.blendwerk.pet.application.views;

import java.util.Optional;

import com.blendwerk.pet.application.models.Model;
import com.blendwerk.pet.application.models.CreateBudgetInput;

public interface View {
    Optional<CreateBudgetInput> getNewBudget();
    void showError(String message);
    void showSuccess(String message);
    void showAbout();
    void addListener(ViewListener listener);
    void removeListener(ViewListener listener);
    void updateModel(Model model);
}
