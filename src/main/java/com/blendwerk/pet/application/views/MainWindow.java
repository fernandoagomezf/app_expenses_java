package com.blendwerk.pet.application.views;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import com.blendwerk.pet.application.controllers.DetailsController;
import com.blendwerk.pet.application.controllers.MainController;
import com.blendwerk.pet.application.controllers.TabbedController;
import com.blendwerk.pet.application.controllers.TreeController;
import com.blendwerk.pet.application.models.BudgetModel;

public class MainWindow extends JFrame {
    private TreePanel _budgetTreePanel;
    private DetailsPanel _budgetDetailsPanel;
    private TabbedPane _budgetTabbedPane;
    private StatusBar _statusBar;
    private JMenuBar _menuBar;
    private JToolBar _toolBar;
    private JSplitPane _leftSplitPane;
    private JSplitPane _rightSplitPane;
    
    private TreeController _treeController;
    private TabbedController _tabbedController;
    private DetailsController _detailsController;
    private MainController _mainController;
    
    
    public MainWindow(BudgetModel budgetModel) {
        if (budgetModel == null) {
            throw new IllegalArgumentException("Budget model cannot be null");
        }
        
        setTitle("Blendwerk Personal Expense Tracker");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(920, 680));        
        initializeComponents();

        _treeController = new TreeController(budgetModel, _budgetTreePanel);
        _tabbedController = new TabbedController(budgetModel, _budgetTabbedPane);
        _detailsController = new DetailsController(budgetModel, _budgetDetailsPanel);
        _mainController = new MainController(budgetModel, this);        
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
            JMenuItem closeBudgetItem = new JMenuItem("Close Budget");
            closeBudgetItem.setMnemonic(KeyEvent.VK_C);
            closeBudgetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
            closeBudgetItem.addActionListener(e -> closeCurrentBudget());        
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.setMnemonic(KeyEvent.VK_X);
            exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(newBudgetItem);
        fileMenu.addSeparator();
        fileMenu.add(closeBudgetItem);
        fileMenu.addSeparator();        
        fileMenu.add(exitItem);
        
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);        
        
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);        
            JMenuItem detailsItem = new JMenuItem("Toggle Details Panel");
            detailsItem.setMnemonic(KeyEvent.VK_D);
            detailsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
            detailsItem.addActionListener(e -> toggleDetailsPanel());        
        viewMenu.add(detailsItem);
        
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
        
        JButton newButton = new JButton("New");
        newButton.setToolTipText("Create new budget");
        newButton.addActionListener(e -> createNewBudget());
        
        JButton toggleDetailsButton = new JButton("Details");
        toggleDetailsButton.setToolTipText("Toggle details panel");
        toggleDetailsButton.addActionListener(e -> toggleDetailsPanel());
        
        toolBar.add(newButton);
        toolBar.addSeparator();
        toolBar.add(toggleDetailsButton);
        
        return toolBar;
    }
    
    public void createNewBudget() {
        _mainController.createNewBudget(this);        
    }
    
    private void closeCurrentBudget() {
        System.out.println("Closing current budget...");
        _budgetTabbedPane.closeCurrentTab();
    }
        
    private void toggleDetailsPanel() {
        boolean visible = _budgetDetailsPanel.isVisible();
        _budgetDetailsPanel.setVisible(!visible);
        if (!visible) {
            _rightSplitPane.setDividerLocation(900);
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
}