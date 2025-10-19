package com.blendwerk.pet.application;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Right panel showing detailed information about the selected budget including transactions.
 * Can be hidden/shown and updates when budget selection changes.
 */
public class BudgetDetailsPanel extends JPanel {
    private JLabel budgetNameLabel;
    private JLabel budgetCurrencyLabel;
    private JLabel totalIncomeLabel;
    private JLabel totalExpensesLabel;
    private JLabel balanceLabel;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private JButton closeButton;
    
    public BudgetDetailsPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateWithSampleData();
    }
    
    private void initializeComponents() {
        // Header labels
        budgetNameLabel = new JLabel("Budget Name");
        budgetNameLabel.setFont(budgetNameLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        budgetCurrencyLabel = new JLabel("Currency: MXN");
        totalIncomeLabel = new JLabel("Total Income: $0.00");
        totalIncomeLabel.setForeground(new Color(0, 128, 0));
        
        totalExpensesLabel = new JLabel("Total Expenses: $0.00");
        totalExpensesLabel.setForeground(new Color(128, 0, 0));
        
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(balanceLabel.getFont().deriveFont(Font.BOLD));
        balanceLabel.setForeground(new Color(0, 0, 128));
        
        // Close button
        closeButton = new JButton("×");
        closeButton.setPreferredSize(new Dimension(25, 25));
        closeButton.setToolTipText("Close details panel");
        closeButton.setFocusPainted(false);
        
        // Transactions table
        String[] columnNames = {"Type", "Category", "Amount", "Date", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        transactionsTable = new JTable(tableModel);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionsTable.setRowHeight(25);
        
        // Set column widths
        transactionsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Type
        transactionsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Category
        transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Amount
        transactionsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Date
        transactionsTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Description
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Budget Details"));
        setPreferredSize(new Dimension(300, 0));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        // Budget info panel
        JPanel budgetInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 2, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        budgetInfoPanel.add(budgetNameLabel, gbc);
        
        gbc.gridy = 1;
        budgetInfoPanel.add(budgetCurrencyLabel, gbc);
        
        gbc.gridy = 2;
        budgetInfoPanel.add(totalIncomeLabel, gbc);
        
        gbc.gridy = 3;
        budgetInfoPanel.add(totalExpensesLabel, gbc);
        
        gbc.gridy = 4;
        budgetInfoPanel.add(balanceLabel, gbc);
        
        // Close button panel
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.add(closeButton);
        
        headerPanel.add(budgetInfoPanel, BorderLayout.CENTER);
        headerPanel.add(closePanel, BorderLayout.NORTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Transactions panel
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));
        
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        transactionsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Transaction actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Add");
        addButton.setToolTipText("Add new transaction");
        
        JButton editButton = new JButton("Edit");
        editButton.setToolTipText("Edit selected transaction");
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.setToolTipText("Delete selected transaction");
        
        actionsPanel.add(addButton);
        actionsPanel.add(editButton);
        actionsPanel.add(deleteButton);
        
        transactionsPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        add(transactionsPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        closeButton.addActionListener(e -> setVisible(false));
        
        // Table selection listener
        transactionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = transactionsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    System.out.println("Selected transaction: " + tableModel.getValueAt(selectedRow, 4));
                }
            }
        });
        
        // Double-click on table to edit
        transactionsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = transactionsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        System.out.println("Editing transaction: " + tableModel.getValueAt(selectedRow, 4));
                        // Show edit dialog
                    }
                }
            }
        });
    }
    
    private void populateWithSampleData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add sample transactions
        addTransactionRow("Income", "SALARY", "$3,500.00", "2025-01-01", "Monthly salary");
        addTransactionRow("Income", "FREELANCE", "$500.00", "2025-01-15", "Freelance project");
        addTransactionRow("Expense", "RENT_MORTGAGE", "$1,200.00", "2025-01-01", "Monthly rent");
        addTransactionRow("Expense", "GROCERIES", "$300.00", "2025-01-05", "Weekly groceries");
        addTransactionRow("Expense", "UTILITIES", "$150.00", "2025-01-10", "Electricity bill");
        addTransactionRow("Expense", "TRANSPORTATION", "$200.00", "2025-01-12", "Gas and maintenance");
        
        updateSummary("Sample Budget", "MXN", 4000.00, 1850.00);
    }
    
    private void addTransactionRow(String type, String category, String amount, String date, String description) {
        Object[] row = {type, category, amount, date, description};
        tableModel.addRow(row);
    }
    
    private void updateSummary(String budgetName, String currency, double totalIncome, double totalExpenses) {
        budgetNameLabel.setText(budgetName);
        budgetCurrencyLabel.setText("Currency: " + currency);
        totalIncomeLabel.setText(String.format("Total Income: $%.2f", totalIncome));
        totalExpensesLabel.setText(String.format("Total Expenses: $%.2f", totalExpenses));
        
        double balance = totalIncome - totalExpenses;
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
        
        // Update balance color based on value
        if (balance > 0) {
            balanceLabel.setForeground(new Color(0, 128, 0)); // Green for positive
        } else if (balance < 0) {
            balanceLabel.setForeground(new Color(128, 0, 0)); // Red for negative
        } else {
            balanceLabel.setForeground(new Color(0, 0, 128)); // Blue for zero
        }
    }
    
    /**
     * Load budget details for the specified budget name.
     * In a real implementation, this would fetch data from the repository.
     */
    public void loadBudgetDetails(String budgetName) {
        System.out.println("Loading details for budget: " + budgetName);
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Mock different data based on budget name
        if (budgetName.contains("January")) {
            addTransactionRow("Income", "SALARY", "$3,500.00", "2025-01-01", "January salary");
            addTransactionRow("Expense", "RENT_MORTGAGE", "$1,200.00", "2025-01-01", "January rent");
            addTransactionRow("Expense", "GROCERIES", "$400.00", "2025-01-15", "January groceries");
            updateSummary(budgetName, "MXN", 3500.00, 1600.00);
        } else if (budgetName.contains("February")) {
            addTransactionRow("Income", "SALARY", "$3,500.00", "2025-02-01", "February salary");
            addTransactionRow("Income", "BUSINESS", "$800.00", "2025-02-15", "Consulting work");
            addTransactionRow("Expense", "RENT_MORTGAGE", "$1,200.00", "2025-02-01", "February rent");
            addTransactionRow("Expense", "UTILITIES", "$180.00", "2025-02-05", "February utilities");
            updateSummary(budgetName, "MXN", 4300.00, 1380.00);
        } else if (budgetName.contains("Vacation")) {
            addTransactionRow("Expense", "TRAVEL", "$2,000.00", "2025-03-15", "Flight tickets");
            addTransactionRow("Expense", "TRAVEL", "$1,500.00", "2025-03-20", "Hotel accommodation");
            addTransactionRow("Expense", "DINING_OUT", "$800.00", "2025-03-21", "Vacation meals");
            updateSummary(budgetName, "MXN", 0.00, 4300.00);
        } else {
            // Default data
            populateWithSampleData();
            updateSummary(budgetName, "MXN", 4000.00, 1850.00);
        }
    }
    
    /**
     * Get the currently selected transaction row index.
     */
    public int getSelectedTransactionIndex() {
        return transactionsTable.getSelectedRow();
    }
    
    /**
     * Clear all transaction data.
     */
    public void clearTransactions() {
        tableModel.setRowCount(0);
        updateSummary("No Budget Selected", "MXN", 0.00, 0.00);
    }
}