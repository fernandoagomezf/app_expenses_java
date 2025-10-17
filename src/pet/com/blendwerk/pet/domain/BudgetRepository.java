package com.blendwerk.pet.domain;

public interface BudgetRepository {
    Budget get(Identifier budgetId) throws RepositoryException;
    void save(Budget budget) throws RepositoryException;
    void delete(Identifier budgetId) throws RepositoryException;
}