package com.blendwerk.pet.infrastructure;

import java.util.UUID;

public interface Storage {
    boolean select(UUID sourceId);
    boolean delete();
    Iterable<String> getSections();
    String get(String sectionName, String key);
    void set(String sectionName, String key, String value);
    void load() throws StorageException;
    void save() throws StorageException;
}
