package com.blendwerk.pet.application.controllers;

import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.models.BudgetModelListener;
import com.blendwerk.pet.application.views.TreePanel;
import com.blendwerk.pet.domain.budgeting.Budget;

/**
 * Controller for the TreePanel - manages budget tree display
 */
public class TreeController implements BudgetModelListener {
    private final BudgetModel _model;
    private final TreePanel _view;
    
    public TreeController(BudgetModel model, TreePanel view) {
        if (model == null) throw new IllegalArgumentException("Model cannot be null");
        if (view == null) throw new IllegalArgumentException("View cannot be null");
        
        _model = model;
        _model.addListener(this);
        _view = view;
        
        loadBudgetSummaries();
    }
    
    public void loadBudgetSummaries() {
        SwingUtilities.invokeLater(() -> {
            var summaries = _model.getSummaries()
                .collect(Collectors.toList());
            _view.updateBudgetTree(summaries);            
        });
    }
    
    public void refreshBudgets() {
        loadBudgetSummaries();
    }
    
    public void onBudgetCreated(Budget budget) {
        SwingUtilities.invokeLater(() -> {
            loadBudgetSummaries();
        });
    }

    public void onError(String message) {                
        System.out.println("TreeController::Error: " + message);
    }    
}