package com.blendwerk.pet.application.views.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TransactionDialog {
    private final JFrame _parent;
    private boolean _confirmed;

    public TransactionDialog(JFrame parent) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent frame cannot be null");
        }
        _parent = parent;
        _confirmed = false;
    }

    public void show() {
        var panel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        int result = JOptionPane.showConfirmDialog(
            _parent,
            panel,
            "Add Transaction",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        _confirmed = (result == JOptionPane.OK_OPTION);
    }

    public boolean isConfirmed() {
        return _confirmed;
    }
}
