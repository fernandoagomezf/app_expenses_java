package com.blendwerk.pet.infrastructure;

import com.blendwerk.pet.domain.Entity;
import com.blendwerk.pet.domain.Identifier;

public interface Cache {
    int size();
    void clear();
    boolean contains(Identifier id);
    Entity get(Identifier id);
    void put(Entity entity);
    void erase(Identifier id);
    void remove(Identifier id);
}