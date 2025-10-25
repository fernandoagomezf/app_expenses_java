package com.blendwerk.pet.application.models;

public record BudgetSummary(
    String id,
    String name,
    String currency,
    String income, 
    String expense, 
    String balance
) {
    public BudgetSummary {
        id = id != null ? id : "";
        name = name != null ? name : "";
        currency = currency != null ? currency : "";
        income = income != null ? income : "0";
        expense = expense != null ? expense : "0";
        balance = balance != null ? balance : "0";
    }
}
