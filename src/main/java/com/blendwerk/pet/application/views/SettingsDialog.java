package com.blendwerk.pet.application.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SettingsDialog extends JDialog {
    private JPanel _buttonPanel;
    private JButton _okButton;
    private JButton _cancelButton;
    private JButton _resetButton;    
    private JTabbedPane _tabbedPane;
    private boolean _settingsChanged;
    
    public SettingsDialog(Frame owner) {
        super(owner, "Application Settings", true);
        _settingsChanged = false;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(owner);
        
        initializeComponents();
        loadSettings();
                
        pack();
    }
    
    private void initializeComponents() {
        _okButton = new JButton("OK");
        _cancelButton = new JButton("Cancel");
        _resetButton = new JButton("Reset to Defaults");
        _tabbedPane = new JTabbedPane();
        _buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        getRootPane().setDefaultButton(_okButton);
        setLayout(new BorderLayout());

        _okButton.addActionListener(e -> acceptSettings());        
        _cancelButton.addActionListener(e -> dispose());        
        _resetButton.addActionListener(e -> resetSettings());
        
        _tabbedPane.addTab("General", createGeneralPanel());   
                
        _buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));        
        _buttonPanel.add(_resetButton);
        _buttonPanel.add(Box.createHorizontalStrut(20));
        _buttonPanel.add(_cancelButton);
        _buttonPanel.add(_okButton);        

        add(_tabbedPane, BorderLayout.CENTER);
        add(_buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
                
        return panel;
    }
            
    private void loadSettings() {
        _settingsChanged = false;
    }

    private void acceptSettings() {
        applySettings();
        dispose();
    }
    
    private void resetSettings() {
        var result = JOptionPane.showConfirmDialog(
            this,
            "Reset all settings to default values?",
            "Reset Settings",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            resetToDefaults();
        }        
    }
    
    private void applySettings() {        
        System.out.println("Settings applied.");
    }
    
    private void resetToDefaults() {
        _settingsChanged = true;
        
        System.out.println("Settings reset to defaults.");
    }
    
    public boolean hasSettingsChanged() {
        return _settingsChanged;
    }
}