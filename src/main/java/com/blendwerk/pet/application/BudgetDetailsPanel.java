package com.blendwerk.pet.application;

import javax.swing.*;
import java.awt.*;

/**
 * Right panel showing detailed information about the selected transaction.
 * Can be hidden/shown and updates when transaction selection changes in the central tabbed panel.
 */
public class BudgetDetailsPanel extends JPanel {
    private JLabel transactionTypeLabel;
    private JLabel transactionCategoryLabel;
    private JLabel transactionAmountLabel;
    private JLabel transactionDateLabel;
    private JLabel transactionCurrencyLabel;
    private JTextArea transactionDescriptionArea;
    private JLabel transactionIdLabel;
    private JButton closeButton;
    private JButton editButton;
    private JButton deleteButton;
    
    public BudgetDetailsPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        showNoSelectionState();
    }
    
    private void initializeComponents() {
        // Header labels for transaction details
        transactionTypeLabel = new JLabel("No Transaction Selected");
        transactionTypeLabel.setFont(transactionTypeLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        transactionCategoryLabel = new JLabel("Category: -");
        transactionAmountLabel = new JLabel("Amount: -");
        transactionDateLabel = new JLabel("Date: -");
        transactionCurrencyLabel = new JLabel("Currency: -");
        transactionIdLabel = new JLabel("ID: -");
        transactionIdLabel.setFont(transactionIdLabel.getFont().deriveFont(Font.PLAIN, 10f));
        transactionIdLabel.setForeground(Color.GRAY);
        
        // Description area
        transactionDescriptionArea = new JTextArea(4, 20);
        transactionDescriptionArea.setLineWrap(true);
        transactionDescriptionArea.setWrapStyleWord(true);
        transactionDescriptionArea.setEditable(false);
        transactionDescriptionArea.setBorder(BorderFactory.createLoweredBevelBorder());
        transactionDescriptionArea.setBackground(getBackground());
        transactionDescriptionArea.setText("No description available");
        
        // Action buttons
        closeButton = new JButton("×");
        closeButton.setPreferredSize(new Dimension(25, 25));
        closeButton.setToolTipText("Close details panel");
        closeButton.setFocusPainted(false);
        
        editButton = new JButton("Edit Transaction");
        editButton.setEnabled(false);
        
        deleteButton = new JButton("Delete Transaction");
        deleteButton.setEnabled(false);
        deleteButton.setForeground(new Color(128, 0, 0));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Transaction Details"));
        setPreferredSize(new Dimension(300, 0));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        // Transaction info panel
        JPanel transactionInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        transactionInfoPanel.add(transactionTypeLabel, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        transactionInfoPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transactionInfoPanel.add(transactionCategoryLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        transactionInfoPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transactionInfoPanel.add(transactionAmountLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        transactionInfoPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transactionInfoPanel.add(transactionDateLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        transactionInfoPanel.add(new JLabel("Currency:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transactionInfoPanel.add(transactionCurrencyLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        transactionInfoPanel.add(transactionIdLabel, gbc);
        
        // Close button panel
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.add(closeButton);
        
        headerPanel.add(transactionInfoPanel, BorderLayout.CENTER);
        headerPanel.add(closePanel, BorderLayout.NORTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Description panel
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(BorderFactory.createTitledBorder("Description"));
        
        JScrollPane descScrollPane = new JScrollPane(transactionDescriptionArea);
        descScrollPane.setPreferredSize(new Dimension(280, 100));
        descriptionPanel.add(descScrollPane, BorderLayout.CENTER);
        
        add(descriptionPanel, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        actionsPanel.add(editButton);
        actionsPanel.add(deleteButton);
        
        add(actionsPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        closeButton.addActionListener(e -> setVisible(false));
        
        editButton.addActionListener(e -> {
            System.out.println("Editing selected transaction");
            // This would open a TransactionDialog with the current transaction data
            TransactionDialog dialog = new TransactionDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                "Edit Transaction", 
                getCurrentTransactionData()
            );
            dialog.setVisible(true);
        });
        
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this transaction?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                System.out.println("Deleting selected transaction");
                showNoSelectionState();
                setVisible(false);
            }
        });
    }
    
    private void showNoSelectionState() {
        transactionTypeLabel.setText("No Transaction Selected");
        transactionTypeLabel.setForeground(Color.GRAY);
        transactionCategoryLabel.setText("-");
        transactionAmountLabel.setText("-");
        transactionDateLabel.setText("-");
        transactionCurrencyLabel.setText("-");
        transactionIdLabel.setText("-");
        transactionDescriptionArea.setText("Select a transaction from the table to view details");
        
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    private Object getCurrentTransactionData() {
        // In a real implementation, this would return the actual transaction object
        // For now, return null to indicate mock data should be used
        return new Object(); // Mock transaction data
    }
    
    /**
     * Load transaction details for the specified transaction data.
     * In a real implementation, this would display data from an actual Transaction object.
     */
    public void loadTransactionDetails(String type, String category, String amount, String date, String description) {
        // Update transaction type with color coding
        transactionTypeLabel.setText(type + " Transaction");
        if ("Income".equals(type)) {
            transactionTypeLabel.setForeground(new Color(0, 128, 0)); // Green
            transactionAmountLabel.setForeground(new Color(0, 128, 0));
        } else {
            transactionTypeLabel.setForeground(new Color(128, 0, 0)); // Red  
            transactionAmountLabel.setForeground(new Color(128, 0, 0));
        }
        
        // Update transaction details
        transactionCategoryLabel.setText(category);
        transactionAmountLabel.setText(amount);
        transactionDateLabel.setText(date);
        transactionCurrencyLabel.setText("MXN"); // Default for now
        transactionIdLabel.setText("ID: " + generateMockId());
        
        // Update description
        if (description != null && !description.trim().isEmpty()) {
            transactionDescriptionArea.setText(description);
        } else {
            transactionDescriptionArea.setText("No description provided");
        }
        
        // Enable action buttons
        editButton.setEnabled(true);
        deleteButton.setEnabled(true);
        
        System.out.println("Loaded transaction details: " + type + " - " + category + " - " + amount);
    }
    
    /**
     * Load transaction details from table row data.
     */
    public void loadTransactionDetails(Object[] rowData) {
        if (rowData != null && rowData.length >= 5) {
            loadTransactionDetails(
                (String) rowData[0], // Type
                (String) rowData[1], // Category  
                (String) rowData[2], // Amount
                (String) rowData[3], // Date
                (String) rowData[4]  // Description
            );
        }
    }
    
    private String generateMockId() {
        return "TXN-" + System.currentTimeMillis() % 100000;
    }
    
    /**
     * Clear transaction details and show no selection state.
     */
    public void clearTransactionDetails() {
        showNoSelectionState();
    }
}