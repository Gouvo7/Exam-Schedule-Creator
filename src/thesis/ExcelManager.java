package thesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gmele.general.sheets.XlsxSheet;
import org.gmele.general.sheets.exception.SheetExc;

/**
 * Η κλάση ExcelManager είναι υπεύθυνη για την δημιουργία και διαχείριση των αρχείων .xlsx
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * @param - myJFrame - Το παράθυρο που το καλεί.
 * @param - fileName - Το όνομα του αρχείου προς ανάγνωση.
 */
public class ExcelManager {
    
    
    private static String fileName;
    private static String sheet1, sheet2,sheet3, sheet4, sheet5, sheet6;
    private static JFrame myJFrame;
    private static Logs logger;
    private Paths paths;

    private List<Professor> profs;
    private List<Course> courses;
    private List<Classroom> classrooms;
    private List<String> timeslots;
    private List<String> dates;
    
    ExcelManager(JFrame x, String y){
        myJFrame = x;
        fileName = y;
        logger = new Logs();
        paths = new Paths();
        fileName = paths.getImportFilePath();
        sheet1 = paths.getSheet1();
        sheet2 = paths.getSheet2();
        sheet3 = paths.getSheet3();
        sheet4 = paths.getSheet4();
        sheet5 = paths.getSheet5();
        sheet6 = paths.getSheet6();
    }
    
    ExcelManager(){
        logger = new Logs();
        paths = new Paths();
        fileName = paths.getImportFilePath();
        sheet1 = paths.getSheet1();
        sheet2 = paths.getSheet2();
        sheet3 = paths.getSheet3();
        sheet4 = paths.getSheet4();
        sheet5 = paths.getSheet5();
        sheet6 = paths.getSheet6();
    }
    
    public List<Professor> getProfs() {
        return profs;
    }

    public void setProfs(List<Professor> profs) {
        this.profs = profs;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    public List<String> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<String> timeslots) {
        this.timeslots = timeslots;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }
    
    /**
     * Η συνάρτηση:
     * 1) Διαβάζει το αρχείο από το μονοπάτι που καθορίζει ο χρήστης από την εφαρμογή.
     * 2) Αποθηκεύει τα δεδομένα σε αντικείμενα κατάλληλου τύπου
     * 3) Παράγει δύο νέα αρχεία όπου το 1ο συμπληρώνεται από τους καθηγητές και 
     * αφορά την διαθεσιμότητά τους για τις ημερομηνίες της εξεταστικής. Για κάθε 
     * καθηγητή παράγεται ένα φύλλο, με γραμμές για τις ημερομηνίες και τα χρονικά 
     * διαστήματα μεταξύ των ωρών λειτουργίας του Πανεπιστημίου. Το 2ο αρχείο
     * συμπληρώνεται από τον υπεύθυνο σύνταξης του προγράμματος εξεταστικής και
     * αφορά την διαθεσιμότητα των αιθουσών για τις ημερομηνίες της εξεταστικής.
     * 
     * Σε περίπτωση που κάποιο από τα φύλλα δεν είναι συμπληρωμένο με έγκυρες πληροφορίες
     * ή εμπεριέχει κενά, θα προκληθεί ένα Exception και η διαδικασία θα διακοπεί.
     */
    public void createExcels(){
        try {
            boolean excel1, excel2, excel3, excel4, excel5 = false;
            
            profs = new ArrayList<>();
            profs = readProfs(fileName);
            excel1 = true;
            if (profs == null) {
                throw new Exception();
            }

            timeslots = new ArrayList<>();
            timeslots = readTimeslots(fileName);
            if (timeslots == null) {
                throw new Exception();
            }
            excel2 = true;

            dates = new ArrayList<>();
            dates = readDates(fileName);
            if (dates == null) {
                throw new Exception();
            }
            excel3 = true;
            Collections.sort(dates, new DateComparator());
            
            classrooms = new ArrayList<>();
            classrooms = readClassrooms(fileName);
            if (classrooms == null) {
                throw new Exception();
            }
            excel4 = true;

            courses = new ArrayList<>();
            courses = readCourses(fileName, profs);
            if (courses == null) {
                throw new Exception();
            }
            excel5 = true;
            addProfsToCourses(profs, courses, fileName);
            removeCoursesWithNoExaminers();
            if (excel1 && excel2 && excel3 && excel4 && excel5) {
                createTemplate(profs, timeslots, dates, classrooms);
            }
            saveObjects();

        } catch (Exception e) {
            return;
        }
    }

    
    
    
    
    /**
     * Ενημέρωση της λίστας των καθηγητών που εξετάζουν το μάθημα σε ολόκληρη την λίστα αντικειμένων Course.
     * @param profs Η λίστα με τους καθηγητές.
     * @param courses Η λίστα με τα μαθήματα.
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @throws SheetExc 
     */
    public void addProfsToCourses(List<Professor> profs, List<Course> courses, String filename) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet6);
            int rowIndex = 1;
            int lastRow = s.GetLastRow();
            List<String> uniqueCourses = new ArrayList<>();
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String course = s.GetCellString(rowIndex, 0).trim();
                    if (uniqueCourses.contains(course)){
                        JOptionPane.showMessageDialog(myJFrame, "Βρέθηκε το ίδιο μάθημα"
                                + " παραπάνω από μια φορά στο φύλλο " + sheet6 + ". Παρακαλώ ελέγξτε τα στοιχεία"
                                + "  και δοκιμάστε ξανά.","Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
                        return ;
                    }else{
                        uniqueCourses.add(course);
                    }
                }
                rowIndex++;
            }
            rowIndex = 1;
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String course = s.GetCellString(rowIndex, 0).trim();
                    String profA = s.GetCellString(rowIndex, 1).trim();
                    String profB = s.GetCellString(rowIndex, 2).trim();
                    String profC = s.GetCellString(rowIndex, 3).trim();
                    String profD = s.GetCellString(rowIndex, 4).trim();
                    boolean exists = false;
                    for (Course tmpCourse : courses){
                        if (tmpCourse.getCourseName().equals(course)){
                            exists = true;
                            if (checkIfValid(course)){
                                for (Professor prof : profs){
                                    if (prof.getProfSurname().equals(profA) || prof.getProfSurname().equals(profB) ||
                                        prof.getProfSurname().equals(profC) || prof.getProfSurname().equals(profD) ){
                                        if (!tmpCourse.getExaminers().contains(prof)){
                                            tmpCourse.addExaminer(prof);
                                            //System.out.println("Added "  + prof.getProfSurname() + " to course " + tmpCourse.getCourseName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!exists){
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            file.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + filename + "'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Βρέθηκε μάθημα στο φύλλο '" + sheet6 + "' που δεν υπάρχει"
                                + " στο φύλλο των μαθημάτων.","Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return ;
    }
    
    /**
     * Εντοπισμός όλων των μαθημάτων που έχουν καταχωρηθεί στο βασικό αρχείο.
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @param profs Η λίστα με τους καθηγητές.
     * @return Μία λίστα με αντικείμενα τύπου Course.
     * @throws SheetExc 
     */
    public List<Course> readCourses(String filename, List<Professor> profs) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet5);
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            List<Course> courses = new ArrayList<>();
            
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0).trim();
                    String cellB = s.GetCellString(rowIndex, 1).trim();
                    String cellC = s.GetCellString(rowIndex, 2).trim();
                    String cellD = s.GetCellString(rowIndex, 3).trim();
                    
                    if (checkIfValid(cellA) && checkIfValid(cellB) && checkIfValid(cellC) && checkIfValid(cellD) ){
                        boolean dupliicate = false;
                        if (courses.isEmpty()){
                            dupliicate = false;
                        }else{
                            for (Course tmp : courses){
                                if (checkDuplicateCourse(tmp,cellA)){
                                    dupliicate = true;
                                    break;
                                }
                            }
                        }
                        if (!dupliicate){
                            if (cellD.equals("+")){
                                Course tmp = new Course(cellA,cellB,cellC,true);
                                courses.add(tmp);
                            }else{
                                Course tmp = new Course(cellA,cellB,cellC,false);
                                courses.add(tmp);
                            }
                        }
                    }
                    else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            file.close();
            return courses;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + filename + "'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα δεδομένα του φύλλου: '"
            + sheet5 + "'. Βεβαιωθείτε ότι δεν υπάρχουν κενά κελιά ή διπλότυπες εγγραφές.",
            "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * Εντοπισμός όλων των αιθουσών που έχουν καταχωρηθεί στο βασικό αρχείο.
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @return Μία λίστα με αντικείμενα τύπου Classroom.
     * @throws SheetExc 
     */
    public List<Classroom> readClassrooms(String filename) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet4);
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            List<Classroom> classrooms = new ArrayList<>();
            
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0);
                    String cellB = s.GetCellString(rowIndex, 1);
                    int cellC = (int) s.GetCellNumeric(rowIndex, 2);
                    String cellD = s.GetCellString(rowIndex, 3);
                    cellA = cellA.trim();
                    cellB = cellB.trim();
                    cellD = cellD.trim();
                    if (cellA != null && cellB != null && cellC != 0 && cellD != null){
                        Classroom tmp = new Classroom(cellA, cellB, cellC, cellD);
                        classrooms.add(tmp);
                    }
                    else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            file.close();
            return classrooms;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + filename + "'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα δεδομένα του φύλλου: '"
            + sheet4 + "'. Βεβαιωθείτε ότι δεν υπάρχουν κενά κελιά ή διπλότυπες εγγραφές.",
            "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * Εντοπισμός όλων των ημερομηνιών της εξεταστικής που έχουν καταχωρηθεί στο βασικό αρχείο.
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @return Ένα HashMap με ζευγάρια ημέρας - ημερομηνίας.
     * @throws SheetExc 
     */
    public List<String> readDates(String filename) throws SheetExc{
        try {
            FileInputStream file = new FileInputStream(new File(filename));
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet3);
            List<String> dates = new ArrayList<>();
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    Date date;
                    try {
                        date = inputDateFormat.parse(s.GetCellDate(rowIndex, 0).toString());
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(myJFrame, "Λάθος τύπος "
                                + "δεδομένων στο φύλλο '" + sheet3 + "'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
                        file.close();
                        return null;
                    }
                    String cellA = outputDateFormat.format(date).trim();
                    if (checkIfValid(cellA) && !dates.contains(cellA)){
                        dates.add(cellA);
                    }else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            Collections.sort(dates);
            file.close();
            return dates;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + filename + "'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα δεδομένα του φύλλου: '"
            + sheet3 + "'. Βεβαιωθείτε ότι δεν υπάρχουν κενά κελιά ή διπλότυπες εγγραφές.",
            "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * Εντοπισμός όλων των διαστημάτων εξέτασης που έχουν καταχωρηθεί στο βασικό αρχείο.
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @return Μία λίστα με Strings.
     * @throws SheetExc 
     */
    public List<String> readTimeslots(String filename) throws SheetExc{
        try {
            FileInputStream file = new FileInputStream(new File(filename));
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet2);
            //Iterate through each rows one by one
            List<String> timeslots = new ArrayList<>();
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0);
                    if(checkIfValid(cellA) && !timeslots.contains(cellA)){
                        timeslots.add(cellA);
                    }else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            file.close();
            return timeslots;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" +filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + filename + "'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα δεδομένα του φύλλου: '"
            + sheet3 + "'. Βεβαιωθείτε ότι δεν υπάρχουν κενά κελιά ή διπλότυπες εγγραφές.",
            "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * Εντοπισμός όλων των καθηγητών που έχουν καταχωρηθεί στο βασικό αρχείο.
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @return Μία λίστα με αντικείμενα τύπου Professor.
     * @throws SheetExc 
     */
    public List<Professor> readProfs(String filename) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet1);
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            List<Professor> profs = new ArrayList<>();

            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0).trim();
                    String cellB = s.GetCellString(rowIndex, 1).trim();
                    String cellC = s.GetCellString(rowIndex, 2).trim();
                    if (cellA != null && cellB != null && cellC != null){
                        boolean duplicate = false;
                        if (profs.isEmpty()){
                            duplicate = false;
                        }else{
                            for (Professor tmp : profs){
                                if (checkDuplicateProfessor(tmp,cellA,cellB,cellC)){
                                    duplicate = true;
                                    break;
                                }
                            }
                        }
                        if (!duplicate){
                            Professor prof = new Professor(cellA, cellB, cellC);
                            profs.add(prof);
                        }else{
                            throw new Exception();
                        }
                    }
                    else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            file.close();
            
            return profs;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + filename + "'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα δεδομένα του φύλλου: '"
            + sheet1 + "'. Βεβαιωθείτε ότι δεν υπάρχουν κενά κελιά ή διπλότυπες εγγραφές.",
            "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * Δημιουργία των template αρχείων προς συμπλήρωση. 
     * 1 αρχείο αφορά τους καθηγητές και την διαθεσιμότητά τους για τις ημερομηνίες 
     * της εξεταστικής. Κάθε φύλλο θα συμπληρώνεται από τον κάθε καθηγητή ξεχωριστά.
     * 2 αρχείο αφορά τις αίθουσες και την διαθεσιμότητά τους για τις ημερομηνίες
     * της εξεταστικής. Τα φύλλα συμπληρώνονται από τον υπεύθυνο για το πρόγραμμα.
     * @param uniqueProfs Λίστα αντικειμένων καθηγητών (Professor).
     * @param timeslots Λίστα strings με τα διαστήματα εξέτασης. 
     * @param dates Λίστα strings με τα ζευγάρια ημέρας - ημερομηνίας.
     * @param classrooms Λίστα αντικειμένων αιθουσών (Course).
     */
    public void createTemplate(List<Professor> uniqueProfs, List<String> timeslots, List<String> dates,List<Classroom> classrooms){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle style = getStyle(workbook);

            for (Professor professor : uniqueProfs) {
                // Δημιουργία φύλλου (sheet) για κάθε καθηγητή
                String sheetName = professor.getProfSurname()+ " " + professor.getProfFirstname();
                    XSSFSheet sheet = workbook.createSheet(sheetName);
                
                
                    // Προσθήκη των χρονικών διαστημάτων (timeslots) ως headers στις στήλες
                    Row timeslotRow = sheet.createRow(0);
                    for (int i = 0; i < timeslots.size(); i++) {
                        Cell cell = timeslotRow.createCell(i+1);
                        cell.setCellValue(timeslots.get(i));
                        sheet.autoSizeColumn(i+1);
                    }

                    // Προσθήκη των ημερομηνιών στις γραμμές της 1ης στήλης
                    int rowIndex = 1;
                    for (String tmp : dates) {
                        Row row = sheet.createRow(rowIndex++);
                        Cell dateCell = row.createCell(0);
                        LocalDate date = LocalDate.parse(tmp, dateFormatter);
                        String greekDayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR"));
                        dateCell.setCellValue(greekDayName + " " + tmp);
                    }
                    for (Row row : sheet) {
                        for (Cell cell : row) {
                            cell.setCellStyle(style);
                        }
                    }
                    sheet.autoSizeColumn(0);
            }

            // Αποθήκευση αρχείου προς συμπλήρωση για τους καθηγητές
        try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\prof.xlsx")) {
                workbook.write(outputStream);
            }
            logger.appendLogger("Η δημιουργία του template για τους καθηγητές ολοκληρώθηκε επιτυχώς.");
        }catch (Exception e){
            logger.appendLogger("Η δημιουργία του template για τους καθηγητές απέτυχε.");
        }
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle style = getStyle(workbook);

            for (Classroom classroom : classrooms) {
                // Δημιουργία ενός φύλλου (sheet) για κάθε αίθουσα
                String sheetName = classroom.getClassroomName();
                XSSFSheet sheet = workbook.createSheet(sheetName);

                // Προσθήκη των χρονικών διαστημάτων (timeslots) ως headers στις στήλες
                Row timeslotRow = sheet.createRow(0);
                for (int i = 0; i < timeslots.size(); i++) {
                    Cell cell = timeslotRow.createCell(i + 1);
                    cell.setCellValue(timeslots.get(i));
                    sheet.autoSizeColumn(i+1);
                }
                // Προσθήκη των ημερομηνιών στις γραμμές της 1ης στήλης
                int rowIndex = 1;
                for (String tmp : dates) {
                        Row row = sheet.createRow(rowIndex++);
                        Cell dateCell = row.createCell(0);
                        LocalDate date = LocalDate.parse(tmp, dateFormatter);
                        String greekDayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR"));
                        dateCell.setCellValue(greekDayName + " " + tmp);
                }
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        cell.setCellStyle(style);
                    }
                }
                sheet.autoSizeColumn(0);
            }
            // Αποθήκευση αρχείου προς συμπλήρωση για τις αίθουσες
            try (FileOutputStream outputStream1 = new FileOutputStream("C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\class.xlsx")) {
                workbook.write(outputStream1);
            }
            logger.appendLogger("Η δημιουργία του template για τις αίθουσες ολοκληρώθηκε επιτυχώς.");
        }catch (Exception e){
            logger.appendLogger("Η δημιουργία του template για τις αίθουσες απέτυχε.");
        }
        JOptionPane.showMessageDialog(myJFrame,logger.getLoggerTxt(),
               "Μήνυμα Εφαρμογής", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    /**
     * Η συνάρτηση διαβάζει το κύριο excel που εμπεριέχει πληροφορίες για τους καθηγητές, τα μαθήματα, τις σχέσεις τους κ.α.Έπειτα, καλεί 2 μεθόδους που συμπληρώνουν στα αντικείμενα καθηγητών και αιθουσών την διαθεσιμότητά τους με βάση τα συμπληρωμένα
     * template.
     * 
     * @return true ή false αντίστοιχα με το εάν ολοκληρώθηκε η διαδικασία ανάγνωσης των αρχείων prof.xlsx και classrooms.xlsx ολοκληρώθηκε επιτυχώς ή όχι
     * @throws org.gmele.general.sheets.exception.SheetExc
     */
    public boolean readTemplates() throws SheetExc{
        try {
            boolean outcome = readObjects();
            addProfessorsAvailability(profs, timeslots.size(), paths.getImportFilePath1());
            addClassroomsAvailability(classrooms, timeslots.size(), paths.getImportFilePath2());
            System.out.println("Professor and classrooms filled template excels have been read.");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά την ανάγνωση των δεδομένων. ",
            " Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    /**
     * Συμπληρώνει για όλη την λίστα των καθηγητών, την διαθεσιμότητά τους από το συμπληρωμένο φύλλο που παράχθηκε από το πρόγραμμα σε προηγούμενο βήμα. 
     * @param professors Η λίστα με τους καθηγητές που θα προστεθεί η διαθεσιμότητά τους.
     * @param lastColumn Το μέγεθος της λίστας timeslots ή το πλήθος των διαφορετικών
     * χρονικών πλαισίων.
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     */    
    public void addProfessorsAvailability(List<Professor> professors,int lastColumn, String filename) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            
            System.out.println("file is:" + filename);
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEEE dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (Professor professor : professors){
                String sheetName = professor.getProfSurname() + " " + professor.getProfFirstname();
                s.SelectSheet(sheetName);
                int lastRow = s.GetLastRow();
                List<Availability> availabilityList = new ArrayList<>();
                for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    String cellDate = s.GetCellString(rowIndex, 0);
                    Date date = inputFormat.parse(cellDate);
                    cellDate = outputFormat.format(date);
                    for (int colIndex = 1; colIndex <= lastColumn; colIndex++){
                        String timeslot = s.GetCellString(0,colIndex);
                        String curCell = s.GetCellString(rowIndex, colIndex);
                        if (curCell.equals("+")){
                            Availability tmp = new Availability(cellDate, timeslot, 1);
                            availabilityList.add(tmp);
                        }else if (curCell.equals("-")){
                            Availability tmp = new Availability(cellDate, timeslot, 0);
                            availabilityList.add(tmp);
                        }else{
                            throw new Exception();
                        }
                    }
                }
                if (!availabilityList.isEmpty()){
                    professor.setAvailability(availabilityList);
                }
            }
            file.close();
            return ;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return ;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα αρχεία διαθεσιμότητας καθηγητών.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα  αρχεία διαθεσιμότητας καθηγητών.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return ;
    }
    
    /**
     * Συμπληρώνει για όλη την λίστα των αιθουσών, την διαθεσιμότητά τους από το συμπληρωμένο φύλλο που παράχθηκε από το πρόγραμμα σε προηγούμενο βήμα. 
     * @param classrooms Η λίστα με τις αίθουσες που θα προστεθεί η διαθεσιμότητά τους.
     * @param lastColumn Το μέγεθος της λίστας timeslots ή το πλήθος των διαφορετικών
     * χρονικών πλαισίων.
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     */
    public void addClassroomsAvailability(List<Classroom> classrooms,int lastColumn, String filename){
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEEE dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (Classroom classroom : classrooms){
                String sheetName = classroom.getClassroomName();
                s.SelectSheet(sheetName);
                int lastRow = s.GetLastRow();
                List<Availability> availabilityList = new ArrayList<>();
                for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    String cellDate = s.GetCellString(rowIndex, 0);
                    Date date = inputFormat.parse(cellDate);
                    cellDate = outputFormat.format(date);
                    for (int colIndex = 1; colIndex <= lastColumn; colIndex++){
                        String timeslot = s.GetCellString(0,colIndex);
                        String curCell = s.GetCellString(rowIndex, colIndex).trim();
                        if (curCell.equals("+")){
                            Availability tmp = new Availability(cellDate, timeslot, 1);
                            availabilityList.add(tmp);
                        }else if (curCell.equals("-")){
                            Availability tmp = new Availability(cellDate, timeslot, 0);
                            availabilityList.add(tmp);
                        }else{
                            System.out.println(sheetName + " " + cellDate);
                            throw new Exception();
                        }
                    }
                }
                if (!availabilityList.isEmpty()){
                    classroom.setAvailability(availabilityList);
                }
            }
            file.close();
            return ;
            
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return ;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα αρχεία διαθεσιμότητας αιθουσών.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα αρχεία διαθεσιμότητας αιθουσών.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return ;
    }
    
    
    /**
     * Συνάρτηση όπου ελέγχει για διπλοεγγραφή καθηγητή.
     * @param prof Αντικείμενο καθηγητή.
     * @param cellA Πληροφορία από 1η στήλη.
     * @param cellB Πληροφορία από 2η στήλη.
     * @param cellC Πληροφορία από 3η στήλη.
     * @return εάν υπάρχει ήδη ή όχι.
     */
    public boolean checkDuplicateProfessor(Professor prof, String cellA, String cellB, String cellC){
        if(prof.getProfSurname().equals(cellA) && prof.getProfFirstname().equals(cellB) && prof.getProfField().equals(cellC) ){
            return true;
        }
        return false;
    }
    
    /**
     * Συνάρτηση όπου ελέγχει για διπλοεγγραφή μαθήματος.
     * @param course Αντικείμενο καθηγητή.
     * @param cellA Πληροφορία από 1η στήλη.
     * @return εάν υπάρχει ήδη ή όχι.
     */
    public boolean checkDuplicateCourse(Course course, String cellA){
        if(course.getCourseName().equals(cellA)){
            return true;
        }
        return false;
    }
    
    /**
     * Συνάρτηση όπου ελέγχει για έγκυρη πληροφορία.
     * @param s Το string προς έλεγχο.
     * @return εάν είναι έγκυρο ή όχι.
     */
    public boolean checkIfValid(String s){
        if(s != null && !s.equals("") && !s.equals(" ")){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Η μέθοδος αυτή αφαιρεί τα μαθήματα που δεν έχουν κάποιον εξεταστή (δεν υπάρχει δηλαδή σχέση μαθήματος - εξεταστών στο φύλλο 'COURSES_PROFESSORS'. Τα θεωρούμε ως μαθήματα που δεν εξετάζονται).
     */
    public void removeCoursesWithNoExaminers(){
        List<Course> copy = new ArrayList<>(courses);
        for (Course course : copy){
            int i = 0;
            for (Professor prof : course.getExaminers()){
                i = i + 1;
            }
            if (i == 0){
                courses.remove(course);
            }
        }
    }

    /**
     * Μέθοδος που δημιουργεί ένα αντικείμενο CellStyle με κάποια ορισμένα χαρακτηριστικά.
     * @param workbook Αντικείμενο τύπου XSSFWorkbook που θα λάβει την τροποποίηση.
     * @return style Το αντικείμενο τύπου CellStyle.
     */
    
    private CellStyle getStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    public boolean readObjects(){
        try {
            String dir = System.getProperty("user.dir");
            FileInputStream fi = new FileInputStream(new File( dir +"\\data\\myObjects.dat"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            profs = (List<Professor>) oi.readObject();
            courses = (List<Course>) oi.readObject();
            classrooms = (List<Classroom>) oi.readObject();
            timeslots = (List<String>) oi.readObject();
            dates = (List<String>) oi.readObject();
            oi.close();
            fi.close();
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά την ανάγνωση των αποθηκευμένων αρχείων."
            + "Τα δεδομένα των συμπληρωμένων αρχείων δεν είναι σωστά." + 
            " Παρακαλώ ελέγξτε τα δεδομένα σας και δοκιμάστε ξανά.", "Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα εφαρμογής", "Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    public void saveObjects(){
        FileOutputStream f = null;
        try {
            String dir = System.getProperty("user.dir");
            f = new FileOutputStream(new File(dir +"\\data\\myObjects.dat"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            if (profs == null || courses == null || classrooms == null || timeslots == null || dates == null ){
                JOptionPane.showMessageDialog(myJFrame, "Τα δεδομένα των συμπληρωμένων αρχείων δεν είναι σωστά." + 
                    " Παρακαλώ ελέγξτε τα δεδομένα σας και δοκιμάστε ξανά.", "Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
                return;
            }
            o.writeObject(profs);
            o.writeObject(courses);
            o.writeObject(classrooms);
            o.writeObject(timeslots);
            o.writeObject(dates);
            o.close();
            f.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα εντοπισμού " + 
                    "των αποθηκευμένων αρχείων του προγράμματος.", "Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα αποθηκευμένα " + 
                    "δεδομένα. Παρακαλώ ελέγξτε τα δεδομένα σας και δοκιμάστε ξανά.", "Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
        }
    }
}