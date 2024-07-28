package forms;

import forms.CourseClassroomsPanel;
import models.Professor;
import models.Course;
import models.Classroom;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import thesis.Availability;
import utils.ButtonTransferHandler;
import utils.CustomErrorException;
import thesis.Definitions;
import thesis.ExamCoursesFromFinalSchedule;
import thesis.ExcelManager;
import utils.LoadingPanel;
import thesis.Logs;
import utils.MultiLineCellRenderer;
import thesis.ScheduledCourse;
import thesis.Unscheduled;
import utils.Utilities;

/**
 * Η κλάση ScheduleManager κληρονομείται από την κλάση JFrame και είναι υπεύθυνη
 * για την διαχείριση του παραθύρου της δημιουργίας του προγράμματος εξεταστικής.
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */

public class ScheduleManager extends JFrame {

    private int excelRows, excelCols;
    private Logs logs;
    private Unscheduled unscheduled;
    private Definitions def;
    private String[] weekdays;
    private List<String> greekSemesters;
    private Utilities utils;
    private LoadingPanel lp;
    
    private List<Course> allCourses;
    private List<Course> courses;
    private List<Classroom> classrooms;
    private List<String> dates;
    private List<String> timeslots;
    
    private JTable table;
    private DefaultTableModel model;
    private ExcelManager excelManager1;
    private List<ScheduledCourse> scheduledCourses;
    private List<ExamCoursesFromFinalSchedule> examCoursesFromFinalSchedule;
    private Map<String, Integer> coursesSemesters = new HashMap<>();
    private JPanel coursesClassroomsPanel;
    private List<String> validCoursesFromSchedule;

    public ScheduleManager(ExcelManager excelManager) {
        initComponents();
        excelManager1 = excelManager;
        initOtherComponents();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void initOtherComponents() {
        lp = new LoadingPanel();
        utils = new Utilities();
        allCourses = new ArrayList<>(excelManager1.getCourses());
        courses = new ArrayList<>(excelManager1.getValidCourses());
        classrooms = new ArrayList<>(excelManager1.getClassrooms());
        dates = new ArrayList<>(excelManager1.getDates());
        timeslots = new ArrayList<>(excelManager1.getTimeslots());
        scheduledCourses = new ArrayList<>();
        unscheduled = new Unscheduled(courses);
        examCoursesFromFinalSchedule = new ArrayList<>();
        excelRows = dates.size();
        excelCols = timeslots.size();
        logs = new Logs();
        def = excelManager1.getDefinitions();
        weekdays = def.getWeekdays();
        greekSemesters = def.getGreekSemesters();
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
        jScrollPaneClassrooms.getVerticalScrollBar().setUnitIncrement(20);
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
        for (int i = 0; i < greekSemesters.size(); i++) {
            coursesSemesters.put(greekSemesters.get(i), i + 1);
        }
    }
    
    public void startProcess(boolean isNew) {
        try{
            modelPanel.add(populateTable());
            populateCourses();
            if (isNew) {
                fillCoursesWithReadExcelScheduleData();
                createScheduledCourseClassroomsPanels();
                sortAndReorderPanels(coursesClassroomsPanel);
            }else{
                fillCoursesWithReadExcelScheduleData();
                createScheduledCourseClassroomsPanels();
                sortAndReorderPanels(coursesClassroomsPanel);
                if(readClassroomsScheduleExcel() == false){
                    if (JOptionPane.showConfirmDialog(this, "Σφάλμα κατά την ανάγνωση των δεδομένων από το αρχείο του προγράμματος εξεταστικής." +
                        " Θέλετε να ξεκινήσετε ένα νέο κενό παράθυρο;", "Σφάλμα εφαρμογής", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                        ScheduleManager b = new ScheduleManager(excelManager1);
                        b.startProcess(true);
                        this.dispose();
                    }else{
                        return;
                    }
                }
            }
            this.setVisible(true);
            this.setSize(1380, 900);
            this.setLocationRelativeTo(null);
        }catch(Exception ex){
            return;
        }
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
        modelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.table.setCellSelectionEnabled(false);
        this.table.setTableHeader(new JTableHeader());
        this.table.getTableHeader().setReorderingAllowed(false);
        //this.table.setShowGrid(true);
        //this.table.setGridColor(Color.BLACK);
    }
    
    /**
     * Η μέθοδος χρησιμοποιείται για την συμπλήρωση των headers των ημερομηνιών και
     * των χρονικών διαστημάτων.
     * 
     * @return Ένα παράθυρο με εμπεριέχει τον πίνακα που δημιουργήσαμε (JScrollPane).
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
    
    /**
     * Η μέθοδος χρησιμοποιείται για την δημιουργία των αντικειμένων των μαθημάτων
     * στο panel στο κάτω μέρος του κεντρικού παραθύρου.
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

    /**
     * Η μέθοδος χρησιμοποιείται για την δημιουργία ενός JButton που αντιστοιχεί
     * σε ένα μάθημα.
     * 
     * @param crs Αντικείμενο μαθήματος (Course).
     * @return Ένα κουμπί για το μάθημα (JButton).
     */
    private JButton createCourseBtn(Course crs) {
        JButton courseButton = new JButton(crs.getCourseName());
        courseButton.setPreferredSize(new Dimension(270, 60)); // Set preferred size as needed
        courseButton.setSize(new Dimension(270, 60));
        courseButton.setMaximumSize(new Dimension(270, 60));
        courseButton.setTransferHandler(new ButtonTransferHandler(crs.getCourseName()));
        courseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                JComponent comp = (JComponent) evt.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, evt, TransferHandler.COPY);
            }
        });
        if (crs.getCourseSeason().equals("ΧΕΙΜΕΡΙΝΟ")) {
            courseButton.setBackground(new Color(102, 178, 255)); // Μπλέ

        } else {
            courseButton.setBackground(new Color(255, 178, 102)); // Πορτοκαλί
        }
        courseButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        return courseButton;
    }

    /**
     * Η μέθοδος χρησιμοποιείται για την δημιουργία και την αρχική παραμετροποίηση
     * του πίνακα table.
     */
    public void prepareTableRenderer() {
        model.setValueAt("ΗΜΕΡΟΜΗΝΙΑ / ΧΡΟΝΙΚΑ ΔΙΑΣΤΗΜΑΤΑ", 0, 0);
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
        table.setRowHeight(60);
        table.setGridColor(Color.GRAY); // you can choose any color
        table.getColumnModel().getColumn(0).setPreferredWidth(30); // Example for the first column
        table.getColumnModel().getColumn(0).setCellRenderer(new MultiLineCellRenderer());
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(70); // Example for the first column
            table.getColumnModel().getColumn(i).setCellRenderer(new MultiLineCellRenderer());
        }
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Change the font size to 18
        table.repaint();
        table.revalidate();
    }

    
    /**
     * Η παρακάτω μέθοδος διαχειρίζεται τα μαθήματα τα οποία ο χρήστης σέρνει προς
     * την φόρμα/πίνακα του προγράμματος και μια σειρά από ελέγχους πραγμαοτποιούνται
     * μέσα από αυτή την μέθοδο.
     * 
     * @param row Η γραμμή στην οποία έγινε σύρσιμο του μαθήματος (Integer).
     * @param col Η στήλη στην οποία έγινε σύρσιμο του μαθήματος (Integer).
     * @param evt 
     */
    public void handleDroppedCourse(int row, int col, DropTargetDropEvent evt) {
        try {
            // To dropCellContents παίρνει το string από το πεδίο που αφήσαμε το μάθημα
            String dropCellContents = (String) table.getValueAt(row, col);
            evt.acceptDrop(DnDConstants.ACTION_MOVE);
            Transferable transferable = evt.getTransferable();
            String buttonText = "";
            buttonText = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            if (row > 0 & col > 0) {
                Course tmpCourse = findCourse(buttonText);
                String dateCell = (String) table.getValueAt(row, 0);
                String timeslotCell = (String) table.getValueAt(0, col);
                dateCell = utils.getDateWithGreekFormat(weekdays, dateCell);
                boolean check1 = checkExaminersConflict(tmpCourse, dateCell, timeslotCell);
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
                                ScheduledCourse sc = new ScheduledCourse(tmpCourse, dateCell, timeslotCell, classrooms);
                                addCourseToClassroomsPanel(sc);
                                scheduledCourses.add(sc);
                                unscheduled.removeCourseFromUnscheduledList(tmpCourse);
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
                                courseToBeRemoved = tmp;
                                removeCourseFromClassroomsPanel(cellCourse);
                                JButton courseButton = new JButton();
                                courseButton = createCourseBtn(cellCourse);
                                coursesPanel.add(courseButton);
                                coursesPanel.revalidate();
                                coursesPanel.repaint();
                                for (Professor prf : tmp.getCourse().getExaminers()) {
                                    prf.changeSpecificAvailability(dateCell, timeslotCell,1);
                                }
                                break;
                            }
                            if(courseToBeRemoved != null){
                                scheduledCourses.remove(courseToBeRemoved);
                            }
                            evt.dropComplete(true);
                            
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Δεν υπάρχουν διαθέσιμοι οι καθηγητές του μαθήματος '" + buttonText + "'" 
                            + " για την ημέρα '" + dateCell + "' και χρονικό διάστημα '" + timeslotCell + "'.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    //evt.rejectDrop();
                }
            }
        } catch (UnsupportedFlavorException ex) {
            //Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.awt.dnd.InvalidDnDOperationException ex) {
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }catch (java.util.ConcurrentModificationException ex){
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }catch (Exception ex){
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /**
     * Η μέθοδος χρησιμοποιείται για την αφαίρεση ενός μαθήματος από τον πίνακα
     * των μαθημάτων. Εάν ολοκληρωθεί επιτυχώς η διαδικασία, τότε το μάθημα προστίθεται
     * στο coursesPanel.
     * 
     * @param selectedRow Η γραμμή που βρίσκεται το στοιχείο προς αφαίρεση (Integer).
     * @param selectedColumn Η στήλη που βρίσκεται το στοιχείο προς αφαίρεση (Integer).
     */
    public void removeCourseFromTable(int selectedRow, int selectedColumn) {
        try{
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
                            throw new CustomErrorException(ScheduleManager.this, msg, false);
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
                if (selectedRow != 0 && selectedColumn != 0) {
                    try {
                        String msg = "Πρόβλημα κατά την αφαίρεση του μαθήματος από το κελί. Παρακαλώ πολύ ελέγξτε τα δεδομένα σας και προσπαθήστε ξανά.";
                        throw new CustomErrorException(ScheduleManager.this, msg, false);
                    } catch (CustomErrorException ex) {
                        Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }catch (Exception ex){
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        autoAddProfessorsToSchedulebtn = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        coursesScrollPane = new javax.swing.JScrollPane();
        coursesPanel = new javax.swing.JPanel();
        autoSelectClassroomsForCoursesBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Σχεδιαστής Προγράμματος Εξετάσεων");

        modelScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        modelScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        modelScrollPane.setPreferredSize(new java.awt.Dimension(800, 450));

        modelPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        modelPanel.setLayout(null);
        modelScrollPane.setViewportView(modelPanel);

        lblScheduleDesigner.setFont(new java.awt.Font("Franklin Gothic Book", 0, 18)); // NOI18N
        lblScheduleDesigner.setText("Πρόγραμμα Εξεταστικής");

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

        autoAddProfessorsToSchedulebtn.setText("Αυτόματη Αντιστοίχιση Μαθημάτων");
        autoAddProfessorsToSchedulebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoAddProfessorsToSchedulebtncreateExcelFromTable(evt);
            }
        });

        btnClear.setText("Καθαρισμός");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearCreateExcelFromTable(evt);
            }
        });

        coursesScrollPane.setPreferredSize(new java.awt.Dimension(1000, 300));

        coursesPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        coursesPanel.setLayout(null);
        coursesScrollPane.setViewportView(coursesPanel);

        autoSelectClassroomsForCoursesBtn.setText("Αυτόματη Επιλογή Αιθουσών");
        autoSelectClassroomsForCoursesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSelectClassroomsForCoursesBtncreateExcelFromTable(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblCourses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(55, 55, 55)
                                .addComponent(autoAddProfessorsToSchedulebtn)
                                .addGap(18, 18, 18)
                                .addComponent(autoSelectClassroomsForCoursesBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnExportXlsx, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(195, 195, 195))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(modelScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(coursesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1001, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblScheduleDesigner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneClassrooms, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCoursesClassrooms, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblScheduleDesigner)
                    .addComponent(lblCoursesClassrooms))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneClassrooms, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(modelScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnExportXlsx)
                            .addComponent(btnClear)
                            .addComponent(autoSelectClassroomsForCoursesBtn)
                            .addComponent(autoAddProfessorsToSchedulebtn)
                            .addComponent(lblCourses))
                        .addGap(4, 4, 4)
                        .addComponent(coursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void autoAddProfessorsToSchedulebtncreateExcelFromTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoAddProfessorsToSchedulebtncreateExcelFromTable
        try{
            JOptionPane.showMessageDialog(null, "Ενημέρωση: Η διαδικασία θα διαρκέσει μερικά δευτερόλεπτα.", "Μήνυμα εφαρμογής", JOptionPane.INFORMATION_MESSAGE );
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<Course> randomized = new ArrayList(unscheduled.getCourses());
            Collections.shuffle(randomized);
            for(Course course : randomized){
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
                                    scheduledCourses.add(sc);
                                    addCourseToClassroomsPanel(sc);
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
            setCursor(Cursor.getDefaultCursor());
        }catch (Exception ex){
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_autoAddProfessorsToSchedulebtncreateExcelFromTable

    private void btnClearCreateExcelFromTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearCreateExcelFromTable
        if (JOptionPane.showConfirmDialog(this, "Είστε σίγουρος ότι επιθυμείτε καθαρισμό όλων των δεδομένων;", "Μήνυμα εφαρμογής", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try{
                ExcelManager newExcelManager = new ExcelManager(this, def);
                if(newExcelManager.readGenericExcel()){
                    if (newExcelManager.readAvailabilityTemplates()){
                        ScheduleManager b = new ScheduleManager(newExcelManager);
                        b.startProcess(true);
                        this.dispose();
                    }
                }
            }catch (Exception ex){
                Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnClearCreateExcelFromTable

    private void autoSelectClassroomsForCoursesBtncreateExcelFromTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSelectClassroomsForCoursesBtncreateExcelFromTable
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        for(ScheduledCourse sc : scheduledCourses){
            CourseClassroomsPanel cpp = utils.findPanelForCourse(sc.getCourse(), coursesClassroomsPanel);
            cpp.autoSelectNeededClassrooms();
        }
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_autoSelectClassroomsForCoursesBtncreateExcelFromTable

    public CourseClassroomsPanel findScheduledCourse(Course course) {
        Component[] components = coursesClassroomsPanel.getComponents();
        for (Component component : components) {
            if (component instanceof CourseClassroomsPanel) {
                CourseClassroomsPanel ccp = (CourseClassroomsPanel) component;
                if (ccp.getScheduledCourse().getCourse().getCourseName().equals(course.getCourseName())) {
                    return ccp;
                }
            }
        }
        return null;
    }
        
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
     * Τοποθέτηση των αναγνωσμένων μαθημάτων στο πάνελ με τα μαθήματα και τις
     * αίθουσες.
     * (Εάν έχει βρεθεί αρχείο προγράμματος εξεταστικής και εάν είναι ορθά όλα τα
     * δεδομένα)
     */
    private void createScheduledCourseClassroomsPanels() {
        for (ScheduledCourse sc : scheduledCourses) {
            CourseClassroomsPanel ccp = new CourseClassroomsPanel(sc, sc.getClassrooms());
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
     * @param crs Το αντικείμενο του μαθήματος για προσθήκη (Course).
     * @param date Το λεκτικό της ημερομηνίας που έχει τοποθετηθεί το μάθημα για
     * εξέταση (String).
     * @param timeslot Το λεκτικό της χρονικής περιόδου που έχει τοποθετηθεί το
     * μάθημα για εξέταση (String).
     */
    private void addCourseToClassroomsPanel(ScheduledCourse sc) {
        try{
            CourseClassroomsPanel ccp = new CourseClassroomsPanel(sc, classrooms);
            //ccp.setSize(100,100);
            coursesClassroomsPanel.add(ccp);
            coursesClassroomsPanel.revalidate();
            coursesClassroomsPanel.repaint();
            sortAndReorderPanels(coursesClassroomsPanel);
        }catch (Exception ex){
            return;
        }
        
    }
    
    /**
     * Μέθοδος που χρησιμοποιείται για την ταξινόμηση των CourseClassroomsPanels
     * @param container Το πανελ με τα παράθυρα (JPanel).
     */
    public void sortAndReorderPanels(JPanel container) throws CustomErrorException{
        List<CourseClassroomsPanel> panels = new ArrayList<>();
        for (Component comp : container.getComponents()) {
            if (comp instanceof CourseClassroomsPanel) {
                panels.add((CourseClassroomsPanel) comp);
            }
        }
        container.removeAll();
        try{
            panels.sort(new Comparator<CourseClassroomsPanel>() {
                @Override
                public int compare(CourseClassroomsPanel o1, CourseClassroomsPanel o2) {
                    String sem1 = o1.getScheduledCourse().getCourse().getCourseSem();
                    String sem2 = o2.getScheduledCourse().getCourse().getCourseSem();
                    return Integer.compare(coursesSemesters.get(sem1), coursesSemesters.get(sem2));
                }
            });
        }catch(NullPointerException ex){
            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + def.getGenericFile() 
            + "' στο φύλλο '" + def.getSheet5() + "'. Κάποιο εξάμηνο δεν βρίσκεται στην λίστα με τα εξάμηνα.";
            throw new CustomErrorException(this, msg, true);
        }
        for (CourseClassroomsPanel panel : panels) {
            container.add(panel);
        }
        container.revalidate();
        container.repaint();
    }
    
    /**
     * Συνάρτηση που χρησιμοποιείται για την τοποθέτηση των προγραμματισμένων
     * μαθημάτων στις κατάλληλες θέσεις στον πίνακα της φόρμας.
     */
    private void fillCoursesWithReadExcelScheduleData() {
        model = (DefaultTableModel) table.getModel();
        try {
            for (ExamCoursesFromFinalSchedule data : examCoursesFromFinalSchedule) {
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
     * @param evt Το event που προκάλεσε την κλήση της μεθόδου (ActionEvent).
     */
    private void createExcelFromTable(java.awt.event.ActionEvent evt) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int rowIndex = 0;
        int colIndex = 0;
        String path = def.getFolderPath() + "\\" + def.getExamScheduleFile();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet1 = workbook.createSheet("ΠΡΟΓΡΑΜΜΑ ΕΞΕΤΑΣΤΙΚΗΣ");
            XSSFSheet sheet2 = workbook.createSheet("ΠΡΟΓΡΑΜΜΑ ΑΙΘΟΥΣΩΝ");
            utils.fillHeaders(workbook, sheet1, timeslots, dates);
            utils.fillHeaders(workbook, sheet2, timeslots, dates);
            int tableRows = table.getRowCount() - 1;
            int tableColumns = table.getColumnCount() - 1;
            for (rowIndex = 1; rowIndex <= tableRows; rowIndex++) {
                for (colIndex = 1; colIndex <= tableColumns; colIndex++) {
                    try {
                        String cellValue = (String) table.getValueAt(rowIndex, colIndex);
                        Course course = findCourse(cellValue);
                        if (course != null) {
                            Cell excelCell1 = (Cell) sheet1.getRow(rowIndex).createCell(colIndex + 1);
                            excelCell1.setCellValue(course.getCourseName());
                            excelCell1.getCellStyle().setWrapText(true);
                            CourseClassroomsPanel tmp;
                            tmp = utils.findPanelForCourse(course, coursesClassroomsPanel);
                            if(tmp != null){
                                Cell excelCell2 = (Cell) sheet2.getRow(rowIndex).createCell(colIndex + 1);
                                excelCell2.getCellStyle().setWrapText(true);
                                String cellValue2 = course.getCourseName() + "\n(";
                                boolean firstValueEntered = false;
                                for(Classroom clr : tmp.getSelectedClassrooms()){
                                    if(firstValueEntered == false){
                                        cellValue2 = cellValue2 + clr.getClassroomName();
                                        firstValueEntered = true;
                                    }else{
                                        cellValue2 = cellValue2 + ", " + clr.getClassroomName();
                                    }
                                }
                                cellValue2 = cellValue2 + ")";
                                excelCell2.setCellValue(cellValue2);
                            }
                        } else {
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Exception thrown:" + rowIndex + " " + colIndex +"\n" + e.getStackTrace(),
                                "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            utils.autoSizeColumns(sheet1, tableColumns + 1);
            utils.applyCellStyles(workbook, sheet1);
            utils.autoSizeColumns(sheet2, tableColumns + 1);
            utils.applyCellStyles(workbook, sheet2);
            
            // Αποθήκευση αρχείου προς συμπλήρωση για τους καθηγητές
            try (FileOutputStream outputStream = new FileOutputStream(path.toString())) {
                workbook.write(outputStream);
            }
            JOptionPane.showMessageDialog(this,
                    "Η δημιουργία του αρχείου προγράμματος εξεταστικής ολοκληρώθηκε επιτυχώς!", "Μήνυμα Σφάλματος",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            try {
                String msg = "Πρόβλημα κατά την αποθήκευση του αρχείου προγράμματος"
                        + " εξεταστικής '" + def.getExamScheduleFile() + "'. Δεν ήταν"
                        + " δυνατή η αποθήκευση του αρχείου. Παρακαλώ πολύ ελέξτε εάν"
                        + " το αρχείο υπάρχει ήδη ανοικτό και δοκιμάστε εκ νέου.";
                throw new CustomErrorException(this, msg, false);
            } catch (CustomErrorException ex) {
                Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, e);
        }
        setCursor(Cursor.getDefaultCursor());
    }

    public boolean readExamScheduleExcel(List<Course> coursesList) {
        validCoursesFromSchedule = new ArrayList<>();
        int rowIndex = 0;
        int colIndex = 0;
        String courseCell = "";
        String courseTimeslot;
        String courseDate;
        String fileName = def.getFolderPath() + "\\" + def.getExamScheduleFile();
        String sheetName = def.getSheet7();
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("el", "GR"));
        symbols.setWeekdays(weekdays);
        boolean errorFlag = false;
        // + 2 για τις 2 πρώτες στήλες
        int lastCol = timeslots.size() + 2;
        try (FileInputStream file = new FileInputStream(new File(fileName))) {
            XlsxSheet sheet = new XlsxSheet(fileName);
            sheet.SelectSheet(sheetName);
            int lastRow = sheet.GetLastRow() + 1;
            String header1 = sheet.GetCellString(0, 0);
            String header2 = sheet.GetCellString(0, 1);
            if (!header1.equals("ΗΜΕΡΑ") || !header2.equals("ΗΜ/ΝΙΑ")) {
                file.close();
                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                        def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                        ". Εντοπίστηκαν διαφορετικά headers στην 1η γραμμή στις πρώτες"
                        + " 2 στήλες. Παρακαλώ πολύ ελέγξτε ότι τα δεδομένα για"
                        + " το 1ο και το 2ο κελί είναι 'ΗΜΕΡΑ' και 'ΗΜ/ΝΙΑ' αντίστοιχα.";
                throw new CustomErrorException(this, msg, true);

            }
            String timeslot = "";
            for (int x = 2; x < lastCol; x++) {
                timeslot = sheet.GetCellString(rowIndex, x);
                if (!timeslots.contains(timeslot)) {
                    file.close();
                    String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                            def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                            ". Το χρονικό διάστημα '" + timeslot + "' δεν υπάρχει καταχωρημένο"
                            + " στο βασικό αρχείο πληροφοριών (" + def.getGenericFile() + ").";
                    throw new CustomErrorException(this, msg, true);
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
                            def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                            ". Η ημερομηνία'" + date + "' δεν υπάρχει καταχωρημένη"
                            + " στο βασικό αρχείο πληροφοριών (" + def.getGenericFile() + ").";
                    throw new CustomErrorException(this, msg, true);
                }
            }
            for (rowIndex = 1; rowIndex < lastRow; rowIndex++) {
                for (colIndex = 2; colIndex < lastCol; colIndex++) {
                    courseCell = utils.getSafeCellString(sheet, rowIndex, colIndex);
                    if (courseCell.equals("") || courseCell.equals(" ")) {
                    } else {
                        boolean isValid = false;
                        for(Course crs : coursesList){
                            if (crs.getCourseName().equals(courseCell)){
                                isValid = true;
                            }
                        }
                        if(isValid == false){
                            file.close();
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                            def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                            ". Το μάθημα '" + courseCell + "' δεν υπάρχει καταχωρημένο"
                            + " στο βασικό αρχείο πληροφοριών (" + def.getGenericFile() + ") ή "
                                    + "δεν είναι προαπαιτεί όλες τις προϋποθέσεις για εξέταση.";
                            new CustomErrorException(this, msg, false);
                            return false;
                        } else {
                            courseTimeslot = sheet.GetCellString(0, colIndex);
                            courseDate = sheet.GetCellString(rowIndex, 1);
                            Course crs = new Course(findCourse(courseCell));
                            if(existsInExamCourses(crs)){
                                file.close();
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                                def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                                ". Το μάθημα '" + crs.getCourseName() + "' υπάρχει διπλοκαταχωρημένο.";
                                throw new CustomErrorException(this, msg, true);
                            }
                            boolean check = checkExaminersConflict(crs, courseDate, courseTimeslot);
                            if (check) {
                                ExamCoursesFromFinalSchedule courseDet = new ExamCoursesFromFinalSchedule(crs, rowIndex, colIndex);
                                examCoursesFromFinalSchedule.add(courseDet);
                                validCoursesFromSchedule.add(crs.getCourseName());
                            } else {
                                logs.appendLogger(logs.getIndexString() + "Πρόβλημα" +
                                " με το μάθημα '" + courseCell + "'. Ένας ή περισσότεροι από τους καθηγητές του μαθήματος δεν είναι " +
                                " διαθέσιμοι για την ημερομηνία '" + courseDate + "' και ώρα '" + courseTimeslot + "'. Παρακαλώ, επιλέξτε " +
                                "άλλη ημερομηνία και ώρα.");
                                errorFlag = true;
                            }
                        }
                    }
                }
            }
            if(errorFlag){
                String message = "<html><body style='width: 600px;'>" + // Set the width here
                 "Κατά την ανάγνωση του αρχείου '" + def.getExamScheduleFile() + 
                 "' στο φύλλο '" + sheetName + 
                 "' εντοπίστηκαν τα εξής σφάλματα:<br><br>" + logs.getLoggerTxt();
                JOptionPane.showMessageDialog(this, message, "Ενημέρωση", JOptionPane.INFORMATION_MESSAGE);
                file.close();
                return false;
            }
            file.close();
            return true;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Το αρχείο '" + def.getExamScheduleFile() + "' δεν βρέθηκε.",
                    "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + def.getExamScheduleFile() + "'.", "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(this,
                    "Στο αρχείο '" + def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή και στήλη " + (rowIndex + 1) + ":"
                            + colIndex
                            + "' με περιεχόμενο κελιού '" + courseCell
                            + "'  δεν μπόρεσε να αντιστοιχηθεί με κάποιο μάθημα/ημερομηνία/timeslot." + ex,
                    "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean isCourseInValidCell(Course course, int x, int y){
        for(ExamCoursesFromFinalSchedule readCoursesFromScheudle : examCoursesFromFinalSchedule){
            if(readCoursesFromScheudle.getCourse().getCourseName() == course.getCourseName()){
                if(readCoursesFromScheudle.getRowIndex() == x && readCoursesFromScheudle.getColIndex() == y){
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean readClassroomsScheduleExcel() {
        int rowIndex = 0;
        int colIndex = 0;
        String courseCell = "";
        String fileName = def.getFolderPath() + "\\" + def.getExamScheduleFile();
        String sheetName = def.getSheet8();
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("el", "GR"));
        symbols.setWeekdays(weekdays);
        boolean errorFlag = false;
        // + 2 για τις 2 πρώτες στήλες
        int lastCol = timeslots.size() + 2;
        try (FileInputStream file = new FileInputStream(new File(fileName))) {
            XlsxSheet sheet = new XlsxSheet(fileName);
            sheet.SelectSheet(sheetName);
            int lastRow = sheet.GetLastRow();
            String header1 = sheet.GetCellString(0, 0);
            String header2 = sheet.GetCellString(0, 1);
            if (!header1.equals("ΗΜΕΡΑ") || !header2.equals("ΗΜ/ΝΙΑ")) {
                file.close();
                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                        def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                        ". Εντοπίστηκαν διαφορετικά headers στην 1η γραμμή στις πρώτες"
                        + " 2 στήλες. Παρακαλώ πολύ ελέγξτε ότι τα δεδομένα για"
                        + " το 1ο και το 2ο κελί είναι 'ΗΜ/ΝΙΑ' και 'ΗΜΕΡΑ' αντίστοιχα.";
                throw new CustomErrorException(this, msg, true);

            }
            String timeslot = "";
            for (int x = 2; x < lastCol; x++) {
                timeslot = sheet.GetCellString(rowIndex, x);
                if (!timeslots.contains(timeslot)) {
                    file.close();
                    String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                            def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                            ". Το χρονικό διάστημα '" + timeslot + "' δεν υπάρχει καταχωρημένο"
                            + " στο βασικό αρχείο πληροφοριών (" + def.getGenericFile() + ").";
                    throw new CustomErrorException(this, msg, true);
                }
            }
            String date;
            String dateName;
            for (rowIndex = 1; rowIndex < lastRow; rowIndex++) {
                date = "";
                dateName = "";
                try{
                    date = sheet.GetCellString(rowIndex, 1);
                }catch(SheetExc ex){
                    date = sheet.GetCellDate(rowIndex, 1).toString();
                }
                dateName = utils.getGreekDayName(date);
                if (dateName == null || !dates.contains(date)) {
                    file.close();
                    String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                            def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                            ". Η ημερομηνία '" + date + "' δεν υπάρχει καταχωρημένη"
                            + " στο βασικό αρχείο πληροφοριών (" + def.getGenericFile() + ") ή δεν βρίσκεται"
                            + " στο έγκαιρο μορφότυπο 'ηη/μμ'ΕΕΕΕ'.";
                    throw new CustomErrorException(this, msg, true);
                }
            }
            for (rowIndex = 1; rowIndex < lastRow; rowIndex++) {
                for (colIndex = 2; colIndex < lastCol; colIndex++) {
                    courseCell = utils.getSafeCellString(sheet, rowIndex, colIndex);
                    if (courseCell.equals("") || courseCell.equals(" ")) {
                        continue;
                    } else {
                        try{
                            String[] lines = courseCell.split("\\r?\\n");
                            String courseName = lines[0].strip();
                            Course tmpCourse = findCourse(courseName);
                            if(tmpCourse == null){
                                file.close();
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                                def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                                ". Το μάθημα '" + courseName + "' δεν υπάρχει καταχωρημένο"
                                + " στο βασικό αρχείο πληροφοριών (" + def.getGenericFile() + ").";
                                new CustomErrorException(this, msg, false);
                                return false;
                            }
                            if(!isCourseInValidCell(tmpCourse, rowIndex, colIndex)){
                                file.close();
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                                def.getExamScheduleFile() + "' στο φύλλο '" + sheetName +
                                "' στην γραμμή " + (rowIndex + 1) + ". To μάθημα '" + tmpCourse.getCourseName() + "' "
                                + "δεν βρίσκεται στην αντίστοιχη θέση με το φύλλο '" + def.getSheet7() + "'.";
                                new CustomErrorException(this, msg, false);
                                return false;
                            }
                            String classroomsWithoutParenthesis = lines[1].replace("(", "").replace(")", "");
                            if(classroomsWithoutParenthesis == null || classroomsWithoutParenthesis.isEmpty()){
                                break;
                            }
                            List<String> classroomsList = Arrays.asList(classroomsWithoutParenthesis.split(",\\s*"));
                            if(!validateClassroomsList(this.getClassroomsNames(), classroomsList)){
                                file.close();
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                                def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                                ". Μία ή περισσότερες από τις αίθουσες του μαθήματος '" + courseName + "' δεν βρέθηκαν καταχωρημένες στο βασικό αρχείο πληροφοριών "
                                + "(" + def.getGenericFile() + ").";
                                new CustomErrorException(this, msg, true);
                            }
                            if(tmpCourse == null){
                                file.close();
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                                def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                                ". Το μάθημα '" + courseName + "' δεν υπάρχει καταχωρημένο"
                                + " στο βασικό αρχείο πληροφοριών (" + def.getGenericFile() + ").";
                                throw new CustomErrorException(this, msg, false);
                            } else {
                                CourseClassroomsPanel tmp;
                                tmp = utils.findPanelForCourse(tmpCourse, coursesClassroomsPanel);
                                tmp.selectClassrooms(classroomsList);
                            }
                        }catch(Exception ex){
                            file.close();
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" +
                            def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) +
                            ". Βεβαιωθείτε πως στο φύλλο βρίσκονται καταχωρημένες οι τιμές ως εξής:\n1η γραμμή: 'ΜΑΘΗΜΑ'\n2η γραμμή: ('ΑΙΘΟΥΣΑ_1','ΑΙΘΟΥΣΑ_2')";
                        }
                    }
                }
            }
            file.close();
            return true;

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Το αρχείο '" + fileName + "' δεν βρέθηκε.",
                    "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + fileName + "'.", "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(this,
                    "Στο αρχείο '" + def.getExamScheduleFile() + "' στο φύλλο '" + sheetName + "' στην γραμμή και στήλη " + (rowIndex + 1) + ":"
                            + colIndex
                            + "' με περιεχόμενο κελιού '" + courseCell
                            + "'  δεν μπόρεσε να αντιστοιχηθεί με κάποιο μάθημα." + ex,
                    "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean validateClassroomsList(List<String> classroomsList, List<String> readContents){
        for(String courseName : readContents){
            if(!classroomsList.contains(courseName)){
                return false;
            }
        }
        return true;
    }
    
    public List<String> getClassroomsNames(){
        List<String> classroomsList = new ArrayList<>();
        for(Classroom cls : classrooms){
            classroomsList.add(cls.getClassroomName());
        }
        return classroomsList;
    }
    
    public boolean checkExaminersConflict(Course course, String date, String timeslot) {
        List<Professor> newCourseExaminers = null;
        boolean allAvailable = true;
        for (Course crs : unscheduled.getCourses()) {
            if (crs.getCourseName().equals(course.getCourseName())) {
                newCourseExaminers = new ArrayList<>(course.getExaminers());
                for (Professor prf1 : newCourseExaminers) {
                    int res1 = prf1.isAvailable(date, timeslot);
                    if (res1 == 0 || res1 == 2) {
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
    
    /**
     * Μέθοδος που χρησιμοποιείται για την αφαίρεση ενός μαθήματος
     * από το πάνελ των μαθημάτων - αιθουσών.
     * 
     * @param crs Το αντικείμενο του μαθήματος προς αφαίρεση (Course).
     */
    private void removeCourseFromClassroomsPanel(Course crs) {
        for (Component comp : coursesClassroomsPanel.getComponents()) {
            if (comp instanceof CourseClassroomsPanel) {
                ((CourseClassroomsPanel) comp).getScheduledCourse().getCourse().getCourseName();
                String tmp = ((CourseClassroomsPanel) comp).getScheduledCourse().getCourse().getCourseName();
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
     * Η μέθοδος χρησιμοποιείται για την εύρεση του αντικειμένου μαθήματος (Course)
     * με βάση το όνομα ή την συντομογραφία του μαθήματος.
     * 
     * @param courseName Το όνομα του μαθήματος προς αναζήτηση (String).
     * @return Αντικείμενο Course ή null ανάλογα με το εάν εντοπίστηκε το μάθημα
     * από το string ή όχι (Course).
     */
    public Course findCourse(String courseName) {
        for (Course crs : allCourses) {
            if (crs.getCourseName().equals(courseName) || crs.getCourseShort().equals(courseName)) {
                return crs;
            }
        }
        return null;
    }
    
    public Classroom findClassroom(String classroomName){
        for (Classroom cls : classrooms) {
            if (cls.getClassroomName().equals(classroomName)) {
                return cls;
            }
        }
        return null;
    }
    
    public boolean existsInExamCourses(Course crs){
        for(ExamCoursesFromFinalSchedule tmp : examCoursesFromFinalSchedule){
            if(tmp.getCourse().getCourseName().equals(crs.getCourseName())){
                return true;
            }
        }
        return false;
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
    
    private void showLoading(){
        this.add(lp, "Loading");
        CardLayout cl = (CardLayout)(this.getLayout());
        cl.show(this, "Loading");
    }
    
    private void hideLoading(){
        this.remove(lp);
        this.revalidate();
        this.repaint();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton autoAddProfessorsToSchedulebtn;
    private javax.swing.JButton autoSelectClassroomsForCoursesBtn;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnExportXlsx;
    private javax.swing.JPanel coursesPanel;
    private javax.swing.JScrollPane coursesScrollPane;
    private javax.swing.JScrollPane jScrollPaneClassrooms;
    private javax.swing.JLabel lblCourses;
    private javax.swing.JLabel lblCoursesClassrooms;
    private javax.swing.JLabel lblScheduleDesigner;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JScrollPane modelScrollPane;
    // End of variables declaration//GEN-END:variables
}