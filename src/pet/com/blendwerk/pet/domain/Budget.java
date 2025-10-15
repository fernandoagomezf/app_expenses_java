package com.blendwerk.pet.domain;

import java.lang.String;
import java.lang.IllegalArgumentException;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;
import com.blendwerk.pet.domain.Identifier;
import com.blendwerk.pet.domain.Currency;
import com.blendwerk.pet.domain.Expense;
import com.blendwerk.pet.domain.ExpenseCategory;
import com.blendwerk.pet.domain.Income;
import com.blendwerk.pet.domain.IncomeCategory;
import com.blendwerk.pet.domain.Money;

public final class Budget {
    private final Identifier _id;
    private Currency _currency;
    private String _name;
    private final HashMap<Identifier, Income> _incomes;
    private final HashMap<Identifier, Expense> _expenses;

    public Budget(String name, Currency currency) {
        if (_name == null || _name.isBlank()) {
            throw new IllegalArgumentException("Budget must have a name.");
        }
        _id = Identifier.create();
        _name = name;
        _currency = currency;
        _incomes = new HashMap<>();
        _expenses = new HashMap<>();
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
        income.update(amount, category);
        _incomes.put(income.id(), income);
        return income;   
    }

    public Expense debit(Money amount, ExpenseCategory category) {
        var expense = new Expense(this);
        expense.update(amount, category);
        _expenses.put(expense.id(), expense);
        return expense;   
    }

    public Optional<BudgetTransaction> find(Identifier id){
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }

        Optional<BudgetTransaction> result = Optional.empty();
        if (_incomes.containsKey(id)) {
            var income = _incomes.get(id);
            result = Optional.of(income);
        } else if (_expenses.containsKey(id)) {
            var expense = _expenses.get(id);
            result = Optional.of(expense);
        }        
        return result;
    }
    
    public BudgetTransaction get(Identifier id){        
        var result = find(id);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("No transaction with the given ID exists in this budget.");
        }

        return result.get();
    }
    
    public Stream<BudgetTransaction> stream() {
        var incomes = _incomes
            .values()
            .stream()
            .map(i -> (BudgetTransaction) i);
        var expenses = _expenses
            .values()
            .stream()
            .map(e -> (BudgetTransaction) e);
        
        return Stream.concat(incomes, expenses);
    }

    public Money balance() {
        var zero = Money.zero(_currency);
        var result = stream()
        .map(BudgetTransaction::signedAmount)
        .reduce(zero, Money::add);        

        return result;
    }
}