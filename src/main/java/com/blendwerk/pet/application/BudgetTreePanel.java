package com.blendwerk.pet.application;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Left panel containing a tree view of budgets organized by grouping criteria.
 * Supports double-clicking to open budget tabs and context menu operations.
 */
public class BudgetTreePanel extends JPanel {
    private JTree budgetTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private MainWindow mainWindow;
    
    public BudgetTreePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateWithSampleData();
    }
    
    private void initializeComponents() {
        // Create root node
        rootNode = new DefaultMutableTreeNode("Budgets");
        treeModel = new DefaultTreeModel(rootNode);
        budgetTree = new JTree(treeModel);
        
        // Tree configuration
        budgetTree.setRootVisible(true);
        budgetTree.setShowsRootHandles(true);
        budgetTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // Custom tree cell renderer to show different icons for different node types
        budgetTree.setCellRenderer(new BudgetTreeCellRenderer());
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Budgets"));
        
        JScrollPane scrollPane = new JScrollPane(budgetTree);
        scrollPane.setPreferredSize(new Dimension(250, 0));
        add(scrollPane, BorderLayout.CENTER);
        
        // Add toolbar for tree operations
        JToolBar treeToolBar = new JToolBar(JToolBar.VERTICAL);
        treeToolBar.setFloatable(false);
        
        JButton addGroupButton = new JButton("+ Group");
        addGroupButton.setToolTipText("Add budget group");
        addGroupButton.addActionListener(e -> addBudgetGroup());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setToolTipText("Refresh budget tree");
        refreshButton.addActionListener(e -> refreshTree());
        
        treeToolBar.add(addGroupButton);
        treeToolBar.add(refreshButton);
        
        add(treeToolBar, BorderLayout.EAST);
    }
    
    private void setupEventHandlers() {
        // Double-click to open budget tab
        budgetTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = budgetTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        handleDoubleClick(node);
                    }
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
        });
        
        // Selection listener to update details panel
        budgetTree.addTreeSelectionListener(e -> {
            TreePath path = e.getNewLeadSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                handleSelection(node);
            }
        });
    }
    
    private void populateWithSampleData() {
        // Sample data structure
        DefaultMutableTreeNode q1_2025 = new DefaultMutableTreeNode("Q1 2025");
        DefaultMutableTreeNode q2_2025 = new DefaultMutableTreeNode("Q2 2025");
        DefaultMutableTreeNode monthly = new DefaultMutableTreeNode("Monthly");
        
        // Q1 2025 budgets
        DefaultMutableTreeNode january = new DefaultMutableTreeNode("January 2025 Budget");
        addBudgetSubNodes(january);
        
        DefaultMutableTreeNode february = new DefaultMutableTreeNode("February 2025 Budget");
        addBudgetSubNodes(february);
        
        q1_2025.add(january);
        q1_2025.add(february);
        
        // Q2 2025 budgets  
        DefaultMutableTreeNode april = new DefaultMutableTreeNode("April 2025 Budget");
        addBudgetSubNodes(april);
        
        q2_2025.add(april);
        
        // Monthly budgets
        DefaultMutableTreeNode vacation = new DefaultMutableTreeNode("Vacation Budget");
        addBudgetSubNodes(vacation);
        
        monthly.add(vacation);
        
        rootNode.add(q1_2025);
        rootNode.add(q2_2025);
        rootNode.add(monthly);
        
        // Expand all nodes initially
        expandAllNodes();
        treeModel.reload();
    }
    
    private void addBudgetSubNodes(DefaultMutableTreeNode budgetNode) {
        DefaultMutableTreeNode income = new DefaultMutableTreeNode("Income");
        DefaultMutableTreeNode expenses = new DefaultMutableTreeNode("Expenses");
        DefaultMutableTreeNode balance = new DefaultMutableTreeNode("Balance: $2,500.00");
        
        budgetNode.add(income);
        budgetNode.add(expenses);
        budgetNode.add(balance);
    }
    
    private void expandAllNodes() {
        for (int i = 0; i < budgetTree.getRowCount(); i++) {
            budgetTree.expandRow(i);
        }
    }
    
    private void handleDoubleClick(DefaultMutableTreeNode node) {
        String nodeText = node.toString();
        
        // Check if it's a budget node (has Income/Expenses/Balance children)
        if (isBudgetNode(node)) {
            System.out.println("Opening budget tab for: " + nodeText);
            mainWindow.openBudgetTab(nodeText);
        }
    }
    
    private void handleSelection(DefaultMutableTreeNode node) {
        String nodeText = node.toString();
        
        // If it's a budget node, show details
        if (isBudgetNode(node)) {
            System.out.println("Selected budget: " + nodeText);
            mainWindow.showBudgetDetails(nodeText);
        }
    }
    
    private boolean isBudgetNode(DefaultMutableTreeNode node) {
        // A budget node should have exactly 3 children: Income, Expenses, Balance
        return node.getChildCount() == 3 && 
               node.getChildAt(0).toString().equals("Income") &&
               node.getChildAt(1).toString().equals("Expenses") &&
               node.getChildAt(2).toString().startsWith("Balance:");
    }
    
    private void showContextMenu(MouseEvent e) {
        TreePath path = budgetTree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            budgetTree.setSelectionPath(path);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            
            JPopupMenu contextMenu = createContextMenu(node);
            contextMenu.show(budgetTree, e.getX(), e.getY());
        }
    }
    
    private JPopupMenu createContextMenu(DefaultMutableTreeNode node) {
        JPopupMenu menu = new JPopupMenu();
        
        if (isBudgetNode(node)) {
            JMenuItem openTab = new JMenuItem("Open in Tab");
            openTab.addActionListener(e -> mainWindow.openBudgetTab(node.toString()));
            
            JMenuItem showDetails = new JMenuItem("Show Details");
            showDetails.addActionListener(e -> mainWindow.showBudgetDetails(node.toString()));
            
            JMenuItem addTransaction = new JMenuItem("Add Transaction");
            addTransaction.addActionListener(e -> {
                System.out.println("Adding transaction to: " + node.toString());
                // This would typically call mainWindow.addTransaction() with budget context
            });
            
            menu.add(openTab);
            menu.add(showDetails);
            menu.addSeparator();
            menu.add(addTransaction);
        } else if (node == rootNode || node.getParent() == rootNode) {
            // Group node
            JMenuItem addBudget = new JMenuItem("Add Budget");
            addBudget.addActionListener(e -> addBudgetToGroup(node));
            
            menu.add(addBudget);
        }
        
        return menu;
    }
    
    private void addBudgetGroup() {
        String groupName = JOptionPane.showInputDialog(
            this,
            "Enter group name:",
            "Add Budget Group",
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (groupName != null && !groupName.trim().isEmpty()) {
            DefaultMutableTreeNode newGroup = new DefaultMutableTreeNode(groupName.trim());
            rootNode.add(newGroup);
            treeModel.reload();
            System.out.println("Added budget group: " + groupName);
        }
    }
    
    private void addBudgetToGroup(DefaultMutableTreeNode groupNode) {
        String budgetName = JOptionPane.showInputDialog(
            this,
            "Enter budget name:",
            "Add Budget to " + groupNode.toString(),
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (budgetName != null && !budgetName.trim().isEmpty()) {
            DefaultMutableTreeNode newBudget = new DefaultMutableTreeNode(budgetName.trim());
            addBudgetSubNodes(newBudget);
            groupNode.add(newBudget);
            treeModel.reload();
            System.out.println("Added budget: " + budgetName + " to group: " + groupNode.toString());
        }
    }
    
    private void refreshTree() {
        System.out.println("Refreshing budget tree...");
        treeModel.reload();
        expandAllNodes();
    }
    
    /**
     * Custom tree cell renderer to show different icons for different node types
     */
    private static class BudgetTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            String nodeText = node.toString();
            
            // Set different icons based on node type
            if (node.isRoot()) {
                setIcon(UIManager.getIcon("FileView.directoryIcon"));
            } else if (nodeText.equals("Income")) {
                setIcon(UIManager.getIcon("FileView.fileIcon"));
                setForeground(new Color(0, 128, 0)); // Green
            } else if (nodeText.equals("Expenses")) {
                setIcon(UIManager.getIcon("FileView.fileIcon"));
                setForeground(new Color(128, 0, 0)); // Red
            } else if (nodeText.startsWith("Balance:")) {
                setIcon(UIManager.getIcon("FileView.fileIcon"));
                setForeground(new Color(0, 0, 128)); // Blue
            } else if (node.getChildCount() > 0) {
                // Group or budget node
                setIcon(expanded ? UIManager.getIcon("Tree.openIcon") : UIManager.getIcon("Tree.closedIcon"));
            }
            
            return this;
        }
    }
}