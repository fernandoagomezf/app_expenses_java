package com.blendwerk.pet.application;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Program {
    public static void main(String[] args) {
        try {
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
        var mainFrame = new JFrame("Blendwerk Personal Expense Tracker");
        mainFrame.setSize(1000, 800);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }
}
