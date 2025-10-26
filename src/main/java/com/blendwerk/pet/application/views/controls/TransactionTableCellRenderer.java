package com.blendwerk.pet.application.views.controls;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TransactionTableCellRenderer extends DefaultTableCellRenderer {
        private static final Color INCOME_COLOR = new Color(200, 255, 200);  // Pastel green
        private static final Color EXPENSE_COLOR = new Color(255, 220, 200); // Pastel orange/red
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            var c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String type = (String)table
                    .getModel()
                    .getValueAt(row, 0);
                
                if ("Income".equals(type)) {
                    c.setBackground(INCOME_COLOR);
                } else if ("Expense".equals(type)) {
                    c.setBackground(EXPENSE_COLOR);
                } else {
                    c.setBackground(Color.WHITE);
                }
            }
            
            return c;
        }
    }
