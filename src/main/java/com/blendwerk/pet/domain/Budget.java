package com.blendwerk.pet.domain;

import java.lang.String;
import java.lang.IllegalArgumentException;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

public final class Budget implements Entity {
    private Identifier _id;
    private Currency _currency;
    private String _name;
    private final HashMap<Identifier, Transaction> _transactions;

    public Budget(String name, Currency currency) {
        _id = Identifier.create();
        _name = name;
        _currency = currency;
        _transactions = new HashMap<>();
    }

    private Budget() {
        _id = null;
        _name = null;
        _currency = null;
        _transactions = new HashMap<>();
    }

    public void ensure() {
        if (_id == null) {
            throw new DomainException("A budget must have an ID.");
        }
        if (_name == null || _name.isBlank()) {
            throw new DomainException("A budget must have a name.");
        }
        if (_currency == null) {
            throw new DomainException("A budget must have a currency.");
        }
        for (var transaction : _transactions.values()) {
            transaction.ensure();
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
        var income = new Income(this);
        income.update(amount);
        income.categorize(category);
        _transactions.put(income.id(), income);
        ensure();

        return income;   
    }

    public Expense debit(Money amount, ExpenseCategory category) {
        var expense = new Expense(this);
        expense.update(amount);
        expense.categorize(category);
        _transactions.put(expense.id(), expense);
        ensure();
        
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
        var result = _transactions
        .values()
        .stream()
        .map(Transaction::signedAmount)
        .reduce(zero, Money::add);        

        return result;
    }

    
    public static BudgetRebuilder rebuilder() {
        return new BudgetRebuilder() {
            private Budget _budget = new Budget();
            
            public BudgetRebuilder withId(String id) {
                _budget._id = Identifier.of(id);
                return this;
            }

            public BudgetRebuilder withProperties(String name, String currency) {
                _budget._name = name;
                _budget._currency = Currency.valueOf(currency);
                return this;
            }

            public BudgetRebuilder withTransaction(String id, String category, String amount, String currency, int sign) {
                var transactionId = Identifier.of(id);
                var transactionAmount = Money.of(amount, Currency.valueOf(currency));
                Transaction transaction = sign >= 0 ?
                    Income.of(_budget, transactionId, transactionAmount, category) :
                    Expense.of(_budget, transactionId, transactionAmount, category);
                _budget._transactions.put(transactionId, transaction);
                return this;
            }

            public Budget get() {
                return _budget;
            }
        };
    }

}