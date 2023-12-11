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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * @author gouvo
 */
public class SceduleManager extends JFrame {
    
    private JTable table;
    private ExcelManager excelManager1;
    private List<Course> courses;
    private List<Professor> professors;
    private List<Classroom> classrooms;
    private Scheduled scheduled;
    private Unscheduled unscheduled;
    

    public SceduleManager() {
    }
    
    public SceduleManager(ExcelManager excelManager) {
        initComponents();
        this.excelManager1 = excelManager;
        
        courses = new ArrayList<>(excelManager1.getCourses());
        
        /*
        for (Course crs : courses){
            List<Professor> ex = crs.getExaminers();
            for (Professor prf : ex){
                prf.prinAvailable();
            }
        }
        */
        professors = new ArrayList<>(excelManager1.getProfs());
        classrooms = new ArrayList<>(excelManager1.getClassrooms());
        scheduled = new Scheduled();
        unscheduled = new Unscheduled(excelManager1.getCourses());
        modelPanel.setLayout(new BorderLayout());
        modelPanel.add(populateTable());
        coursesPanel.setLayout(new GridLayout(0,3));
        populateCourses();
        for (Course course : courses){
            for (Professor prf : course.getExaminers()){
                //prf.prinAvailable();
            }
        }
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public void populateCourses() {
        List<Course> notSettled = new ArrayList<>(unscheduled.getCourses());
        for (Course course : notSettled) {
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
    
    public boolean checkExaminersConflict(Course course, String date, String timeslot){
        boolean myBool = false;
        List<Professor> newCourseExaminers = null;
        for (Course crs : unscheduled.getCourses()){
            if (crs.getCourseName().equals(course.getCourseName())){
                newCourseExaminers = new ArrayList<>(course.getExaminers());
                for (Professor prf1 : newCourseExaminers){
                    System.out.println(prf1.getProfSurname());
                }
                for (Professor prf1 : newCourseExaminers){
                    int res1 = prf1.isAvailable(date, timeslot);
                    if (res1 == 0 || res1 == 2){
                        return false;
                    }
                }
                for (Professor prf1 : newCourseExaminers){
                    prf1.changeSpecificAvailability(date, timeslot, 2);
                }
                scheduled.addCourse(crs, date, timeslot);
                unscheduled.getCourses().remove(crs);
                
            }
            if (newCourseExaminers != null){
                return true;
            }
        }
        return false;
    }
    
    public JScrollPane populateTable(){
        List<String> timeslots = excelManager1.getTimeslots();
        List<String> dates = new ArrayList<>(excelManager1.getDates());
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
        table.setRowHeight(60);
        table.setCellSelectionEnabled(false);
        table.setTableHeader(new JTableHeader());
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = evt.getTransferable();
                    String buttonText = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    Point dropLocation = evt.getLocation();
                    int row = table.rowAtPoint(dropLocation);
                    int col = table.columnAtPoint(dropLocation);
                    
                    Course tmpCourse = new Course(findCourse(buttonText));
                    String rowValue = (String) table.getValueAt(row, 0);
                    String colValue = (String) table.getValueAt(0, col);
                    
                    // Format from 'dd/mm/YYYY Weekday' to simply 'dd/mm/YYYY'
                    rowValue = getDateWithGreekFormat(rowValue);
                    boolean check1 = checkExaminersConflict(tmpCourse, rowValue, colValue);
                    if (check1){
                        table.getColumnModel().getColumn(col).setCellRenderer(new CustomCellRenderer(Color.GREEN));
                        model.setValueAt(buttonText, row, col);
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
                    }else{
                        //evt.rejectDrop();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    evt.rejectDrop();
                }
            }
        });
        
        

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    int selectedColumn = table.getSelectedColumn();
                    String date = table.getValueAt(selectedRow, 0).toString();
                    String timeslot = table.getValueAt(0, selectedColumn).toString();
                    Object cellValue = model.getValueAt(selectedRow, selectedColumn);
                    Course courseToDelete = null;
                    if (cellValue != null && cellValue instanceof String) {
                        String buttonText = (String) cellValue;
                        for (Course crs : scheduled.getCourses()){
                            if (crs.getCourseName().equals(buttonText)){
                                for (Professor prf : crs.getExaminers()){
                                    prf.setAvailable(date, timeslot);
                                }
                                courseToDelete = crs;
                            }
                        }
                        if (courseToDelete != null){
                            unscheduled.getCourses().add(courseToDelete);
                            scheduled.getCourses().remove(courseToDelete);
                        }
                        // Add the button back to coursesPanel
                        JButton button = new JButton(buttonText);
                        button.setPreferredSize(new Dimension(270, 40));
                        button.setBackground(Color.lightGray);
                        button.setTransferHandler(new ButtonTransferHandler(buttonText));
                        button.addMouseListener(new MouseAdapter() {
                            @Override
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
    
    public String getDateWithGreekFormat(String dateStr){
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy EEEE", new Locale("el"));
        LocalDate date = LocalDate.parse(dateStr, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Format the LocalDate to the desired output pattern
        String formattedDate = date.format(outputFormatter);
        return formattedDate;
    }
    
    public boolean checkAvailabilityForProfessors(Course course, String dateStr, String timeslotStr){
        List<Professor> profs = new ArrayList<>(course.getExaminers());
        List<Integer> results = new ArrayList<>();
        String date = getDateWithGreekFormat(dateStr);
        for (Professor prof : profs){
            int tmp = prof.isAvailable(date, timeslotStr);
            results.add(tmp);
        }
        for (Integer i : results){
            if (i == 0 || i == 2){
                return false;
            }
        }
        return true;
    }
    
    public Course findCourse(String tmp){
        for (Course course : this.courses){
            if (course.getCourseName().equals(tmp)){
                return course;
            }
        }
        return null;
    }
    
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
    
    class CustomCellRenderer extends DefaultTableCellRenderer {
        private Color backgroundColor;

        public CustomCellRenderer(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBackground(backgroundColor);
            return cell;
        }
    }

}

