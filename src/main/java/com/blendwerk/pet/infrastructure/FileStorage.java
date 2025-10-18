package com.blendwerk.pet.infrastructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileStorage implements Storage {
    private final Map<String, Map<String, String>> _sections;
    private Path _sourcePath;

    public FileStorage() {
        _sourcePath = null;
        _sections = new HashMap<>();
    }

    public boolean select(UUID sourceId) {
        var fileName = new StringBuilder()
            .append(System.getProperty("user.home"))
            .append("\\Blendwerk\\PET\\")
            .append(sourceId)
            .append(".budget")
            .toString();
        _sourcePath = Path.of(fileName);
        var validSource = Files.exists(_sourcePath) 
                       && Files.isRegularFile(_sourcePath);
        return validSource;
    }

    public Iterable<String> getSections() {
        return _sections.keySet();
    }

    public String get(String sectionName, String key) {
        return get(sectionName, key, "");
    }

    public String get(String sectionName, String key, String defaultValue){
        if (sectionName == null || key == null || defaultValue == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        String value = defaultValue;
        if (_sections.containsKey(sectionName)) {
            var map = _sections.get(sectionName);
            if (map.containsKey(key)) {
                value = map.get(key);
            }
        }
        return value;
    }

    public void set(String sectionName) {
        if (sectionName == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (!_sections.containsKey(sectionName)) {
            _sections.put(sectionName, new HashMap<>());
        }
    }

    public void set(String sectionName, String key, String value) {
        if (sectionName == null || key == null || value == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        set(sectionName);
        var map = _sections.get(sectionName);
        map.put(key, value);
    }

    public void load() throws StorageException {
        if (_sourcePath == null) {
            throw new IllegalStateException("Source path is not selected.");
        }

        _sections.clear();
        try  {
            var lines = Files.readAllLines(_sourcePath, StandardCharsets.UTF_8);

            String currentSection = "";
            for (var line : lines) {
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; 
                }

                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line
                        .replace('[', ' ')
                        .replace(']', ' ')
                        .strip();
                    if (!_sections.containsKey(currentSection)) {
                        _sections.put(currentSection, new HashMap<String, String>());
                    }
                } else {
                    var splitted = line.split("=");
                    if (splitted.length == 2) {
                        _sections
                            .get(currentSection)
                            .put(splitted[0].strip(), splitted[1].strip());
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new StorageException("Failed to load a file from the specificed storage.", ex);
        }
    }

    public void save() throws StorageException {
        if (_sourcePath == null) {
            throw new IllegalStateException("Source path is not selected.");
        }

        try {
            var lines = new StringBuilder();
            for (var sectionEntry : _sections.entrySet()) {
                lines.append("[")
                     .append(sectionEntry.getKey())
                     .append("]")
                     .append(System.lineSeparator());
                for (var keyEntry : sectionEntry.getValue().entrySet()) {
                    lines
                        .append(keyEntry.getKey())
                        .append("=")
                        .append(keyEntry.getValue())
                        .append(System.lineSeparator());
                }
                lines.append(System.lineSeparator());
            }
            Files.writeString(_sourcePath, lines.toString(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new StorageException("Failed to save a file to the specificed storage.", ex);
        }
    }

    public boolean delete() {
        if (_sourcePath == null) {
            throw new IllegalStateException("Source path is not selected.");
        }
        
        boolean deleted;
        try {
            deleted = Files.deleteIfExists(_sourcePath);
        } catch (IOException ex) {
            ex.printStackTrace();
            deleted = false;
        }

        return deleted;
    }
}
