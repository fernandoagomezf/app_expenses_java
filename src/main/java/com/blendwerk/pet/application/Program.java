package com.blendwerk.pet.application;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.blendwerk.pet.application.controllers.MainController;
import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.views.windows.MainWindow;
import com.blendwerk.pet.infrastructure.repositories.FileBudgetRepository;
import com.blendwerk.pet.infrastructure.services.FileStorage;
import com.blendwerk.pet.infrastructure.services.FileStorageScanner;
import com.blendwerk.pet.infrastructure.services.MemoryCache;

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
        var summary = new FileStorageScanner();
        var repository = new FileBudgetRepository(cache, storage, summary);
        
        var model = new BudgetModel(repository);
        var view = new MainWindow();
        var controller = new MainController(model, view);
        view.setVisible(true);
        
        controller.updateView();
        
        System.out.println("Personal Expense Tracker started with MVC architecture.");
    }
}
