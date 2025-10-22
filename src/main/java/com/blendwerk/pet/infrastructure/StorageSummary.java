package com.blendwerk.pet.infrastructure;

import java.util.UUID;

public interface StorageSummary {
    void track(String sectionName, String key);
    void load() throws StorageException;
    String get(UUID sourceId, String key);
    Iterable<UUID> getSources();
}
