package com.blendwerk.nova.domain;

import java.lang.IllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;
import com.blendwerk.pet.domain.Identifier;

@DisplayName("PET::Domain::Identifier record")
public class IdentifierTests {
    @Test
    @DisplayName("ctor :: use valid parameters :: valid instance ")
    public void ctor_validParameters_validInstance() {
        // arrange
        var uuid = UUID.randomUUID();
        // act
        var subject = new Identifier(uuid);
        // assert
        Assertions.assertEquals(uuid, subject.value());
        Assertions.assertFalse(subject.isEmpty());
    }

    @Test 
    @DisplayName("ctor :: use null parameter :: throws exception")
    public void ctor_nullParameter_throwsException() {
        // arrange 
        UUID uuid = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var subject = new Identifier(uuid);
        });
    }

    @Test 
    @DisplayName("ctor :: use empty uuid :: valid instance") 
    public void ctor_emptyParameter_validInstance() {
        // arrange 
        var uuid = new UUID(0l, 0l);
        // act 
        var subject = new Identifier(uuid);
        // assert
        Assertions.assertEquals(uuid, subject.value());
        Assertions.assertTrue(subject.isEmpty());
    }

    @Test 
    @DisplayName("equals :: same record :: is equal")
    public void equals_sameRecord_isEqual() {
        // arrange 
        var subject = Identifier.create();
        // act 
        var result = subject.equals(subject);
        // assert
        Assertions.assertTrue(result);
    }

    @Test 
    @DisplayName("equals :: same values :: are equal")
    public void equals_sameValues_areEqual() {
        // arrange 
        var uuid = UUID.randomUUID();
        var subject1 = new Identifier(uuid);
        var subject2 = new Identifier(uuid);
        // act 
        var result = subject1.equals(subject2);
        // assert
        Assertions.assertTrue(result);
        Assertions.assertEquals(subject1, subject2);
        Assertions.assertEquals(subject2, subject1);
    }

    @Test 
    @DisplayName("equals :: different values :: are not equal ")
    public void equals_differentValues_areNotEqual() {
        // arrange 
        var uuid1 = UUID.randomUUID();
        var uuid2 = UUID.randomUUID();
        var subject1 = new Identifier(uuid1);
        var subject2 = new Identifier(uuid2);
        // act 
        var result = subject1.equals(subject2);
        // assert
        Assertions.assertNotEquals(uuid1, uuid2);
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(subject1, subject2);
        Assertions.assertNotEquals(subject2, subject1);
    }

    @Test 
    @DisplayName("isEmpty :: empty identifier :: returns true") 
    public void isEmpty_emptyIdentifier_returnsTrue() {
        // arrange 
        var uuid = new UUID(0L, 0L);
        var subject = new Identifier(uuid);
        // act 
        var result = subject.isEmpty();
        // assert
        Assertions.assertTrue(result);
    }

    @Test 
    @DisplayName("isEmpty :: non-empty identifier :: returns false")
    public void isEmpty_nonEmptyIdentifier_returnsFalse() {
        // arrange 
        var uuid = UUID.randomUUID();
        var subject = new Identifier(uuid);
        // act 
        var result = subject.isEmpty();
        // assert
        Assertions.assertFalse(result);
    }

    @Test 
    @DisplayName("toString :: valid identifier :: returns string representation")
    public void toString_validIdentifier_returnsStringRepresentation() {
        // arrange 
        var uuid = UUID.randomUUID();
        var subject = new Identifier(uuid);
        // act 
        var result = subject.toString();
        // assert
        Assertions.assertEquals(uuid.toString(), result);
    }

    @Test 
    @DisplayName("create :: call method :: returns non-empty identifier")
    public void create_callMethod_returnsNonEmptyIdentifier() {
        // arrange 
        // act 
        var subject = Identifier.create();
        // assert
        Assertions.assertNotNull(subject);
        Assertions.assertFalse(subject.isEmpty());
    }

    @Test 
    @DisplayName("empty :: call method :: returns empty identifier")
    public void empty_callMethod_returnsEmptyIdentifier() {
        // arrange 
        // act 
        var subject = Identifier.empty();
        // assert
        Assertions.assertNotNull(subject);
        Assertions.assertTrue(subject.isEmpty());
    }
}