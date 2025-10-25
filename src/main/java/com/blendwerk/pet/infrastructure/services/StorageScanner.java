package com.blendwerk.pet.infrastructure.services;

import java.util.UUID;

public interface StorageScanner {
    void scan() throws StorageException;
    Iterable<UUID> sources();
    int count();
}
