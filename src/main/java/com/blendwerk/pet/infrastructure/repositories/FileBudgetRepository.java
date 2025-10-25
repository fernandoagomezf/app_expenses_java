package com.blendwerk.pet.infrastructure.repositories;

import java.lang.String;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.blendwerk.pet.domain.budgeting.Budget;
import com.blendwerk.pet.domain.budgeting.Transaction;
import com.blendwerk.pet.domain.core.DomainException;
import com.blendwerk.pet.domain.core.Identifier;
import com.blendwerk.pet.infrastructure.services.Cache;
import com.blendwerk.pet.infrastructure.services.Storage;
import com.blendwerk.pet.infrastructure.services.StorageException;
import com.blendwerk.pet.infrastructure.services.StorageScanner;

public final class FileBudgetRepository implements BudgetRepository {
    private final Cache _cache;
    private final Storage _storage;
    private final StorageScanner _scanner;
    
    public FileBudgetRepository(Cache cache, Storage storage, StorageScanner scanner) {
        if (cache == null) {
            throw new IllegalArgumentException("Cache cannot be null.");
        }
        if (storage == null) {
            throw new IllegalArgumentException("Storage cannot be null.");
        }
        if (scanner == null) {
            throw new IllegalArgumentException("Storage scanner cannot be null.");
        }
        _cache = cache;
        _storage = storage;
        _scanner = scanner;
    }

    public Stream<Budget> all() throws RepositoryException {
        var budgets = new ArrayList<Budget>();

        try {
            _scanner.scan();
            var sources = _scanner.sources();
            for (var source : sources) {
                var id = new Identifier(source);                
                var budget = get(id);                
                budgets.add(budget);
            }
        } catch (StorageException ex) {
            throw new RepositoryException("Could not retrieve budget views: storage not available.", ex);
        }

        return budgets.stream();
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

                var rebuilder = Budget.rebuilder();

                var budgetId = _storage.get("Budget", "ID");
                var budgetName = _storage.get("Budget", "Name");
                var budgetCurrency = _storage.get("Budget", "Currency");
                var budgetTransactionCount = _storage.get("Budget", "TransactionCount");
                var budgetBalance = _storage.get("Budget", "Balance");
                
                rebuilder.withId(budgetId)
                         .withProperties(budgetName, budgetCurrency);

                var uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
                var pattern = Pattern.compile(uuidRegex);

                for (var section : _storage.getSections()) {
                    if (!pattern.matcher(section).matches()) {
                        continue;
                    }

                    var transactionSign = _storage.get(section, "Sign");
                    var transactionAmount = _storage.get(section, "Amount");
                    var transactionCurrency = _storage.get(section, "Currency");
                    var transactionCategory = _storage.get(section, "Category");
                    
                    rebuilder.withTransaction(
                        section,
                        transactionCategory, 
                        transactionAmount,
                        transactionCurrency, 
                        Integer.parseInt(transactionSign)
                    );                    
                }
                
                budget = rebuilder.get();
                
                var count = Integer.parseInt(budgetTransactionCount);
                if (budget.stream().count() != count) {
                    throw new RepositoryException("Could not reconstruct a budget: transaction count mismatch.");
                }
                var balance = new BigDecimal(budgetBalance)
                    .setScale(8, RoundingMode.HALF_UP);
                if (!budget.balance().value().equals(balance)) {
                    throw new RepositoryException("Could not reconstruct a budget: balance mismatch.");
                }

                _cache.put(budget);
            }
        } catch (StorageException ex) {
            throw new RepositoryException("Could not reconstruct a budget: storage not available.", ex);
        } catch (NumberFormatException ex) {
            throw new RepositoryException("Could not reconstruct a budget: storage with invalid format.", ex);
        } catch (DomainException ex) {
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
            _storage.set("Budget", "TransactionCount", String.valueOf(budget.stream().count()));
            _storage.set("Budget", "Balance", budget.balance().value().toPlainString());
            
            var i = budget.stream()
                .sorted(Comparator.comparing(Transaction::category))
                .iterator();
            while (i.hasNext()) {
                var item = i.next();
                var sectionName = item.id().value().toString();
                _storage.set(sectionName, "Sign", String.valueOf(item.sign()));
                _storage.set(sectionName, "Amount", item.amount().value().toPlainString());
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