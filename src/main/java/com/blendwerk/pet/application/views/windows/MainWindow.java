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
import java.util.stream.Collectors;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import com.blendwerk.pet.application.models.Model;
import com.blendwerk.pet.application.models.TransactionInput;
import com.blendwerk.pet.application.models.BudgetInput;
import com.blendwerk.pet.application.views.View;
import com.blendwerk.pet.application.views.ViewListener;
import com.blendwerk.pet.application.views.controls.TransactionTableCellRenderer;
import com.blendwerk.pet.application.views.controls.TreeCellRenderer;
import com.blendwerk.pet.domain.budgeting.Budget;

public class MainWindow extends JFrame implements View {
    private JMenuBar _menuBar;
    private JToolBar _toolBar;
    private JPanel _statusPanel;
    private JPanel _treePanel;
    private JPanel _dataPanel;
    private JPanel _detailsPanel;
    private JSplitPane _leftSplitPane;
    private JSplitPane _rightSplitPane;
    private final List<ViewListener> _listeners;
    
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
            newBudgetItem.setIcon(loadIcon("icons8-budget-48.png"));
            newBudgetItem.addActionListener(e -> onCreateNewBudget());                    
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.setMnemonic(KeyEvent.VK_X);
            exitItem.setIcon(loadIcon("icons8-close-48.png"));
            exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(newBudgetItem);
        fileMenu.addSeparator();        
        fileMenu.add(exitItem);
        
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E); 
            JMenuItem addTransactionItem = new JMenuItem("Add Transaction");
            addTransactionItem.setMnemonic(KeyEvent.VK_A);
            addTransactionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
            addTransactionItem.setIcon(loadIcon("icons8-transaction-48.png"));
            addTransactionItem.addActionListener(e -> onAddTransaction());
        editMenu.add(addTransactionItem);
        
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);              
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);        
            JMenuItem aboutItem = new JMenuItem("About");
            aboutItem.setIcon(loadIcon("icons8-about-48.png"));
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
        
        JButton newBudgetButton = new JButton(loadIcon("icons8-budget-48.png"));
        newBudgetButton.setToolTipText("Create new budget");
        newBudgetButton.addActionListener(e -> onCreateNewBudget());  
        JButton addTransactionButton = new JButton(loadIcon("icons8-transaction-48.png"));
        addTransactionButton.setToolTipText("Add new transaction");
        addTransactionButton.addActionListener(e -> onAddTransaction());      
        
        toolBar.add(newBudgetButton);
        toolBar.addSeparator();
        toolBar.add(addTransactionButton);
        
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
        
        tree.setName("BudgetSummaryTree");
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new TreeCellRenderer());
        scrollPane.setPreferredSize(new Dimension(250, 0));
        
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath selectedPath = tree.getSelectionPath();
                if (selectedPath != null) {
                    var lastComponent = selectedPath.getLastPathComponent();
                    if (lastComponent instanceof DefaultMutableTreeNode) {
                        var selectedNode = (DefaultMutableTreeNode) lastComponent;
                        var selectedBudget = selectedNode.getUserObject();
                        if (selectedBudget instanceof Budget) {
                            onSelectBudget((Budget)selectedBudget);
                        }
                    }
                }
            }
        });
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.setPreferredSize(new Dimension(0, 25));

        var statusLabel = new JLabel("Ready");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setName("StatusMessage");
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
        
        transactionTypeLabel.setName("TransactionTypeLabel");
        transactionCategoryLabel.setName("TransactionCategoryLabel");
        transactionAmountLabel.setName("TransactionAmountLabel");
        transactionCurrencyLabel.setName("TransactionCurrencyLabel");
        transactionIdLabel.setName("TransactionIdLabel");
        
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
        var incomeLabel = new JLabel("-");
        incomeLabel.setFont(incomeLabel.getFont().deriveFont(Font.BOLD));
        incomeLabel.setName("BudgetIncomeLabel");
        summaryPanel.add(incomeLabel, gbc);
        
        gbc.gridx = 2; 
        gbc.gridy = 1;
        summaryPanel.add(new JLabel("Total Expenses:"), gbc);
        gbc.gridx = 3;
        var expensesLabel = new JLabel("-");
        expensesLabel.setFont(expensesLabel.getFont().deriveFont(Font.BOLD));
        expensesLabel.setName("BudgetExpensesLabel");
        summaryPanel.add(expensesLabel, gbc);
        
        gbc.gridx = 4; 
        gbc.gridy = 0; 
        gbc.gridheight = 2;
        var balanceLabel = new JLabel(String.format("Balance: -"));
        balanceLabel.setFont(balanceLabel.getFont().deriveFont(Font.BOLD, 16f));
        balanceLabel.setName("BudgetBalanceLabel");
        summaryPanel.add(balanceLabel, gbc);
        
        panel.add(summaryPanel, BorderLayout.NORTH);

        String[] columnNames = { "Type", "Amount", "Currency", "Category" };
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
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Amount
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Currency
        table.getColumnModel().getColumn(3).setPreferredWidth(400); // Category
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {                    
                    var transactionId = (String)table.getClientProperty(selectedRow);
                    onSelectTransaction(transactionId);
                }
            }
        });

        var scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transactions"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public void addListener(ViewListener listener) {
        if (listener != null) {
            _listeners.add(listener);
        }
    }

    public void removeListener(ViewListener listener) {
        _listeners.remove(listener);
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
    
    private void onCreateNewBudget() {
        for (var listener : _listeners) {
            listener.requestNewBudget();
        }
    }

    private void onAddTransaction() {
        for (var listener : _listeners) {
            listener.requestNewTransaction();
        }
    }

    private void onSelectBudget(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be null");
        }
        for (var listener : _listeners) {
            listener.requestSelectBudget(budget.id().toString());
        }
    }

    private void onSelectTransaction(String transactionId) {
        if (transactionId == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        for (var listener : _listeners) {
            listener.requestSelectTransaction(transactionId);
        }
    }
    
    private void selectTransaction(String type, String amount, String currency, String category) {
        var typeLabel = (JLabel) findComponent(_detailsPanel, "TransactionTypeLabel").orElse(null);
        var categoryLabel = (JLabel) findComponent(_detailsPanel, "TransactionCategoryLabel").orElse(null);
        var amountLabel = (JLabel) findComponent(_detailsPanel, "TransactionAmountLabel").orElse(null);
        var currencyLabel = (JLabel) findComponent(_detailsPanel, "TransactionCurrencyLabel").orElse(null);
        
        if (typeLabel != null) {
            typeLabel.setText(type);
        }
        if (categoryLabel != null) {
            categoryLabel.setText(category);
        }
        if (amountLabel != null) {
            amountLabel.setText(amount);
        }
        if (currencyLabel != null) {
            currencyLabel.setText(currency);
        }

    }
    
    private void clearTransactionDetails() {
        var typeLabel = (JLabel) findComponent(_detailsPanel, "TransactionTypeLabel").orElse(null);
        var categoryLabel = (JLabel) findComponent(_detailsPanel, "TransactionCategoryLabel").orElse(null);
        var amountLabel = (JLabel) findComponent(_detailsPanel, "TransactionAmountLabel").orElse(null);
        var currencyLabel = (JLabel) findComponent(_detailsPanel, "TransactionCurrencyLabel").orElse(null);
        
        if (typeLabel != null) {
            typeLabel.setText("No Transaction Selected");
        }
        if (categoryLabel != null) {
            categoryLabel.setText("Category: -");
        }
        if (amountLabel != null) {
            amountLabel.setText("Amount: -");
        }
        if (currencyLabel != null) {
            currencyLabel.setText("Currency: -");
        }
    }

    public void showError(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        var statusLabel = (JLabel)findComponent(_statusPanel, "StatusMessage").get();
        statusLabel.setText("Error: " + message);

        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        
    }

    public void showSuccess(String message, boolean echo) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        
        var statusLabel = (JLabel)findComponent(_statusPanel, "StatusMessage").get();
        statusLabel.setText(message);

        if (echo) {
            JOptionPane.showMessageDialog(
                this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public Optional<BudgetInput> getNewBudget() {
        var dialog = new BudgetDialog(this);
        dialog.show();
        
        Optional<BudgetInput> result = Optional.empty();
        if (dialog.isConfirmed()) {
            var budgetName = dialog.getBudgetName();
            var currency = dialog.getCurrency();
            
            var input = new BudgetInput(budgetName, currency);
            result = Optional.of(input);
        }

        return result;
    }

    public Optional<TransactionInput> getNewTransaction() {
        var dialog = new TransactionDialog(this);
        dialog.show();
        
        Optional<TransactionInput> result = Optional.empty();
        if (dialog.isConfirmed()) {
            var type = dialog.getType();
            var category = dialog.getCategory();
            var amount = String.valueOf(dialog.getAmount());
            var currency = dialog.getCurrency();
            var input = new TransactionInput(amount, currency, category, type);
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

    public void updateModel(Model model) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        updateTreePanel(model);
        updateDataPanel(model);
        updateDetailsPanel(model);
    }

    private void updateDataPanel(Model model) {
        var nameLabel = (JLabel)findComponent(_dataPanel, "BudgetNameLabel").get();
        var currencyLabel = (JLabel)findComponent(_dataPanel, "BudgetCurrencyLabel").get();
        var incomeLabel = (JLabel)findComponent(_dataPanel, "BudgetIncomeLabel").get();
        var expensesLabel = (JLabel)findComponent(_dataPanel, "BudgetExpensesLabel").get();
        var balanceLabel = (JLabel)findComponent(_dataPanel, "BudgetBalanceLabel").get();
        var table = (JTable)findComponent(_dataPanel, "TransactionTable").get();
        var tableModel = (DefaultTableModel)table.getModel();

        tableModel.setRowCount(0);

        var selectedBudgetOpt = model.getSelectedBudget();
        if (selectedBudgetOpt.isPresent()) {
            var budget = selectedBudgetOpt.get();
            nameLabel.setText(budget.name());
            currencyLabel.setText(budget.currency().toString());
            incomeLabel.setText(budget.incomes().toString());
            expensesLabel.setText(budget.expenses().toString());
            balanceLabel.setText(budget.balance().toString());
            
            var balance = budget.balance();
            if (balance.isPositive()) {
                balanceLabel.setForeground(new Color(0, 150, 0)); 
            } else if (balance.isNegative()) {
                balanceLabel.setForeground(new Color(200, 0, 0));
            } else {
                balanceLabel.setForeground(Color.BLACK);
            }

            var transactions = budget.stream().collect(Collectors.toList());
            var idx = 0;
            for (var transaction : transactions) {
                Object[] rowData = {
                    transaction.sign() >= 0 ? "Income" : "Expense",
                    transaction.amount().toString(),
                    transaction.amount().currency().toString(),
                    transaction.category()
                };
                tableModel.addRow(rowData);
                table.putClientProperty(idx, transaction.id().toString());
                idx++;
            }
            
            var cellRenderer = new TransactionTableCellRenderer();
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel()
                     .getColumn(i)
                     .setCellRenderer(cellRenderer);
            }
        } else {
            nameLabel.setText("-");
            currencyLabel.setText("-");
            incomeLabel.setText("-");
            expensesLabel.setText("-");
            balanceLabel.setText(String.format("Balance: -"));
            balanceLabel.setForeground(Color.BLACK);
        }

        
    }

    private void updateDetailsPanel(Model model) {
        var selectedTransactionOpt = model.getSelectedTransaction();
        if (selectedTransactionOpt.isPresent()) {
            var transaction = selectedTransactionOpt.get();
            selectTransaction(
                transaction.sign() >= 0 ? "Income" : "Expense",
                transaction.amount().toString(),
                transaction.amount().currency().toString(),
                transaction.category()
            );
        } else {
            clearTransactionDetails();
        }
    }
    
    private void updateTreePanel(Model model) {
        var tree = (JTree)findComponent(_treePanel, "BudgetSummaryTree").get();
        var treeModel = (DefaultTreeModel)tree.getModel();
        var treeRootNode = (DefaultMutableTreeNode)treeModel.getRoot();

        treeRootNode.removeAllChildren();
        for (var budget : model.getAllBudgets()) {
            var budgetNode = new DefaultMutableTreeNode(budget);            
            
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