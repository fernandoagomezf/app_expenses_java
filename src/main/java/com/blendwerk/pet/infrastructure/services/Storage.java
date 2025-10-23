package com.blendwerk.pet.infrastructure.services;

import java.util.UUID;

public interface Storage {
    Iterable<String> getSections();
    String get(String sectionName, String key);
    void set(String sectionName, String key, String value);
    void load(UUID sourceId) throws StorageException;
    void save(UUID sourceId) throws StorageException;
    boolean delete(UUID sourceId);
    void clear();
}
