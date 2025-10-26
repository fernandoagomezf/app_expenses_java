package com.blendwerk.pet.application;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.blendwerk.pet.application.controllers.Controller;
import com.blendwerk.pet.application.models.Model;
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
        // services to be injected
        var cache = new MemoryCache();
        var storage = new FileStorage();
        var summary = new FileStorageScanner();
        var repository = new FileBudgetRepository(cache, storage, summary);
        
        // model view controller
        var model = new Model(repository);
        var view = new MainWindow();
        view.setVisible(true);
        var controller = new Controller(model, view);
        controller.updateView();
    }
}
