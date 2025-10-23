package com.blendwerk.pet.application.services;

public record ServiceResults<T>(boolean success, String message, Iterable<T> results) {
    public static <T> ServiceResults<T> success(Iterable<T> results) {
        return new ServiceResults<T>(true, "Operation completed successfully.", results);
    }

    public static <T> ServiceResults<T> failure(String message) {
        return new ServiceResults<T>(false, message, null);
    }
}
