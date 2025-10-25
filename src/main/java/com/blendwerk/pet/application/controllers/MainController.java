package com.blendwerk.pet.application.controllers;

import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.models.BudgetModelListener;
import com.blendwerk.pet.application.models.CreateBudgetInput;
import com.blendwerk.pet.application.views.MainWindow;
import com.blendwerk.pet.domain.budgeting.Budget;
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

public class MainController implements BudgetModelListener {
    private final BudgetModel _model;
    private final MainWindow _view;
    
    public MainController(BudgetModel model, MainWindow view) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if (view == null) { 
            throw new IllegalArgumentException("View cannot be null");
        }        
        _model = model;
        _model.addListener(this);
        _view = view;
    }

    public void onBudgetCreated(Budget budget) {
        JOptionPane.showMessageDialog(_view,
            "Budget '" + budget.name() + "' created successfully.",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void onError(String message) {                
        JOptionPane.showMessageDialog(_view,
            "Failed to create budget: " + message,
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    public void createNewBudget(JFrame parentWindow) {
        CreateBudgetDialog dialog = new CreateBudgetDialog(parentWindow);
        dialog.show();
        
        if (dialog.isConfirmed()) {
            String budgetName = dialog.getBudgetName();
            String currency = dialog.getCurrency();
            
            var input = new CreateBudgetInput(budgetName, currency);
            _model.createBudget(input);            
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