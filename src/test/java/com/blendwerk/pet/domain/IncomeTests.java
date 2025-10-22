package com.blendwerk.pet.domain;

import java.lang.IllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

@DisplayName("PET::Domain::Income class")
public class IncomeTests {
    @Test 
    @DisplayName("ctor :: use valid parameters :: valid instance ")
    public void ctor_validParameters_validInstance() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        // act 
        Income subject = new Income(budget);
        // assert
        Assertions.assertNotNull(subject.id());
        Assertions.assertFalse(subject.id().isEmpty());
        Assertions.assertEquals(Currency.MXN, subject.amount().currency());
        Assertions.assertEquals(Money.zero(budget.currency()), subject.amount());
        Assertions.assertEquals(Income.CATEGORY_GENERAL, subject.category());
    }

    @Test 
    @DisplayName("ctor :: use null parameter :: throws exception")
    public void ctor_nullParameter_throwsException() {
        // arrange 
        Budget budget = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Income(budget);
        });
    }

    @Test 
    @DisplayName("update :: use valid parameters :: updates properties")
    public void update_validParameters_updatesProperties() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new Income(budget);
        var amount = Money.of("123.456", budget.currency());
        // act 
        subject.update(amount);
        // assert
        Assertions.assertEquals(amount, subject.amount());
    }

    @Test 
    @DisplayName("update :: use null amount parameter :: throws exception")
    public void update_nullAmountParameter_throwsException() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new Income(budget);
        Money amount = null;
        // act & assert
        Assertions.assertThrows(DomainException.class, () -> {
            subject.update(amount);
        });
    }

    @Test 
    @DisplayName("signedAmount :: called :: returns amount with positive sign")
    public void signedAmount_called_returnsAmountWithPositiveSign() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new Income(budget);        
        var amount = Money.of("123.456", budget.currency());
        subject.update(amount);
        var expected = Money.of("123.456", budget.currency());
        // act 
        var actual = subject.signedAmount();
        // assert
        Assertions.assertEquals(expected, actual);
    }

    @Test 
    @DisplayName("categorize :: valid category :: updates correctly")
    public void categorize_validCategory_updatesCorrectly() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new Income(budget);        
        var category = IncomeCategory.SALARY;
        // act 
        subject.categorize(category);
        // assert
        Assertions.assertEquals(category.toString(), subject.category());
    }

    @Test 
    @DisplayName("categorize :: null category :: throws exception")
    public void categorize_nullCategory_throwsException() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new Income(budget);        
        IncomeCategory category = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.categorize(category);
        });
    }
}
