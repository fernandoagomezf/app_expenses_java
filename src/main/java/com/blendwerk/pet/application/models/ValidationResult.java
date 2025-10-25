package com.blendwerk.pet.application.models;

public record ValidationResult(boolean isValid, String errorMessage) {
    public ValidationResult {
        if (errorMessage == null) {
            errorMessage = "";
        }
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }
    public static ValidationResult invalid(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }
}
