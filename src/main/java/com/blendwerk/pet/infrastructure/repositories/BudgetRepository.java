package com.blendwerk.pet.infrastructure.repositories;

import com.blendwerk.pet.domain.budgeting.Budget;
import com.blendwerk.pet.domain.budgeting.BudgetView;
import com.blendwerk.pet.domain.core.Identifier;

public interface BudgetRepository {
    Iterable<BudgetView> getViews() throws RepositoryException;
    Budget get(Identifier id) throws RepositoryException;
    void save(Budget budget) throws RepositoryException;
    void delete(Identifier budgetId) throws RepositoryException;
}
