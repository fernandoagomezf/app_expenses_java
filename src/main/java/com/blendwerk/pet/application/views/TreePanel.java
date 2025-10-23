package com.blendwerk.pet.application.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class TreePanel extends JPanel {
    private MainWindow _mainWindow;
    private DefaultTreeModel _treeModel;
    private DefaultMutableTreeNode _rootNode;
    private JTree _tree;
    private JScrollPane _scrollPane;
    private JToolBar _treeToolBar;
    private JButton _refreshButton;
    
    public TreePanel(MainWindow mainWindow) {
        if (mainWindow == null) {
            throw new IllegalArgumentException("The main window cannot be null");
        }
        _mainWindow = mainWindow;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Budgets"));

        initializeComponents();
        populateWithSampleData();
    }
    
    private void initializeComponents() {
        _rootNode = new DefaultMutableTreeNode("Budgets");
        _treeModel = new DefaultTreeModel(_rootNode);
        _tree = new JTree(_treeModel);
        _scrollPane = new JScrollPane(_tree);
        _treeToolBar = new JToolBar(JToolBar.HORIZONTAL);
        _refreshButton= new JButton("Refresh");
        
        _tree.setRootVisible(true);
        _tree.setShowsRootHandles(true);
        _tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        _tree.setCellRenderer(new TreeCellRenderer());
        _scrollPane.setPreferredSize(new Dimension(250, 0));
        _treeToolBar.setFloatable(false);
        _refreshButton.setToolTipText("Refresh budget tree");
        _refreshButton.addActionListener(e -> refreshTree());        
        _treeToolBar.add(_refreshButton);

        _tree.addTreeSelectionListener(e -> { onTreeSelectionChanged(e); });
        _tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { onMouseClicked(e); }            
            public void mousePressed(MouseEvent e) { onMousePressedOrReleased(e); }            
            public void mouseReleased(MouseEvent e) { onMousePressedOrReleased(e); }
        });
        
        add(_scrollPane, BorderLayout.CENTER);
        add(_treeToolBar, BorderLayout.NORTH);
    }

    private void onMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            var path = _tree.getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                var node = (DefaultMutableTreeNode) path.getLastPathComponent();
                handleDoubleClick(node);
            }
        }
    }

    private void onMousePressedOrReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showContextMenu(e);
        }
    }

    private void onTreeSelectionChanged(TreeSelectionEvent e) {
        var path = e.getNewLeadSelectionPath();
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            handleSelection(node);
        }
    }
    
    private void populateWithSampleData() {
        var q1_2025 = new DefaultMutableTreeNode("Q1 2025");
        var q2_2025 = new DefaultMutableTreeNode("Q2 2025");
        
        var january = new DefaultMutableTreeNode("January 2025 Budget");
        populateSubNodesWithSampleData(january);        
        var february = new DefaultMutableTreeNode("February 2025 Budget");
        populateSubNodesWithSampleData(february);
        
        q1_2025.add(january);
        q1_2025.add(february);
        
        var april = new DefaultMutableTreeNode("April 2025 Budget");
        populateSubNodesWithSampleData(april);
        
        q2_2025.add(april);
        
        _rootNode.add(q1_2025);
        _rootNode.add(q2_2025);
        
        expandAllNodes();
        _treeModel.reload();
    }
    
    private void populateSubNodesWithSampleData(DefaultMutableTreeNode budgetNode) {
        var income = new DefaultMutableTreeNode("$ 5,000.00");
        var expenses = new DefaultMutableTreeNode("$ 3,500.00");
        var balance = new DefaultMutableTreeNode("$ 1,500.00");
        
        budgetNode.add(income);
        budgetNode.add(expenses);
        budgetNode.add(balance);
    }
    
    private void expandAllNodes() {
        for (int i = 0; i < _tree.getRowCount(); i++) {
            _tree.expandRow(i);
        }
    }
    
    private void handleDoubleClick(DefaultMutableTreeNode node) {
        var nodeText = node.toString();        
        if (isBudgetNode(node)) {
            System.out.println("Opening budget tab for: " + nodeText);
            _mainWindow.openBudgetTab(nodeText);
        }
    }
    
    private void handleSelection(DefaultMutableTreeNode node) {
        String nodeText = node.toString();
        
        // If it's a budget node, we could show some summary info, but for now just log
        if (isBudgetNode(node)) {
            System.out.println("Selected budget: " + nodeText);
            // Note: We don't automatically show details for budget selection anymore
            // Details panel now shows transaction details when a transaction is selected
        }
    }
    
    private boolean isBudgetNode(DefaultMutableTreeNode node) {
        return node.getLevel() == 2;
    }
    
    private void showContextMenu(MouseEvent e) {
        TreePath path = _tree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            _tree.setSelectionPath(path);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            
            JPopupMenu contextMenu = createContextMenu(node);
            contextMenu.show(_tree, e.getX(), e.getY());
        }
    }
    
    private JPopupMenu createContextMenu(DefaultMutableTreeNode node) {
        var menu = new JPopupMenu();
        
        if (isBudgetNode(node)) {
            var openTab = new JMenuItem("Open in Tab");
            openTab.addActionListener(e -> _mainWindow.openBudgetTab(node.toString()));
            
            var showDetails = new JMenuItem("Show Details");
            showDetails.addActionListener(e -> {
                System.out.println("Showing details for budget: " + node.toString());
                _mainWindow.openBudgetTab(node.toString());
            });
            
            var addTransaction = new JMenuItem("Add Transaction");
            addTransaction.addActionListener(e -> {
                System.out.println("Adding transaction to: " + node.toString());
            });
            
            menu.add(openTab);
            menu.add(showDetails);
            menu.addSeparator();
            menu.add(addTransaction);
        } else if (node == _rootNode || node.getParent() == _rootNode) {
            var addBudget = new JMenuItem("Add Budget");
            addBudget.addActionListener(e -> addBudgetToGroup(node));
            
            menu.add(addBudget);
        }
        
        return menu;
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
            populateSubNodesWithSampleData(newBudget);
            groupNode.add(newBudget);
            _treeModel.reload();
            System.out.println("Added budget: " + budgetName + " to group: " + groupNode.toString());
        }
    }
    
    private void refreshTree() {
        System.out.println("Refreshing budget tree...");
        _treeModel.reload();
        expandAllNodes();
    }
}