package com.blendwerk.pet.application.models;

import com.blendwerk.pet.application.services.BudgetingService;
import com.blendwerk.pet.application.services.ServiceResult;
import com.blendwerk.pet.application.services.ServiceResults;
import com.blendwerk.pet.domain.budgeting.Budget;
import com.blendwerk.pet.domain.budgeting.BudgetView;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BudgetModel {
    private final BudgetingService _budgetingService;
    private final List<BudgetModelListener> _listeners;
    
    public BudgetModel(BudgetingService budgetingService) {
        if (budgetingService == null) {
            throw new IllegalArgumentException("BudgetingService cannot be null");
        }
        _budgetingService = budgetingService;
        _listeners = new CopyOnWriteArrayList<>();
    }
    
    public void addListener(BudgetModelListener listener) {
        if (listener != null) {
            _listeners.add(listener);
        }
    }
    
    public void removeListener(BudgetModelListener listener) {
        _listeners.remove(listener);
    }
    
    public ServiceResults<BudgetView> getBudgetSummaries() {
        return _budgetingService.getSummaries();
    }
    
    public ServiceResult<Budget> getBudget(String budgetId) {
        return _budgetingService.getBudget(budgetId);
    }
    
    public ServiceResult<Budget> createBudget(String name, String currency) {
        var result = _budgetingService.createBudget(name, currency);
        if (result.success()) {
            result.result().ifPresent(this::notifyBudgetCreated);
        }
        return result;
    }
    
    private void notifyBudgetCreated(Budget budget) {
        for (BudgetModelListener listener : _listeners) {
            listener.onBudgetCreated(budget);
        }
    }
    
    private void notifyBudgetUpdated(Budget budget) {
        for (BudgetModelListener listener : _listeners) {
            listener.onBudgetUpdated(budget);
        }
    }
    
    private void notifyBudgetDeleted(String budgetId) {
        for (BudgetModelListener listener : _listeners) {
            listener.onBudgetDeleted(budgetId);
        }
    }
}