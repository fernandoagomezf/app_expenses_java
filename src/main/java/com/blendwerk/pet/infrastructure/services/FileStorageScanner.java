package com.blendwerk.pet.infrastructure.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileStorageScanner implements StorageScanner {
    private final List<UUID> _sources;
    
    public FileStorageScanner() {
        _sources = new ArrayList<>();
    }
    
    private Path getPath() {
        var fileName = new StringBuilder()
            .append(System.getProperty("user.home"))
            .append("\\Blendwerk\\PET\\")
            .toString();
        return Path.of(fileName);
    }

    public Iterable<UUID> sources() {
        return _sources;
    }

    public int count() {
        return _sources.size();
    }

    public void scan() throws StorageException {        
        var path = getPath();
    
        try {
            _sources.clear();
            for (var file : Files.list(path).toList()){
                if (Files.isRegularFile(file) && file.toString().endsWith(".dat")) {
                    var fileName = file.getFileName().toString();
                    var sourceId = fileName.substring(0, fileName.length() - 4);
                    try {
                        var uid = UUID.fromString(sourceId);
                        _sources.add(uid);
                    } catch (IllegalArgumentException e) {
                        /* the file is not a budget file, so just ignore it. */
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            throw new StorageException("Failed to reindex storage", e);
        } 
    }
}
