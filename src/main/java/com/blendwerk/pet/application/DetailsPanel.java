package com.blendwerk.pet.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class DetailsPanel extends JPanel {
    private JLabel _transactionTypeLabel;
    private JLabel _transactionCategoryLabel;
    private JLabel _transactionAmountLabel;
    private JLabel _transactionDateLabel;
    private JLabel _transactionCurrencyLabel;
    private JTextArea _transactionDescriptionArea;
    private JLabel _transactionIdLabel;
    private JButton _closeButton;
    private JButton _editButton;
    private JButton _deleteButton;
    
    public DetailsPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        showNoSelectionState();
    }
    
    private void initializeComponents() {
        _transactionTypeLabel = new JLabel("No Transaction Selected");
        _transactionCategoryLabel = new JLabel("Category: -");
        _transactionAmountLabel = new JLabel("Amount: -");
        _transactionDateLabel = new JLabel("Date: -");
        _transactionCurrencyLabel = new JLabel("Currency: -");
        _transactionIdLabel = new JLabel("ID: -");
        _transactionIdLabel.setFont(_transactionIdLabel.getFont().deriveFont(Font.PLAIN, 10f));
        _transactionIdLabel.setForeground(Color.GRAY);

        
        _transactionTypeLabel.setFont(_transactionTypeLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        // Description area
        _transactionDescriptionArea = new JTextArea(4, 20);
        _transactionDescriptionArea.setLineWrap(true);
        _transactionDescriptionArea.setWrapStyleWord(true);
        _transactionDescriptionArea.setEditable(false);
        _transactionDescriptionArea.setBorder(BorderFactory.createLoweredBevelBorder());
        _transactionDescriptionArea.setBackground(getBackground());
        _transactionDescriptionArea.setText("No description available");
        
        // Action buttons
        _closeButton = new JButton("×");
        _closeButton.setPreferredSize(new Dimension(25, 25));
        _closeButton.setToolTipText("Close details panel");
        _closeButton.setFocusPainted(false);
        
        _editButton = new JButton("Edit Transaction");
        _editButton.setEnabled(false);
        
        _deleteButton = new JButton("Delete Transaction");
        _deleteButton.setEnabled(false);
        _deleteButton.setForeground(new Color(128, 0, 0));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Transaction Details"));
        setPreferredSize(new Dimension(300, 0));
        
        // Header panel
        var headerPanel = new JPanel(new BorderLayout());
        
        // Transaction info panel
        var transactionInfoPanel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        transactionInfoPanel.add(_transactionTypeLabel, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        transactionInfoPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transactionInfoPanel.add(_transactionCategoryLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        transactionInfoPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transactionInfoPanel.add(_transactionAmountLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        transactionInfoPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transactionInfoPanel.add(_transactionDateLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        transactionInfoPanel.add(new JLabel("Currency:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transactionInfoPanel.add(_transactionCurrencyLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        transactionInfoPanel.add(_transactionIdLabel, gbc);
        
        // Close button panel
        var closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.add(_closeButton);
        
        headerPanel.add(transactionInfoPanel, BorderLayout.CENTER);
        headerPanel.add(closePanel, BorderLayout.NORTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Description panel
        var descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(BorderFactory.createTitledBorder("Description"));
        
        var descScrollPane = new JScrollPane(_transactionDescriptionArea);
        descScrollPane.setPreferredSize(new Dimension(280, 100));
        descriptionPanel.add(descScrollPane, BorderLayout.CENTER);
        
        add(descriptionPanel, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        actionsPanel.add(_editButton);
        actionsPanel.add(_deleteButton);
        
        add(actionsPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        _closeButton.addActionListener(e -> setVisible(false));
        
        _editButton.addActionListener(e -> {
            System.out.println("Editing selected transaction");
            // This would open a TransactionDialog with the current transaction data
            TransactionDialog dialog = new TransactionDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                "Edit Transaction", 
                getCurrentTransactionData()
            );
            dialog.setVisible(true);
        });
        
        _deleteButton.addActionListener(e -> {
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
        _transactionTypeLabel.setText("No Transaction Selected");
        _transactionTypeLabel.setForeground(Color.GRAY);
        _transactionCategoryLabel.setText("-");
        _transactionAmountLabel.setText("-");
        _transactionDateLabel.setText("-");
        _transactionCurrencyLabel.setText("-");
        _transactionIdLabel.setText("-");
        _transactionDescriptionArea.setText("Select a transaction from the table to view details");
        
        _editButton.setEnabled(false);
        _deleteButton.setEnabled(false);
    }
    
    private Object getCurrentTransactionData() {
        // In a real implementation, this would return the actual transaction object
        // For now, return null to indicate mock data should be used
        return new Object(); // Mock transaction data
    }
    
    public void loadTransactionDetails(String type, String category, String amount, String date, String description) {
        // Update transaction type with color coding
        _transactionTypeLabel.setText(type + " Transaction");
        if ("Income".equals(type)) {
            _transactionTypeLabel.setForeground(new Color(0, 128, 0)); // Green
            _transactionAmountLabel.setForeground(new Color(0, 128, 0));
        } else {
            _transactionTypeLabel.setForeground(new Color(128, 0, 0)); // Red  
            _transactionAmountLabel.setForeground(new Color(128, 0, 0));
        }
        
        // Update transaction details
        _transactionCategoryLabel.setText(category);
        _transactionAmountLabel.setText(amount);
        _transactionDateLabel.setText(date);
        _transactionCurrencyLabel.setText("MXN"); // Default for now
        _transactionIdLabel.setText("ID: " + generateMockId());
        
        // Update description
        if (description != null && !description.trim().isEmpty()) {
            _transactionDescriptionArea.setText(description);
        } else {
            _transactionDescriptionArea.setText("No description provided");
        }
        
        // Enable action buttons
        _editButton.setEnabled(true);
        _deleteButton.setEnabled(true);
        
        System.out.println("Loaded transaction details: " + type + " - " + category + " - " + amount);
    }
    
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
    
    public void clearTransactionDetails() {
        showNoSelectionState();
    }
}