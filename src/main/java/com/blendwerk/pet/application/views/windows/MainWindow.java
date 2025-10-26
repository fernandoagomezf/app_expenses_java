package com.blendwerk.pet.application.views.windows;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.blendwerk.pet.application.models.BudgetModel;
import com.blendwerk.pet.application.models.CreateBudgetInput;
import com.blendwerk.pet.application.views.MainView;
import com.blendwerk.pet.application.views.MainViewListener;
import com.blendwerk.pet.application.views.controls.TreeCellRenderer;

public class MainWindow extends JFrame implements MainView {
    private JMenuBar _menuBar;
    private JToolBar _toolBar;
    private JPanel _statusPanel;
    private JPanel _treePanel;
    private JPanel _dataPanel;
    private JPanel _detailsPanel;
    private JSplitPane _leftSplitPane;
    private JSplitPane _rightSplitPane;
    private final List<MainViewListener> _listeners;
    
    public MainWindow() {
        _listeners = new CopyOnWriteArrayList<>();
          
        setTitle("Blendwerk Personal Expense Tracker");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(920, 680));        
        initializeComponents();
    }
    
    private void initializeComponents() {
        _treePanel = createTreePanel();
        _dataPanel = createDataPanel();
        _detailsPanel = createDetailPanel();
        _statusPanel = createStatusPanel();
        _menuBar = createMenuBar();
        _toolBar = createToolBar();
        _leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        _rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        _leftSplitPane.setLeftComponent(_treePanel);
        _leftSplitPane.setRightComponent(_dataPanel);
        _leftSplitPane.setDividerLocation(250);
        _leftSplitPane.setOneTouchExpandable(true);                
        
        _rightSplitPane.setLeftComponent(_leftSplitPane);
        _rightSplitPane.setRightComponent(_detailsPanel);
        _rightSplitPane.setDividerLocation(900);
        _rightSplitPane.setOneTouchExpandable(true);
        
        _treePanel.setVisible(true);
        _dataPanel.setVisible(true);
        _detailsPanel.setVisible(true);
        
        setJMenuBar(_menuBar);
        add(_toolBar, BorderLayout.NORTH);
        add(_statusPanel, BorderLayout.SOUTH);
        add(_rightSplitPane, BorderLayout.CENTER);
    }

    private ImageIcon loadIcon(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        var icon = new ImageIcon("C:\\Users\\SPARTANPC\\source\\repos\\expenses\\src\\main\\resources\\images\\" + fileName);
        var scaled = icon.getImage().getScaledInstance(16, 16,  Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);        
            JMenuItem newBudgetItem = new JMenuItem("New Budget");
            newBudgetItem.setMnemonic(KeyEvent.VK_N);
            newBudgetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
            newBudgetItem.setIcon(loadIcon("icons8-add-48.png"));
            newBudgetItem.addActionListener(e -> onCreateNewBudget());                    
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.setMnemonic(KeyEvent.VK_X);
            exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(newBudgetItem);
        fileMenu.addSeparator();        
        fileMenu.add(exitItem);
        
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);        
        
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);              
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);        
            JMenuItem aboutItem = new JMenuItem("About");
            aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar("Main Toolbar");
        toolBar.setFloatable(false);
        
        JButton newButton = new JButton(loadIcon("icons8-add-48.png"));
        newButton.setToolTipText("Create new budget");
        newButton.addActionListener(e -> onCreateNewBudget());        
        
        toolBar.add(newButton);
        toolBar.addSeparator();
        
        return toolBar;
    }
    
    private JPanel createTreePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Budgets"));

        var rootNode = new DefaultMutableTreeNode("Budgets");
        var treeModel = new DefaultTreeModel(rootNode);
        var tree = new JTree(treeModel);
        var scrollPane = new JScrollPane(tree);
        var treeToolBar = new JToolBar(JToolBar.HORIZONTAL);
        var refreshButton= new JButton("Refresh");
        
        tree.setName("BudgetSummaryTree");
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new TreeCellRenderer());
        scrollPane.setPreferredSize(new Dimension(250, 0));
        treeToolBar.setFloatable(false);
        refreshButton.setToolTipText("Refresh budget tree");
        refreshButton.addActionListener(e -> {
            treeModel.reload();
            for (int i = 0; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }
        });        
        treeToolBar.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(treeToolBar, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.setPreferredSize(new Dimension(0, 25));

        var statusLabel = new JLabel("Ready");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        var leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leftPanel.add(statusLabel);        
        panel.add(leftPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Transaction Details"));
        panel.setPreferredSize(new Dimension(300, 0));

        var transactionTypeLabel = new JLabel("No Transaction Selected");
        var transactionCategoryLabel = new JLabel("Category: -");
        var transactionAmountLabel = new JLabel("Amount: -");        
        var transactionCurrencyLabel = new JLabel("Currency: -");
        var transactionIdLabel = new JLabel("ID: -");
        var editButton = new JButton("Edit Transaction");
        var deleteButton = new JButton("Delete Transaction");
        
        transactionIdLabel.setFont(transactionIdLabel.getFont().deriveFont(Font.PLAIN, 10f));
        transactionIdLabel.setForeground(Color.GRAY);
        transactionTypeLabel.setFont(transactionTypeLabel.getFont().deriveFont(Font.BOLD, 16f));        
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        deleteButton.setForeground(new Color(128, 0, 0));
        
        var headerPanel = new JPanel(new BorderLayout());

        var transactionInfoPanel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
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
        transactionInfoPanel.add(new JLabel("Currency:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transactionInfoPanel.add(transactionCurrencyLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        transactionInfoPanel.add(transactionIdLabel, gbc);
        
        headerPanel.add(transactionInfoPanel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));        
        actionsPanel.add(editButton);
        actionsPanel.add(deleteButton);
        
        panel.add(actionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDataPanel() {
        var panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Budget Summary"));
        
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; 
        gbc.gridy = 0;
        summaryPanel.add(new JLabel("Budget:"), gbc);
        gbc.gridx = 1;
        var nameLabel = new JLabel("-");
        nameLabel.setName("BudgetNameLabel");
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        summaryPanel.add(nameLabel, gbc);
        
        gbc.gridx = 0; 
        gbc.gridy = 1;
        summaryPanel.add(new JLabel("Currency:"), gbc);
        gbc.gridx = 1;
        var currencyLabel = new JLabel("-");
        currencyLabel.setName("BudgetCurrencyLabel");
        summaryPanel.add(currencyLabel, gbc);
        
        gbc.gridx = 2; 
        gbc.gridy = 0;
        summaryPanel.add(new JLabel("Total Income:"), gbc);
        gbc.gridx = 3;
        var incomeLabel = new JLabel(String.format("$%.2f", 0.0));
        incomeLabel.setFont(incomeLabel.getFont().deriveFont(Font.BOLD));
        incomeLabel.setName("BudgetIncomeLabel");
        summaryPanel.add(incomeLabel, gbc);
        
        gbc.gridx = 2; 
        gbc.gridy = 1;
        summaryPanel.add(new JLabel("Total Expenses:"), gbc);
        gbc.gridx = 3;
        var expensesLabel = new JLabel(String.format("$%.2f", 0.0));
        expensesLabel.setFont(expensesLabel.getFont().deriveFont(Font.BOLD));
        expensesLabel.setName("BudgetExpensesLabel");
        summaryPanel.add(expensesLabel, gbc);
        
        gbc.gridx = 4; 
        gbc.gridy = 0; 
        gbc.gridheight = 2;
        var balanceLabel = new JLabel(String.format("Balance: $%.2f", 0.0));
        balanceLabel.setFont(balanceLabel.getFont().deriveFont(Font.BOLD, 16f));
        balanceLabel.setName("BudgetBalanceLabel");
        summaryPanel.add(balanceLabel, gbc);
        
        panel.add(summaryPanel, BorderLayout.NORTH);

        String[] columnNames = {"Type", "Category", "Amount", "Date", "Description"};
        var tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        var table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setName("TransactionTable");
        
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Type
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Category
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Amount
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Date
        table.getColumnModel().getColumn(4).setPreferredWidth(300); // Description

        var scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transactions"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public void addListener(MainViewListener listener) {
        if (listener != null) {
            _listeners.add(listener);
        }
    }

    public void removeListener(MainViewListener listener) {
        _listeners.remove(listener);
    }
    
    private void onCreateNewBudget() {
        for (var listener : _listeners) {
            listener.requestNewBudget();
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public Optional<CreateBudgetInput> getNewBudget() {
        var dialog = new BudgetDialog(this);
        dialog.show();
        
        Optional<CreateBudgetInput> result = Optional.empty();
        if (dialog.isConfirmed()) {
            String budgetName = dialog.getBudgetName();
            String currency = dialog.getCurrency();
            
            var input = new CreateBudgetInput(budgetName, currency);
            result = Optional.of(input);
        }

        return result;
    }
    
    public void showAbout() {
        JOptionPane.showMessageDialog(
            this,
            "Blendwerk Personal Expense Tracker\n" +
            "Version 1.0\n" +
            "A simple application to track personal expenses and income.",
            "About",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void updateModel(BudgetModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        updateTreePanel(model);
    }

    private Optional<Component> findComponent(Container container, String name) {      
        if (container == null) {
            throw new IllegalArgumentException("Container cannot be null");
        }  
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        if (name.equals(container.getName())) {
            return Optional.of(container);
        }
        
        for (var component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return Optional.of(component);
            }
            
            if (component instanceof Container) {
                var found = findComponent((Container)component, name);
                if (found.isPresent()) {
                    return found;
                }
            }
        }

        return Optional.empty(); 
    }
    
    private void updateTreePanel(BudgetModel model) {
        var tree = (JTree)findComponent(_treePanel, "BudgetSummaryTree").get();
        var treeModel = (DefaultTreeModel)tree.getModel();
        var treeRootNode = (DefaultMutableTreeNode)treeModel.getRoot();

        treeRootNode.removeAllChildren();
        for (var budget : model.getAllBudgets()) {
            var budgetNode = new DefaultMutableTreeNode(budget.name());
            
            var incomeNode = new DefaultMutableTreeNode("Income: " + budget.incomes().toString());
            var expenseNode = new DefaultMutableTreeNode("Expenses: " + budget.expenses().toString());
            var balanceNode = new DefaultMutableTreeNode("Balance: " + budget.balance().toString());            
            budgetNode.add(incomeNode);
            budgetNode.add(expenseNode);
            budgetNode.add(balanceNode);
            treeRootNode.add(budgetNode);
        }

        treeModel.reload();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
}