package com.blendwerk.pet.application.views;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import com.blendwerk.pet.application.services.BudgetingService;

public class MainWindow extends JFrame {
    private TreePanel _budgetTreePanel;
    private DetailsPanel _budgetDetailsPanel;
    private TabbedPane _budgetTabbedPane;
    private StatusBar _statusBar;
    private JMenuBar _menuBar;
    private JToolBar _toolBar;
    private JSplitPane _leftSplitPane;
    private JSplitPane _rightSplitPane;
    private BudgetingService _service;
    
    public MainWindow(BudgetingService service) {        
        if (service == null) {
            throw new IllegalArgumentException("BudgetingService cannot be null");
        }
        _service = service;

        setTitle("Blendwerk Personal Expense Tracker");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(920, 680));

        initializeComponents();
    }
    
    private void initializeComponents() {        
        _budgetTreePanel = new TreePanel(this);
        _budgetDetailsPanel = new DetailsPanel();
        _budgetTabbedPane = new TabbedPane(this);
        _statusBar = new StatusBar();
        _menuBar = createMenuBar();
        _toolBar = createToolBar();
        _leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        _rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        _leftSplitPane.setLeftComponent(_budgetTreePanel);
        _leftSplitPane.setRightComponent(_budgetTabbedPane);
        _leftSplitPane.setDividerLocation(250);
        _leftSplitPane.setOneTouchExpandable(true);                
        
        _rightSplitPane.setLeftComponent(_leftSplitPane);
        _rightSplitPane.setRightComponent(_budgetDetailsPanel);
        _rightSplitPane.setDividerLocation(900);
        _rightSplitPane.setOneTouchExpandable(true);
        
        _budgetDetailsPanel.setVisible(true);
        
        setJMenuBar(_menuBar);
        add(_toolBar, BorderLayout.NORTH);
        add(_statusBar, BorderLayout.SOUTH);
        add(_rightSplitPane, BorderLayout.CENTER);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
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
        
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JMenuItem detailsItem = new JMenuItem("Toggle Details Panel");
        detailsItem.setMnemonic(KeyEvent.VK_D);
        detailsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
        detailsItem.addActionListener(e -> toggleDetailsPanel());
        
        viewMenu.add(detailsItem);
        
        JMenu windowMenu = new JMenu("Window");
        windowMenu.setMnemonic(KeyEvent.VK_W);
        
        JMenuItem organizeBudgetsItem = new JMenuItem("Organize Budget Tabs");
        organizeBudgetsItem.addActionListener(e -> organizeBudgetTabs());
        windowMenu.add(organizeBudgetsItem);
        
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
        
        JButton newButton = new JButton("New");
        newButton.setToolTipText("Create new budget");
        newButton.addActionListener(e -> createNewBudget());
        
        JButton openButton = new JButton("Open");
        openButton.setToolTipText("Open budget");
        openButton.addActionListener(e -> openBudget());
        
        JButton addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setToolTipText("Add new transaction");
        addTransactionButton.addActionListener(e -> addTransaction());
        
        JButton settingsButton = new JButton("Settings");
        settingsButton.setToolTipText("Application settings");
        settingsButton.addActionListener(e -> showSettings());
        
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
    
    private void createNewBudget() {
        System.out.println("Creating new budget...");
        _budgetTabbedPane.addNewBudgetTab("New Budget " + (_budgetTabbedPane.getTabCount() + 1));
    }
    
    private void openBudget() {
        System.out.println("Opening budget...");
        
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getName();
            _budgetTabbedPane.addNewBudgetTab("Opened: " + fileName);
        }
    }
    
    private void closeCurrentBudget() {
        System.out.println("Closing current budget...");
        _budgetTabbedPane.closeCurrentTab();
    }
    
    private void addTransaction() {
        System.out.println("Adding transaction...");
        TransactionDialog dialog = new TransactionDialog(this, "Add transaction", null);
        dialog.setVisible(true);
    }
    
    private void editTransaction() {
        System.out.println("Editing transaction...");
        
        TransactionDialog dialog = new TransactionDialog(null, "Edit transaction", null);
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
        boolean visible = _budgetDetailsPanel.isVisible();
        _budgetDetailsPanel.setVisible(!visible);
        if (!visible) {
            _rightSplitPane.setDividerLocation(900);
        }
    }
    
    private void organizeBudgetTabs() {
        System.out.println("Organizing budget tabs...");
        
        String[] tabs = new String[_budgetTabbedPane.getTabCount()];
        for (int i = 0; i < tabs.length; i++) {
            tabs[i] = _budgetTabbedPane.getTitleAt(i);
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
                        _budgetTabbedPane.setSelectedIndex(i);
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
    public void showTransactionDetails(Object[] transactionData) {
        _budgetDetailsPanel.loadTransactionDetails(transactionData);
        if (!_budgetDetailsPanel.isVisible()) {
            toggleDetailsPanel();
        }
    }
    
    public void showTransactionDetails(String type, String category, String amount, String date, String description) {
        _budgetDetailsPanel.loadTransactionDetails(type, category, amount, date, description);
        if (!_budgetDetailsPanel.isVisible()) {
            toggleDetailsPanel();
        }
    }
    
    public void clearTransactionDetails() {
        _budgetDetailsPanel.clearTransactionDetails();
    }
    
    public void openBudgetTab(String budgetName) {
        _budgetTabbedPane.addNewBudgetTab(budgetName);
    }
}