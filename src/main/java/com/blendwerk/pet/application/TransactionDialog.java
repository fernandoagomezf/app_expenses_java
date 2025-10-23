package com.blendwerk.pet.application;

import com.blendwerk.pet.domain.budgeting.Currency;
import com.blendwerk.pet.domain.budgeting.ExpenseCategory;
import com.blendwerk.pet.domain.budgeting.IncomeCategory;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionDialog extends JDialog {
    private JComboBox<String> typeComboBox;
    private JComboBox<String> categoryComboBox;
    private JTextField amountField;
    private JSpinner dateSpinner;
    private JTextArea descriptionArea;
    private JComboBox<Currency> currencyComboBox;
    private JButton okButton;
    private JButton cancelButton;
    
    private boolean confirmed = false;
    private boolean isEditMode = false;
    
    public TransactionDialog(Frame owner, String title, Object transactionData) {
        super(owner, title, true);
        this.isEditMode = transactionData != null;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        if (isEditMode) {
            populateFieldsFromTransaction(transactionData);
        }
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }
        
    private void initializeComponents() {
        // Transaction type
        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});
        typeComboBox.setSelectedIndex(0);
        
        // Category (will be populated based on type)
        categoryComboBox = new JComboBox<>();
        updateCategoryList();
        
        // Amount
        amountField = new JTextField(15);
        amountField.setToolTipText("Enter amount (e.g., 1500.50)");
        
        // Currency
        currencyComboBox = new JComboBox<>(Currency.values());
        currencyComboBox.setSelectedItem(Currency.MXN);
        
        // Date
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        
        // Description
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setToolTipText("Enter a description for this transaction");
        
        // Buttons
        okButton = new JButton(isEditMode ? "Update" : "Add");
        cancelButton = new JButton("Cancel");
        
        // Set default button
        getRootPane().setDefaultButton(okButton);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Type
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(typeComboBox, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(categoryComboBox, gbc);
        
        // Amount and Currency (on same row)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Amount:"), gbc);
        
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        amountPanel.add(amountField);
        amountPanel.add(Box.createHorizontalStrut(10));
        amountPanel.add(new JLabel("Currency:"));
        amountPanel.add(Box.createHorizontalStrut(5));
        amountPanel.add(currencyComboBox);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(amountPanel, gbc);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dateSpinner, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Type change listener to update categories
        typeComboBox.addActionListener(e -> updateCategoryList());
        
        // OK button
        okButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
                logTransactionData();
            }
        });
        
        // Cancel button
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        // Amount field validation
        amountField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                validateAmountField();
            }
        });
    }
    
    private void updateCategoryList() {
        categoryComboBox.removeAllItems();
        
        String selectedType = (String) typeComboBox.getSelectedItem();
        if ("Income".equals(selectedType)) {
            for (IncomeCategory category : IncomeCategory.values()) {
                categoryComboBox.addItem(category.toString());
            }
        } else if ("Expense".equals(selectedType)) {
            for (ExpenseCategory category : ExpenseCategory.values()) {
                categoryComboBox.addItem(category.toString());
            }
        }
    }
    
    private boolean validateInput() {
        // Validate amount
        if (!validateAmountField()) {
            return false;
        }
        
        // Validate description (optional but warn if empty)
        if (descriptionArea.getText().trim().isEmpty()) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Description is empty. Continue without description?",
                "Empty Description",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (result != JOptionPane.YES_OPTION) {
                descriptionArea.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    private boolean validateAmountField() {
        String amountText = amountField.getText().trim();
        
        if (amountText.isEmpty()) {
            showValidationError("Amount cannot be empty.", amountField);
            return false;
        }
        
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showValidationError("Amount must be greater than zero.", amountField);
                return false;
            }
            
            // Format the amount to 2 decimal places
            amountField.setText(String.format("%.2f", amount));
            return true;
            
        } catch (NumberFormatException e) {
            showValidationError("Invalid amount format. Please enter a valid number.", amountField);
            return false;
        }
    }
    
    private void showValidationError(String message, JComponent component) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE
        );
        component.requestFocus();
    }
    
    private void populateFieldsFromTransaction(Object transactionData) {
        // In a real implementation, this would populate fields from actual transaction data
        // For now, we'll populate with sample data to demonstrate editing mode
        typeComboBox.setSelectedItem("Expense");
        updateCategoryList();
        categoryComboBox.setSelectedItem("GROCERIES");
        amountField.setText("150.00");
        descriptionArea.setText("Weekly grocery shopping at supermarket");
        
        // Set date to yesterday for demonstration
        long yesterday = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        dateSpinner.setValue(new Date(yesterday));
    }
    
    private void logTransactionData() {
        // Log the transaction data (in real implementation, this would save to repository)
        String type = (String) typeComboBox.getSelectedItem();
        String category = (String) categoryComboBox.getSelectedItem();
        String amount = amountField.getText();
        Currency currency = (Currency) currencyComboBox.getSelectedItem();
        Date date = (Date) dateSpinner.getValue();
        String description = descriptionArea.getText().trim();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        System.out.println("=== Transaction " + (isEditMode ? "Updated" : "Added") + " ===");
        System.out.println("Type: " + type);
        System.out.println("Category: " + category);
        System.out.println("Amount: " + amount + " " + currency);
        System.out.println("Date: " + sdf.format(date));
        System.out.println("Description: " + (description.isEmpty() ? "(none)" : description));
        System.out.println("=====================================");
    }
    
    // Getter methods for retrieving form data
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getTransactionType() {
        return (String)typeComboBox.getSelectedItem();
    }
    
    public String getTransactionCategory() {
        return (String) categoryComboBox.getSelectedItem();
    }
    
    public String getTransactionAmount() {
        return amountField.getText();
    }
    
    public Currency getTransactionCurrency() {
        return (Currency) currencyComboBox.getSelectedItem();
    }
    
    public Date getTransactionDate() {
        return (Date) dateSpinner.getValue();
    }
    
    public String getTransactionDescription() {
        return descriptionArea.getText().trim();
    }
}