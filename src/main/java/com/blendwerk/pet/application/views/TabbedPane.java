package com.blendwerk.pet.application.views;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
 
public class TabbedPane extends JTabbedPane {
    private MainWindow _mainWindow;
    
    public TabbedPane(MainWindow mainWindow) {
        if (mainWindow == null) {
            throw new IllegalArgumentException("The main window cannot be null");
        }
        _mainWindow = mainWindow;
        
        initializeComponent();
        setupEventHandlers();
        addWelcomeTab();
    }
    
    private void initializeComponent() {
        setTabPlacement(JTabbedPane.TOP);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }
    
    private void setupEventHandlers() {
        // Tab selection listener
        addChangeListener(e -> {
            int selectedIndex = getSelectedIndex();
            if (selectedIndex >= 0) {
                String tabTitle = getTitleAt(selectedIndex);
                System.out.println("Selected tab: " + tabTitle);
                
                // Clear transaction details when switching tabs since no transaction is selected
                if (!tabTitle.equals("Welcome")) {
                    _mainWindow.clearTransactionDetails();
                }
            }
        });
    }
    
    private void addWelcomeTab() {
        var welcomePanel = createWelcomePanel();
        addTab("Welcome", welcomePanel);
        setToolTipTextAt(0, "Welcome to Personal Expense Tracker");
    }
    
    private JPanel createWelcomePanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome message
        var messagePanel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        var titleLabel = new JLabel("Welcome to Personal Expense Tracker");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        gbc.gridx = 0; gbc.gridy = 0;
        messagePanel.add(titleLabel, gbc);
        
        var instructionLabel = new JLabel("<html><center>" +
            "Get started by creating a new budget or opening an existing one.<br/>" +
            "Use the File menu or toolbar buttons to begin tracking your expenses." +
            "</center></html>");
        instructionLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        messagePanel.add(instructionLabel, gbc);
        
        // Quick action buttons
        var buttonPanel = new JPanel(new FlowLayout());
        
        var newBudgetButton = new JButton("Create New Budget");
        newBudgetButton.addActionListener(e -> {
            String budgetName = JOptionPane.showInputDialog(
                this,
                "Enter budget name:",
                "Create New Budget",
                JOptionPane.PLAIN_MESSAGE
            );
            if (budgetName != null && !budgetName.trim().isEmpty()) {
                addNewBudgetTab(budgetName.trim());
            }
        });
        
        var openBudgetButton = new JButton("Open Budget");
        openBudgetButton.addActionListener(e -> {
            var fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                var fileName = fileChooser.getSelectedFile().getName();
                addNewBudgetTab("Opened: " + fileName);
            }
        });
        
        buttonPanel.add(newBudgetButton);
        buttonPanel.add(openBudgetButton);
        
        gbc.gridy = 2;
        messagePanel.add(buttonPanel, gbc);
        
        panel.add(messagePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void addNewBudgetTab(String budgetName) {
        for (int i = 0; i < getTabCount(); i++) {
            if (getTitleAt(i).equals(budgetName)) {
                setSelectedIndex(i);
                return;
            }
        }
        
        var budgetPanel = createBudgetPanel(budgetName);
        
        // Create tab with close button
        addTab(budgetName, budgetPanel);
        var tabIndex = getTabCount() - 1;
        setTabComponentAt(tabIndex, createTabHeader(budgetName, tabIndex));
        setToolTipTextAt(tabIndex, "Budget: " + budgetName);
        
        // Select the new tab
        setSelectedIndex(tabIndex);
        
        System.out.println("Added budget tab: " + budgetName);
    }
    
    private JPanel createBudgetPanel(String budgetName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Budget summary panel
        JPanel summaryPanel = createBudgetSummaryPanel(budgetName);
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Transactions table
        var transactionsTable = createTransactionsTable(budgetName);
        var scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transactions"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Actions panel
        var actionsPanel = createActionsPanel(budgetName, transactionsTable);
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBudgetSummaryPanel(String budgetName) {
        var panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Budget Summary"));
        
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Budget name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Budget:"), gbc);
        gbc.gridx = 1;
        var nameLabel = new JLabel(budgetName);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        panel.add(nameLabel, gbc);
        
        // Currency
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Currency:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("MXN"), gbc);
        
        // Summary values (mock data based on budget name)
        double[] summary = getMockSummaryData(budgetName);
        
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Total Income:"), gbc);
        gbc.gridx = 3;
        var incomeLabel = new JLabel(String.format("$%.2f", summary[0]));
        incomeLabel.setForeground(new Color(0, 128, 0));
        incomeLabel.setFont(incomeLabel.getFont().deriveFont(Font.BOLD));
        panel.add(incomeLabel, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Total Expenses:"), gbc);
        gbc.gridx = 3;
        var expensesLabel = new JLabel(String.format("$%.2f", summary[1]));
        expensesLabel.setForeground(new Color(128, 0, 0));
        expensesLabel.setFont(expensesLabel.getFont().deriveFont(Font.BOLD));
        panel.add(expensesLabel, gbc);
        
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2;
        var balanceLabel = new JLabel(String.format("Balance: $%.2f", summary[0] - summary[1]));
        balanceLabel.setFont(balanceLabel.getFont().deriveFont(Font.BOLD, 16f));
        balanceLabel.setForeground(summary[0] - summary[1] >= 0 ? new Color(0, 128, 0) : new Color(128, 0, 0));
        panel.add(balanceLabel, gbc);
        
        return panel;
    }
    
    private JTable createTransactionsTable(String budgetName) {
        String[] columnNames = {"Type", "Category", "Amount", "Date", "Description"};
        var tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Type
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Category
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Amount
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Date
        table.getColumnModel().getColumn(4).setPreferredWidth(300); // Description
        
        // Populate with mock data
        populateTableWithMockData(tableModel, budgetName);
        
        // Double-click listener
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    var selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        Object[] rowData = new Object[tableModel.getColumnCount()];
                        for (int i = 0; i < rowData.length; i++) {
                            rowData[i] = tableModel.getValueAt(selectedRow, i);
                        }
                        System.out.println("Double-clicked transaction: " + rowData[4]);
                        _mainWindow.showTransactionDetails(rowData);
                    }
                }
            }
        });
        
        // Selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                var selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    Object[] rowData = new Object[tableModel.getColumnCount()];
                    for (int i = 0; i < rowData.length; i++) {
                        rowData[i] = tableModel.getValueAt(selectedRow, i);
                    }
                    System.out.println("Selected transaction in tab: " + rowData[4]);
                    _mainWindow.showTransactionDetails(rowData);
                }
            }
        });
        
        return table;
    }
    
    private JPanel createActionsPanel(String budgetName, JTable table) {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        var addButton = new JButton("Add Transaction");
        addButton.addActionListener(e -> {
            System.out.println("Adding transaction to budget: " + budgetName);
            TransactionDialog dialog = new TransactionDialog(_mainWindow, "Add Transaction", null);
            dialog.setVisible(true);
        });
        
        var editButton = new JButton("Edit Transaction");
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                System.out.println("Editing transaction in budget: " + budgetName);
                var dialog = new TransactionDialog(_mainWindow, "Edit Transaction", null);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a transaction to edit.", 
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        var deleteButton = new JButton("Delete Transaction");
        deleteButton.addActionListener(e -> {
            var selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                var result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the selected transaction?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.removeRow(selectedRow);
                    System.out.println("Deleted transaction from budget: " + budgetName);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a transaction to delete.", 
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        
        return panel;
    }
    
    private JPanel createTabHeader(String title, int tabIndex) {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        
        var titleLabel = new JLabel(title);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        var closeButton = new JButton("×");
        closeButton.setPreferredSize(new Dimension(17, 17));
        closeButton.setFont(closeButton.getFont().deriveFont(Font.BOLD, 12f));
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setToolTipText("Close tab");
        
        closeButton.addActionListener(e -> closeTabAt(tabIndex));
        
        panel.add(titleLabel);
        panel.add(closeButton);
        
        return panel;
    }
    
    private void populateTableWithMockData(DefaultTableModel tableModel, String budgetName) {
        if (budgetName.contains("January")) {
            tableModel.addRow(new Object[]{"Income", "SALARY", "$3,500.00", "2025-01-01", "January salary payment"});
            tableModel.addRow(new Object[]{"Expense", "RENT_MORTGAGE", "$1,200.00", "2025-01-01", "Monthly rent payment"});
            tableModel.addRow(new Object[]{"Expense", "GROCERIES", "$400.00", "2025-01-15", "Weekly grocery shopping"});
            tableModel.addRow(new Object[]{"Expense", "UTILITIES", "$150.00", "2025-01-10", "Electricity and water"});
        } else if (budgetName.contains("February")) {
            tableModel.addRow(new Object[]{"Income", "SALARY", "$3,500.00", "2025-02-01", "February salary payment"});
            tableModel.addRow(new Object[]{"Income", "BUSINESS", "$800.00", "2025-02-15", "Freelance consulting work"});
            tableModel.addRow(new Object[]{"Expense", "RENT_MORTGAGE", "$1,200.00", "2025-02-01", "Monthly rent payment"});
            tableModel.addRow(new Object[]{"Expense", "TRANSPORTATION", "$250.00", "2025-02-05", "Car maintenance and gas"});
        } else if (budgetName.contains("Vacation")) {
            tableModel.addRow(new Object[]{"Expense", "TRAVEL", "$2,000.00", "2025-03-15", "Flight tickets to Europe"});
            tableModel.addRow(new Object[]{"Expense", "TRAVEL", "$1,500.00", "2025-03-20", "Hotel accommodation"});
            tableModel.addRow(new Object[]{"Expense", "DINING_OUT", "$800.00", "2025-03-22", "Vacation restaurant meals"});
        } else {
            // Default sample data
            tableModel.addRow(new Object[]{"Income", "SALARY", "$3,000.00", "2025-01-01", "Monthly salary"});
            tableModel.addRow(new Object[]{"Income", "FREELANCE", "$500.00", "2025-01-15", "Freelance project"});
            tableModel.addRow(new Object[]{"Expense", "RENT_MORTGAGE", "$1,000.00", "2025-01-01", "Monthly rent"});
            tableModel.addRow(new Object[]{"Expense", "GROCERIES", "$300.00", "2025-01-05", "Weekly groceries"});
            tableModel.addRow(new Object[]{"Expense", "ENTERTAINMENT", "$150.00", "2025-01-12", "Movie tickets and dinner"});
        }
    }
    
    private double[] getMockSummaryData(String budgetName) {
        if (budgetName.contains("January")) {
            return new double[]{3500.00, 1750.00};
        } else if (budgetName.contains("February")) {
            return new double[]{4300.00, 1450.00};
        } else if (budgetName.contains("Vacation")) {
            return new double[]{0.00, 4300.00};
        } else {
            return new double[]{3500.00, 1450.00};
        }
    }
    
    public void closeCurrentTab() {
        var selectedIndex = getSelectedIndex();
        if (selectedIndex >= 0 && !getTitleAt(selectedIndex).equals("Welcome")) {
            closeTabAt(selectedIndex);
        }
    }
    
    public void closeTabAt(int index) {
        if (index >= 0 && index < getTabCount() && !getTitleAt(index).equals("Welcome")) {
            var tabTitle = getTitleAt(index);
            removeTabAt(index);
            System.out.println("Closed budget tab: " + tabTitle);
            
            // If no tabs remain except welcome, ensure welcome is selected
            if (getTabCount() == 1) {
                setSelectedIndex(0);
            }
        }
    }
}