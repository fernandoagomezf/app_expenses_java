package com.blendwerk.pet.domain;

import java.lang.IllegalArgumentException;
import java.lang.NumberFormatException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public record Money(BigDecimal value, Currency currency) implements ValueObject {
    public static final int INTERNAL_SCALE = 8;
    public static final int DISPLAY_SCALE = 2;

    public Money {
        ensure();
        if (value.scale() != INTERNAL_SCALE) {
            value = value.setScale(INTERNAL_SCALE, RoundingMode.HALF_UP);
        }
    }

    public void ensure() {
        if (currency == null) {
            throw new DomainException("A money value must have a valid currency.");
        }
        if (value == null) {
            throw new IllegalArgumentException("A money value must have a valid amount.");
        }
    }
    
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        var newAmount = value.add(other.value);
        return new Money(newAmount, currency);
    }

    public Money substract(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot subtract different currencies");
        }

        var newAmount = value.subtract(other.value);
        return new Money(newAmount, currency);
    }

    public Money scale(BigDecimal factor) {
        if (factor == null) {
            throw new IllegalArgumentException("Scale factor cannot be null");
        }

        var newAmount = value.multiply(factor);
        return new Money(newAmount, this.currency);
    }

    public Money scale(int factor) {
        var bigFactor = BigDecimal.valueOf(factor);
        return scale(bigFactor);
    }

    public String toString() {
        var displayAmount = value.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP);

        var locale = switch (currency) {
            case MXN -> Locale.forLanguageTag("es-MX");            
            default -> Locale.getDefault(); 
        };
        var formatter = NumberFormat.getCurrencyInstance(locale);
        return formatter.format(displayAmount);
    }

    public static Money zero(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        
        return new Money(BigDecimal.ZERO, currency);
    }

    public static Money of(String amount, Currency currency) {
        if (amount == null || amount.isBlank()) {
            throw new IllegalArgumentException("Amount cannot be null or blank");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }

        Money result;
        try {
            var value = new BigDecimal(amount);
            result = new Money(value, currency);
        } catch (NumberFormatException e) {
            result = zero(currency);
        }

        return result;
    }

    public static Money of(double amount, Currency currency) {
        var str = Double.toString(amount);
        return of(str, currency);
    }

    public static Money of(long amount, Currency currency) {
        var str = Long.toString(amount);
        return of(str, currency);
    }
}
