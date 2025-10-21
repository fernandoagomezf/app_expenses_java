package com.blendwerk.pet.application;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
        
        System.out.println("Personal Expense Tracker started successfully.");
    }
}
