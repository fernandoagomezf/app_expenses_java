package com.blendwerk.pet.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class StatusBar extends JPanel {
    private JPanel _leftPanel;
    private JLabel _statusLabel;
    
    public StatusBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(0, 25));

        initializeComponents();
    }
    
    private void initializeComponents() {        
        _statusLabel = new JLabel("Ready");
        _statusLabel.setHorizontalAlignment(JLabel.CENTER);
        _leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        _leftPanel.add(_statusLabel);
        
        add(_leftPanel, BorderLayout.WEST);
    }
    
    public void setStatus(String message) {
        _statusLabel.setText(message);
    }
    
    public void setTemporaryStatus(String message, int delayMilliseconds) {
        _statusLabel.setText(message);
        
        Timer timer = new Timer(delayMilliseconds, e -> _statusLabel.setText("Ready"));
        timer.setRepeats(false);
        timer.start();
    }    
}