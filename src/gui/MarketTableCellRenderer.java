/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author hieldc
 */
public class MarketTableCellRenderer extends DefaultTableCellRenderer {

    private final HashMap<Integer, Double> widthPrices = new HashMap<>();
    
    public MarketTableCellRenderer() {
        super();
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (column == 0) {
            String symbol = (String) value;
            double d = MarketDisplay.basePrices.get(symbol);
            d *= 0.005;
            widthPrices.put(row, d);
        }
        if (column != 3)
        {
            setForeground(Color.black);
            setBackground(Color.white);
            setText(value.toString());
            return this;
        }
        
        String cleanValue = value.toString().replaceAll("\\$", "");
        double doubleValue = Double.parseDouble(cleanValue);
        doubleValue = Math.abs(doubleValue);


        System.out.println(row);
        if (doubleValue == 0.00) {
            setForeground(Color.black);
            setBackground(Color.lightGray);
        } else if (doubleValue < widthPrices.get(row)) {
            setForeground(Color.black);
            setBackground(Color.green);
        } else if (doubleValue < (widthPrices.get(row) * 2)) {
            setForeground(Color.black);
            setBackground(Color.cyan);
        } else {
            setForeground(Color.black);
            setBackground(Color.white);
        }
        setText(value.toString());
        return this;
    }
}
