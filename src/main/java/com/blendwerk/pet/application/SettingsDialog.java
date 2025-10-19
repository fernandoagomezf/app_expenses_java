package com.blendwerk.pet.application;

import com.blendwerk.pet.domain.Currency;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Settings dialog for application-wide configuration options.
 * Provides common settings like default currency, date format, display preferences, etc.
 */
public class SettingsDialog extends JDialog {
    // General Settings
    private JComboBox<Currency> defaultCurrencyComboBox;
    private JComboBox<String> dateFormatComboBox;
    private JComboBox<String> numberFormatComboBox;
    
    // Display Settings
    private JCheckBox showDetailsOnStartupCheckBox;
    private JCheckBox confirmDeleteCheckBox;
    private JCheckBox autoSaveCheckBox;
    private JSlider autoSaveIntervalSlider;
    private JLabel autoSaveIntervalLabel;
    
    // Appearance Settings
    private JComboBox<String> lookAndFeelComboBox;
    private JSpinner fontSizeSpinner;
    private JCheckBox showToolbarCheckBox;
    private JCheckBox showStatusBarCheckBox;
    
    // Data Settings
    private JTextField defaultBudgetNameField;
    private JCheckBox createBackupsCheckBox;
    private JSpinner backupRetentionSpinner;
    
    // Buttons
    private JButton okButton;
    private JButton cancelButton;
    private JButton applyButton;
    private JButton resetButton;
    
    private boolean settingsChanged = false;
    
    public SettingsDialog(Frame owner) {
        super(owner, "Application Settings", true);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadCurrentSettings();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }
    
    private void initializeComponents() {
        // General Settings
        defaultCurrencyComboBox = new JComboBox<>(Currency.values());
        defaultCurrencyComboBox.setSelectedItem(Currency.MXN);
        
        dateFormatComboBox = new JComboBox<>(new String[]{
            "yyyy-MM-dd", "MM/dd/yyyy", "dd/MM/yyyy", "dd-MMM-yyyy"
        });
        dateFormatComboBox.setSelectedIndex(0);
        
        numberFormatComboBox = new JComboBox<>(new String[]{
            "1,234.56", "1.234,56", "1 234.56", "1234.56"
        });
        numberFormatComboBox.setSelectedIndex(0);
        
        // Display Settings
        showDetailsOnStartupCheckBox = new JCheckBox("Show details panel on startup");
        confirmDeleteCheckBox = new JCheckBox("Confirm before deleting transactions");
        confirmDeleteCheckBox.setSelected(true);
        
        autoSaveCheckBox = new JCheckBox("Auto-save changes");
        autoSaveCheckBox.setSelected(true);
        
        autoSaveIntervalSlider = new JSlider(1, 10, 5);
        autoSaveIntervalSlider.setMajorTickSpacing(1);
        autoSaveIntervalSlider.setPaintTicks(true);
        autoSaveIntervalSlider.setPaintLabels(true);
        autoSaveIntervalLabel = new JLabel("Auto-save interval: 5 minutes");
        
        // Appearance Settings
        lookAndFeelComboBox = new JComboBox<>();
        populateLookAndFeelOptions();
        
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(12, 8, 24, 1));
        
        showToolbarCheckBox = new JCheckBox("Show toolbar");
        showToolbarCheckBox.setSelected(true);
        
        showStatusBarCheckBox = new JCheckBox("Show status bar");
        showStatusBarCheckBox.setSelected(true);
        
        // Data Settings
        defaultBudgetNameField = new JTextField("My Budget", 15);
        
        createBackupsCheckBox = new JCheckBox("Create automatic backups");
        createBackupsCheckBox.setSelected(true);
        
        backupRetentionSpinner = new JSpinner(new SpinnerNumberModel(7, 1, 30, 1));
        
        // Buttons
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        applyButton = new JButton("Apply");
        resetButton = new JButton("Reset to Defaults");
        
        getRootPane().setDefaultButton(okButton);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create tabbed pane for different setting categories
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // General tab
        tabbedPane.addTab("General", createGeneralPanel());
        
        // Display tab
        tabbedPane.addTab("Display", createDisplayPanel());
        
        // Appearance tab
        tabbedPane.addTab("Appearance", createAppearancePanel());
        
        // Data tab
        tabbedPane.addTab("Data", createDataPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        buttonPanel.add(resetButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Default Currency
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Default Currency:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(defaultCurrencyComboBox, gbc);
        
        // Date Format
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Date Format:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dateFormatComboBox, gbc);
        
        // Number Format
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Number Format:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(numberFormatComboBox, gbc);
        
        return panel;
    }
    
    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        
        // Checkboxes
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(showDetailsOnStartupCheckBox, gbc);
        
        gbc.gridy = 1;
        panel.add(confirmDeleteCheckBox, gbc);
        
        gbc.gridy = 2;
        panel.add(autoSaveCheckBox, gbc);
        
        // Auto-save interval
        gbc.gridy = 3; gbc.gridwidth = 1;
        panel.add(new JLabel("Auto-save interval:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(autoSaveIntervalSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(autoSaveIntervalLabel, gbc);
        
        return panel;
    }
    
    private JPanel createAppearancePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Look and Feel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Look and Feel:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lookAndFeelComboBox, gbc);
        
        // Font Size
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Font Size:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fontSizeSpinner, gbc);
        
        // UI Elements
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(showToolbarCheckBox, gbc);
        
        gbc.gridy = 3;
        panel.add(showStatusBarCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createDataPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Default Budget Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Default Budget Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(defaultBudgetNameField, gbc);
        
        // Backup Settings
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(createBackupsCheckBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Backup Retention (days):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(backupRetentionSpinner, gbc);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Auto-save interval slider
        autoSaveIntervalSlider.addChangeListener(e -> {
            int value = autoSaveIntervalSlider.getValue();
            autoSaveIntervalLabel.setText("Auto-save interval: " + value + " minute" + (value != 1 ? "s" : ""));
            settingsChanged = true;
        });
        
        // Auto-save checkbox
        autoSaveCheckBox.addActionListener(e -> {
            autoSaveIntervalSlider.setEnabled(autoSaveCheckBox.isSelected());
            autoSaveIntervalLabel.setEnabled(autoSaveCheckBox.isSelected());
            settingsChanged = true;
        });
        
        // OK button
        okButton.addActionListener(e -> {
            applySettings();
            dispose();
        });
        
        // Cancel button
        cancelButton.addActionListener(e -> dispose());
        
        // Apply button
        applyButton.addActionListener(e -> {
            applySettings();
            settingsChanged = false;
        });
        
        // Reset button
        resetButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Reset all settings to default values?",
                "Reset Settings",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                resetToDefaults();
            }
        });
        
        // Add change listeners to all components
        addChangeListeners();
    }
    
    private void addChangeListeners() {
        ActionListener changeListener = e -> settingsChanged = true;
        
        defaultCurrencyComboBox.addActionListener(changeListener);
        dateFormatComboBox.addActionListener(changeListener);
        numberFormatComboBox.addActionListener(changeListener);
        showDetailsOnStartupCheckBox.addActionListener(changeListener);
        confirmDeleteCheckBox.addActionListener(changeListener);
        lookAndFeelComboBox.addActionListener(changeListener);
        showToolbarCheckBox.addActionListener(changeListener);
        showStatusBarCheckBox.addActionListener(changeListener);
        createBackupsCheckBox.addActionListener(changeListener);
        
        fontSizeSpinner.addChangeListener(e -> settingsChanged = true);
        backupRetentionSpinner.addChangeListener(e -> settingsChanged = true);
        
        defaultBudgetNameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { settingsChanged = true; }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { settingsChanged = true; }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { settingsChanged = true; }
        });
    }
    
    private void populateLookAndFeelOptions() {
        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo info : lafInfo) {
            lookAndFeelComboBox.addItem(info.getName());
        }
        
        // Set current L&F as selected
        String currentLAF = UIManager.getLookAndFeel().getName();
        lookAndFeelComboBox.setSelectedItem(currentLAF);
    }
    
    private void loadCurrentSettings() {
        // In a real implementation, this would load settings from a configuration file
        // For now, we'll just use default values
        
        autoSaveIntervalSlider.setEnabled(autoSaveCheckBox.isSelected());
        autoSaveIntervalLabel.setEnabled(autoSaveCheckBox.isSelected());
        
        settingsChanged = false;
    }
    
    private void applySettings() {
        // In a real implementation, this would save settings to a configuration file
        // and apply them to the application
        
        System.out.println("=== Settings Applied ===");
        System.out.println("Default Currency: " + defaultCurrencyComboBox.getSelectedItem());
        System.out.println("Date Format: " + dateFormatComboBox.getSelectedItem());
        System.out.println("Number Format: " + numberFormatComboBox.getSelectedItem());
        System.out.println("Show Details on Startup: " + showDetailsOnStartupCheckBox.isSelected());
        System.out.println("Confirm Delete: " + confirmDeleteCheckBox.isSelected());
        System.out.println("Auto-save: " + autoSaveCheckBox.isSelected());
        if (autoSaveCheckBox.isSelected()) {
            System.out.println("Auto-save Interval: " + autoSaveIntervalSlider.getValue() + " minutes");
        }
        System.out.println("Look and Feel: " + lookAndFeelComboBox.getSelectedItem());
        System.out.println("Font Size: " + fontSizeSpinner.getValue());
        System.out.println("Show Toolbar: " + showToolbarCheckBox.isSelected());
        System.out.println("Show Status Bar: " + showStatusBarCheckBox.isSelected());
        System.out.println("Default Budget Name: " + defaultBudgetNameField.getText());
        System.out.println("Create Backups: " + createBackupsCheckBox.isSelected());
        if (createBackupsCheckBox.isSelected()) {
            System.out.println("Backup Retention: " + backupRetentionSpinner.getValue() + " days");
        }
        System.out.println("========================");
        
        JOptionPane.showMessageDialog(
            this,
            "Settings have been applied successfully.\nSome changes may require restarting the application.",
            "Settings Applied",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void resetToDefaults() {
        defaultCurrencyComboBox.setSelectedItem(Currency.MXN);
        dateFormatComboBox.setSelectedIndex(0);
        numberFormatComboBox.setSelectedIndex(0);
        showDetailsOnStartupCheckBox.setSelected(false);
        confirmDeleteCheckBox.setSelected(true);
        autoSaveCheckBox.setSelected(true);
        autoSaveIntervalSlider.setValue(5);
        fontSizeSpinner.setValue(12);
        showToolbarCheckBox.setSelected(true);
        showStatusBarCheckBox.setSelected(true);
        defaultBudgetNameField.setText("My Budget");
        createBackupsCheckBox.setSelected(true);
        backupRetentionSpinner.setValue(7);
        
        autoSaveIntervalSlider.setEnabled(autoSaveCheckBox.isSelected());
        autoSaveIntervalLabel.setEnabled(autoSaveCheckBox.isSelected());
        autoSaveIntervalLabel.setText("Auto-save interval: 5 minutes");
        
        settingsChanged = true;
        
        System.out.println("Settings reset to defaults.");
    }
    
    public boolean hasSettingsChanged() {
        return settingsChanged;
    }
}