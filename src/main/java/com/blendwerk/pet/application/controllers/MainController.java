package com.blendwerk.pet.application.controllers;

import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.models.BudgetModelListener;
import com.blendwerk.pet.application.views.MainView;
import com.blendwerk.pet.application.views.MainViewListener;
import com.blendwerk.pet.domain.budgeting.Budget;

public class MainController implements BudgetModelListener, MainViewListener {
    private final BudgetModel _model;
    private final MainView _view;
    
    public MainController(BudgetModel model, MainView view) {
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
}