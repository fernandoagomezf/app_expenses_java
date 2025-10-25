package com.blendwerk.pet.application.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import com.blendwerk.pet.application.models.BudgetSummary;

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
        
        add(_scrollPane, BorderLayout.CENTER);
        add(_treeToolBar, BorderLayout.NORTH);
    }

    private void expandAllNodes() {
        for (int i = 0; i < _tree.getRowCount(); i++) {
            _tree.expandRow(i);
        }
    }
        
    private void refreshTree() {
        System.out.println("Refreshing budget tree...");
        _treeModel.reload();
        expandAllNodes();
    }
    
    public void updateBudgetTree(Iterable<BudgetSummary> budgetSummaries) {        
        _rootNode.removeAllChildren();        
        for (var budgetSummary : budgetSummaries) {
            var budgetNode = new DefaultMutableTreeNode(budgetSummary.name());
            
            var incomeNode = new DefaultMutableTreeNode("Income: " + budgetSummary.income());
            var expenseNode = new DefaultMutableTreeNode("Expenses: " + budgetSummary.expense());
            var balanceNode = new DefaultMutableTreeNode("Balance: " + budgetSummary.balance());            
            budgetNode.add(incomeNode);
            budgetNode.add(expenseNode);
            budgetNode.add(balanceNode);            
            _rootNode.add(budgetNode);
        }
        
        _treeModel.reload();
        expandAllNodes();
    }    
}