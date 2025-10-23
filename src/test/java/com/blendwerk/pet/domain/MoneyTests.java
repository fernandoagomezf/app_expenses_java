package com.blendwerk.pet.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.blendwerk.pet.domain.budgeting.Currency;
import com.blendwerk.pet.domain.budgeting.Money;

import org.junit.jupiter.api.Assertions;

@DisplayName("PET::Domain::Money record")
public class MoneyTests {
    @Test 
    @DisplayName("ctor :: use valid parameters :: valid instance ")
    public void ctor_validParameters_validInstance() {
        // arrange 
        var amount = new BigDecimal("123.45678901");
        amount = amount.setScale(Money.INTERNAL_SCALE, RoundingMode.HALF_UP);
        var currency = Currency.MXN;
        // act 
        var subject = new Money(amount, currency);
        // assert
        Assertions.assertEquals(amount, subject.value());
        Assertions.assertEquals(currency, subject.currency());
    }    
}