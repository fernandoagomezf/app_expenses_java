package com.blendwerk.pet.domain;

import java.lang.String;
import com.blendwerk.pet.domain.Identifier;

public final class Budget {
    private final Identifier _id;
    private String _name;

    public Budget(String name) {
        _id = Identifier.create();
        _name = name;
        ensure();
    }

    public void ensure() {
        if (_name == null || _name.isBlank()) {
            throw new IllegalArgumentException("Budget must have a name.");
        }
    }

    public Identifier id() {
        return _id;
    }

    public String name() {
        return _name;
    }

    public boolean equals(Object obj) {
        var result = false;
        if (this == obj) {
            result = true;
        } else if (obj != null && getClass() == obj.getClass()) {
            Budget other = (Budget) obj;
            result = _id.equals(other._id);
        }
        return result;
    }

    public final int hashCode() {
        return _id.hashCode();
    }
}