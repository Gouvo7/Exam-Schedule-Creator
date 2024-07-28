package utils;

/**
 *
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MultiLineCellRenderer extends JLabel implements TableCellRenderer {

    public MultiLineCellRenderer() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.TOP);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText("<html><div style='text-align: center;'>" + (value == null ? "" : value.toString()) + "</div></html>");
        String tmp = this.getText();
        /*
        if(tmp != null && !tmp.isEmpty()){
            this.setForeground(Color.red);
        }
        */
        return this;
    }
}
