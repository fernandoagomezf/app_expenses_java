package com.blendwerk.pet.infrastructure;

import java.lang.String;
import java.util.ArrayList;
import java.util.Comparator;
import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.BudgetRepository;
import com.blendwerk.pet.domain.Identifier;
import com.blendwerk.pet.domain.RepositoryException;
import com.blendwerk.pet.domain.Currency;
import com.blendwerk.pet.domain.Transaction;
import com.blendwerk.pet.domain.Income;
import com.blendwerk.pet.domain.Expense;
import com.blendwerk.pet.domain.Money;

public final class FileBudgetRepository implements BudgetRepository {
    private final Cache _cache;
    private final Storage _storage;
    
    public FileBudgetRepository(Cache cache, Storage storage) {
        if (cache == null) {
            throw new IllegalArgumentException("Cache cannot be null.");
        }
        if (storage == null) {
            throw new IllegalArgumentException("Storage cannot be null.");
        }
        _cache = cache;
        _storage = storage;
    }    

    public Budget get(Identifier id) throws RepositoryException {
        if (id == null) {
            throw new IllegalArgumentException("Budget ID cannot be null.");
        }

        Budget budget = null;
        try {
            if (_cache.contains(id)) {
                budget = (Budget)_cache.get(id);
            } else {
                _storage.clear();
                _storage.load(id.value());

                String budgetId = _storage.get("Budget", "ID");
                String budgetName = _storage.get("Budget", "Name");
                String budgetCurrency = _storage.get("Budget", "Currency");

                var transactions = new ArrayList<Transaction>();
                for (var section : _storage.getSections()) {
                    var transactionSign = _storage.get(section, "Sign");
                    var transactionAmount = _storage.get(section, "Amount");
                    var transactionCurrency = _storage.get(section, "Currency");
                    var transactionCategory = _storage.get(section, "Category");
                    
                    var amount = Money.of(
                        transactionAmount, 
                        Currency.valueOf(transactionCurrency)
                    );
                    Transaction transaction = Integer.parseInt(transactionSign) > 0 ?
                        new Income(Identifier.of(section), transactionCategory, amount) :
                        new Expense(Identifier.of(section), transactionCategory, amount);                    
                    transactions.add(transaction);
                }
                
                budget = new Budget(
                    Identifier.of(budgetId), 
                    budgetName, 
                    Currency.valueOf(budgetCurrency), 
                    transactions
                );
                _cache.put(budget);
            }
        } catch (StorageException ex) {
            throw new RepositoryException("Could not reconstruct a budget: storage not available.", ex);
        } catch (NumberFormatException ex) {
            throw new RepositoryException("Could not reconstruct a budget: storage with invalid format.", ex);
        } catch (IllegalArgumentException ex) {
            throw new RepositoryException("Could not reconstruct a budget: storage with invalid data.", ex);
        } 

        return budget;
    }

    public void save(Budget budget) throws RepositoryException {
        if (budget == null) {
            throw new IllegalArgumentException("Cannot save a null budget.");
        }
        
        try {            
            _storage.clear();
            _storage.set("Header", "Version", "0.1.0");
            _storage.set("Budget", "ID", budget.id().value().toString());
            _storage.set("Budget", "Name", budget.name());
            _storage.set("Budget", "Currency", budget.currency().toString());
            
            var i = budget.stream()
                .sorted(Comparator.comparing(Transaction::category))
                .iterator();
            while (i.hasNext()) {
                var item = i.next();
                var sectionName = item.id().value().toString();
                _storage.set(sectionName, "Sign", String.valueOf(item.sign()));
                _storage.set(sectionName, "Amount", item.amount().value().toString());
                _storage.set(sectionName, "Currency", item.amount().currency().toString());
                _storage.set(sectionName, "Category", item.category());
            }
            _storage.save(budget.id().value());
            _cache.put(budget);
        } catch (StorageException ex) {
            ex.printStackTrace();
            throw new RepositoryException("Could not persist a budget: " + ex.getMessage(), ex);
        }
    }

    public void delete(Identifier budgetId) throws RepositoryException {
        if (budgetId == null) {
            throw new IllegalArgumentException("Budget ID cannot be null.");
        }

        _storage.delete(budgetId.value());
        _cache.erase(budgetId);
    }

}