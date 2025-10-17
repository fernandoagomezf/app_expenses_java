package com.blendwerk.pet.infrastructure;

import java.util.LinkedHashMap;
import com.blendwerk.pet.domain.Entity;
import com.blendwerk.pet.domain.Identifier;

public class MemoryCache implements Cache {
    private static final int DEFAULT_MAX_SIZE = 10;

    private final LinkedHashMap<Identifier, Entity> _entities;
    private int _maxSize;

    public MemoryCache() {
        this(DEFAULT_MAX_SIZE);
    }

    public MemoryCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Maximum cache size must be greater than zero");
        }
        _maxSize = maxSize;
        _entities = new LinkedHashMap<>(_maxSize, 0.75f, true);;
    }

    public int size() {
        return _entities.size();
    }

    public void clear() {
        _entities.clear();
    }

    private void ensureNotNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }
    }

    private void evictOldestEntity() {
        if (!_entities.isEmpty()) {
            var iterator = _entities.keySet()
                                    .iterator();
            iterator.next();
            iterator.remove(); 
        }
    }

    public boolean contains(Identifier id) {
        ensureNotNull(id);
        return _entities.containsKey(id);
    }

    public Entity get(Identifier id) {
        ensureNotNull(id);
        return _entities.get(id);
    }

    public void put(Entity entity) {
        ensureNotNull(entity);
        
        if (_entities.size() >= _maxSize && !_entities.containsKey(entity.id())) {            
            evictOldestEntity();
        }
        
        _entities.put(entity.id(), entity);
    }

    public void remove(Identifier id) {
        _entities.remove(id);
    }

    public void erase(Identifier id) {
        ensureNotNull(id);
        _entities.put(id, null);
    }

    public void setMaxSize(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Maximum cache size must be greater than zero");
        }
        
        while (_entities.size() > _maxSize) {
            evictOldestEntity();
        }
    }    
}
