package com.blendwerk.pet.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main application window for the Personal Expense Tracker.
 * Follows standard Swing practices by extending JFrame and configuring components in constructor.
 */
public class MainWindow extends JFrame {
    private BudgetTreePanel budgetTreePanel;
    private BudgetDetailsPanel budgetDetailsPanel;
    private BudgetTabbedPane budgetTabbedPane;
    private StatusBar statusBar;
    private JSplitPane leftSplitPane;
    private JSplitPane rightSplitPane;
    
    public MainWindow() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Window properties
        setTitle("Blendwerk Personal Expense Tracker");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set minimum size to prevent layout issues
        setMinimumSize(new Dimension(800, 600));
    }
    
    private void initializeComponents() {
        // Initialize main panels
        budgetTreePanel = new BudgetTreePanel(this);
        budgetDetailsPanel = new BudgetDetailsPanel();
        budgetTabbedPane = new BudgetTabbedPane(this);
        statusBar = new StatusBar();
        
        // Menu bar
        setJMenuBar(createMenuBar());
        
        // Toolbar
        add(createToolBar(), BorderLayout.NORTH);
        
        // Status bar
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupLayout() {
        // Main content area with split panes
        // Left: Budget tree, Center: Tabbed pane, Right: Details panel (initially hidden)
        
        leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        leftSplitPane.setLeftComponent(budgetTreePanel);
        leftSplitPane.setRightComponent(budgetTabbedPane);
        leftSplitPane.setDividerLocation(250);
        leftSplitPane.setOneTouchExpandable(true);
        
        rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplitPane.setLeftComponent(leftSplitPane);
        rightSplitPane.setRightComponent(budgetDetailsPanel);
        rightSplitPane.setDividerLocation(900);
        rightSplitPane.setOneTouchExpandable(true);
        
        // Initially hide the details panel
        budgetDetailsPanel.setVisible(false);
        
        add(rightSplitPane, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        // Window close confirmation could be added here
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newBudgetItem = new JMenuItem("New Budget");
        newBudgetItem.setMnemonic(KeyEvent.VK_N);
        newBudgetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newBudgetItem.addActionListener(e -> createNewBudget());
        
        JMenuItem openBudgetItem = new JMenuItem("Open Budget");
        openBudgetItem.setMnemonic(KeyEvent.VK_O);
        openBudgetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openBudgetItem.addActionListener(e -> openBudget());
        
        JMenuItem closeBudgetItem = new JMenuItem("Close Budget");
        closeBudgetItem.setMnemonic(KeyEvent.VK_C);
        closeBudgetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        closeBudgetItem.addActionListener(e -> closeCurrentBudget());
        
        fileMenu.add(newBudgetItem);
        fileMenu.add(openBudgetItem);
        fileMenu.addSeparator();
        fileMenu.add(closeBudgetItem);
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem addTransactionItem = new JMenuItem("Add Transaction");
        addTransactionItem.setMnemonic(KeyEvent.VK_A);
        addTransactionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        addTransactionItem.addActionListener(e -> addTransaction());
        
        JMenuItem editTransactionItem = new JMenuItem("Edit Transaction");
        editTransactionItem.setMnemonic(KeyEvent.VK_E);
        editTransactionItem.addActionListener(e -> editTransaction());
        
        JMenuItem deleteTransactionItem = new JMenuItem("Delete Transaction");
        deleteTransactionItem.setMnemonic(KeyEvent.VK_D);
        deleteTransactionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteTransactionItem.addActionListener(e -> deleteTransaction());
        
        editMenu.add(addTransactionItem);
        editMenu.add(editTransactionItem);
        editMenu.add(deleteTransactionItem);
        editMenu.addSeparator();
        
        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.setMnemonic(KeyEvent.VK_S);
        settingsItem.addActionListener(e -> showSettings());
        editMenu.add(settingsItem);
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JMenuItem detailsItem = new JMenuItem("Toggle Details Panel");
        detailsItem.setMnemonic(KeyEvent.VK_D);
        detailsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
        detailsItem.addActionListener(e -> toggleDetailsPanel());
        
        viewMenu.add(detailsItem);
        
        // Window Menu
        JMenu windowMenu = new JMenu("Window");
        windowMenu.setMnemonic(KeyEvent.VK_W);
        
        JMenuItem organizeBudgetsItem = new JMenuItem("Organize Budget Tabs");
        organizeBudgetsItem.addActionListener(e -> organizeBudgetTabs());
        windowMenu.add(organizeBudgetsItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar("Main Toolbar");
        toolBar.setFloatable(false);
        
        // New Budget
        JButton newButton = new JButton("New");
        newButton.setToolTipText("Create new budget");
        newButton.addActionListener(e -> createNewBudget());
        
        // Open Budget
        JButton openButton = new JButton("Open");
        openButton.setToolTipText("Open budget");
        openButton.addActionListener(e -> openBudget());
        
        // Add Transaction
        JButton addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setToolTipText("Add new transaction");
        addTransactionButton.addActionListener(e -> addTransaction());
        
        // Settings
        JButton settingsButton = new JButton("Settings");
        settingsButton.setToolTipText("Application settings");
        settingsButton.addActionListener(e -> showSettings());
        
        // Toggle Details
        JButton toggleDetailsButton = new JButton("Details");
        toggleDetailsButton.setToolTipText("Toggle details panel");
        toggleDetailsButton.addActionListener(e -> toggleDetailsPanel());
        
        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.addSeparator();
        toolBar.add(addTransactionButton);
        toolBar.addSeparator();
        toolBar.add(toggleDetailsButton);
        toolBar.addSeparator();
        toolBar.add(settingsButton);
        
        return toolBar;
    }
    
    // Event handler methods
    private void createNewBudget() {
        System.out.println("Creating new budget...");
        budgetTabbedPane.addNewBudgetTab("New Budget " + (budgetTabbedPane.getTabCount() + 1));
    }
    
    private void openBudget() {
        System.out.println("Opening budget...");
        // Show file dialog (mock implementation)
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getName();
            budgetTabbedPane.addNewBudgetTab("Opened: " + fileName);
        }
    }
    
    private void closeCurrentBudget() {
        System.out.println("Closing current budget...");
        budgetTabbedPane.closeCurrentTab();
    }
    
    private void addTransaction() {
        System.out.println("Adding transaction...");
        TransactionDialog dialog = new TransactionDialog(this, "Add Transaction", null);
        dialog.setVisible(true);
    }
    
    private void editTransaction() {
        System.out.println("Editing transaction...");
        // Mock transaction data
        TransactionDialog dialog = new TransactionDialog(this, "Edit Transaction", null);
        dialog.setVisible(true);
    }
    
    private void deleteTransaction() {
        System.out.println("Deleting transaction...");
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete the selected transaction?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            System.out.println("Transaction deleted.");
        }
    }
    
    private void showSettings() {
        System.out.println("Showing settings...");
        SettingsDialog dialog = new SettingsDialog(this);
        dialog.setVisible(true);
    }
    
    private void toggleDetailsPanel() {
        boolean visible = budgetDetailsPanel.isVisible();
        budgetDetailsPanel.setVisible(!visible);
        if (!visible) {
            rightSplitPane.setDividerLocation(900);
        }
    }
    
    private void organizeBudgetTabs() {
        System.out.println("Organizing budget tabs...");
        // Show dialog with list of open tabs
        String[] tabs = new String[budgetTabbedPane.getTabCount()];
        for (int i = 0; i < tabs.length; i++) {
            tabs[i] = budgetTabbedPane.getTitleAt(i);
        }
        
        if (tabs.length > 0) {
            String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select a budget tab to activate:",
                "Organize Budget Tabs",
                JOptionPane.PLAIN_MESSAGE,
                null,
                tabs,
                tabs[0]
            );
            
            if (selected != null) {
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i].equals(selected)) {
                        budgetTabbedPane.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }
    
    private void showAbout() {
        JOptionPane.showMessageDialog(
            this,
            "Blendwerk Personal Expense Tracker\n" +
            "Version 1.0\n" +
            "A simple application to track personal expenses and income.",
            "About",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // Public methods for inter-component communication
    public void showBudgetDetails(String budgetName) {
        budgetDetailsPanel.loadBudgetDetails(budgetName);
        if (!budgetDetailsPanel.isVisible()) {
            toggleDetailsPanel();
        }
    }
    
    public void openBudgetTab(String budgetName) {
        budgetTabbedPane.addNewBudgetTab(budgetName);
    }
}