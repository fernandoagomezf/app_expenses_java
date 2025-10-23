package com.blendwerk.pet.infrastructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.blendwerk.pet.infrastructure.services.FileStorage;
import com.blendwerk.pet.infrastructure.services.StorageException;

import org.junit.jupiter.api.Assertions;

@DisplayName("PET::Infrastructure::FileStorage class")
public class FileStorageTests {
    private UUID _testSourceId;

    @BeforeEach
    public void setUp() throws IOException {
        _testSourceId = UUID.randomUUID();
    }

    @Test
    @DisplayName("ctor :: use valid parameters :: valid instance")
    public void ctor_validParameters_validInstance() {
        // arrange & act
        var subject = new FileStorage();
        // assert
        Assertions.assertNotNull(subject);
        Assertions.assertNotNull(subject.getSections());
    }

    @Test 
    @DisplayName("clear :: after adding data :: empties storage")
    public void clear_afterAddingData_emptiesStorage() {
        // arrange
        var subject = new FileStorage();
        subject.set("Section1", "Key1", "Value1");
        subject.set("Section2", "Key2", "Value2");
        // act
        subject.clear();
        // assert
        var sections = subject.getSections();
        Assertions.assertFalse(sections.iterator().hasNext());
    }

    @Test
    @DisplayName("get :: section with key :: returns value")
    public void get_sectionWithKey_returnsValue() {
        // arrange
        var subject = new FileStorage();
        subject.set("TestSection", "TestKey", "TestValue");
        // act
        var result = subject.get("TestSection", "TestKey");
        // assert
        Assertions.assertEquals("TestValue", result);
    }

    @Test
    @DisplayName("get :: section without key :: returns default value")
    public void get_sectionWithoutKey_returnsDefaultValue() {
        // arrange
        var subject = new FileStorage();
        subject.set("TestSection", "OtherKey", "OtherValue");
        // act
        var result = subject.get("TestSection", "NonExistentKey", "DefaultValue");
        // assert
        Assertions.assertEquals("DefaultValue", result);
    }

    @Test
    @DisplayName("get :: non-existing section :: returns default value")
    public void get_nonExistingSection_returnsDefaultValue() {
        // arrange
        var subject = new FileStorage();
        // act
        var result = subject.get("NonExistentSection", "SomeKey", "DefaultValue");
        // assert
        Assertions.assertEquals("DefaultValue", result);
    }

    @Test
    @DisplayName("get :: null arguments :: throws exception")
    public void get_nullArguments_throwsException() {
        // arrange
        var subject = new FileStorage();
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.get(null, "key", "default");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.get("section", null, "default");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.get("section", "key", null);
        });
    }

    @Test
    @DisplayName("set :: section only :: creates section")
    public void set_sectionOnly_createsSection() {
        // arrange
        var subject = new FileStorage();
        // act
        subject.set("NewSection");
        // assert
        var sections = subject.getSections();
        Assertions.assertTrue(((Iterable<String>)sections).iterator().hasNext());
    }

    @Test
    @DisplayName("set :: section with null :: throws exception")
    public void set_sectionWithNull_throwsException() {
        // arrange
        var subject = new FileStorage();
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.set(null);
        });
    }

    @Test
    @DisplayName("set :: section key value :: stores value")
    public void set_sectionKeyValue_storesValue() {
        // arrange
        var subject = new FileStorage();
        // act
        subject.set("Section1", "Key1", "Value1");
        // assert
        var result = subject.get("Section1", "Key1");
        Assertions.assertEquals("Value1", result);
    }

    @Test
    @DisplayName("set :: null arguments :: throws exception")
    public void set_nullArguments_throwsException() {
        // arrange
        var subject = new FileStorage();
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.set(null, "key", "value");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.set("section", null, "value");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.set("section", "key", null);
        });
    }

    @Test
    @DisplayName("save :: valid data :: writes file")
    public void save_validData_writesFile() throws StorageException, IOException {
        // arrange
        var targetDir = Path.of(System.getProperty("user.home"), "Blendwerk", "PET");
        Files.createDirectories(targetDir);
        var budgetFile = targetDir.resolve(_testSourceId + ".dat");
        var subject = new FileStorage();
        subject.set("Section1", "Key1", "Value1");
        subject.set("Section2", "Key2", "Value2");
        // act
        subject.save(_testSourceId);
        // assert
        var content = Files.readString(budgetFile, StandardCharsets.UTF_8);
        Assertions.assertTrue(content.contains("[Section1]"));
        Assertions.assertTrue(content.contains("Key1=Value1"));
        Assertions.assertTrue(content.contains("[Section2]"));
        Assertions.assertTrue(content.contains("Key2=Value2"));
        // cleanup
        Files.deleteIfExists(budgetFile);
    }

    @Test
    @DisplayName("save :: null source ID :: throws exception")
    public void save_nullSourceId_throwsException() {
        // arrange
        UUID sourceId = null;
        var subject = new FileStorage();
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.save(sourceId);
        });
    }

    @Test
    @DisplayName("delete :: existing file :: returns true")
    public void delete_existingFile_returnsTrue() throws IOException {
        // arrange
        var targetDir = Path.of(System.getProperty("user.home"), "Blendwerk", "PET");
        Files.createDirectories(targetDir);
        var budgetFile = targetDir.resolve(_testSourceId + ".dat");
        Files.writeString(budgetFile, "test content", StandardCharsets.UTF_8);
        var subject = new FileStorage();
        // act
        var result = subject.delete(_testSourceId);
        // assert
        Assertions.assertTrue(result);
        Assertions.assertFalse(Files.exists(budgetFile));
    }

    @Test
    @DisplayName("delete :: null Source ID :: throws exception")
    public void delete_nullSourceId_throwsException() {
        // arrange
        var subject = new FileStorage();
        UUID sourceId = null;
        // act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            subject.delete(sourceId);
        });
    }

    @Test
    @DisplayName("getSections :: after adding sections :: returns all sections")
    public void getSections_afterAddingSections_returnsAllSections() {
        // arrange
        var subject = new FileStorage();
        subject.set("Section1");
        subject.set("Section2");
        subject.set("Section3");
        // act
        var sections = subject.getSections();
        // assert
        int count = 0;
        for (var section : sections) {
            Assertions.assertTrue(section != null);
            count++;
        }
        Assertions.assertEquals(3, count);
    }
}
