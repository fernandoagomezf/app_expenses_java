package com.blendwerk.pet.application.controllers;

import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.views.TabbedPane;
import com.blendwerk.pet.domain.budgeting.Budget;
import javax.swing.SwingUtilities;
import java.util.HashMap;
import java.util.Map;

public class TabbedController {
    private final BudgetModel _model;
    private final TabbedPane _view;
    private final Map<String, String> _budgetIdMap; // budgetName -> budgetId
    
    public TabbedController(BudgetModel model, TabbedPane view) {
        if (model == null) throw new IllegalArgumentException("Model cannot be null");
        if (view == null) throw new IllegalArgumentException("View cannot be null");
        
        _model = model;
        _view = view;
        _budgetIdMap = new HashMap<>();
        
        // Initialize the budget name to ID mapping
        initializeBudgetMapping();
    }
    
    private void initializeBudgetMapping() {
        // Load budget summaries to create name->ID mapping
        SwingUtilities.invokeLater(() -> {
            var result = _model.getBudgetSummaries();
            if (result.success()) {
                for (var budgetView : result.results()) {
                    _budgetIdMap.put(budgetView.name(), budgetView.id().toString());
                }
            }
        });
    }
    
    public void openBudgetTab(String budgetName) {
        // Check if tab already exists
        if (_view.hasTab(budgetName)) {
            _view.selectTab(budgetName);
            return;
        }
        
        // Get budget ID from name
        String budgetId = _budgetIdMap.get(budgetName);
        if (budgetId == null) {
            _view.showError("Budget not found: " + budgetName);
            return;
        }
        
        // Load budget details
        SwingUtilities.invokeLater(() -> {
            var result = _model.getBudget(budgetId);
            if (result.success() && result.result().isPresent()) {
                Budget budget = result.result().get();
                _view.addBudgetTab(budgetName, budget);
            } else {
                _view.showError("Failed to load budget: " + result.message());
            }
        });
    }
    
    public void closeBudgetTab(String budgetName) {
        _view.closeTab(budgetName);
    }
    
    public void createNewBudget() {
        _view.showCreateBudgetDialog();
    }
    
    public void openBudgetTabForNewBudget(String budgetName, Budget budget) {
        // Add to budget ID mapping
        _budgetIdMap.put(budgetName, budget.id().toString());
        
        // Open the tab with real budget data
        SwingUtilities.invokeLater(() -> {
            _view.addBudgetTab(budgetName, budget);
        });
    }
    
    public void refreshBudgetMapping() {
        // Refresh the budget name to ID mapping
        initializeBudgetMapping();
    }
}