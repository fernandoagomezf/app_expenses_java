package com.blendwerk.pet.application.models;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import com.blendwerk.pet.domain.budgeting.Budget;
import com.blendwerk.pet.domain.budgeting.Currency;
import com.blendwerk.pet.domain.budgeting.ExpenseCategory;
import com.blendwerk.pet.domain.budgeting.IncomeCategory;
import com.blendwerk.pet.domain.budgeting.Money;
import com.blendwerk.pet.domain.budgeting.Transaction;
import com.blendwerk.pet.domain.core.Identifier;
import com.blendwerk.pet.infrastructure.repositories.BudgetRepository;

public class Model {
    private final BudgetRepository _repository;
    private final List<ModelListener> _listeners;
    private Optional<Budget> _selectedBudget;
    private Optional<Transaction> _selectedTransaction;
    
    public Model(BudgetRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        _repository = repository;
        _listeners = new CopyOnWriteArrayList<>();
        _selectedBudget = Optional.empty();
        _selectedTransaction = Optional.empty();
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

    public Optional<Budget> createBudget(BudgetInput input) {
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

    public void deleteTransaction() {
        
        try {
            Budget budget = _selectedBudget.orElseThrow(() -> new IllegalStateException("No budget selected."));
            Transaction transaction = _selectedTransaction.orElseThrow(() -> new IllegalStateException("No transaction selected."));            
            budget.remove(transaction.id());            
            _repository.save(budget);
            _selectedTransaction = Optional.empty();
            notifyBudgetUpdated(budget);            
        } catch (Exception ex) {
            notifyError("Could not delete transaction: " + ex.getMessage());
        }
    }

    public void deleteBudget() {
        try {
            Budget budget = _selectedBudget.orElseThrow(() -> new IllegalStateException("No budget selected."));
            _repository.delete(budget.id());    
            _selectedBudget = Optional.empty();
            _selectedTransaction = Optional.empty();
            notifyBudgetUpdated(null);
        } catch (Exception ex) {
            notifyError("Could not delete budget: " + ex.getMessage());
        }
    }

    public Optional<Transaction> createTransaction(TransactionInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }

        Optional<Transaction> result = Optional.empty();
        try {            
            var validation = input.validate();
            if (validation.isValid()) {
                Budget budget = _selectedBudget.orElseThrow(() -> new IllegalStateException("No budget selected."));
                var currency = Currency.valueOf(input.currency());
                var amount = Money.of(input.amount(), currency);
                
                if (input.type().equals("Expense")) {
                    var category = ExpenseCategory.valueOf(input.category());
                    var expense = budget.debit(amount, category);
                    result = Optional.of(expense);
                } else if (input.type().equals("Income")) {
                    var category = IncomeCategory.valueOf(input.category());
                    var income = budget.credit(amount, category);
                    result = Optional.of(income);
                } else {
                    notifyError("Could not create transaction: Unknown transaction type '" + input.type() + "'.");
                }

                _repository.save(budget);
                notifyBudgetUpdated(budget);           
            } else {
                notifyError("Could not create transaction: " + validation.errorMessage());
            }
        } catch (Exception ex) {
            notifyError("Could not create transaction: " + ex.getMessage());
        }
        
        return result;
    }
    
    private void notifyBudgetCreated(Budget budget) {
        for (ModelListener listener : _listeners) {
            listener.onBudgetCreated(budget);
        }
    }

    private void notifyBudgetUpdated(Budget budget) {
        for (ModelListener listener : _listeners) {
            listener.onBudgetUpdated(budget);
        }
    }
    
    private void notifyError(String message) {
        for (ModelListener listener : _listeners) {
            listener.onError(message);
        }
    }

    public Optional<Budget> getSelectedBudget() {
        return _selectedBudget;
    }

    public void selectBudget(String budgetId) {
        if (budgetId == null || budgetId.trim().isEmpty()) {
            _selectedBudget = Optional.empty();
            return;
        }

        try {
            var id = Identifier.of(budgetId);
            var budget = _repository.get(id);
            _selectedBudget = Optional.of(budget);
        } catch (Exception ex) {
            notifyError("Could not select budget: " + ex.getMessage());
            _selectedBudget = Optional.empty();
        }
    }

    public Optional<Transaction> getSelectedTransaction() {
        return _selectedTransaction;
    }

    public void selectTransaction(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            _selectedTransaction = Optional.empty();
            return;
        }

        try {
            Budget budget = _selectedBudget.orElseThrow(() -> new IllegalStateException("No budget selected."));
            var id = Identifier.of(transactionId);
            var transaction = budget.find(id);
            if (transaction.isPresent()) {
                _selectedTransaction = transaction;
            } else {
                notifyError("Could not find transaction with ID: " + transactionId);
                _selectedTransaction = Optional.empty();
            }
        } catch (Exception ex) {
            notifyError("Could not select transaction: " + ex.getMessage());
            _selectedTransaction = Optional.empty();
        }
    }
}