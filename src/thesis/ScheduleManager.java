package thesis;

// 1) When I insert a course into another course above it, I have to check the availability of the new course and if thats okay, I can add the course into the timeslot BUT for the course
// that was already there, I have to make sure to remove it from the table and add it back to the CoursesPanel
// 2) When I inesrt a course into the table, it adds the courseclassroomspanel. But, if i double click to remove it, it wont do anything.

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gmele.general.sheets.XlsxSheet;
import org.gmele.general.sheets.exception.SheetExc;

/**
 * Η κλάση ScheduleManager κληρονομείται από την κλάση JFrame και είναι υπεύθυνη
 * για την διαχείριση του παραθύρου της δημιουργίας του προγράμματος εξεταστικής
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */

public class ScheduleManager extends JFrame {

    private String[] weekdays = { "ΚΥΡΙΑΚΗ", "ΔΕΥΤΕΡΑ", "ΤΡΙΤΗ", "ΤΕΤΑΡΤΗ", "ΠΕΜΠΤΗ", "ΠΑΡΑΣΚΕΥΗ", "ΣΑΒΒΑΤΟ" };
    private JTable table;
    private ExcelManager excelManager1;
    Definitions def;
    private List<Professor> professors;
    private List<Course> courses;
    private List<Classroom> classrooms;
    private List<String> dates;
    private List<String> timeslots;
    private DefaultTableModel model;
    private int excelRows, excelCols;
    private Logs logs;
    private String finalExcelSheet;
    private List<ScheduledCourse> scheduledCourses;
    private Unscheduled unscheduled;
    private List<ExamCoursesFromFinalSchedule> crsList;
    private String regex;
    private JPanel coursesClassroomsPanel;
    private Utilities utils;

    public ScheduleManager(ExcelManager excelManager) {
        initComponents();
        excelManager1 = excelManager;
        initOtherComponents();
    }

    public void initOtherComponents() {
        utils = new Utilities();
        courses = new ArrayList<>(excelManager1.getCourses());
        classrooms = new ArrayList<>(excelManager1.getClassrooms());
        dates = new ArrayList<>(excelManager1.getDates());
        timeslots = new ArrayList<>(excelManager1.getTimeslots());
        scheduledCourses = new ArrayList<>();
        unscheduled = new Unscheduled(excelManager1.getCourses());
        crsList = new ArrayList<>();
        excelRows = dates.size();
        excelCols = timeslots.size();
        logs = new Logs();
        def = excelManager1.getDefinitions();
        professors = new ArrayList<>(excelManager1.getProfs());
        finalExcelSheet = def.getSheet7();
        regex = "\\b\\d{2}/\\d{2}/\\d{4}\\b";
        modelPanel.setLayout(new BorderLayout());
        coursesPanel.setLayout(new GridLayout(0, 3));
        JScrollBar verticalScrollBar = coursesScrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(20);
        coursesClassroomsPanel = new JPanel();
        coursesClassroomsPanel.setLayout(new BoxLayout(coursesClassroomsPanel, BoxLayout.Y_AXIS));
        jScrollPaneClassrooms.setViewportView(coursesClassroomsPanel);
        Dimension jScrollPaneDimensions = new Dimension(200,800);
        jScrollPaneClassrooms.setSize(jScrollPaneDimensions);
        jScrollPaneClassrooms.setPreferredSize(jScrollPaneDimensions);
        jScrollPaneClassrooms.setMaximumSize(jScrollPaneDimensions);
        model = new DefaultTableModel(excelRows + 1, excelCols + 1) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            public boolean isCellSelected(int row, int column) {
                return false;
            }
        };
        initTable();
    }

    public void startProcess(boolean isNew) {
        modelPanel.add(populateTable());
        populateCourses();
        if (!isNew) {
            fillCoursesWithReadExcelScheduleData();
            createScheduledCourseClassroomsPanels();
        } else {
        }
        this.setVisible(true);
        this.setSize(1450, 900);
        this.setLocationRelativeTo(null);
    }

    public Course findCourses(String courseName) {
        for (Course course : this.courses) {
            if (course.getCourseName().equals(courseName) || course.getCourseShort().equals(courseName)) {
                return course;
            }
        }
        return null;
    }

    public void prepareTableRenderer() {
        model.setValueAt("ΗΜΕΡΟΜΗΝΙΑ / ΗΜΕΡΑ", 0, 0);
        this.table = new JTable(model){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                Color headerCells = Color.decode("#A9A9A9");
                Color firstCell = Color.decode("#3333FF");
                Color otherCells = Color.decode("#FFFFFF");
                // Πρώτο κελί πρώτη στήλη
                if (column == 0 && row == 0) {
                    comp.setBackground(firstCell);
                    // Οποιδήποτε κελί στην 1η στήλη
                } else if (column == 0) {
                    comp.setBackground(headerCells);
                    // Οποιδήποτε κελί στην 1η γραμμή (από την 2η στήλη και μετά)
                } else if (row == 0 && column > 0) {
                    comp.setBackground(headerCells);
                    // Όλα τα υπόλοιπα κελιά
                } else {
                    comp.setBackground(otherCells);
                }
                return comp;
            }
        };
        
        table.setRowHeight(80);
        table.setShowGrid(true); // to show the grid
        table.setGridColor(Color.GRAY); // you can choose any color
        table.getColumnModel().getColumn(0).setPreferredWidth(30); // Example for the first column
        table.getColumnModel().getColumn(0).setCellRenderer(new MultiLineCellRenderer());
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(90); // Example for the first column
            table.getColumnModel().getColumn(i).setCellRenderer(new MultiLineCellRenderer());
        }
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Change the font size to 18
        table.repaint();
        table.revalidate();
    }

    public void initTable() {
        prepareTableRenderer();
        this.table.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                Point dropLocation = evt.getLocation();
                int row = table.rowAtPoint(dropLocation);
                int col = table.columnAtPoint(dropLocation);
                handleDroppedCourse(row, col, evt);
            }
        });
        this.table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    int selectedColumn = table.getSelectedColumn();
                    removeCourseFromTable(selectedRow, selectedColumn);
                }
            }
        });
        this.table.setCellSelectionEnabled(false);
        this.table.setTableHeader(new JTableHeader());
        this.table.getTableHeader().setReorderingAllowed(false);
        //this.table.setShowGrid(true);
        //this.table.setGridColor(Color.BLACK);
    }

    public void handleDroppedCourse(int row, int col, DropTargetDropEvent evt) {
        try {
            // To dropCellContents παίρνει το string από το πεδίο που αφήσαμε το μάθημα
            String dropCellContents = (String) table.getValueAt(row, col);
            evt.acceptDrop(DnDConstants.ACTION_MOVE);
            Transferable transferable = evt.getTransferable();
            String buttonText = "";
            // buttonText = Το string του Μαθήματος του button
            buttonText = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            if (row > 0 & col > 0) {
                Course tmpCourse = findCourse(buttonText);
                String rowValue = (String) table.getValueAt(row, 0);
                String colValue = (String) table.getValueAt(0, col);
                System.out.println("Searching for Course: '" + dropCellContents + "'");
                rowValue = utils.getDateWithGreekFormat(weekdays, rowValue);
                boolean check1 = checkExaminersConflict(tmpCourse, rowValue, colValue);
                if (check1) {
                    boolean added = false;
                    model.setValueAt(buttonText, row, col);
                    Component[] components = coursesPanel.getComponents();
                    for (Component component : components) {
                        if (component instanceof JButton) {
                            JButton button = (JButton) component;
                            if (buttonText.equals(button.getText())) {
                                coursesPanel.remove(button);
                                coursesPanel.revalidate();
                                coursesPanel.repaint();
                                ScheduledCourse sc = new ScheduledCourse(tmpCourse, rowValue, colValue, classrooms);
                                addCourseToClassroomsPanel(sc, rowValue, colValue);
                                //unscheduled.removeCourseFromUnscheduledList(tmpCourse);
                                added = true;
                                break;
                            }
                        }
                    }
                    if (dropCellContents != null && added) {
                        Course cellCourse = findCourse(dropCellContents);
                        ScheduledCourse courseToBeRemoved = null;
                        for (ScheduledCourse tmp : scheduledCourses) {
                            if (tmp.getCourse().getCourseName() == cellCourse.getCourseName()) {
                                for (Professor prf : tmp.getCourse().getExaminers()) {
                                    prf.changeSpecificAvailability(rowValue, colValue,1);
                                }
                                //System.out.println("------------------------------------------------------ Unscheduled Before: " + tmpCourse.getCourseName());
                                //unscheduled.printCourses();
                                //unscheduled.addCourseToUnscheduledList(tmpCourse);
                                //System.out.println("------------------------------------------------------ Unscheduled After:");
                                //unscheduled.printCourses();
                                courseToBeRemoved = tmp;
                                removeCourseFromClassroomsPanel(cellCourse);
                                JButton courseButton = new JButton();
                                courseButton = createCourseBtn(cellCourse);
                                coursesPanel.add(courseButton);
                                coursesPanel.revalidate();
                                coursesPanel.repaint();
                                break;
                            }
                            if(courseToBeRemoved != null){
                                scheduledCourses.remove(courseToBeRemoved);
                            }
                            evt.dropComplete(true);
                            
                        }
                        // Μετατροπή της ημερομηνίας από την μορφή 'ηη/μμ/εεεε Ημέρα' to simply
                        // 'ηη/μμ/εεεε'
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Δεν υπάρχουν διαθέσιμοι οι καθηγητές του μαθήματος"
                            + " για εκείνη την ημέρα.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    evt.rejectDrop();
                }
            }
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.awt.dnd.InvalidDnDOperationException ex) {
        } catch (IOException ex) {
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }catch (java.util.ConcurrentModificationException ex){
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            System.out.println(ex.getStackTrace());
        }catch (Exception ex){
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            System.out.println(ex.getStackTrace());
        }
    }

    public void removeCourseFromTable(int selectedRow, int selectedColumn) {
        String date = table.getValueAt(selectedRow, 0).toString();
        date = utils.getDateWithGreekFormat(weekdays, date);
        String timeslot = table.getValueAt(0, selectedColumn).toString();
        Object cellValue = model.getValueAt(selectedRow, selectedColumn);
        ScheduledCourse courseToDelete = null;
        if (utils.checkDate(dates, date) && utils.checkTimeslot(timeslots, timeslot)) {
            if (cellValue != null && cellValue instanceof String && selectedRow > 0 && selectedColumn != 0) {
                String buttonText = (String) cellValue;
                for (ScheduledCourse sc : scheduledCourses) {
                    if (sc.getScheduledCourse().getCourseName().equals(buttonText)
                            || sc.getScheduledCourse().getCourseShort().equals(buttonText)) {
                        for (Professor prf : sc.getScheduledCourse().getExaminers()) {
                            prf.changeSpecificAvailability(date, timeslot, 1);
                        }
                        courseToDelete = sc;
                    }
                }
                if (courseToDelete != null) {
                    unscheduled.addCourseToUnscheduledList(courseToDelete.getScheduledCourse());
                    scheduledCourses.remove(courseToDelete);
                    removeCourseFromClassroomsPanel(courseToDelete.getScheduledCourse());
                } else {
                    try {
                        String msg = "Πρόβλημα κατά την αφαίρεση του μαθήματος από το κελί. Παρακαλώ πολύ ελέγξτε τα δεδομένα σας και προσπαθήστε ξανά.";
                        throw new CustomErrorException(ScheduleManager.this, msg);
                    } catch (CustomErrorException ex) {
                        Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                // Προσθήκη του κουμπιού πίσω στο coursesPanel
                JButton courseButton = new JButton();
                courseButton = createCourseBtn(courseToDelete.getCourse());
                coursesPanel.add(courseButton);
                coursesPanel.revalidate();
                coursesPanel.repaint();
                model.setValueAt(null, selectedRow, selectedColumn);
            }
        } else {
            if (selectedRow == 0 || selectedColumn == 0) {
            } else {
                try {
                    String msg = "Πρόβλημα κατά την αφαίρεση του μαθήματος από το κελί. Παρακαλώ πολύ ελέγξτε τα δεδομένα σας και προσπαθήστε ξανά.";
                    throw new CustomErrorException(ScheduleManager.this, msg);
                } catch (CustomErrorException ex) {
                    Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Η μέθοδος είναι υπεύθυνη για τον εντοπισμό του μαθήματος από ένα string
     * 
     * @param courseName Το όνομα του μαθήματος προς αναζήτηση
     * @return Αντικείμενο Course ή null ανάλογα με το εάν εντοπίστηκε το μάθημα από
     *         το string ή όχι
     */
    public Course findCourse(String courseName) {
        for (Course crs : courses) {
            if (crs.getCourseName().equals(courseName) || crs.getCourseShort().equals(courseName)) {
                return crs;
            }
        }
        return null;
    }
    
    public boolean existsInExamCourses(Course crs){
        for(ExamCoursesFromFinalSchedule tmp : crsList){
            if(tmp.getCourse().getCourseName().equals(crs.getCourseName())){
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modelScrollPane = new javax.swing.JScrollPane();
        modelPanel = new javax.swing.JPanel();
        lblScheduleDesigner = new javax.swing.JLabel();
        lblCourses = new javax.swing.JLabel();
        lblCoursesClassrooms = new javax.swing.JLabel();
        btnExportXlsx = new javax.swing.JButton();
        jScrollPaneClassrooms = new javax.swing.JScrollPane();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        coursesScrollPane = new javax.swing.JScrollPane();
        coursesPanel = new javax.swing.JPanel();
        autoAddProfessorsToSchedulebtn = new javax.swing.JButton();
        btnExportXlsx1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Φόρμα Δημιουργίας Προγράμματος");
        setPreferredSize(new java.awt.Dimension(1200, 900));
        setResizable(false);

        modelScrollPane.setPreferredSize(new java.awt.Dimension(800, 450));

        modelPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        modelPanel.setLayout(null);
        modelScrollPane.setViewportView(modelPanel);

        lblScheduleDesigner.setFont(new java.awt.Font("Franklin Gothic Book", 0, 18)); // NOI18N
        lblScheduleDesigner.setText("Σχεδιαστής Προγράμματος Εξεταστικής");

        lblCourses.setFont(new java.awt.Font("Franklin Gothic Book", 0, 18)); // NOI18N
        lblCourses.setText("Μαθήματα");

        lblCoursesClassrooms.setFont(new java.awt.Font("Franklin Gothic Book", 0, 18)); // NOI18N
        lblCoursesClassrooms.setText("Μαθήματα - Αίθουσες");

        btnExportXlsx.setText("Εξαγωγή");
        btnExportXlsx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createExcelFromTable(evt);
            }
        });

        jScrollPaneClassrooms.setMaximumSize(new java.awt.Dimension(200, 800));
        jScrollPaneClassrooms.setMinimumSize(new java.awt.Dimension(200, 800));

        coursesScrollPane.setPreferredSize(new java.awt.Dimension(1000, 300));

        coursesPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        coursesPanel.setLayout(null);
        coursesScrollPane.setViewportView(coursesPanel);

        jLayeredPane1.setLayer(coursesScrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(coursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(coursesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
        );

        autoAddProfessorsToSchedulebtn.setText("Αυτόματη Αντιστοίχιση Μαθημάτων");
        autoAddProfessorsToSchedulebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoAddProfessorsToSchedulebtncreateExcelFromTable(evt);
            }
        });

        btnExportXlsx1.setText("Καθαρισμός");
        btnExportXlsx1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportXlsx1createExcelFromTable(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(365, 365, 365)
                        .addComponent(lblScheduleDesigner))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(modelScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 990, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(467, 467, 467)
                                .addComponent(lblCourses)
                                .addGap(18, 18, 18)
                                .addComponent(autoAddProfessorsToSchedulebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnExportXlsx1)
                                .addGap(18, 18, 18)
                                .addComponent(btnExportXlsx)))
                        .addGap(8, 8, 8)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblCoursesClassrooms)
                        .addGap(89, 89, 89))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jScrollPaneClassrooms, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblScheduleDesigner)
                    .addComponent(lblCoursesClassrooms, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(modelScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCourses)
                            .addComponent(btnExportXlsx)
                            .addComponent(autoAddProfessorsToSchedulebtn)
                            .addComponent(btnExportXlsx1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPaneClassrooms, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(32, 32, 32))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void autoAddProfessorsToSchedulebtncreateExcelFromTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoAddProfessorsToSchedulebtncreateExcelFromTable
        JOptionPane.showMessageDialog(null, "Ενημέρωση: Η διαδικασία θα διαρκέσει μερικά δευτερόλεπτα.", "Μήνυμα εφαρμογής", JOptionPane.OK_OPTION );
        unscheduled.printCourses();
        Unscheduled copy = new Unscheduled(unscheduled);
        for(Course course : copy.getCourses()){
            
            boolean placedCourse = false;
            List<Availability> professorsAvailability = new ArrayList<>();
            List<String> nonAvailableDates = new ArrayList<>(getInvalidExaminationDates(course.getCourseName(), course.getCourseSem()));
            professorsAvailability = initAvailabilityForValidDatesWithNoCourse(course, nonAvailableDates);
            for(Availability a : professorsAvailability){
                String date = a.getDate();
                String timeslot = a.getTimeSlot();
                if(course.checkIfProfessorsAreAvailable(date, timeslot)){
                    int row = getRowFromDate(date);
                    int col = getColFromTimeslot(timeslot);
                    if(row == 0 && col == 0){
                        break;
                    }
                    System.out.println(course.getCourseName() + " : " + row + " " + col);
                    model.setValueAt(course.getCourseName(), row, col);
                    Component[] components = coursesPanel.getComponents();
                    for (Component component : components) {
                        if (component instanceof JButton) {
                            JButton button = (JButton) component;
                            if (course.getCourseName().equals(button.getText())){
                                coursesPanel.remove(button);
                                coursesPanel.revalidate();
                                coursesPanel.repaint();
                                ScheduledCourse sc = new ScheduledCourse(course, date, timeslot, classrooms);
                                addCourseToClassroomsPanel(sc, date, timeslot);
                                coursesPanel.repaint();
                                placedCourse = true;
                                unscheduled.removeCourseFromUnscheduledList(course);
                            }
                        }
                    }
                }
                if(placedCourse == true){
                    break;
                }
            }
        }
    }//GEN-LAST:event_autoAddProfessorsToSchedulebtncreateExcelFromTable

    private void btnExportXlsx1createExcelFromTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportXlsx1createExcelFromTable
        // TODO add your handling code here:
    }//GEN-LAST:event_btnExportXlsx1createExcelFromTable
    
    public List<String> getInvalidExaminationDates(String courseName, String courseSemester){
        List<String> list = new ArrayList<>();
        for (int i = 1; i < table.getRowCount(); i++){
            for (int j = 1; j < table.getColumnCount(); j++){
                if(table.getValueAt(i,j) != null){
                    Course crs = new Course(findCourse((String) table.getValueAt(i, j)));
                    if (crs != null && !crs.equals(courseName) && crs.getCourseSem().equals(courseSemester)){
                        String date = getDateFromTableRow(i);
                        List<String> tmp = new ArrayList<>(getPrevCurrNextInvalidDates(date));
                        for(String s : tmp){
                            if(!list.contains(s)){
                                list.add(s);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    
    public List<Availability> initAvailabilityForValidDatesWithNoCourse(Course crs, List<String> invalidDates){
        List<Availability> professorAvailability = new ArrayList<>();
        for (int i = 1; i < table.getRowCount(); i++){
            for (int j = 1; j < table.getColumnCount(); j++){
                if(table.getValueAt(i,j) == null){
                    String timeslot = table.getValueAt(0, j).toString();
                    String date = getDateFromTableRow(i);
                    // Προσθήκη μόνο των ημερομηνιών που όλοι οι καθηγητές του μαθήματος
                    // είναι διαθέσιμοι.
                    if(!invalidDates.contains(date) && crs.checkIfProfessorsAreAvailable(date, timeslot)){
                        professorAvailability.add(new Availability(date, timeslot, 1));
                    }
                }
            }
        }
        return professorAvailability;
    }
    
    public List<String> getPrevCurrNextInvalidDates(String date){
        List<String> list = new ArrayList<>();
        String dt2 = utils.modifyDate(date, 1, '+');
        String dt3 = utils.modifyDate(date, 1, '-');
        String dt4 = utils.modifyDate(date, 2, '+');
        String dt5 = utils.modifyDate(date, 2, '-');
        if(dates.contains(date)){
            list.add(date);
        }
        if(dates.contains(dt2)){
            list.add(dt2);
        }
        if(dates.contains(dt3)){
            list.add(dt3);
        }
        if(dates.contains(dt4)){
            list.add(dt4);
        }
        if(dates.contains(dt5)){
            list.add(dt5);
        }
        return list;
    }
    
    public String getDateFromTableRow(int row){
        String strDate = (String) table.getValueAt(row, 0);
        String date = utils.getDateWithGreekFormat(weekdays, strDate);
        return date;
    }
    
    public String getDateFromTableCol(int col){
        String strTimeslot = (String) table.getValueAt(0, col);
        return strTimeslot;
    }
    
    public int getRowFromDate(String date){
        for(int i = 1; i < table.getRowCount(); i++){
            String tmp = (String) table.getValueAt(i, 0);
            tmp = utils.getDateWithGreekFormat(weekdays, tmp);
            if(tmp.equals(date)){
                return i;
            }
        }
        return 0;
    }
    
    public int getColFromTimeslot(String timeslot){
        for(int j = 1; j < table.getColumnCount(); j++){
            if(table.getValueAt(0, j).equals(timeslot)){
                return j;
            }
        }
        return 0;
    }
    
    /**
     * 
     * @param date
     * @param timeslot
     * @return
     */
    private List<Classroom> getClassroomsAvailableOn(String date, String timeslot) {
        // This method should return a list of classrooms that are available on the
        // specified date and timeslot
        // Implement the logic based on how the availability data is stored and
        // retrieved
        List<Classroom> availableClassrooms = new ArrayList<>();
        // ... logic to populate availableClassrooms ...
        return availableClassrooms;
    }

    
    /**
     * Τοποθέτηση των αναγνωσμένων μαθημάτων στο πάνελ με τα μαθήματα και τις
     * αίθουσες.
     * (Εάν έχει βρεθεί αρχείο προγράμματος εξεταστικής και εάν είναι ορθά όλα τα
     * δεδομένα)
     */
    private void createScheduledCourseClassroomsPanels() {
        for (ScheduledCourse sc : scheduledCourses) {
            CourseClassroomsPanel ccp = new CourseClassroomsPanel(sc, sc.getClassrooms(), sc.getDate(),
                    sc.getTimeslot());
            //ccp.setSize(100,100);
            coursesClassroomsPanel.add(ccp);
        }
        coursesClassroomsPanel.revalidate();
        coursesClassroomsPanel.repaint();
    }

    /**
     * Μέθοδος που χρησιμοποιείται για την αφαίρεση ενός μαθήματος
     * από το πάνελ των μαθημάτων - αιθουσών.
     * 
     * @param crs      Το αντικείμενο του μαθήματος για προσθήκη (Course).
     * @param date     Το λεκτικό της ημερομηνίας που έχει τοποθετηθεί το μάθημα για
     *                 εξέταση (String).
     * @param timeslot Το λεκτικό της χρονικής περιόδου που έχει τοποθετηθεί το
     *                 μάθημα για εξέταση (String).
     */
    private void addCourseToClassroomsPanel(ScheduledCourse sc, String date, String timeslot) {
        CourseClassroomsPanel ccp = new CourseClassroomsPanel(sc, classrooms, date, timeslot);
        //ccp.setSize(100,100);
        coursesClassroomsPanel.add(ccp);
        coursesClassroomsPanel.revalidate();
        coursesClassroomsPanel.repaint();
    }

    /**
     * Μέθοδος που χρησιμοποιείται για την αφαίρεση ενός μαθήματος
     * από το πάνελ των μαθημάτων - αιθουσών.
     * 
     * @param crs Το αντικείμενο του μαθήματος προς αφαίρεση (Course).
     */
    private void removeCourseFromClassroomsPanel(Course crs) {
        for (Component comp : coursesClassroomsPanel.getComponents()) {
            if (comp instanceof CourseClassroomsPanel) {
                ((CourseClassroomsPanel) comp).getCourse().getCourse().getCourseName();
                String tmp = ((CourseClassroomsPanel) comp).getCourse().getCourse().getCourseName();
                if (tmp.equals(crs.getCourseName()) || tmp.equals(crs.getCourseShort())) {
                    coursesClassroomsPanel.remove(comp);
                    coursesClassroomsPanel.revalidate();
                    coursesClassroomsPanel.repaint();
                    break; // Assuming each course only has one panel associated with it
                }
            }
        }
    }
    
    /**
     * Συνάρτηση που χρησιμοποιείται για την τοποθέτηση των προγραμματισμένων
     * μαθημάτων στις κατάλληλες θέσεις στον πίνακα της φόρμας.
     */
    private void fillCoursesWithReadExcelScheduleData() {
        model = (DefaultTableModel) table.getModel();
        try {
            for (ExamCoursesFromFinalSchedule data : crsList) {
                int row = data.getRowIndex();
                int col = data.getColIndex();
                model.setValueAt(data.getCourse().getCourseName(), row, col - 1);
            }
            // Ανανέωση για την απεικόνιση των δεδομένων.
            table.revalidate();
            table.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Πρόβλημα κατά την συμπλήρωση των μαθημάτων από το διαβασμένο αρχείο προγράμματος εξεταστικής.",
                    "Σφάλμα εφαρμογής", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Μέθοδος που αποθηκεύει το τελικό πρόγραμμα της εξεταστικής που απεικονίζεται
     * στο παράθυρο JForm.
     * 
     * @param evt Το event που προκάλεσε την κλήση της μεθόδου.
     */
    private void createExcelFromTable(java.awt.event.ActionEvent evt) {
        int rowIndex = 0;
        int colIndex = 0;
        String path = def.getFolderPath() + "\\" + def.getExamScheduleFile();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("ΠΡΟΓΡΑΜΜΑ ΕΞΕΤΑΣΤΙΚΗΣ");
            utils.fillHeaders(workbook, sheet, timeslots, dates);
            int tableRows = table.getRowCount() - 1;
            int tableColumns = table.getColumnCount() - 1;
            System.out.println(timeslots.size() + " " + dates.size() + " " + tableRows + " " + tableColumns);
            for (rowIndex = 1; rowIndex <= tableRows; rowIndex++) {
                for (colIndex = 1; colIndex <= tableColumns; colIndex++) {
                    try {
                        String cellValue = (String) table.getValueAt(rowIndex, colIndex);
                        Course a = utils.getCourse(courses, cellValue);
                        if (a != null) {
                            Cell excelCell1 = (Cell) sheet.getRow(rowIndex).createCell(colIndex + 1);
                            excelCell1.setCellValue(a.getCourseName());
                            excelCell1.getCellStyle().setWrapText(true);
                            // Cell excelCell2 = (Cell) sheet.getRow(i).createCell(j + 2);
                            // excelCell2.setCellValue(a.getCourseSem());
                        } else {
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Exception thrown:" + rowIndex + " " + colIndex + e,
                                "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            utils.autoSizeColumns(sheet, tableColumns + 1);
            utils.applyCellStyles(workbook, sheet);

            JOptionPane.showMessageDialog(this,
                    "Η δημιουργία του αρχείου προγράμματος εξεταστικής ολοκληρώθηκε επιτυχώς!", "Μήνυμα Λάθους",
                    JOptionPane.INFORMATION_MESSAGE);
            // Αποθήκευση αρχείου προς συμπλήρωση για τους καθηγητές
            try (FileOutputStream outputStream = new FileOutputStream(path.toString())) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            System.out.println("Η δημιουργία του template για τους καθηγητές απέτυχε." + rowIndex + " " + colIndex);
        }
    }

    /**
     * Η συνάρτηση χρησιμοποιείται για να προτρέψει τον χρήστη στο να επιλέξει την
     * τοποθεσία
     * αλλά και το όνομα αποθήκευσης του τελικού αρχείου προγράμματος σε .xlsx
     * μορφή.
     * 
     * @return Το πλήρες μονοπάτι του αρχείου (String) ή null εάν ο χρήστης ακυρώσει
     *         την διαδικασία.
     */
    public String askUserToSaveFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files (*.xlsx, *.xls)", "xlsx");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            String fullPath = selectedFile.getAbsolutePath();
            if (!fullPath.contains(".xlsx")) {
                fullPath = fullPath + ".xlsx";
                return fullPath;
            } else {
                return fullPath;
            }
        }
        return null;
    }

    public boolean readExamScheduleExcel() {
        int rowIndex = 0;
        int colIndex = 0;
        String courseCell = "";
        String fileName = def.getFolderPath() + "\\" + def.getExamScheduleFile();
        String sheetName = def.getSheet7();
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("el", "GR"));
        symbols.setWeekdays(weekdays);
        // + 2 για τις 2 πρώτες στήλες
        int lastCol = timeslots.size() + 2;
        try (FileInputStream file = new FileInputStream(new File(fileName))) {
            XlsxSheet sheet = new XlsxSheet(fileName);
            sheet.SelectSheet(sheetName);
            int lastRow = sheet.GetLastRow();

            String header1 = sheet.GetCellString(0, 0);
            String header2 = sheet.GetCellString(0, 1);

            if (!header1.equals("ΗΜ/ΝΙΑ") || !header2.equals("ΗΜΕΡΑ")) {
                file.close();
                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                        fileName + "' στην γραμμή " + (rowIndex + 1) +
                        ". Εντοπίστηκαν διαφορετικά headers στην 1η γραμμή στις πρώτες"
                        + " 2 στήλες. Παρακαλώ πολύ ελέγξτε ότι τα δεδομένα για"
                        + " το 1ο και το 2ο κελί είναι 'ΗΜ/ΝΙΑ' και 'ΗΜΕΡΑ' αντίστοιχα.";
                throw new CustomErrorException(this, msg);

            }
            String timeslot = "";
            for (int x = 2; x < lastCol; x++) {
                timeslot = sheet.GetCellString(rowIndex, x);
                if (!timeslots.contains(timeslot)) {
                    file.close();
                    String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                            fileName + "' στην γραμμή " + (rowIndex + 1) +
                            ". Το χρονικό διάστημα '" + timeslot + "' δεν υπάρχει καταχωρημένο"
                            + " στο βασικό αρχείο πληροφοριών (" + def.getGenericFile() + ").";
                    throw new CustomErrorException(this, msg);
                }
            }
            String date;
            String dateName;
            for (rowIndex = 1; rowIndex < lastRow; rowIndex++) {
                date = "";
                dateName = "";
                date = sheet.GetCellString(rowIndex, 1);
                dateName = utils.getGreekDayName(date);
                if (dateName == null || !dates.contains(date)) {
                    file.close();
                    String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                            fileName + "' στην γραμμή " + (rowIndex + 1) +
                            ". Η ημερομηνία'" + date + "' δεν υπάρχει καταχωρημένη"
                            + " στο βασικό αρχείο πληροφοριών (" + def.getGenericFile() + ").";
                    throw new CustomErrorException(this, msg);
                }
            }
            for (rowIndex = 1; rowIndex < lastRow; rowIndex++) {
                for (colIndex = 2; colIndex < lastCol; colIndex++) {
                    courseCell = utils.getSafeCellString(sheet, rowIndex, colIndex);
                    if (courseCell.equals("") || courseCell.equals(" ")) {
                    } else {
                        if (findCourse(courseCell) == null) {
                            file.close();
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                                    fileName + "' στην γραμμή " + (rowIndex + 1) +
                                    ". Το μάθημα '" + courseCell + "' δεν υπάρχει καταχωρημένο"
                                    + " στο βασικό αρχείο πληροφοριών (" + def.getGenericFile() + ").";
                            throw new CustomErrorException(this, msg);
                        } else {
                            String courseTimeslot = sheet.GetCellString(0, colIndex);
                            String courseDate = sheet.GetCellString(rowIndex, 1);
                            Course crs = new Course(findCourse(courseCell));
                            boolean check = checkExaminersConflict(crs, courseDate, courseTimeslot);
                            if (check) {
                                if(!existsInExamCourses(crs)){
                                    ExamCoursesFromFinalSchedule courseDet = new ExamCoursesFromFinalSchedule(crs, rowIndex, colIndex);
                                    this.crsList.add(courseDet);
                                }
                            } else {
                                logs.appendLogger(logs.getIndexString() + "Πρόβλημα με το μάθημα '" + courseCell + "' και \n"
                                                + "την διαθεσιμότητα των καθηγητών. Το μάθημα θα αγνοηθεί.");
                            }
                        }
                    }
                }
            }
            file.close();
            return true;

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Το αρχείο '" + fileName + "' δεν βρέθηκε.",
                    "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + fileName + "'.", "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(this,
                    "Στο αρχείο '" + def.getExamScheduleFile() + "' στην γραμμή και στήλη " + (rowIndex + 1) + ":"
                            + colIndex
                            + "' με περιεχόμενο κελιού '" + courseCell
                            + "'  δεν μπόρεσε να αντιστοιχηθεί με κάποιο μάθημα." + ex,
                    "Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }

    /**
     * Η μέθοδος χρησιμοποιείται για την δημιουργία των αντικειμένων των μαθημάτων
     * στο panel στο κάτω μέρος
     * του κεντρικού παραθύρου
     */
    public void populateCourses() {
        List<Course> notSettled = new ArrayList<>(unscheduled.getCourses());
        for (Course course : notSettled) {
            // Create a custom component (e.g., JPanel or JButton) for each course
            JButton courseButton = new JButton();
            courseButton = createCourseBtn(course);
            coursesPanel.add(courseButton);
        }
    }

    private JButton createCourseBtn(Course crs) {
        JButton courseButton = new JButton(crs.getCourseName());
        courseButton.setPreferredSize(new Dimension(270, 40)); // Set preferred size as needed
        courseButton.setTransferHandler(new ButtonTransferHandler(crs.getCourseName()));
        courseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                JComponent comp = (JComponent) evt.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, evt, TransferHandler.COPY);
            }
        });
        if (crs.getCourseSeason().equals("ΧΕΙΜΕΡΙΝΟ")) {
            // courseButton.setBackground(Color.red);
            courseButton.setBackground(new Color(102, 178, 255)); // RGB values for light blue

        } else {
            // courseButton.setBackground(Color.blue);
            courseButton.setBackground(new Color(255, 178, 102)); // RGB values for light blue
        }
        courseButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        return courseButton;
    }

    /**
     * Η μέθοδος χρησιμοποιείται για την καταχώρηση των headers των ημερομηνιών και
     * των
     * χρονικών διαστημάτων.
     */
    public JScrollPane populateTable() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (int j = 0; j < excelCols; j++) {
            model.setValueAt(timeslots.get(j), 0, j + 1);
        }

        for (int i = 0; i < excelRows; i++) {
            LocalDate date = LocalDate.parse(dates.get(i), dateFormatter);
            String greekDayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR"))
                    .toUpperCase();
            String greekDayNameWithoutAccents = Normalizer.normalize(greekDayName, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            model.setValueAt(dates.get(i) + " " + greekDayNameWithoutAccents, i + 1, 0);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    public boolean checkExaminersConflict(Course course, String date, String timeslot) {
        List<Professor> newCourseExaminers = null;
        boolean found = false;
        String errorMsg = course.getCourseName();
        boolean allAvailable = true;
        for (Course crs : unscheduled.getCourses()) {
            if (crs.getCourseName().equals(course.getCourseName())) {
                found = true;
                newCourseExaminers = new ArrayList<>(course.getExaminers());
                for (Professor prf1 : newCourseExaminers) {
                    int res1 = prf1.isAvailable(date, timeslot);
                    if (res1 == 0 || res1 == 2) {
                        logs.appendLogger(logs.getIndex() + ") Πρόβλημα με την διαθεσιμότητα καθηγητών για το μάθημα: '"
                                + crs.getCourseName() + "')");
                        errorMsg = errorMsg + "\nΟ καθηγητής " + prf1.getProfSurname() + " " + prf1.getProfFirstname() +
                                " δεν είναι διαθέσιμος την ημερομηνία " + date + " και ώρα " + timeslot + ".";
                        allAvailable = false;
                    }
                }
                if (allAvailable) {
                    for (Professor prf2 : newCourseExaminers) {
                        prf2.changeSpecificAvailability(date, timeslot, 2);
                    }
                    ScheduledCourse sc = new ScheduledCourse(crs, date, timeslot, classrooms);
                    scheduledCourses.add(sc);
                    unscheduled.removeCourseFromUnscheduledList(crs);
                    return true;
                }
            }
        }
        if(found){
            logs.appendLogger("Το μάθημα " + course.getCourseName() + " θα αγνοηθεί καθώς έχει ήδη"
                    + "τοποθετηθεί στο πρόγραμμα εξεταστικής.");
        }
        return false;
    }

    /**
     * Η μέθοδος ευθύνεται για τον έλεγχο διαθεσιμότητας των καθηγητών ενός
     * μαθήματος για μία συγκεκριμενη ημερομηνία και χρονικό πλαίσιο
     * 
     * @param course      Αντικείμενο της κλάσης Course όπου από αυτό θα αντλήσουμε
     *                    τους εξεταστές καθηγητές
     * @param dateStr     Η ημερομηνία προς έλεγχο
     * @param timeslotStr Το χρονικό πλαίσιο προς έλεγχο
     * @return true ή false ανάλογα με το εάν όλοι οι καθηγητές του μαθήματος θα
     *         ήταν διαθέσιμοι εκείνη την συγκεκριμένη χρονική περίοδο ή όχι
     */
    public boolean checkAvailabilityForProfessors(Course course, String dateStr, String timeslotStr) {
        List<Professor> profs = new ArrayList<>(course.getExaminers());
        List<Integer> results = new ArrayList<>();
        String date = utils.getDateWithGreekFormat(weekdays, dateStr);
        for (Professor prof : profs) {
            int tmp = prof.isAvailable(date, timeslotStr);
            results.add(tmp);
        }
        for (Integer i : results) {
            if (i == 0 || i == 2) {
                return false;
            }
        }
        return true;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton autoAddProfessorsToSchedulebtn;
    private javax.swing.JButton btnExportXlsx;
    private javax.swing.JButton btnExportXlsx1;
    private javax.swing.JPanel coursesPanel;
    private javax.swing.JScrollPane coursesScrollPane;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPaneClassrooms;
    private javax.swing.JLabel lblCourses;
    private javax.swing.JLabel lblCoursesClassrooms;
    private javax.swing.JLabel lblScheduleDesigner;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JScrollPane modelScrollPane;
    // End of variables declaration//GEN-END:variables
}