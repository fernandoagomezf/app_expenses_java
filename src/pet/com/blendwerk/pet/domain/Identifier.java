package com.blendwerk.pet.domain;

import java.lang.IllegalArgumentException;
import java.util.UUID;

public record Identifier(UUID value) {
    public Identifier {
        if (value == null) {
            throw new IllegalArgumentException("Identifier value cannot be null");
        }
    }

    public boolean isEmpty() {
        return value.getLeastSignificantBits() == 0L 
            && value.getMostSignificantBits() == 0L;
    }

    public final String toString() {
        return value().toString();
    }

    public static Identifier empty() {
        return new Identifier(new UUID(0L, 0L));
    }

    public static Identifier create() {
        return new Identifier(UUID.randomUUID());
    }
}