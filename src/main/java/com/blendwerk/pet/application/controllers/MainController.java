package com.blendwerk.pet.application.controllers;

import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.domain.budgeting.Currency;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class MainController {
    private final BudgetModel _model;
    //private final TreeController _treeController;
    private final TabbedController _tabbedController;
    //private final DetailsController _detailsController;
    
    public MainController(BudgetModel model, TreeController treeController, 
                         TabbedController tabbedController, DetailsController detailsController) {
        if (model == null) throw new IllegalArgumentException("Model cannot be null");
        if (treeController == null) throw new IllegalArgumentException("TreeController cannot be null");
        if (tabbedController == null) throw new IllegalArgumentException("TabbedController cannot be null");
        if (detailsController == null) throw new IllegalArgumentException("DetailsController cannot be null");
        
        _model = model;
        //_treeController = treeController;
        _tabbedController = tabbedController;
        //_detailsController = detailsController;
    }
    
    public void createNewBudget(JFrame parentWindow) {
        // Show budget creation dialog
        CreateBudgetDialog dialog = new CreateBudgetDialog(parentWindow);
        dialog.show();
        
        if (dialog.isConfirmed()) {
            String budgetName = dialog.getBudgetName();
            String currency = dialog.getCurrency();
            
            // Create budget via model
            var result = _model.createBudget(budgetName, currency);
            
            if (result.success() && result.result().isPresent()) {
                var budget = result.result().get();
                
                // Budget is automatically added to tree via listener in TreeController
                // Now open a tab for the new budget
                _tabbedController.openBudgetTabForNewBudget(budgetName, budget);
                
                System.out.println("✅ Created budget: " + budgetName + " with currency: " + currency);
            } else {
                JOptionPane.showMessageDialog(parentWindow, 
                    "Failed to create budget: " + result.message(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Inner class for the Create Budget Dialog
     */
    private static class CreateBudgetDialog {
        private final JFrame _parent;
        private JTextField _budgetNameField;
        private JComboBox<Currency> _currencyComboBox;
        private boolean _confirmed;
        
        public CreateBudgetDialog(JFrame parent) {
            _parent = parent;
            _confirmed = false;
        }
        
        public void show() {
            // Create dialog components
            _budgetNameField = new JTextField(20);
            _currencyComboBox = new JComboBox<>(Currency.values());
            _currencyComboBox.setSelectedItem(Currency.MXN); // Default currency
            
            // Create panel with layout
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Budget name
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Budget Name:"), gbc);
            gbc.gridx = 1;
            panel.add(_budgetNameField, gbc);
            
            // Currency
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Currency:"), gbc);
            gbc.gridx = 1;
            panel.add(_currencyComboBox, gbc);
            
            // Show dialog
            int result = JOptionPane.showConfirmDialog(
                _parent,
                panel,
                "Create New Budget",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            _confirmed = (result == JOptionPane.OK_OPTION) && !getBudgetName().trim().isEmpty();
        }
        
        public boolean isConfirmed() {
            return _confirmed;
        }
        
        public String getBudgetName() {
            return _budgetNameField != null ? _budgetNameField.getText().trim() : "";
        }
        
        public String getCurrency() {
            Currency selected = (Currency) _currencyComboBox.getSelectedItem();
            return selected != null ? selected.toString() : Currency.MXN.toString();
        }
    }
}