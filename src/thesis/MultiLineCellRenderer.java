package thesis;

/**
 *
 * @author gouvo
 */
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class MultiLineCellRenderer extends JLabel  implements TableCellRenderer {

    public MultiLineCellRenderer() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText("<html><div style='text-align: center;'>" + (value == null ? "" : value.toString()) + "</div></html>");
        //setText("<html><center>" + (value == null ? "" : value.toString()) + "</center></html>");
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        
        return this;
    }
}
