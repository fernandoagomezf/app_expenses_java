package com.blendwerk.pet.domain;

import java.lang.IllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.Currency;
import com.blendwerk.pet.domain.Expense;
import com.blendwerk.pet.domain.ExpenseCategory;

@DisplayName("PET::Domain::Expense class")
public class ExpenseTests {
    @Test 
    @DisplayName("ctor :: use valid parameters :: valid instance ")
    public void ctor_validParameters_validInstance() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        // act 
        Expense subject = new Expense(budget);
        // assert
        Assertions.assertNotNull(subject.id());
        Assertions.assertFalse(subject.id().isEmpty());
        Assertions.assertEquals(budget, subject.budget());
        Assertions.assertEquals(Money.zero(budget.currency()), subject.amount());
        Assertions.assertEquals(ExpenseCategory.OTHER, subject.category());
    }

    @Test 
    @DisplayName("ctor :: use null parameter :: throws exception")
    public void ctor_nullParameter_throwsException() {
        // arrange 
        Budget budget = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Expense subject = new Expense(budget);
        });
    }

    @Test 
    @DisplayName("update :: use valid parameters :: updates properties")
    public void update_validParameters_updatesProperties() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new Expense(budget);
        var amount = Money.of("123.456", budget.currency());
        var category = ExpenseCategory.TAXES;
        // act 
        subject.update(amount, category);
        // assert
        Assertions.assertEquals(amount, subject.amount());
        Assertions.assertEquals(category, subject.category());
    }

    @Test 
    @DisplayName("update :: use null amount parameter :: throws exception")
    public void update_nullAmountParameter_throwsException() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new Expense(budget);
        Money amount = null;
        var category = ExpenseCategory.TAXES;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.update(amount, category);
        });
    }

    @Test 
    @DisplayName("update :: use null category parameter :: throws exception")
    public void update_nullCategoryParameter_throwsException() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new Expense(budget);
        var amount = Money.of("123.456", budget.currency());
        ExpenseCategory category = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.update(amount, category);
        });
    }

    @Test 
    @DisplayName("signedAmount :: called :: returns amount with negative sign")
    public void signedAmount_called_returnsAmountWithPositiveSign() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new Expense(budget);        
        var amount = Money.of("123.456", budget.currency());
        subject.update(amount, ExpenseCategory.TAXES);
        var expected = Money.of("-123.456", budget.currency());
        // act 
        var actual = subject.signedAmount();
        // assert
        Assertions.assertEquals(expected, actual);
    }
}
