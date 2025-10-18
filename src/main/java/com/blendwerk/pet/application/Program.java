package com.blendwerk.pet.application;

import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.Currency;
import com.blendwerk.pet.domain.RepositoryException;
import com.blendwerk.pet.infrastructure.FileBudgetRepository;
import com.blendwerk.pet.infrastructure.FileStorage;
import com.blendwerk.pet.infrastructure.MemoryCache;

public class Program {
    public static void main(String[] args) {
        var budget = new Budget("Minino", Currency.MXN);
        var cache = new MemoryCache();
        var storage = new FileStorage();
        var repository = new FileBudgetRepository(cache, storage);

        try {
            repository.save(budget);
        } catch (RepositoryException ex) {
            ex.printStackTrace();
        }
    }
}
