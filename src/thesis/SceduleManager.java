package thesis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author gouvo
 */
public class SceduleManager extends JFrame {
    
    private JTable table;
    private ExcelManager excelManager;
    private List<Course> courses;
    private List<Professor> professors;
    

    public SceduleManager() {
    }
    
    public SceduleManager(ExcelManager excelManager) {
        initComponents();
        this.excelManager = excelManager;
        courses = new ArrayList<>(excelManager.getCourses());
        professors = new ArrayList<>(excelManager.getProfs());
        modelPanel.setLayout(new BorderLayout());
        modelPanel.add(populateTable());
        coursesPanel.setLayout(new GridLayout(0,3));
        populateCourses();
        
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void populateCourses() {
        for (Course course : courses) {
            // Create a custom component (e.g., JPanel or JButton) for each course
            JButton courseButton = new JButton(course.getCourseName());
            courseButton.setPreferredSize(new Dimension(270, 40)); // Set preferred size as needed
            courseButton.setText(course.getCourseName());
            courseButton.setBackground(Color.lightGray);
            courseButton.setTransferHandler(new ButtonTransferHandler(course.getCourseName()));

            courseButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    JComponent comp = (JComponent) evt.getSource();
                    TransferHandler handler = comp.getTransferHandler();
                    handler.exportAsDrag(comp, evt, TransferHandler.COPY);
                }
            });
            coursesPanel.add(courseButton);
        }
    }
    
    public JScrollPane populateTable(){
        List<String> timeslots = excelManager.getTimeslots();
        List<String> dates = new ArrayList<>(excelManager.getDates());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        int excelRows = dates.size();
        int excelCols = timeslots.size();
        
        // Create a DefaultTableModel with custom data
        DefaultTableModel model = new DefaultTableModel(excelRows + 1, excelCols + 1){
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
            public boolean isCellSelected(int row, int column) {
                // Make all cells non-selectable
                return false;
            }
        };

        for (int j = 0; j < excelCols; j++){
            model.setValueAt(timeslots.get(j), 0, j + 1);
        }

        for (int i = 0; i < excelRows; i++) {
            LocalDate date = LocalDate.parse(dates.get(i), dateFormatter);
            String greekDayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR"));
            model.setValueAt(dates.get(i) + " " + greekDayName, i + 1, 0);
        }
        
        table = new JTable(model);
        table.setRowHeight(40);
        table.setCellSelectionEnabled(false);
        table.setTableHeader(new JTableHeader());
        table.getTableHeader().setReorderingAllowed(false);
        table.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = evt.getTransferable();
                    String buttonText = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    Point dropLocation = evt.getLocation();
                    int row = table.rowAtPoint(dropLocation);
                    int col = table.columnAtPoint(dropLocation);

                    for (Course course : courses){
                        if (course.getCourseName().equals(buttonText)){
                            
                            String rowValue = (String) table.getValueAt(row, 0);
                            String colValue = (String) table.getValueAt(0, col);
                            List<Professor> examiners = new ArrayList<>();
                            System.out.println("Fix");

                            for (Professor prf : course.getExaminers()){
                                System.out.println(prf.getProfSurname());
                            }
                            examiners = course.getExaminers();
                            System.out.println(examiners.size());
                            for (Professor prof : professors){
                                if(examiners.contains(prof)){
                                    System.out.println("I FOUND:" + prof.getProfSurname());
                                }
                            }
                        }
                        
                        
                    }
                    
                    model.setValueAt(buttonText, row, col);

                    // Remove the button from coursesPanel
                    Component[] components = coursesPanel.getComponents();
                    for (Component component : components) {
                        if (component instanceof JButton) {
                            JButton button = (JButton) component;
                            if (buttonText.equals(button.getText())) {
                                coursesPanel.remove(button);
                                break;
                            }
                        }
                    }
                    coursesPanel.revalidate();
                    coursesPanel.repaint();

                    evt.dropComplete(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    evt.rejectDrop();
                }
            }
        });
        

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    int selectedColumn = table.getSelectedColumn();

                    Object cellValue = model.getValueAt(selectedRow, selectedColumn);
                    if (cellValue != null && cellValue instanceof String) {
                        String buttonText = (String) cellValue;

                        // Add the button back to coursesPanel
                        JButton button = new JButton(buttonText);
                        button.setPreferredSize(new Dimension(270, 40));
                        button.setBackground(Color.lightGray);
                        button.setTransferHandler(new ButtonTransferHandler(buttonText));
                        button.addMouseListener(new MouseAdapter() {
                            public void mousePressed(MouseEvent evt) {
                                JComponent comp = (JComponent) evt.getSource();
                                TransferHandler handler = comp.getTransferHandler();
                                handler.exportAsDrag(comp, evt, TransferHandler.COPY);
                            }
                        });

                        coursesPanel.add(button);
                        coursesPanel.revalidate();
                        coursesPanel.repaint();

                        // Clear the cell value
                        model.setValueAt(null, selectedRow, selectedColumn);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return scrollPane;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modelScrollPane = new javax.swing.JScrollPane();
        modelPanel = new javax.swing.JPanel();
        coursesScrollPane = new javax.swing.JScrollPane();
        coursesPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Φόρμα Δημιουργίας Προγράμματος");
        setPreferredSize(new java.awt.Dimension(1200, 1000));
        getContentPane().setLayout(new java.awt.FlowLayout());

        modelScrollPane.setPreferredSize(new java.awt.Dimension(1000, 600));

        modelPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        modelPanel.setPreferredSize(new java.awt.Dimension(900, 500));
        modelPanel.setLayout(null);
        modelScrollPane.setViewportView(modelPanel);

        getContentPane().add(modelScrollPane);

        coursesScrollPane.setPreferredSize(new java.awt.Dimension(1000, 300));

        coursesPanel.setLayout(null);
        coursesScrollPane.setViewportView(coursesPanel);

        getContentPane().add(coursesScrollPane);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SceduleManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SceduleManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SceduleManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SceduleManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SceduleManager().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel coursesPanel;
    private javax.swing.JScrollPane coursesScrollPane;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JScrollPane modelScrollPane;
    // End of variables declaration//GEN-END:variables
}
