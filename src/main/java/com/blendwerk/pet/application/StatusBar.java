package com.blendwerk.pet.application;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Status bar component showing date/time and application status information.
 * Updates automatically to show current date and time.
 */
public class StatusBar extends JPanel {
    private JLabel dateTimeLabel;
    private JLabel statusLabel;
    private JLabel memoryLabel;
    private Timer timer;
    private SimpleDateFormat dateFormat;
    
    public StatusBar() {
        initializeComponents();
        setupLayout();
        initializeTimer();
    }
    
    private void initializeComponents() {
        // Date/time label
        dateTimeLabel = new JLabel();
        dateTimeLabel.setHorizontalAlignment(JLabel.LEFT);
        
        // Status message label
        statusLabel = new JLabel("Ready");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Memory usage label
        memoryLabel = new JLabel();
        memoryLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        // Date format
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // Set initial values
        updateDateTime();
        updateMemoryInfo();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(0, 25));
        
        // Left panel - Date/Time
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leftPanel.add(dateTimeLabel);
        
        // Center panel - Status
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        centerPanel.add(statusLabel);
        
        // Right panel - Memory info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        rightPanel.add(memoryLabel);
        
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private void initializeTimer() {
        // Update every second
        timer = new Timer(1000, e -> {
            updateDateTime();
            updateMemoryInfo();
        });
        timer.start();
    }
    
    private void updateDateTime() {
        Date now = new Date();
        dateTimeLabel.setText(dateFormat.format(now));
    }
    
    private void updateMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        // Convert to MB
        long usedMB = usedMemory / (1024 * 1024);
        long totalMB = totalMemory / (1024 * 1024);
        
        memoryLabel.setText(String.format("Memory: %d/%d MB", usedMB, totalMB));
    }
    
    /**
     * Set the status message displayed in the center of the status bar.
     */
    public void setStatus(String message) {
        statusLabel.setText(message);
    }
    
    /**
     * Set the status message temporarily, then revert to "Ready" after a delay.
     */
    public void setTemporaryStatus(String message, int delayMilliseconds) {
        statusLabel.setText(message);
        
        Timer tempTimer = new Timer(delayMilliseconds, e -> statusLabel.setText("Ready"));
        tempTimer.setRepeats(false);
        tempTimer.start();
    }
    
    /**
     * Set custom date format for the date/time display.
     */
    public void setDateFormat(String pattern) {
        try {
            dateFormat = new SimpleDateFormat(pattern);
            updateDateTime();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid date format pattern: " + pattern);
        }
    }
    
    /**
     * Stop the timer when the status bar is no longer needed.
     */
    public void stopTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
    
    /**
     * Start the timer if it's been stopped.
     */
    public void startTimer() {
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }
    }
}