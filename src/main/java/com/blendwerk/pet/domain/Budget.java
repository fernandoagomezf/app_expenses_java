package com.blendwerk.pet.domain;

import java.lang.String;
import java.lang.IllegalArgumentException;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

public final class Budget implements Entity {
    private final Identifier _id;
    private Currency _currency;
    private String _name;
    private final HashMap<Identifier, Transaction> _transactions;

    public Budget(String name, Currency currency) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Budget must have a name.");
        }
        _id = Identifier.create();
        _name = name;
        _currency = currency;
        _transactions = new HashMap<>();
    }

    public Budget(Identifier id, String name, Currency currency, Iterable<Transaction> transactions) {
        if (id == null || name == null || name.isBlank() || currency == null || transactions == null) {
            throw new IllegalArgumentException("Cannot reconstruct budget from invalid arguments.");
        }
        _id = id;
        _name = name;
        _currency = currency;
        _transactions = new HashMap<>();
        for (var transaction : transactions) {
            if (transaction.amount().currency() != currency) {
                throw new IllegalArgumentException("Cannot reconstruct budget: transaction currency mismatch.");
            }
            _transactions.put(transaction.id(), transaction);
        }
    }

    public Identifier id() {
        return _id;
    }

    public Currency currency() {
        return _currency;
    }

    public String name() {
        return _name;
    }

    public Income credit(Money amount, IncomeCategory category) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        if (_currency != amount.currency()) {
            throw new IllegalArgumentException("Amount currency must match the budget's.");
        }

        var income = new Income(this);
        income.update(amount);
        income.categorize(category);
        _transactions.put(income.id(), income);
        return income;   
    }

    public Expense debit(Money amount, ExpenseCategory category) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        if (_currency != amount.currency()) {
            throw new IllegalArgumentException("Amount currency must match the budget's.");
        }

        var expense = new Expense(this);
        expense.update(amount);
        expense.categorize(category);
        _transactions.put(expense.id(), expense);
        return expense;   
    }

    public Optional<Transaction> find(Identifier id){
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }

        Optional<Transaction> result = Optional.empty();
        if (_transactions.containsKey(id)) {
            var item = _transactions.get(id);
            result = Optional.of(item);
        }

        return result;
    }
    
    public Transaction get(Identifier id){        
        var result = find(id);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("No transaction with the given ID exists in this budget.");
        }

        return result.get();
    }
    
    public Stream<Transaction> stream() {
        return _transactions
            .values()
            .stream();
    }

    public Money balance() {
        var zero = Money.zero(_currency);
        var result = stream()
        .map(Transaction::signedAmount)
        .reduce(zero, Money::add);        

        return result;
    }
}