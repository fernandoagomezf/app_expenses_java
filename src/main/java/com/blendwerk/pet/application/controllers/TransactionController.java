package com.blendwerk.pet.application.controllers;

import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.views.TransactionDialog;
import javax.swing.JFrame;

/**
 * Controller for TransactionDialog - manages transaction creation and editing
 */
public class TransactionController {
    private final BudgetModel _model;
    
    public TransactionController(BudgetModel model) {
        if (model == null) throw new IllegalArgumentException("Model cannot be null");
        _model = model;
    }
    
    public void showCreateTransactionDialog(JFrame parent, String budgetName) {
        var dialog = new TransactionDialog(parent, "Add Transaction", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            // TODO: Extract transaction data and save via model
            System.out.println("Transaction created for budget: " + budgetName);
            // Example: _model.addTransactionToBudget(budgetId, transactionData);
        }
    }
    
    public void showEditTransactionDialog(JFrame parent, String budgetName, Object transactionData) {
        var dialog = new TransactionDialog(parent, "Edit Transaction", transactionData);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            // TODO: Extract transaction data and update via model
            System.out.println("Transaction updated for budget: " + budgetName);
            // Example: _model.updateTransactionInBudget(budgetId, transactionId, transactionData);
        }
    }
}