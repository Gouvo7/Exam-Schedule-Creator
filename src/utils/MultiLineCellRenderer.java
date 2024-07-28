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

public class MultiLineCellRenderer extends JLabel implements TableCellRenderer {

    public MultiLineCellRenderer() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //setText("<html><div style='text-align: center;'>" + (value == null ? "" : value.toString()) + "</div></html>");
        setText("<html><div style='text-align: center; vertical-align: middle;'>" + 
               (value == null ? "" : value.toString()) + "</div></html>");

        setSize(table.getColumnModel().getColumn(column).getWidth(),Short.MAX_VALUE);
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        return this;
    }
    
    public void UpdateHeight(JTable table, int row, int col, int valueCharacters){
        for(int i = 1; i < table.getColumnCount(); i++){
            if(i != col){
                String tmp = (String) table.getValueAt(row, i);
                if (tmp!=null){
                    if(tmp.toString().length() <= 26){
                        table.setRowHeight(row, 40);
                        System.out.println("Got in first case");
                    }else if (tmp.toString().length() <= 48){
                        table.setRowHeight(row, 80);
                        System.out.println("Got in second case");
                    }
                }
            }
        }
        return;
    }
}
