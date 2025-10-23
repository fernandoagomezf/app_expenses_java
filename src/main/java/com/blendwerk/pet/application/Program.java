package com.blendwerk.pet.application;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.blendwerk.pet.infrastructure.FileBudgetRepository;
import com.blendwerk.pet.infrastructure.FileStorage;
import com.blendwerk.pet.infrastructure.FileStorageSummary;
import com.blendwerk.pet.infrastructure.MemoryCache;

public class Program {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            
            SwingUtilities.invokeLater(() -> {
                new Program();
            });
        } catch (Exception ex) {
            System.out.println("The application has encountered an unrecoverable error and needs to close.");
            System.out.println("Error details: " + ex.getMessage());
            System.out.println("More details:");
            ex.printStackTrace();
        }
    }

    public Program() {       
        var cache = new MemoryCache();
        var storage = new FileStorage();
        var summary = new FileStorageSummary();
        var budgetRepository = new FileBudgetRepository(cache, storage, summary);
        var budgetingService = new BudgetingService(budgetRepository);

        MainWindow mainWindow = new MainWindow(budgetingService);
        mainWindow.setVisible(true);
        
        System.out.println("Personal Expense Tracker started.");
    }
}
