package com.blendwerk.pet.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import com.blendwerk.pet.domain.Budget;
import com.blendwerk.pet.domain.Currency;
import com.blendwerk.pet.domain.Identifier;
import com.blendwerk.pet.domain.Money;
import com.blendwerk.pet.domain.IncomeCategory;
import com.blendwerk.pet.domain.ExpenseCategory;

@DisplayName("PET::Infrastructure::FileBudgetRepository class")
public class FileBudgetRepositoryTests {
    private Cache _mockCache;
    private Storage _mockStorage;
    private StorageSummary _mockSummary;

    @BeforeEach
    public void setUp() {
        _mockCache = new MemoryCache();
        _mockStorage = new MockStorage();
        _mockSummary = new MockStorageSummary();
    }

    @Test
    @DisplayName("ctor :: use valid parameters :: valid instance")
    public void ctor_validParameters_validInstance() {
        // arrange & act
        var subject = new FileBudgetRepository(_mockCache, _mockStorage, _mockSummary);
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
            new FileBudgetRepository(nullCache, _mockStorage, _mockSummary);
        });
    }

    @Test
    @DisplayName("ctor :: use null storage :: throws exception")
    public void ctor_nullStorage_throwsException() {
        // arrange
        Storage nullStorage = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FileBudgetRepository(_mockCache, nullStorage, _mockSummary);
        });
    }

    @Test
    @DisplayName("ctor :: use null summary :: throws exception")
    public void ctor_nullSummary_throwsException() {
        // arrange
        StorageSummary nullSummary = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FileBudgetRepository(_mockCache, _mockStorage, nullSummary);
        });
    }

    @Test
    @DisplayName("get :: null id :: throws exception")
    public void get_nullId_throwsException() {
        // arrange
        var subject = new FileBudgetRepository(_mockCache, _mockStorage, _mockSummary);
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
        var expectedBudget = new Budget("Test Budget", Currency.MXN);
        _mockCache.put(expectedBudget);
        var subject = new FileBudgetRepository(_mockCache, _mockStorage, _mockSummary);
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
        var mockStorage = new MockStorageWithTransactionFilter();
        mockStorage.set("Budget", "ID", budgetId.value().toString());
        mockStorage.set("Budget", "Name", "Test Budget");
        mockStorage.set("Budget", "Currency", "MXN");
        mockStorage.set("Budget", "TransactionCount", "0");
        mockStorage.set("Budget", "Balance", "0.00");
        var subject = new FileBudgetRepository(_mockCache, mockStorage, _mockSummary);
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
        var subject = new FileBudgetRepository(_mockCache, failingStorage, _mockSummary);
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
        var subject = new FileBudgetRepository(_mockCache, mockStorage, _mockSummary);
        // act
        subject.save(budget);
        // assert
        Assertions.assertTrue(mockStorage.saveCalled());
        Assertions.assertTrue(_mockCache.contains(budget.id()));
        Assertions.assertEquals(budget.id().value().toString(), mockStorage.get("Budget", "ID"));
        Assertions.assertEquals("Test Budget", mockStorage.get("Budget", "Name"));
        Assertions.assertEquals("MXN", mockStorage.get("Budget", "Currency"));
    }

    @Test
    @DisplayName("save :: null budget :: throws exception")
    public void save_nullBudget_throwsException() {
        // arrange
        var subject = new FileBudgetRepository(_mockCache, _mockStorage, _mockSummary);
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
        var income = budget.credit(Money.of("1000.00", Currency.MXN), IncomeCategory.SALARY);
        var expense = budget.debit(Money.of("500.00", Currency.MXN), ExpenseCategory.RENT_MORTGAGE);
        var mockStorage = new MockStorage();
        var subject = new FileBudgetRepository(_mockCache, mockStorage, _mockSummary);
        // act
        subject.save(budget);
        // assert
        var incomeSign = mockStorage.get(income.id().value().toString(), "Sign");
        var expenseSign = mockStorage.get(expense.id().value().toString(), "Sign");
        Assertions.assertTrue(mockStorage.saveCalled());
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
        var subject = new FileBudgetRepository(cache, mockStorage, _mockSummary);
        // act
        subject.delete(budget.id());
        // assert
        Assertions.assertTrue(mockStorage.deleteCalled());
        Assertions.assertNull(cache.get(budget.id()));
    }

    @Test
    @DisplayName("delete :: null id :: throws exception")
    public void delete_nullId_throwsException() {
        // arrange
        var subject = new FileBudgetRepository(_mockCache, _mockStorage, _mockSummary);
        Identifier nullId = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.delete(nullId);
        });
    }

    @Test
    @DisplayName("get :: budget not in cache :: calls storage load")
    public void get_budgetNotInCache_callsStorageLoad() throws RepositoryException {
        // arrange
        var budgetId = Identifier.create();
        var mockStorage = new MockStorageWithTransactionFilter();
        mockStorage.set("Budget", "ID", budgetId.value().toString());
        mockStorage.set("Budget", "Name", "Test Budget");
        mockStorage.set("Budget", "Currency", "MXN");
        mockStorage.set("Budget", "TransactionCount", "0");
        mockStorage.set("Budget", "Balance", "0.00");
        var subject = new FileBudgetRepository(_mockCache, mockStorage, _mockSummary);
        
        // act
        subject.get(budgetId);
        
        // assert
        Assertions.assertTrue(mockStorage.loadCalled(), "Storage load() method should be called when budget is not in cache");
    }

    private class MockStorage implements Storage {
        protected Map<String, Map<String, String>> _data = new HashMap<>();
        private boolean _loadCalled = false;
        private boolean _saveCalled = false;
        private boolean _deleteCalled = false;

        public boolean loadCalled() {
            return _loadCalled;
        }

        public boolean saveCalled() {
            return _saveCalled;
        }

        public boolean deleteCalled() {
            return _deleteCalled;
        }

        public void clear() {
            _data.clear();
        }

        public Iterable<String> getSections() {
            return _data.keySet();
        }

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

        public void set(String sectionName, String key, String value) {
            set(sectionName);
            _data.get(sectionName).put(key, value);
        }

        public void load(UUID sourceId) throws StorageException {
            _loadCalled = true;
        }

        public void save(UUID sourceId) throws StorageException {
            _saveCalled = true;
        }

        public boolean delete(UUID sourceId) {
            _deleteCalled = true;
            return true;
        }
    }

    private class MockStorageWithTransactionFilter extends MockStorage {
        private Map<String, java.util.Map<String, String>> _backupData = new HashMap<>();
        
        @Override
        public void clear() {
            // Back up the data before clearing
            _backupData.clear();
            for (var entry : _data.entrySet()) {
                _backupData.put(entry.getKey(), new HashMap<>(entry.getValue()));
            }
            super.clear();
        }
        
        @Override
        public void load(UUID sourceId) throws StorageException {
            super.load(sourceId);
            // Restore the backed up data
            _data.clear();
            for (var entry : _backupData.entrySet()) {
                _data.put(entry.getKey(), new HashMap<>(entry.getValue()));
            }
        }
        
        @Override
        public Iterable<String> getSections() {
            var sections = new ArrayList<String>();
            for (var section : super.getSections()) {
                // Only include sections that are transaction IDs (not "Budget" or "Header")
                if (!section.equals("Budget") && !section.equals("Header")) {
                    sections.add(section);
                }
            }
            return sections;
        }
    }

    private class FailingStorage implements Storage {
        public void clear() {
        }

        public Iterable<String> getSections() {
            return new ArrayList<>();
        }

        public String get(String sectionName, String key) {
            return "";
        }

        public void set(String sectionName, String key, String value) {
        }

        public void load(UUID sourceId) throws StorageException {
            throw new StorageException("Simulated storage failure");
        }

        public void save(UUID sourceId) throws StorageException {
            throw new StorageException("Simulated storage failure");
        }

        public boolean delete(UUID sourceId) {
            return false;
        }
    }

    private class MockStorageSummary implements StorageSummary {
        private boolean _loadCalled = false;

        @SuppressWarnings("unused")
        public boolean loadCalled() {
            return _loadCalled;
        }

        public void track(String sectionName, String key) {
            // Mock implementation - do nothing
        }

        public void load() throws StorageException {
            _loadCalled = true;
            // Mock implementation - do nothing
        }

        public String get(UUID sourceId, String key) {
            // Mock implementation - return empty string
            return "";
        }

        public Iterable<UUID> getSources() {
            // Mock implementation - return empty list
            return new ArrayList<>();
        }
    }
}
