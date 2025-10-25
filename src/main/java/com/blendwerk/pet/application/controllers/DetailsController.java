package com.blendwerk.pet.application.controllers;

import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.models.BudgetModelListener;
import com.blendwerk.pet.application.views.DetailsPanel;
import com.blendwerk.pet.domain.budgeting.Budget;

public class DetailsController implements BudgetModelListener {
    private final BudgetModel _model;
    private final DetailsPanel _view;
    
    public DetailsController(BudgetModel model, DetailsPanel view) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        _model = model;
        _model.addListener(this);
        _view = view;
    }
    
    public void showTransactionDetails(Object[] transactionData) {
        if (transactionData == null || transactionData.length < 6) {
            _view.clearTransactionDetails();
            return;
        }
        
        _view.loadTransactionDetails(transactionData);
        _view.setVisible(true);
    }
    
    public void hideDetails() {
        _view.setVisible(false);
    }
    
    public void clearDetails() {
        _view.clearTransactionDetails();
    }

    public void onBudgetCreated(Budget budget) { }

    public void onError(String message) { }
}