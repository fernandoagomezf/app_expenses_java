package com.blendwerk.pet.domain;

import java.lang.IllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

@DisplayName("PET::Domain::Transaction class")
public class TransactionTests {
    class TestTransaction extends Transaction {
        int _sign;
        TestTransaction(Budget budget) {
            super(budget);
            _sign = 1;
            ensure();
        }

        public int sign() {
            return _sign;
        }
    }

    @Test 
    @DisplayName("ctor :: use valid parameters :: valid instance ")
    public void ctor_validParameters_validInstance() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        // act 
        Transaction subject = new TestTransaction(budget);
        // assert
        Assertions.assertNotNull(subject.id());
        Assertions.assertFalse(subject.id().isEmpty());
        Assertions.assertEquals(Currency.MXN, subject.amount().currency());
        Assertions.assertEquals(Money.zero(budget.currency()), subject.amount());
    }

    @Test 
    @DisplayName("ctor :: use null parameter :: throws exception")
    public void ctor_nullParameter_throwsException() {
        // arrange 
        Budget budget = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TestTransaction(budget);
        });
    }

    @Test
    @DisplayName("update :: valid amount :: updates correctly")
    public void update_validAmount_updatesCorrectly() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        Transaction subject = new TestTransaction(budget);        
        var amount = Money.of("123.45678", budget.currency());
        // act 
        subject.update(amount);
        // assert
        Assertions.assertEquals(amount, subject.amount());
    }

    @Test 
    @DisplayName("update :: null amount :: throws exception")
    public void update_nullAmount_throwsException() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        Transaction subject = new TestTransaction(budget);        
        Money amount = null;
        // act & assert
        Assertions.assertThrows(DomainException.class, () -> {
            subject.update(amount);
        });
    }

    @Test 
    @DisplayName("signedAmount :: positive sign :: returns positive amount")
    public void signedAmount_positiveSign_returnsPositiveAmount() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new TestTransaction(budget);        
        subject._sign = 1;
        var expected = Money.of("123.456", budget.currency());
        // act
        subject.update(expected);  
        var result = subject.signedAmount();
        // assert
        Assertions.assertEquals(expected, result);
    }

    @Test 
    @DisplayName("signedAmount :: negative sign :: returns negative amount")
    public void signedAmount_negativeSign_returnsNegativeAmount() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        var subject = new TestTransaction(budget);        
        subject._sign = -1;
        var money = Money.of("123.456", budget.currency());
        var expected = Money.of("-123.456", budget.currency());
        // act         
        subject.update(money);
        var result = subject.signedAmount();
        // assert
        Assertions.assertEquals(expected, result);
    }

    @Test 
    @DisplayName("categorize :: valid category :: updates correctly")
    public void categorize_validCategory_updatesCorrectly() {
        // arrange 
        var budget = new Budget("Test", Currency.MXN);
        Transaction subject = new TestTransaction(budget);        
        var category = "Food";
        // act 
        subject.categorize(category);
        // assert
        Assertions.assertEquals(category, subject.category());
    }
}
