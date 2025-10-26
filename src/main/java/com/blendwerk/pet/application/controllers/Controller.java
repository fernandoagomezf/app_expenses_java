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
        _view.showSuccess(message, false);
        updateView();
    }

    public void onBudgetUpdated(Budget budget) {
        var message = "Budget '" + budget.name() + "' updated successfully.";
        _view.showSuccess(message, false);
        updateView();
    }

    public void onError(String message) {                
        _view.showError(message);
    }
    
    public void requestAbout() {
        _view.showAbout();
    }

    public void requestNewBudget() {
        var input = _view.getNewBudget();
        if (input.isPresent()) {
            _model.createBudget(input.get());
        }
        updateView();
    }
    
    public void updateView() {
        _view.updateModel(_model);
    }

    public void requestSelectBudget(String budgetId) {
        _model.selectBudget(budgetId);
        updateView();
        var msg = "Budget selected: " + _model.getSelectedBudget().get().name();
        _view.showSuccess(msg, false);
    }

    public void requestNewTransaction() {
        var budget = _model.getSelectedBudget();
        if (budget.isPresent()){
            var input = _view.getNewTransaction();
            if (input.isPresent()) {
                _model.createTransaction(input.get());
            }
        } else {
            _view.showError("No budget selected. Please select a budget first.");
        }
    }

    public void requestSelectTransaction(String transactionId) {
        _model.selectTransaction(transactionId);
        updateView();
        var msg = "Transaction selected: " + _model.getSelectedTransaction().get().toString();
        _view.showSuccess(msg, false);
    }

    public void requestRefresh() {
        updateView();
        _view.showSuccess("Data refreshed.", false);
    }

    public void requestDeleteTransaction(String transactionId) {
        //_model.deleteTransaction(transactionId);
        updateView();
        _view.showSuccess("Transaction deleted successfully.", false);
    }

    public void requestDeleteBudget(String budgetId) {
        //_model.deleteBudget(budgetId);
        updateView();
        _view.showSuccess("Budget deleted successfully.", false);
    }
}