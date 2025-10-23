package com.blendwerk.pet.infrastructure.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileStorageSummary implements StorageSummary {
    private final Map<String, List<String>> _tracked;
    private final Map<UUID, Map<String, String>> _summary;
    
    public FileStorageSummary() {
        _summary = new HashMap<>();
        _tracked = new HashMap<>();
    }

    public void track(String sectionName, String key) {
        if (sectionName == null || key == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (!_tracked.containsKey(sectionName)) {
            _tracked.put(sectionName, new ArrayList<>());
        }
        var keys = _tracked.get(sectionName);
        if (!keys.contains(key)) {
            keys.add(key);
        }
    }
    
    private Path getPath() {
        var fileName = new StringBuilder()
            .append(System.getProperty("user.home"))
            .append("\\Blendwerk\\PET\\")
            .toString();
        return Path.of(fileName);
    }

    public void load() throws StorageException {        
        var path = getPath();
    
        try {
            _summary.clear();
            for (var file : Files.list(path).toList()){
                if (Files.isRegularFile(file) && file.toString().endsWith(".dat")) {
                    var storage = new FileStorage();
    
                    var fileName = file.getFileName().toString();
                    var sourceId = fileName.substring(0, fileName.length() - 4);
                    var uid = UUID.fromString(sourceId);
                    storage.load(uid);
                    
                    var entries = new HashMap<String, String>();
                    for (var trackedEntry : _tracked.entrySet()) {
                        var sectionName = trackedEntry.getKey();
                        var keys = trackedEntry.getValue();
                        for (var key : keys) {
                            var value = storage.get(sectionName, key, "");
                            entries.put(key, value);
                        }
                    }
                    _summary.put(uid, entries);
                }
            }
        } catch (Exception e) {
            throw new StorageException("Failed to reindex storage", e);
        } 
    }

    public String get(UUID sourceId, String key) {
        return get(sourceId, key, "");
    }

    public String get(UUID sourceId, String key, String defaultValue) {
        if (sourceId == null || key == null || defaultValue == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        String result = defaultValue;
        if (_summary.containsKey(sourceId)) {
            var entries = _summary.get(sourceId);
            if (entries.containsKey(key)) {
                result = entries.get(key);
            }
        }
        
        return result;
    }

    public Iterable<UUID> getSources() {
        return _summary.keySet();
    }    
}
