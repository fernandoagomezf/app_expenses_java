package com.blendwerk.pet.application.views.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

import com.blendwerk.pet.domain.budgeting.Currency;
import com.blendwerk.pet.domain.budgeting.ExpenseCategory;
import com.blendwerk.pet.domain.budgeting.IncomeCategory;

public class TransactionDialog {
    private final JFrame _parent;
    private JComboBox<String> _typeComboBox;
    private JComboBox<Object> _categoryComboBox;
    private JComboBox<Currency> _currencyComboBox;
    private JFormattedTextField _amountField;
    private boolean _confirmed;

    public TransactionDialog(JFrame parent) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent frame cannot be null");
        }
        _parent = parent;
        _confirmed = false;
    }

    public void show() {
        _typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});        
        _categoryComboBox = new JComboBox<>();
        _currencyComboBox = new JComboBox<>(Currency.values());
        _currencyComboBox.setSelectedItem(Currency.MXN);
        
        var decimalFormat = new DecimalFormat("#,##0.00");
        decimalFormat.setMaximumFractionDigits(2);
        var formatter = new NumberFormatter(decimalFormat);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setAllowsInvalid(false);
        
        _amountField = new JFormattedTextField(formatter);
        _amountField.setColumns(15);
        _amountField.setValue(0.0);
        
        _typeComboBox.addActionListener((ActionEvent e) -> updateCategoryOptions());
        
        updateCategoryOptions();
        
        var panel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; 
        gbc.gridy = 0;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(_typeComboBox, gbc);
        
        gbc.gridx = 0; 
        gbc.gridy = 1;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        panel.add(_categoryComboBox, gbc);
        
        gbc.gridx = 0; 
        gbc.gridy = 2;
        panel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        panel.add(_amountField, gbc);
        
        gbc.gridx = 0; 
        gbc.gridy = 3;
        panel.add(new JLabel("Currency:"), gbc);
        gbc.gridx = 1;
        panel.add(_currencyComboBox, gbc);

        int result = JOptionPane.showConfirmDialog(
            _parent,
            panel,
            "Add Transaction",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        _confirmed = (result == JOptionPane.OK_OPTION) && validateInput();
    }

    private void updateCategoryOptions() {
        _categoryComboBox.removeAllItems();
        
        String selectedType = (String) _typeComboBox.getSelectedItem();
        
        if ("Income".equals(selectedType)) {
            for (IncomeCategory category : IncomeCategory.values()) {
                _categoryComboBox.addItem(category);
            }
        } else {
            for (ExpenseCategory category : ExpenseCategory.values()) {
                _categoryComboBox.addItem(category);
            }
        }
    }
    
    private boolean validateInput() {
        try {
            _amountField.commitEdit();
            double amount = getAmount();
            return amount > 0;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isConfirmed() {
        return _confirmed;
    }
    
    public String getType() {
        return _typeComboBox != null ? (String) _typeComboBox.getSelectedItem() : "Income";
    }
    
    public String getCategory() {
        Object selected = _categoryComboBox != null ? _categoryComboBox.getSelectedItem() : null;
        return selected != null ? selected.toString() : "";
    }
    
    public double getAmount() {
        if (_amountField != null && _amountField.getValue() != null) {
            return ((Number) _amountField.getValue()).doubleValue();
        }
        return 0.0;
    }
    
    public String getCurrency() {
        Currency selected = (Currency) _currencyComboBox.getSelectedItem();
        return selected != null ? selected.toString() : Currency.MXN.toString();
    }
}
