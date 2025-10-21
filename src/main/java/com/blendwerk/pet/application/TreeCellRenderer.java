package com.blendwerk.pet.application;

import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeCellRenderer extends DefaultTreeCellRenderer {
    private ImageIcon loadIcon(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        var icon = new ImageIcon("C:\\Users\\SPARTANPC\\source\\repos\\expenses\\src\\main\\resources\\images\\" + fileName);
        var scaled = icon.getImage().getScaledInstance(16, 16,  Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);        
        
        /* the icon files used in the tree were obtained from the following links:
        * https://icons8.com/icon/eYaVJ9Nbqqbw/dollar-bag
        * https://icons8.com/icon/6Rw34qscHREa/pay-date
        * https://icons8.com/icon/rEUVwsj9R7SJ/withdrawal
        * https://icons8.com/icon/oAMgq0s0bDXC/deposit
        * https://icons8.com/icon/szZDhO4hzAsa/scales
        * https://icons8.com/icon/U5tawfPekk34/get-cash
        */
        
        var node = (DefaultMutableTreeNode)value;
        var icon = switch (node.getLevel()) {
            case 0 -> loadIcon("icons8-savings-48.png");
            case 1 -> loadIcon("icons8-pay-date-48.png");
            case 2 -> loadIcon("icons8-budget-48.png");
            case 3 -> {
                if (isIncomeNode(node)) {
                    yield loadIcon("icons8-deposit-48.png");
                } else if (isExpenseNode(node)) {
                    yield loadIcon("icons8-withdrawal-48.png");
                } else if (isBalanceNode(node)) {
                    yield loadIcon("icons8-balance-48.png");
                } else {
                    yield null;
                }
            }
            default -> null;
        };

        if (icon != null) {
            setIcon(icon);
        }
        
        return this;
    }
    
    private boolean isIncomeNode(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        if (parent != null && parent.getChildCount() == 3) {
            return parent.getIndex(node) == 0;
        }
        return false;
    }
    
    private boolean isExpenseNode(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        if (parent != null && parent.getChildCount() == 3) {
            return parent.getIndex(node) == 1;
        }
        return false;
    }
    
    private boolean isBalanceNode(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        if (parent != null && parent.getChildCount() == 3) {
            return parent.getIndex(node) == 2;
        }
        return false;
    }
}
