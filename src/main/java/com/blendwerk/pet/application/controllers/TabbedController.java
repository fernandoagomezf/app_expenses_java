package com.blendwerk.pet.application.controllers;

import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.models.BudgetModelListener;
import com.blendwerk.pet.application.views.TabbedPane;
import com.blendwerk.pet.domain.budgeting.Budget;

public class TabbedController implements BudgetModelListener {
    private final BudgetModel _model;
    private final TabbedPane _view;
    
    public TabbedController(BudgetModel model, TabbedPane view) {
        if (model == null) throw new IllegalArgumentException("Model cannot be null");
        if (view == null) throw new IllegalArgumentException("View cannot be null");
        
        _model = model;
        _model.addListener(this);
        _view = view;
    }

    public void onBudgetCreated(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be null");
        }

        var budgetName = budget.name();
        if (_view.hasTab(budgetName)) {
            _view.selectTab(budgetName);
        } else {        
            _view.addBudgetTab(budget.name(), budget);
        }        
    }

    public void onError(String message) {                
        System.out.println("TabbedController::Error: " + message);
    }  
    
    public void closeBudgetTab(String budgetName) {
        _view.closeTab(budgetName);
    }
    
    public void createNewBudget() {
        _view.showCreateBudgetDialog();
    }    
}