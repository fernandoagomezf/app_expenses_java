package com.blendwerk.pet.application;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.BudgetView;
import com.blendwerk.pet.domain.Currency;
import com.blendwerk.pet.domain.Identifier;
import com.blendwerk.pet.infrastructure.BudgetRepository;

public class BudgetingService {
    private final BudgetRepository _budgetRepository;

    public BudgetingService(BudgetRepository budgetRepository) {
        _budgetRepository = budgetRepository;
    }

    public ServiceResults<BudgetView> getSummaries() {
        var future = CompletableFuture.supplyAsync(() -> {
            try {
                var views = _budgetRepository.getViews();
                return ServiceResults.success(views);
            } catch (Exception ex) {
                return ServiceResults.<BudgetView>failure("Failed to load budget summary: " + ex.getMessage());
            }
        });
        
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            return ServiceResults.failure("Service execution failed: " + ex.getMessage());
        }
    }

    public ServiceResult<Budget> createBudget(String name, String currency) {
        if (name == null || name.isBlank()) {
            return ServiceResult.<Budget>failure("The budget name cannot be null or blank.");
        }
        if (currency == null) {
            return ServiceResult.<Budget>failure("The currency cannot be null.");
        }

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                var budget = new Budget(name, Currency.valueOf(currency));
                _budgetRepository.save(budget);
                return ServiceResult.success(budget);
            } catch (Exception ex) {
                return ServiceResult.<Budget>failure("Failed to create new budget: " + ex.getMessage());
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            return ServiceResult.<Budget>failure("Service execution failed: " + ex.getMessage());
        }
    }

    public ServiceResult<Budget> getBudget(String budgetId) {
        if (budgetId == null || budgetId.isBlank()) {
            return ServiceResult.<Budget>failure("The budget ID cannot be null or blank.");
        }

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                var id = Identifier.of(budgetId);
                var budget = _budgetRepository.get(id);            
                return ServiceResult.success(budget);
                
            } catch (Exception ex) {
                return ServiceResult.<Budget>failure("Failed to load budget: " + ex.getMessage());
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            return ServiceResult.<Budget>failure("Service execution failed: " + ex.getMessage());
        }        
    }
}
