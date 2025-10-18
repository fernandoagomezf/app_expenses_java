package com.blendwerk.pet.domain;

import java.lang.Exception;
import java.lang.String;

public class RepositoryException extends Exception {
    public RepositoryException() {
        super("An error occurred in the file repository.");
    }
    
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}