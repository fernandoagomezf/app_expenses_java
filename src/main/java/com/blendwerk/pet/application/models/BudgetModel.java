package com.blendwerk.pet.application.models;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import com.blendwerk.pet.domain.budgeting.Budget;
import com.blendwerk.pet.domain.core.Identifier;
import com.blendwerk.pet.infrastructure.repositories.BudgetRepository;

public class BudgetModel {
    private final BudgetRepository _repository;
    private final List<BudgetModelListener> _listeners;
    
    public BudgetModel(BudgetRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        _repository = repository;
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
    
    public Stream<BudgetSummary> getSummaries() {
        Stream<BudgetSummary> results = null;

        try {
            results = _repository
                .all()            
                .map(budget -> new BudgetSummary(
                    budget.id().toString(),
                    budget.name(),
                    budget.currency().toString(),
                    budget.incomes().toString(),
                    budget.expenses().toString(),
                    budget.balance().toString()
                ))
                .sorted(Comparator.comparing(BudgetSummary::name, String.CASE_INSENSITIVE_ORDER));
        } catch (Exception ex) {
            notifyError("Could not retrieve budget summaries: " + ex.getMessage());
            results = Stream.empty();
        }

        return results;
    }

    public Optional<Budget> getBudget(String budgetId) {
        Optional<Budget> result;
        
        try {
            var id = Identifier.of(budgetId);
            var budget = _repository.get(id);
            result = Optional.of(budget);
        } catch (Exception ex) {
            notifyError("Could not retrieve budget summaries: " + ex.getMessage());
            result = Optional.empty();
        }

        return result;
    }

    public Optional<Budget> createBudget(CreateBudgetInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }

        Optional<Budget> result = Optional.empty();
        try {
            var validation = input.validate();
            if (validation.isValid()) {
                var budget = new Budget(input.name(), input.asCurrency());
                _repository.save(budget);
                result = Optional.of(budget);
                notifyBudgetCreated(budget);
            } else {
                notifyError("Could not create budget: " + validation.errorMessage());
            }
        } catch (Exception ex) {
            notifyError("Could not create budget: " + ex.getMessage());
        }
        
        return result;
    }
    
    private void notifyBudgetCreated(Budget budget) {
        for (BudgetModelListener listener : _listeners) {
            listener.onBudgetCreated(budget);
        }
    }
    
    private void notifyError(String message) {
        for (BudgetModelListener listener : _listeners) {
            listener.onError(message);
        }
    }
}