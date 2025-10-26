package com.blendwerk.pet.application.models;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import com.blendwerk.pet.domain.budgeting.Budget;
import com.blendwerk.pet.domain.core.Identifier;
import com.blendwerk.pet.infrastructure.repositories.BudgetRepository;

public class Model {
    private final BudgetRepository _repository;
    private final List<ModelListener> _listeners;
    private Optional<Budget> _selected;
    
    public Model(BudgetRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        _repository = repository;
        _listeners = new CopyOnWriteArrayList<>();
        _selected = Optional.empty();
    }
    
    public void addListener(ModelListener listener) {
        if (listener != null) {
            _listeners.add(listener);
        }
    }
    
    public void removeListener(ModelListener listener) {
        _listeners.remove(listener);
    }

    public List<Budget> getAllBudgets() {
        List<Budget> result;
        
        try {
            result = _repository.all()
                .sorted(Comparator.comparing(Budget::name))
                .collect(Collectors.toList());
        } catch (Exception ex) {
            notifyError("Could not load budgets: " + ex.getMessage());
            result = List.of();
        }

        return result;
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
        for (ModelListener listener : _listeners) {
            listener.onBudgetCreated(budget);
        }
    }
    
    private void notifyError(String message) {
        for (ModelListener listener : _listeners) {
            listener.onError(message);
        }
    }

    public Optional<Budget> getSelectedBudget() {
        return _selected;
    }

    public void select(String budgetId) {
        if (budgetId == null || budgetId.trim().isEmpty()) {
            _selected = Optional.empty();
            return;
        }

        try {
            var id = Identifier.of(budgetId);
            var budget = _repository.get(id);
            _selected = Optional.of(budget);
        } catch (Exception ex) {
            notifyError("Could not select budget: " + ex.getMessage());
            _selected = Optional.empty();
        }
    }
}