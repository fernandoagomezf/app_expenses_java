package com.blendwerk.pet.infrastructure.services;

import com.blendwerk.pet.domain.core.Entity;
import com.blendwerk.pet.domain.core.Identifier;

public interface Cache {
    int size();
    void clear();
    boolean contains(Identifier id);
    Entity get(Identifier id);
    void put(Entity entity);
    void erase(Identifier id);
    void remove(Identifier id);
}