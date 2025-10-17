package com.blendwerk.pet.infrastructure;

import java.io.IOException;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.System;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.Identifier;
import com.blendwerk.pet.domain.RepositoryException;
import com.blendwerk.pet.domain.Currency;
import com.blendwerk.pet.domain.Transaction;
import com.blendwerk.pet.domain.Income;
import com.blendwerk.pet.domain.Expense;
import com.blendwerk.pet.domain.Money;
import com.blendwerk.pet.infrastructure.Cache;

public final class FileBudgetRepository {
    private final Cache _cache;
    private final String _basePath;
    
    public FileBudgetRepository(Cache cache) {
        if (cache == null) {
            throw new IllegalArgumentException("Cache cannot be null.");
        }
        _cache = cache;
        
        var appData = System.getProperty("user.home");
        _basePath = appData + "\\Blendwerk\\PET\\";
    }    

    private Path getPath(Identifier id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }

        return Path.of(_basePath + id.toString() + ".json");
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
                var path = getPath(id);
                var parser = new FileParser();
                parser.parse(path);

                var transactions = new ArrayList<Transaction>();
                for (var transactionId : parser.getTransactions()) {
                    var sign = Integer.valueOf(parser.getTransactionSign(transactionId));        
                    var tranId = Identifier.of(transactionId);
                    var category = parser.getTransactionCategory(transactionId);
                    var amount = Money.of(
                        parser.getTransactionAmount(transactionId), 
                        Currency.valueOf(parser.getTransactionCurrency(transactionId))
                    );
                    Transaction transaction = sign > 0 ?
                        new Income(tranId, category, amount) :
                        new Expense(tranId, category, amount);
                    /*if (sign >= 0) {
                        transaction = new Income(id, category, amount);
                    } else {
                        transaction = new Expense(id, category, amount);
                    }*/
                    transactions.add(transaction);
                }
                
                budget = new Budget(
                    Identifier.of(parser.getBudgetId()), 
                    parser.getBudgetName(), 
                    Currency.valueOf(parser.getBudgetCurrency()), 
                    transactions
                );
                _cache.put(budget);
            }
        } catch (IOException ex) {

        } catch (NumberFormatException ex) {

        } catch (IllegalArgumentException ex) {

        } 

        return budget;
    }

    public void save(Budget budget) throws RepositoryException {
        if (budget == null) {
            throw new IllegalArgumentException("Cannot save a null budget.");
        }
        
        try {
            var text = new StringBuilder();
            text.append("[Blendwerk PET Budget File]\n")
                .append("Version=0.1.0\n")
                .append("\n");

            text.append("[Budget]\n")
                .append("ID=").append(budget.id()).append("\n")
                .append("Name=").append(budget.name()).append("\n")
                .append("Currency=").append(budget.currency()).append("\n")
                .append("\n");

            text.append("[Transactions]\n");
            var i = budget.stream()
                .sorted(Comparator.comparing(Transaction::category))
                .iterator();
            while (i.hasNext()) {
                var item = i.next();
                text.append("ID=").append(item.id())
                    .append("\n\tAmount=").append(item.signedAmount())
                    .append("\n\tCurrency=").append(item.signedAmount().currency())
                    .append("\n\tCategory").append(item.category())
                    .append("\n");
            }

            var path = getPath(budget.id());
            Files.writeString(path, text.toString());
            _cache.put(budget);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RepositoryException("Failed to save budget file: " + e.getMessage(), e);
        }
    }

    public void delete(Identifier budgetId) throws RepositoryException {
        if (budgetId == null) {
            throw new IllegalArgumentException("Budget ID cannot be null.");
        }

        try {
            var path = getPath(budgetId);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RepositoryException("Failed to delete budget file: " + e.getMessage(), e);
        }

        _cache.erase(budgetId);
    }

}