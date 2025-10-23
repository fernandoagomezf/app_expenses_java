package com.blendwerk.pet.infrastructure;

import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.BudgetView;
import com.blendwerk.pet.domain.Identifier;

public interface BudgetRepository {
    Iterable<BudgetView> getViews() throws RepositoryException;
    Budget get(Identifier id) throws RepositoryException;
    void save(Budget budget) throws RepositoryException;
    void delete(Identifier budgetId) throws RepositoryException;
}
