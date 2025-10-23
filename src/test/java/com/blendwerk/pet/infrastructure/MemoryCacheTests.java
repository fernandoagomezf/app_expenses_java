package com.blendwerk.pet.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import com.blendwerk.pet.domain.core.Entity;
import com.blendwerk.pet.domain.core.Identifier;
import com.blendwerk.pet.infrastructure.services.MemoryCache;

@DisplayName("PET::Infrastructure::MemoryCache class")
public class MemoryCacheTests {
    @Test
    @DisplayName("ctor :: default parameters :: valid instance")
    public void ctor_defaultParameters_validInstance() {
        // arrange & act
        var cache = new MemoryCache();
        // assert
        Assertions.assertNotNull(cache);
        Assertions.assertEquals(0, cache.size());
    }

    @Test 
    @DisplayName("ctor :: non-zero max size :: valid instance")
    public void ctor_nonZeroMaxSize_validInstance() {
        // arrange & act
        var cache = new MemoryCache(5);
        // assert
        Assertions.assertNotNull(cache);
        Assertions.assertEquals(0, cache.size());
    }

    @Test 
    @DisplayName("ctor :: negative max size :: throws exception")
    public void ctor_negativeMaxSize_throwsException() {
        // arrange & act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new MemoryCache(-1);
        });
    }

    @Test 
    @DisplayName("ctor :: zero max size :: throws exception")
    public void ctor_zeroMaxSize_throwsException() {
        // arrange & act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new MemoryCache(0);
        });
    }

    @Test 
    @DisplayName("put :: null entity :: throws exception")
    public void put_nullEntity_throwsException() {
        // arrange
        var cache = new MemoryCache();
        Entity entity = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cache.put(entity);
        });
    }

    @Test 
    @DisplayName("put :: valid entity :: stores entity")
    public void put_validEntity_storesEntity() {
        // arrange
        var cache = new MemoryCache();
        var entity = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() {
                return _id;
            }
            public void ensure() {}
        };
        // act
        cache.put(entity);
        // assert
        Assertions.assertEquals(1, cache.size());
        Assertions.assertTrue(cache.contains(entity.id()));
        Assertions.assertEquals(entity, cache.get(entity.id()));
    }

    @Test
    @DisplayName("put :: exceeds max size :: evicts oldest entity")
    public void put_exceedsMaxSize_evictsOldestEntity() {
        // arrange
        var cache = new MemoryCache(2);
        var entity1 = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() {
                return _id;
            }
            public void ensure() {}
        };
        var entity2 = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() {
                return _id;
            }
            public void ensure() {}
        };
        var entity3 = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() {
                return _id;
            }
            public void ensure() {}
        };
        // act
        cache.put(entity1);
        cache.put(entity2);
        cache.put(entity3); // This should evict entity1
        // assert
        Assertions.assertEquals(2, cache.size());
        Assertions.assertFalse(cache.contains(entity1.id()));
        Assertions.assertTrue(cache.contains(entity2.id()));
        Assertions.assertTrue(cache.contains(entity3.id()));
    }

    @Test
    @DisplayName("remove :: existing entity id :: removes entity")
    public void remove_existingEntityId_removesEntity() {
        // arrange
        var cache = new MemoryCache();
        var entity = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() { return _id; }
            public void ensure() {}
        };
        cache.put(entity);
        // act
        cache.remove(entity.id());
        // assert
        Assertions.assertEquals(0, cache.size());
        Assertions.assertFalse(cache.contains(entity.id()));
    }

    @Test 
    @DisplayName("remove :: null id :: throws exception")
    public void remove_nullId_throwsException() {
        // arrange
        var cache = new MemoryCache();
        Identifier id = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cache.remove(id);
        });
    }

    @Test 
    @DisplayName("clear :: non-empty cache :: empties cache")
    public void clear_nonEmptyCache_emptiesCache() {
        // arrange
        var cache = new MemoryCache();
        var entity = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() { return _id; }
            public void ensure() {}
        };
        cache.put(entity);
        // act
        cache.clear();
        // assert
        Assertions.assertEquals(0, cache.size());
    }

    @Test 
    @DisplayName("contains :: null id :: throws exception")
    public void contains_nullId_throwsException() {
        // arrange
        var cache = new MemoryCache();
        Identifier id = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cache.contains(id);
        });
    }

    @Test 
    @DisplayName("contains :: valid id :: returns correct result")
    public void contains_validId_returnsCorrectResult() {
        // arrange
        var cache = new MemoryCache();
        var entity = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() { return _id; }
            public void ensure() {}
        };
        cache.put(entity);
        // act
        var result1 = cache.contains(entity.id());
        var result2 = cache.contains(Identifier.create());
        // assert
        Assertions.assertTrue(result1);
        Assertions.assertFalse(result2);
    }

    @Test 
    @DisplayName("get :: valid id :: returns correct entity")
    public void get_validId_returnsCorrectEntity() {
        // arrange
        var cache = new MemoryCache();
        var entity = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() { return _id; }
            public void ensure() {}
        };
        cache.put(entity);
        // act
        var retrievedEntity = cache.get(entity.id());
        var missingEntity = cache.get(Identifier.create());
        // assert
        Assertions.assertEquals(entity, retrievedEntity);
        Assertions.assertNull(missingEntity);
    }

    @Test 
    @DisplayName("get :: null id :: throws exception")
    public void get_nullId_throwsException() {
        // arrange
        var cache = new MemoryCache();
        Identifier id = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cache.get(id);
        });
    }

    @Test 
    @DisplayName("size :: after multiple operations :: returns correct size")
    public void size_afterMultipleOperations_returnsCorrectSize() {
        // arrange
        var cache = new MemoryCache(3);
        var entity1 = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() { return _id; }
            public void ensure() {}
        };
        var entity2 = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() { return _id; }
            public void ensure() {}
        };
        var entity3 = new Entity() {
            private final Identifier _id = Identifier.create();
            public Identifier id() { return _id; }
            public void ensure() {}
        };
        // act
        cache.put(entity1);
        var size1 = cache.size();
        cache.put(entity2);
        var size2 = cache.size();
        cache.put(entity3);
        var size3 = cache.size();
        cache.remove(entity2.id());
        var size4 = cache.size();
        cache.clear();
        var size5 = cache.size();
        // assert
        Assertions.assertEquals(1, size1);
        Assertions.assertEquals(2, size2);
        Assertions.assertEquals(3, size3);
        Assertions.assertEquals(2, size4);
        Assertions.assertEquals(0, size5);
    }
}
