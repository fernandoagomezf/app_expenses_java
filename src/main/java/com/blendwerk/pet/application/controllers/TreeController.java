package com.blendwerk.pet.application.controllers;

import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.models.BudgetModelListener;
import com.blendwerk.pet.application.views.TreePanel;
import com.blendwerk.pet.domain.budgeting.Budget;
import javax.swing.SwingUtilities;

/**
 * Controller for the TreePanel - manages budget tree display
 */
public class TreeController implements BudgetModelListener {
    private final BudgetModel _model;
    private final TreePanel _view;
    private TabbedController _tabbedController; // Optional reference for coordination
    
    public TreeController(BudgetModel model, TreePanel view) {
        if (model == null) throw new IllegalArgumentException("Model cannot be null");
        if (view == null) throw new IllegalArgumentException("View cannot be null");
        
        _model = model;
        _view = view;
        
        // Listen to model changes
        _model.addListener(this);
        
        // Load initial data
        loadBudgetSummaries();
    }
    
    public void setTabbedController(TabbedController tabbedController) {
        _tabbedController = tabbedController;
    }
    
    public void loadBudgetSummaries() {
        // Load in background to avoid blocking UI
        SwingUtilities.invokeLater(() -> {
            var result = _model.getBudgetSummaries();
            if (result.success()) {
                _view.updateBudgetTree(result.results());
            } else {
                _view.showError("Failed to load budgets: " + result.message());
            }
        });
    }
    
    public void refreshBudgets() {
        loadBudgetSummaries();
    }
    
    public void createBudget(String name, String currency) {
        var result = _model.createBudget(name, currency);
        if (!result.success()) {
            _view.showError("Failed to create budget: " + result.message());
        }
        // If successful, the model listener will update the view
    }
    
    // BudgetModelListener implementation
    @Override
    public void onBudgetCreated(Budget budget) {
        SwingUtilities.invokeLater(() -> {
            // Refresh the entire tree to show the new budget
            loadBudgetSummaries();
            
            // Notify TabbedController to refresh its mapping
            if (_tabbedController != null) {
                _tabbedController.refreshBudgetMapping();
            }
        });
    }
    
    @Override
    public void onBudgetUpdated(Budget budget) {
        SwingUtilities.invokeLater(() -> {
            loadBudgetSummaries(); // Refresh the tree
        });
    }
    
    @Override
    public void onBudgetDeleted(String budgetId) {
        SwingUtilities.invokeLater(() -> {
            loadBudgetSummaries(); // Refresh the tree
        });
    }
}