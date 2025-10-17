package com.blendwerk.pet.infrastructure;

import java.lang.String;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileParser {
    private final Map<String, Map<String, String>> _sections;

    public FileParser() {
        _sections = new HashMap<>();
    }

    private String getProperty(String sectionName, String key) {
        return getProperty(sectionName, key, "");
    }

    private String getProperty(String sectionName, String key, String defaultValue){
        String value = defaultValue;
        if (_sections.containsKey(sectionName)) {
            var map = _sections.get(sectionName);
            if (map.containsKey(key)) {
                value = map.get(key);
            }
        }
        return value;
    }

    public String getVersion() {
        return getProperty("Header", "Version");
    }

    public String getBudgetId() {
        return getProperty("Budget", "ID");
    }

    public String getBudgetName() {
        return getProperty("Budget", "Name");
    }

    public String getBudgetCurrency() {
        return getProperty("Budget", "Currency");
    }

    public List<String> getTransactions() {
        var list = new ArrayList<String>();
        for (var key : _sections.keySet()) {
            list.add(key);
        }
        return list;
    }

    public String getTransactionAmount(String transaction) {
        return getProperty(transaction, "Amount");
    }

    public String getTransactionCurrency(String transaction) {
        return getProperty(transaction, "Currency");
    }

    public String getTransactionCategory(String transaction) {
        return getProperty(transaction, "Category");
    }
    
    public String getTransactionSign(String transaction) {
        return getProperty(transaction, "Sign");
    }

    public void parse(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }

        _sections.clear();
        try  {
            var lines = Files.readAllLines(path, StandardCharsets.UTF_8);

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
            throw ex;
        }
    }
}