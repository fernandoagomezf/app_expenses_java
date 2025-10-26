package com.blendwerk.pet.application.controllers;

import com.blendwerk.pet.application.models.Model;
import com.blendwerk.pet.application.models.ModelListener;
import com.blendwerk.pet.application.views.View;
import com.blendwerk.pet.application.views.ViewListener;
import com.blendwerk.pet.domain.budgeting.Budget;

public class Controller implements ModelListener, ViewListener {
    private final Model _model;
    private final View _view;
    
    public Controller(Model model, View view) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if (view == null) { 
            throw new IllegalArgumentException("View cannot be null");
        }        
        _model = model;
        _model.addListener(this);
        _view = view;
        _view.addListener(this);
    }

    public void onBudgetCreated(Budget budget) {
        var message = "Budget '" + budget.name() + "' created successfully.";
        _view.showSuccess(message);
        updateView();
    }

    public void onError(String message) {                
        _view.showError(message);
    }
    
    public void createNewBudget() {
        var input = _view.getNewBudget();
        if (input.isPresent()) {
            _model.createBudget(input.get());
        }
    }

    public void requestAbout() {
        _view.showAbout();
    }

    public void requestNewBudget() {
        createNewBudget();
        updateView();
    }
    
    public void updateView() {
        _view.updateModel(_model);
    }

    public void requestSelectBudget(String budgetId) {
        _model.select(budgetId);
        updateView();
    }
}