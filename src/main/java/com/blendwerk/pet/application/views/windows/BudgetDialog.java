package com.blendwerk.pet.application.views.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.blendwerk.pet.domain.budgeting.Currency;

public class BudgetDialog {
    private final JFrame _parent;
        private JTextField _budgetNameField;
        private JComboBox<Currency> _currencyComboBox;
        private boolean _confirmed;
        
        public BudgetDialog(JFrame parent) {
            _parent = parent;
            _confirmed = false;
        }
        
        public void show() {
            _budgetNameField = new JTextField(20);
            _currencyComboBox = new JComboBox<>(Currency.values());
            _currencyComboBox.setSelectedItem(Currency.MXN); 
            
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Budget Name:"), gbc);
            gbc.gridx = 1;
            panel.add(_budgetNameField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Currency:"), gbc);
            gbc.gridx = 1;
            panel.add(_currencyComboBox, gbc);
            
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
