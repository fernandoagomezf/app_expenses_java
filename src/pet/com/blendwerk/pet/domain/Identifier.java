package com.blendwerk.pet.domain;

import java.lang.IllegalArgumentException;
import java.util.UUID;

public record Identifier(UUID value) {
    public Identifier {
        ensure();
    }

    public void ensure() {
        if (value == null) {
            throw new IllegalArgumentException("Identifier value cannot be null");
        }
    }

    public boolean isEmpty() {
        return value.equals(empty());
    }

    public final String toString() {
        return value().toString();
    }

    public static Identifier empty() {
        return new Identifier(new UUID(0l, 0l));
    }

    public static Identifier create() {
        return new Identifier(UUID.randomUUID());
    }
}