package com.blendwerk.pet.application.controllers;

import com.blendwerk.pet.application.views.DetailsPanel;

public class DetailsController {
    private final DetailsPanel _view;
    
    public DetailsController(DetailsPanel view) {
        if (view == null) throw new IllegalArgumentException("View cannot be null");
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
}