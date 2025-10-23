package com.blendwerk.pet.infrastructure.services;

public class StorageException extends Exception {
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(){
        super("An error occurred during a file store operation.");
    }
}
