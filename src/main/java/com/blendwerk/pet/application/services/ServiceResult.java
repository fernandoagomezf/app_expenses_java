package com.blendwerk.pet.application.services;

import java.util.Optional;

public record ServiceResult<T>(boolean success, String message, Optional<T> result) {
    public static <T> ServiceResult<T> success(T result) {
        return new ServiceResult<T>(true, "Operation completed successfully.", Optional.of(result));
    }

    public static <T> ServiceResult<T> failure(String message) {
        return new ServiceResult<T>(false, message, Optional.empty());
    }
}
