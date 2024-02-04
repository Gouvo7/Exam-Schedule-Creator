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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gmele.general.sheets.XlsxSheet;
import org.gmele.general.sheets.exception.SheetExc;

/**
 * Η κλάση ScheduleManager κληρονομείται από την κλάση JFrame και είναι υπεύθυνη για την διαχείριση του παραθύρου της δημιουργίας του προγράμματος εξεταστικής
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */

public class ScheduleManager extends JFrame {
    
    private String[] weekdays = {"ΚΥΡΙΑΚΗ", "ΔΕΥΤΕΡΑ", "ΤΡΙΤΗ", "ΤΕΤΑΡΤΗ", "ΠΕΜΠΤΗ", "ΠΑΡΑΣΚΕΥΗ", "ΣΑΒΒΑΤΟ"};
    private JTable table;
    private ExcelManager excelManager1;
    Definitions def;
    private List<Course> courses;
    private List<Professor> professors;
    private List<Classroom> classrooms;
    private String fileName;
    private DefaultTableModel model;
    private Logs logs;
    private String finalExcelSheet;
    private Scheduled scheduled;
    private Unscheduled unscheduled;
    private List<ExamCoursesFromFinalSchedule> crsList;

    public ScheduleManager() {
    }
    
    public ScheduleManager(ExcelManager excelManager) {
        initComponents();
        this.excelManager1 = excelManager;
        courses = new ArrayList<>(excelManager1.getCourses());
        professors = new ArrayList<>(excelManager1.getProfs());
        classrooms = new ArrayList<>(excelManager1.getClassrooms());
        scheduled = new Scheduled();
        unscheduled = new Unscheduled(excelManager1.getCourses());
        crsList = new ArrayList<>();
        logs = new Logs();
        def = excelManager1.getDefinitions();
        finalExcelSheet = def.getSheet7();
        fileName = def.getFolderPath() + "\\" + def.getExamScheduleFile();
        modelPanel.setLayout(new BorderLayout());
        coursesPanel.setLayout(new GridLayout(0,3));
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modelScrollPane = new javax.swing.JScrollPane();
        modelPanel = new javax.swing.JPanel();
        coursesScrollPane = new javax.swing.JScrollPane();
        coursesPanel = new javax.swing.JPanel();
        courseClassroomPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Φόρμα Δημιουργίας Προγράμματος");
        setPreferredSize(new java.awt.Dimension(1200, 1050));

        modelScrollPane.setPreferredSize(new java.awt.Dimension(1000, 600));

        modelPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        modelPanel.setLayout(null);
        modelScrollPane.setViewportView(modelPanel);

        coursesScrollPane.setPreferredSize(new java.awt.Dimension(1000, 300));

        coursesPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        coursesPanel.setLayout(null);
        coursesScrollPane.setViewportView(coursesPanel);

        jLabel1.setFont(new java.awt.Font("Franklin Gothic Book", 0, 18)); // NOI18N
        jLabel1.setText("Σχεδιαστής Προγράμματος Εξεταστικής");

        jLabel2.setFont(new java.awt.Font("Franklin Gothic Book", 0, 18)); // NOI18N
        jLabel2.setText("Μαθήματα");

        jLabel3.setFont(new java.awt.Font("Franklin Gothic Book", 0, 18)); // NOI18N
        jLabel3.setText("Μαθήματα - Αίθουσες");

        jButton1.setText("Εξαγωγή");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createExcelFromTable(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(modelScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(365, 365, 365)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(coursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(458, 458, 458)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(27, 27, 27)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(courseClassroomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1050, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(464, 464, 464))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(modelScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(coursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(courseClassroomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1146, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void startProcess(boolean isNew){
        modelPanel.add(populateTable());
        populateCourses();
        if (!isNew){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fillCoursesFromExamScheduleData();
                }
            });
        }
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setSize(1500, 1050);
    }
    
    public void fillCoursesFromExamScheduleData(){
        model = (DefaultTableModel) table.getModel();
        try{
            for (ExamCoursesFromFinalSchedule data : crsList) {
                int row = data.getRowIndex();
                int col = data.getColIndex();
                Course course = data.getCourse();
                // Assuming course name is what's being displayed in the table
                model.setValueAt(course.getCourseName(), row, col - 1);
            }
            // Refresh the table to reflect changes
            table.revalidate();
            table.repaint();
        }catch(Exception ex){
            System.out.println(ex);
        }
    }
    
    private void createExcelFromTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createExcelFromTable
        int rowIndex = 0;
        int colIndex = 0;
        String path = def.getFolderPath() + "\\" + def.getExamScheduleFile();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("DD/MM/yyyy");
        List<String> timeslots = new ArrayList<>(excelManager1.getTimeslots());
        List<String> dates = new ArrayList<>(excelManager1.getDates());
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("ΠΡΟΓΡΑΜΜΑ ΕΞΕΤΑΣΤΙΚΗΣ");
            
            //fillHeaders(workbook, sheet, dateFormatter, timeslots, dates);
            int tableRows = table.getRowCount();
            int tableColumns = table.getColumnCount();
            for (rowIndex = 1; rowIndex < tableRows; rowIndex++){
                for (colIndex = 1; colIndex < tableColumns; colIndex++){
                    try{
                        String cellValue = (String) table.getValueAt(rowIndex, colIndex);
                        Course a = getCourse(cellValue);
                        if (a != null){
                            Cell excelCell1 = (Cell) sheet.getRow(rowIndex).createCell(colIndex + 1);
                            excelCell1.setCellValue(a.getCourseName());
                            //Cell excelCell2 = (Cell) sheet.getRow(i).createCell(j + 2);
                            //excelCell2.setCellValue(a.getCourseSem());
                        }
                    }catch (Exception e){
                        JOptionPane.showMessageDialog(this, "Exception thrown:" + rowIndex + " " + colIndex + e,"Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            autoSizeColumns(sheet, tableColumns);
            applyCellStyles(workbook, sheet, tableRows, tableColumns);
            
            JOptionPane.showMessageDialog(this, "Η δημιουργία του αρχείου προγράμματος εξεταστικής ολοκληρώθηκε επιτυχώς!", "Μήνυμα Λάθους", JOptionPane.INFORMATION_MESSAGE);
            // Αποθήκευση αρχείου προς συμπλήρωση για τους καθηγητές
            try (FileOutputStream outputStream = new FileOutputStream(path.toString())) {
                workbook.write(outputStream);
            }
            
            //logger.appendLogger("Η δημιουργία του template για τους καθηγητές ολοκληρώθηκε επιτυχώς.");
            //logger.appendLogger("Η δημιουργία του template για τους καθηγητές ολοκληρώθηκε επιτυχώς.");
        }catch (Exception e){
            //logger.appendLogger("Η δημιουργία του template για τους καθηγητές απέτυχε.");
            System.out.println("Η δημιουργία του template για τους καθηγητές απέτυχε." + rowIndex + " " + colIndex);
        }
    }//GEN-LAST:event_createExcelFromTable

    public Course getCourse(String cellValue){
        for (Course tmp : this.courses){
            if (tmp.getCourseName().equals(cellValue)){
                return tmp;
            }
        }
        return null;
    }
    
    /**
     * Η συνάρτηση χρησιμοποιείται για να προτρέψει τον χρήστη στο να επιλέξει την τοποθεσία
     * αλλά και το όνομα αποθήκευσης του τελικού αρχείου προγράμματος σε .xlsx μορφή.
     * @return Το πλήρες μονοπάτι του αρχείου.
     */
    public String askUserToSaveFile(){
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files (*.xlsx, *.xls)", "xlsx");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            String fullPath = selectedFile.getAbsolutePath();
            if (!fullPath.contains(".xlsx")){
                fullPath = fullPath + ".xlsx";
                return fullPath;
            }else{
                return fullPath;
            }
        }
        return null;
    }
    
    public void readExamSchedule(){
        int rowIndex = 0;
        int colIndex = 0;
        String courseCell = "";
        String fileName = def.getFolderPath() + "\\" + def.getExamScheduleFile();
        String sheetName= def.getSheet7();
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("el", "GR"));
        symbols.setWeekdays(weekdays);
        List<String> timeslots = new ArrayList<>(excelManager1.getTimeslots());
        List<String> dates = new ArrayList<>(excelManager1.getDates());
        // + 2 για τις 2 πρώτες στήλες
        int lastCol = timeslots.size() + 2;
        try (FileInputStream file = new FileInputStream(new File(fileName))) {
            XlsxSheet sheet = new XlsxSheet(fileName);
            sheet.SelectSheet(sheetName);
            int lastRow = sheet.GetLastRow();
            
            String header1 = sheet.GetCellString(0, 0);
            String header2 = sheet.GetCellString(0, 1);
            
            if (!header1.equals("ΗΜ/ΝΙΑ") || !header2.equals("ΗΜΕΡΑ")){
                //throw new NotEqualHeadersException();
            }
            String timeslot = "";
            for(int x = 2; x < lastCol; x++){
                timeslot = sheet.GetCellString(rowIndex, x);
                if (!timeslots.contains(timeslot)){
                    //throw new TimeslotNotFoundException();
                }
            }
            String date;
            String dateName;
            for(rowIndex = 1; rowIndex < lastRow; rowIndex++){
                date = "";
                dateName = "";            
                date = sheet.GetCellString(rowIndex, 1);
                dateName = getGreekDayName(date);
                if (dateName == null || !dates.contains(date)){
                    //throw new DateNotFoundException();
                    logs.appendLogger(logs.getIndexString() + "Πρόβλημα με την ημερομηνία '" + date  + "' γραμμή " + rowIndex + ".");
                }
            }
            logs.appendLogger("Κατά την διαδικασία ανάγνωσης του αρχείου '" + fileName + "', εντοπίστικαν" +
                            " τα παρακάτω σφάλματα:");
            for(rowIndex = 1; rowIndex < lastRow; rowIndex++){
                for(colIndex = 2; colIndex < lastCol; colIndex++){
                    courseCell = getSafeCellString(sheet, rowIndex, colIndex);
                    if (courseCell.equals("") || courseCell.equals(" ") || courseCell == null){
                        continue;
                    }
                    else{
                        if (findCourse(courseCell)== null){ 
                            //throw new CourseNotFoundException();
                            logs.appendLogger(logs.getIndexString() + "Πρόβλημα με το μάθημα '" + courseCell + "' στην γραμμή/στήλη" + rowIndex + ":" + colIndex + "."
                                    + " Το μάθημα δεν βρέθηκε στην λίστα των μαθημάτων.");
                        }else{
                            String courseTimeslot = sheet.GetCellString(0, colIndex );
                            String courseDate = sheet.GetCellString(rowIndex, 1);
                            Course crs = new Course(findCourse(courseCell));
                            boolean check = checkExaminersConflict(crs, courseDate, courseTimeslot);
                            if (check){
                                ExamCoursesFromFinalSchedule courseDet = new ExamCoursesFromFinalSchedule(crs, rowIndex, colIndex);
                                crsList.add(courseDet);
                            }else{
                                logs.appendLogger(logs.getIndexString() + "Πρόβλημα με το μάθημα '" + courseCell + "' και \n"
                                + "την διαθεσιμότητα των καθηγητών. Το μάθημα θα αγνοηθεί.");
                            }
                        }
                    }
                }
            }
            System.out.println(logs.getLoggerTxt());
            file.close();
            
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Το αρχείο '" + fileName + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(this, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + fileName + "'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex){
            JOptionPane.showMessageDialog(this, "Στο αρχείο '" + def.getExamScheduleFile() + "' στην γραμμή και στήλη " + rowIndex + ":" + colIndex
                    + "' με περιεχόμενο κελιού '" + courseCell + "'  δεν μπόρεσε να αντιστοιχηθεί με κάποιο μάθημα."  + ex ,"Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(this, "Βρέθηκε μάθημα στο αρχείο '" + fileName + "' στο φύλλο '" + finalExcelSheet + "' στην γραμμή " + rowIndex + " που δεν υπάρχει"
                                + " στο φύλλο των μαθημάτων. ","Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public String getSafeCellString(XlsxSheet sheet, int rowIndex, int colIndex) {
    try {
        return sheet.GetCellString(rowIndex, colIndex);
    } catch (Exception e) { // Σε περίπτωση που το κελί είναι κενό (null), επιστρέφω ένα κενό string.
        return "";
    }
}
    
    /**
     * Η μέθοδος χρησιμοποιείται για την δημιουργία των αντικειμένων των μαθημάτων στο panel στο κάτω μέρος
     * του κεντρικού παραθύρου
     */
    public void populateCourses() {
        List<Course> notSettled = new ArrayList<>(unscheduled.getCourses());
        for (Course course : notSettled) {
            // Create a custom component (e.g., JPanel or JButton) for each course
            JButton courseButton = new JButton(course.getCourseName());
            courseButton.setPreferredSize(new Dimension(270, 40)); // Set preferred size as needed
            courseButton.setText(course.getCourseName());
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
    
    /**
     * Η μέθοδος χρησιμοποιείται για την καταχώρηση των headers των ημερομηνιών και των 
     * χρονικών διαστημάτων.
     */
    public JScrollPane populateTable(){
        List<String> timeslots = excelManager1.getTimeslots();
        List<String> dates = new ArrayList<>(excelManager1.getDates());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        int excelRows = dates.size();
        int excelCols = timeslots.size();
        
        model = new DefaultTableModel(excelRows + 1, excelCols + 1){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            public boolean isCellSelected(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                Color headerCells = Color.decode("#A9A9A9");
                Color firstCell = Color.decode("#3333FF");
                Color otherCells = Color.decode("#FFFFFF");
                // Apply the custom background color logic
                if (column == 0 && row == 0) {
                    // First cell in the first column and first row
                    comp.setBackground(firstCell);
                } else if (column == 0) {
                    // Any cell in the first column (excluding the first cell)
                    comp.setBackground(headerCells);
                } else if (row == 0 && column > 0) {
                    // Any cell in the first row (excluding the first cell)
                    comp.setBackground(headerCells);
                } else {
                    // All other cells
                    comp.setBackground(otherCells);
                }
                return comp;
            }
        };
        table.setRowHeight(60);
        table.setCellSelectionEnabled(false);
        table.setTableHeader(new JTableHeader());
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        this.table = table;
        model.setValueAt("ΗΜΕΡΟΜΗΝΙΑ / ΗΜΕΡΑ", 0, 0);
        for (int j = 0; j < excelCols; j++){
            model.setValueAt(timeslots.get(j), 0, j + 1);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            table.getColumnModel().getColumn(j).setCellRenderer(centerRenderer);
            if (j == (excelCols - 1)){
                table.getColumnModel().getColumn(excelCols).setCellRenderer(centerRenderer);
            }
        }
        
        for (int i = 0; i < excelRows; i++) {
            LocalDate date = LocalDate.parse(dates.get(i), dateFormatter);
            String greekDayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR")).toUpperCase();
            String greekDayNameWithoutAccents = Normalizer.normalize(greekDayName, Normalizer.Form.NFD)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            model.setValueAt(dates.get(i) + " " + greekDayNameWithoutAccents, i + 1, 0);
        }
        
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
                    
                    if (row > 0 & col > 0){
                        Course tmpCourse = new Course(findCourse(buttonText));
                        String rowValue = (String) table.getValueAt(row, 0);
                        String colValue = (String) table.getValueAt(0, col);

                        // Μετατροπή της ημερομηνίας από την μορφή 'ηη/μμ/εεεε Ημέρα' to simply 'ηη/μμ/εεεε'
                        rowValue = getDateWithGreekFormat(rowValue);
                        //if (rowValue == null){
                            //System.exit(-12);
                        //}
                        boolean check1 = checkExaminersConflict(tmpCourse, rowValue, colValue);
                        if (check1){
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
                        }
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
                    date = getDateWithGreekFormat(date);
                    String timeslot = table.getValueAt(0, selectedColumn).toString();
                    Object cellValue = model.getValueAt(selectedRow, selectedColumn);
                    Course courseToDelete = null;
                    if (cellValue != null && cellValue instanceof String && selectedRow > 0 && selectedColumn != 0) {
                        String buttonText = (String) cellValue;
                        for (Course crs : scheduled.getCourses()){
                            if (crs.getCourseName().equals(buttonText)){
                                for (Professor prf : crs.getExaminers()){
                                    prf.changeSpecificAvailability(date, timeslot,1);
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
                        model.setValueAt(null, selectedRow, selectedColumn);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ScheduleManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ScheduleManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ScheduleManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ScheduleManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ScheduleManager().setVisible(true);
            }
        });
    }
    
     public boolean checkExaminersConflict(Course course, String date, String timeslot){
        List<Professor> newCourseExaminers = null;
        String errorMsg = "";
        boolean notAllAvailable = false;
        for (Course crs : unscheduled.getCourses()){
            if (crs.getCourseName().equals(course.getCourseName())){
                newCourseExaminers = new ArrayList<>(course.getExaminers());
                errorMsg = "Για το μάθημα " + crs.getCourseName() + " :";
                for (Professor prf1 : newCourseExaminers){
                    int res1 = prf1.isAvailable(date, timeslot);
                    if (res1 == 0 || res1 == 2){
                        logs.appendLogger(logs.getIndex() + ") Πρόβλημα με την διαθεσιμότητα καθηγητών για το μάθημα: '" + crs.getCourseName() + "')");
                        errorMsg = errorMsg + "\nΟ καθηγητής " + prf1.getProfSurname() + " " + prf1.getProfFirstname() +
                        " δεν είναι διαθέσιμος την ημερομηνία " + date + " και ώρα " + timeslot + ".";
                        notAllAvailable = true;
                    }
                }
                if (!notAllAvailable){
                    for (Professor prf2 : newCourseExaminers){
                        prf2.changeSpecificAvailability(date, timeslot, 2);
                    }
                    scheduled.addCourse(crs, date, timeslot);
                    unscheduled.getCourses().remove(crs);
                    return true;
                }
            }
        }
        JOptionPane.showMessageDialog(this, errorMsg, "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    public String getDateWithGreekFormat(String dateStr) {
        try {
            Locale greekLocale = new Locale("el", "GR");
            SimpleDateFormat inputFormatter = new SimpleDateFormat("dd/MM/yyyy EEEE", greekLocale);
            
            // Create a custom DateFormatSymbols with weekdays without accents
            DateFormatSymbols customSymbols = new DateFormatSymbols(greekLocale);
            customSymbols.setWeekdays(weekdays);
            inputFormatter.setDateFormatSymbols(customSymbols);

            Date date = inputFormatter.parse(dateStr);

            SimpleDateFormat outputFormatter = new SimpleDateFormat("dd/MM/yyyy");
            return outputFormatter.format(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getGreekDayName(String dateStr) {
        try {
            // Parse the input date string
            SimpleDateFormat inputFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormatter.parse(dateStr);

            // Get the Greek day name without accents and in capital letters
            SimpleDateFormat outputFormatter = new SimpleDateFormat("EEEE", new Locale("el", "GR"));
            String greekDayName = outputFormatter.format(date).toUpperCase();

            return greekDayName;
        } catch (ParseException e) {
            // Handle parsing exception
            e.printStackTrace();
            return null;
        }
    }

    
    /**
     * Η μέθοδος ευθύνεται για τον έλεγχο διαθεσιμότητας των καθηγητών ενός μαθήματος για μία συγκεκριμενη ημερομηνία και χρονικό πλαίσιο
     * 
     * @param course Αντικείμενο της κλάσης Course όπου από αυτό θα αντλήσουμε τους εξεταστές καθηγητές
     * @param dateStr Η ημερομηνία προς έλεγχο
     * @param timeslotStr Το χρονικό πλαίσιο προς έλεγχο
     * @return true ή false ανάλογα με το εάν όλοι οι καθηγητές του μαθήματος θα ήταν διαθέσιμοι εκείνη την συγκεκριμένη χρονική περίοδο ή όχι
     */
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
    
    /**
     * Η μέθοδος είναι υπεύθυνη για τον εντοπισμό του μαθήματος από ένα string
     * 
     * @param courseName Το όνομα του μαθήματος προς αναζήτηση
     * @return Αντικείμενο Course ή null ανάλογα με το εάν εντοπίστηκε το μάθημα από το string ή όχι
     */
    public Course findCourse(String courseName){
        for (Course course : this.courses){
            if (course.getCourseName().equals(courseName)){
                return course;
            }
        }
        return null;
    }
    
    private void fillHeaders(XSSFWorkbook workbook, XSSFSheet sheet, DateTimeFormatter dateFormatter, List<String> timeslots, List<String> dates){
        CellStyles cs = new CellStyles();
        // Προσθήκη των χρονικών διαστημάτων (timeslots) ως headers στις στήλες
        Row timeslotRow = sheet.createRow(0);
        Cell cell1 = timeslotRow.createCell(0);
        Cell cell2 = timeslotRow.createCell(1);
        cell1.setCellValue("ΗΜ/ΝΙΑ");
        cell2.setCellValue("ΗΜΕΡΑ");
        for (int i = 0; i < timeslots.size(); i++) {
            Cell cell = timeslotRow.createCell(i + 2);
            cell.setCellValue(timeslots.get(i));
            cell.setCellStyle(cs.getDateValuesStyle(workbook));
        }

        // Προσθήκη των ημερομηνιών στις γραμμές της 1ης στήλης
        int rowIndex = 1;
        for (String tmp : dates) {
            Row row = sheet.createRow(rowIndex++);
            Cell dayCell = row.createCell(0);
            String greekDayName = getGreekDayName(tmp);
            greekDayName = removeAccents(greekDayName);
            dayCell.setCellValue(greekDayName.toUpperCase());
            Cell dateCell = row.createCell(1);
            dateCell.setCellValue(tmp);
        }
    }
    
    private void applyCellStyles(XSSFWorkbook workbook, XSSFSheet sheet, int rows, int columns){
        int curRow, curCol = 0;
        CellStyles cs = new CellStyles();
        for (Row row : sheet) {
            for (Cell cell : row) {
                curRow = cell.getRowIndex();
                curCol = cell.getColumnIndex();
                if (curRow > 0 && curCol >= 2){
                    cell.setCellStyle(cs.getFinalScheduleBasicStyle(workbook));
                }else
                if (curRow == 0 && curCol <= 1){
                    cell.setCellStyle(cs.getDateHeadersStyle(workbook));
                }else
                if (curRow > 0 && curCol < 2){
                    cell.setCellStyle(cs.getDateValuesStyle(workbook));
                }else
                if (curRow == 0 && curCol > 1){
                    cell.setCellStyle(cs.getTimeslotsHeadersStyle(workbook));
                }
            }
        }
    }
    
    private static String removeAccents(String s) {
        return s.replaceAll("ά", "α")
            .replaceAll("έ", "ε")
            .replaceAll("ή", "η")
            .replaceAll("ί", "ι")
            .replaceAll("ό", "ο")
            .replaceAll("ύ", "υ")
            .replaceAll("ώ", "ω")
            .replaceAll("Ά", "Α")
            .replaceAll("Έ", "Ε")
            .replaceAll("Ή", "Η")
            .replaceAll("Ί", "Ι")
            .replaceAll("Ό", "Ο")
            .replaceAll("Ύ", "Υ")
            .replaceAll("Ώ", "Ω");
    }
    
    private void autoSizeColumns(XSSFSheet sheet, int columns){
        for (int i = 0; i <= columns; i++){
            sheet.autoSizeColumn(i);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel courseClassroomPanel;
    private javax.swing.JPanel coursesPanel;
    private javax.swing.JScrollPane coursesScrollPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JScrollPane modelScrollPane;
    // End of variables declaration//GEN-END:variables

}