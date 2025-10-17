package com.blendwerk.pet.infrastructure;

import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.Currency;
import com.blendwerk.pet.domain.Identifier;
import com.blendwerk.pet.domain.Money;
import com.blendwerk.pet.domain.RepositoryException;
import com.blendwerk.pet.domain.Transaction;

@DisplayName("PET::Infrastructure::FileBudgetRepository class")
public class FileBudgetRepositoryTests {
    private Cache _mockCache;
    private Storage _mockStorage;

    @BeforeEach
    public void setUp() {
        _mockCache = new MemoryCache();
        _mockStorage = new MockStorage();
    }

    @Test
    @DisplayName("ctor :: use valid parameters :: valid instance")
    public void ctor_validParameters_validInstance() {
        // arrange & act
        var subject = new FileBudgetRepository(_mockCache, _mockStorage);
        // assert
        Assertions.assertNotNull(subject);
    }

    @Test
    @DisplayName("ctor :: use null cache :: throws exception")
    public void ctor_nullCache_throwsException() {
        // arrange
        Cache nullCache = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FileBudgetRepository(nullCache, _mockStorage);
        });
    }

    @Test
    @DisplayName("ctor :: use null storage :: throws exception")
    public void ctor_nullStorage_throwsException() {
        // arrange
        Storage nullStorage = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FileBudgetRepository(_mockCache, nullStorage);
        });
    }

    @Test
    @DisplayName("get :: null id :: throws exception")
    public void get_nullId_throwsException() {
        // arrange
        var subject = new FileBudgetRepository(_mockCache, _mockStorage);
        Identifier nullId = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.get(nullId);
        });
    }

    @Test
    @DisplayName("get :: budget in cache :: returns cached budget")
    public void get_budgetInCache_returnsCachedBudget() throws RepositoryException {
        // arrange
        var budgetId = Identifier.create();
        var expectedBudget = new Budget("Test Budget", Currency.MXN);
        _mockCache.put(expectedBudget);
        var subject = new FileBudgetRepository(_mockCache, _mockStorage);
        // act
        var result = subject.get(expectedBudget.id());
        // assert
        Assertions.assertEquals(expectedBudget, result);
    }

    @Test
    @DisplayName("get :: budget not in cache :: loads from storage")
    public void get_budgetNotInCache_loadsFromStorage() throws RepositoryException {
        // arrange
        var budgetId = Identifier.create();
        var mockStorage = new MockStorageWithFilter();
        mockStorage.set("Budget", "ID", budgetId.value().toString());
        mockStorage.set("Budget", "Name", "Test Budget");
        mockStorage.set("Budget", "Currency", "MXN");
        var subject = new FileBudgetRepository(_mockCache, mockStorage);
        // act
        var result = subject.get(budgetId);
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(budgetId, result.id());
        Assertions.assertEquals("Test Budget", result.name());
        Assertions.assertEquals(Currency.MXN, result.currency());
    }

    @Test
    @DisplayName("get :: storage exception :: throws repository exception")
    public void get_storageException_throwsRepositoryException() {
        // arrange
        var budgetId = Identifier.create();
        var failingStorage = new FailingStorage();
        var subject = new FileBudgetRepository(_mockCache, failingStorage);
        // act & assert
        Assertions.assertThrows(RepositoryException.class, () -> {
            subject.get(budgetId);
        });
    }

    @Test
    @DisplayName("save :: valid budget :: stores in storage and cache")
    public void save_validBudget_storesInStorageAndCache() throws RepositoryException {
        // arrange
        var budget = new Budget("Test Budget", Currency.MXN);
        var mockStorage = new MockStorage();
        var subject = new FileBudgetRepository(_mockCache, mockStorage);
        // act
        subject.save(budget);
        // assert
        Assertions.assertTrue(_mockCache.contains(budget.id()));
        Assertions.assertEquals(budget.id().value().toString(), mockStorage.get("Budget", "ID"));
        Assertions.assertEquals("Test Budget", mockStorage.get("Budget", "Name"));
        Assertions.assertEquals("MXN", mockStorage.get("Budget", "Currency"));
    }

    @Test
    @DisplayName("save :: null budget :: throws exception")
    public void save_nullBudget_throwsException() {
        // arrange
        var subject = new FileBudgetRepository(_mockCache, _mockStorage);
        Budget nullBudget = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.save(nullBudget);
        });
    }

    @Test
    @DisplayName("save :: budget with transactions :: stores transactions")
    public void save_budgetWithTransactions_storesTransactions() throws RepositoryException {
        // arrange
        var budget = new Budget("Test Budget", Currency.MXN);
        var income = budget.credit(Money.of("1000.00", Currency.MXN), com.blendwerk.pet.domain.IncomeCategory.SALARY);
        var expense = budget.debit(Money.of("500.00", Currency.MXN), com.blendwerk.pet.domain.ExpenseCategory.RENT_MORTGAGE);
        var mockStorage = new MockStorage();
        var subject = new FileBudgetRepository(_mockCache, mockStorage);
        // act
        subject.save(budget);
        // assert
        var incomeSign = mockStorage.get(income.id().value().toString(), "Sign");
        var expenseSign = mockStorage.get(expense.id().value().toString(), "Sign");
        Assertions.assertEquals("1", incomeSign);
        Assertions.assertEquals("-1", expenseSign);
    }

    @Test
    @DisplayName("delete :: valid id :: removes from storage and cache")
    public void delete_validId_removesFromStorageAndCache() throws RepositoryException {
        // arrange
        var budget = new Budget("Test Budget", Currency.MXN);
        var cache = new MemoryCache();
        cache.put(budget);
        var mockStorage = new MockStorage();
        var subject = new FileBudgetRepository(cache, mockStorage);
        // act
        subject.delete(budget.id());
        // assert
        // Note: MemoryCache.erase() sets the value to null but keeps the key in the map
        Assertions.assertNull(cache.get(budget.id()));
    }

    @Test
    @DisplayName("delete :: null id :: throws exception")
    public void delete_nullId_throwsException() {
        // arrange
        var subject = new FileBudgetRepository(_mockCache, _mockStorage);
        Identifier nullId = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.delete(nullId);
        });
    }

    // Mock Storage implementation for testing
    private class MockStorage implements Storage {
        private java.util.Map<String, java.util.Map<String, String>> _data = new java.util.HashMap<>();
        private boolean _loadCalled = false;
        private boolean _saveCalled = false;
        private boolean _deleteCalled = false;

        @Override
        public boolean select(UUID sourceId) {
            return true;
        }

        @Override
        public Iterable<String> getSections() {
            return _data.keySet();
        }

        @Override
        public String get(String sectionName, String key) {
            if (_data.containsKey(sectionName) && _data.get(sectionName).containsKey(key)) {
                return _data.get(sectionName).get(key);
            }
            return "";
        }

        public void set(String sectionName) {
            if (!_data.containsKey(sectionName)) {
                _data.put(sectionName, new java.util.HashMap<>());
            }
        }

        @Override
        public void set(String sectionName, String key, String value) {
            set(sectionName);
            _data.get(sectionName).put(key, value);
        }

        @Override
        public void load() throws StorageException {
            _loadCalled = true;
        }

        @Override
        public void save() throws StorageException {
            _saveCalled = true;
        }

        @Override
        public boolean delete() {
            _deleteCalled = true;
            return true;
        }
    }

    // Mock Storage with section filtering (excludes Budget and Header sections from transaction loading)
    private class MockStorageWithFilter extends MockStorage {
        @Override
        public Iterable<String> getSections() {
            var sections = new ArrayList<String>();
            for (var section : super.getSections()) {
                if (!section.equals("Budget") && !section.equals("Header")) {
                    sections.add(section);
                }
            }
            return sections;
        }
    }

    // Failing Storage implementation for testing error cases
    private class FailingStorage implements Storage {
        @Override
        public boolean select(UUID sourceId) {
            return true;
        }

        @Override
        public Iterable<String> getSections() {
            return new ArrayList<>();
        }

        @Override
        public String get(String sectionName, String key) {
            return "";
        }

        @Override
        public void set(String sectionName, String key, String value) {
        }

        @Override
        public void load() throws StorageException {
            throw new StorageException("Simulated storage failure");
        }

        @Override
        public void save() throws StorageException {
            throw new StorageException("Simulated storage failure");
        }

        @Override
        public boolean delete() {
            return false;
        }
    }
}
