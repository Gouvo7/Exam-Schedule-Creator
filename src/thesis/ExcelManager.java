package thesis;

import utils.CellStyles;
import utils.CustomErrorException;
import utils.DateComparator;
import models.Professor;
import models.Course;
import models.Classroom;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gmele.general.sheets.XlsxSheet;
import org.gmele.general.sheets.exception.SheetExc;
import utils.Utilities;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση ExcelManager είναι υπεύθυνη για τη διαχείριση και επεξεργασία αρχείων .xlsx
 * που σχετίζονται με το τελικό πρόγραμμα εξεταστικής του τμήματος. Αναλαμβάνει τη 
 * δημιουργία template αρχείων προς συμπλήρωση για τους καθηγητές και τις αίθουσες
 * (templates διαθεσιμότητας), την ανάγνωση και την συγκέντρωση δεδομένων για μαθήματα,
 * καθηγητές και αίθουσες, όσο και την ανάγνωση των συμπληρωμένων αρχείων διαθεσιμοτήτων
 * τους.
 */
public class ExcelManager {
    
    private String[] weekdays;
    private static String fileName;
    private static String sheet1, sheet2,sheet3, sheet4, sheet5, sheet6;
    private boolean excel1, excel2, excel3, excel4, excel5, excel6;
    private static JFrame myJFrame;
    private static Logs logger;
    private Definitions def;
    private Utilities utils;
    private List<Professor> profs;
    private List<Course> courses;
    private List<Classroom> classrooms;
    private List<String> timeslots;
    private List<String> dates;
    private String regex;
    private Pattern patternForDate;
        
    /**
     * Κατασκευαστής του ExcelManager αντικειμένου στον οποίο οι περισσότερες
     * ιδιότητες του ExcelManager αρχικοποιούνται με βάση τις σταθερές από το
     * αντικείμενο της κλάσης Definitions.
     * 
     * @param jFrame Το JFrame που χρησιμοποιείται για την εμφάνιση διαλογικών 
     * παραθύρων και μηνυμάτων (JFrame).
     * @param def Αντικείμενο της κλάσης Definitions που περιέχει ρυθμίσεις και 
     * πληροφορίες διαδρομών αρχείων (Definitions).
     */
    public ExcelManager(JFrame jFrame, Definitions def){
        myJFrame = jFrame;
        this.def = def;
        initData();
        utils = new Utilities();
    }
    
    public ExcelManager(ExcelManager ex){
        profs = new ArrayList<Professor>(ex.getProfs());
        courses = new ArrayList<Course>(ex.getCourses());
        classrooms = new ArrayList<Classroom>(ex.getClassrooms());
        timeslots = new ArrayList<String>(ex.getTimeslots());
        dates = new ArrayList<String>(ex.getDates());
        def = ex.getDefinitions();
        utils = new Utilities();
        initData();
    }
    
    public void initData(){
        logger = new Logs();
        fileName = def.getFolderPath() + "\\" + def.getGenericFile();
        sheet1 = def.getSheet1();
        sheet2 = def.getSheet2();
        sheet3 = def.getSheet3();
        sheet4 = def.getSheet4();
        sheet5 = def.getSheet5();
        sheet6 = def.getSheet6();
        excel1 = true;
        excel2 = true;
        excel3 = true;
        excel4 = true;
        excel5 = true;
        excel6 = true;
        regex = "\\b\\d{2}/\\d{2}/\\d{4}\\b";
        patternForDate = Pattern.compile(regex);
        weekdays = def.getWeekdays();
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
    
    public Definitions getDefinitions(){
        return def;
    }
    
    public void setDefinitions(Definitions def) {
        this.def = def;
    }
    
    /**
     * Η μέθοδος χρησιμοποιείται για την κλήση της private μεθόδου createTemplate 
     * η οποία δημιουργεί τα template αρχεία διαθεσιμότητας καθηγητών και αιθουσών.
     */
    public void createExcels() throws IOException, FileNotFoundException, SheetExc{
        createTemplate(profs, timeslots, dates, classrooms);
    }

    /**
     * Μέθοδος για την ανάγνωση του βασικού αρχείου Excel που περιέχει τις γενικές
     * πληροφορίες για την εξεταστική. Συλλέγει δεδομένα για καθηγητές, μαθήματα,
     * αίθουσες, ημερομηνίες και χρονικά διαστήματα εξέτασης.
     * 
     * @return true εάν η διαδικασία ανάγνωσης του βασικού αρχείου excel (όλων των φύλλων)
     * ήταν επιτυχής, αλλιώς false (boolean).
     */
    public boolean readGenericExcel(){
        try{
            profs = new ArrayList<>();
            profs = readProfs(fileName);
            if (profs == null) {
                return false;
            }
            timeslots = new ArrayList<>();
            timeslots = readTimeslots(fileName);
            if (timeslots == null) {
                return false;
            }
            dates = new ArrayList<>();
            dates = readDates(fileName);
            if (dates == null) {
                return false;
            }
            classrooms = new ArrayList<>();
            classrooms = readClassrooms(fileName);
            if (classrooms == null) {
                return false;
            }
            courses = new ArrayList<>();
            courses = readCourses(fileName);
            if (courses == null) {
                return false;
            }
            Collections.sort(dates, new DateComparator());
            if (excel1 && excel2 && excel3 && excel4 && excel5) {
                if(addProfsToCourses(profs, courses)){
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
    
    /**
     * Η μέθοδος προσθέτει στα μαθήματα (αντικείμενα τύπου Course) τους καθηγητές που
     * εξετάζουν το κάθε μάθημα.
     * 
     * @param profs Η λίστα των καθηγητών (List <Professor>).
     * @param courses Η λίστα των μαθημάτων (List <Course>).
     * @return true εάν η προσθήκη ήταν επιτυχής, αλλιώς false (boolean).
     */
    public boolean addProfsToCourses(List<Professor> profs, List<Course> courses){
        int rowIndex = 0;
        boolean error = false;
        String course = null;
        try (FileInputStream file = new FileInputStream(new File(fileName))) {
            XlsxSheet s = new XlsxSheet(fileName);
            s.SelectSheet(sheet6);
            rowIndex = 1;
            int lastRow = s.GetLastRow();
            List<String> uniqueCourses = new ArrayList<>();
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    course = s.GetCellString(rowIndex, 0).trim();
                    if (uniqueCourses.contains(course)){
                        file.close();
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + " στην γραμμή " + (rowIndex + 1) + "'."
                                + "Εντοπίστηκε διπλοεγγραφή μαθήματος στην γραμμή " + (rowIndex + 1) + ".";
                        excel6 = false;
                        throw new CustomErrorException(myJFrame, msg, true);
                    }else{
                        uniqueCourses.add(course);
                    }
                }
                rowIndex++;
            }
            rowIndex = 1;
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    course = s.GetCellString(rowIndex, 0).trim();
                    String profA = s.GetCellString(rowIndex, 1).trim();
                    String profB = s.GetCellString(rowIndex, 2).trim();
                    String profC = s.GetCellString(rowIndex, 3).trim();
                    String profD = s.GetCellString(rowIndex, 4).trim();
                    double studentNum = 0;
                    int intValue = -1;
                    try{
                        studentNum = s.GetCellNumeric(rowIndex, 5);
                        intValue = (int) studentNum;
                    }catch(SheetExc e){
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Παρακαλώ εισάγεται ακέραιους αριθμούς μεγαλύτερους ή ίσους με το 0 στην στήλη με τον εκτιμώμενο αριθμό φοιτητών.";
                        excel6 = false;
                        throw new CustomErrorException(myJFrame, msg, true);
                    }
                    
                    if (intValue < 0){
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Παρακαλώ εισάγεται ακέραιους αριθμούς μεγαλύτερους ή ίσους με το 0 στην στήλη με τον εκτιμώμενο αριθμό φοιτητών.";
                        excel6 = false;
                        throw new CustomErrorException(myJFrame, msg, true);
                    }
                    if(!checkIfProfIsValid(profA) || !checkIfProfIsValid(profB) || !checkIfProfIsValid(profC) || !checkIfProfIsValid(profD)){
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Παρακαλώ βεβαιωθείτε ότι οι τιμές των πεδίων είναι είτε συμπληρωμένες είτε με έγκυρο όνομα καθηγητή είτε κενές ('').";
                        excel6 = false;
                        throw new CustomErrorException(myJFrame, msg, true);
                    }
                    boolean exists = false;
                    Course tmpCourse = utils.getCourse(courses, course);
                    if(tmpCourse != null){
                        tmpCourse.setApproxStudents(intValue);
                        exists = true;
                        boolean hasProfA = false;
                        boolean hasProfB = false;
                        boolean hasProfC = false;
                        boolean hasProfD = false;
                        
                        List<String> profSurnames = new ArrayList<>(utils.getCourseExaminersSurnames(tmpCourse));
                        if(checkIfProfessorExists(profA)){
                            if(!profSurnames.contains(profA)){
                               tmpCourse.addExaminer( getProfessor(profA));
                               hasProfA = true;
                            }else{
                                excel6 = false;
                                error = true;
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Εντοπίστηκαν διπλοεγγραφές καθηγητή για το ίδιο μάθημα στην στήλη 'B'.";
                                throw new CustomErrorException(myJFrame, msg, true);
                            }
                        }
                        profSurnames = utils.getCourseExaminersSurnames(tmpCourse);
                        if(checkIfProfessorExists(profB)){
                            if (!profSurnames.contains(profB)){
                                tmpCourse.addExaminer(getProfessor(profB));
                                hasProfB = true;
                            }else{
                                excel6 = false;
                                error = true;
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Εντοπίστηκαν διπλοεγγραφές καθηγητή για το ίδιο μάθημα στην στήλη 'C'.";
                                throw new CustomErrorException(myJFrame, msg, true);
                            }
                        }
                        if(checkIfProfessorExists(profC)){
                        profSurnames = utils.getCourseExaminersSurnames(tmpCourse);
                            if (!profSurnames.contains(profC)){
                                tmpCourse.addExaminer(getProfessor(profC));
                                hasProfC = true;
                            }else{
                                excel6 = false;
                                error = true;
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Εντοπίστηκαν διπλοεγγραφές καθηγητή για το ίδιο μάθημα στην στήλη 'D'.";
                                throw new CustomErrorException(myJFrame, msg, true);
                            }
                        }
                        if(checkIfProfessorExists(profD)){
                            profSurnames = utils.getCourseExaminersSurnames(tmpCourse);
                            if (!profSurnames.contains(profD)){
                                tmpCourse.addExaminer(getProfessor(profD));
                                hasProfD = true;
                            }else{
                                excel6 = false;
                                error = true;
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Εντοπίστηκαν διπλοεγγραφές καθηγητή για το ίδιο μάθημα στην στήλη 'E'.";
                                throw new CustomErrorException(myJFrame, msg, true);
                            }
                        }
                        if(!checkProfessorsOrder(hasProfA, hasProfB, hasProfC,hasProfD)){
                            excel6 = false;
                            error = true;
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Λάθος κατά την καταχώρηση των καθηγητών. Βεβαιωθείτε ότι  "
                                + "οι καθηγητές έχουν καταχωρηθεί με την σωστή σειρά.";
                            throw new CustomErrorException(myJFrame, msg, true);
                        }
                    }
                    if (!exists){
                        excel6 = false;
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Δεν βρέθηκε το μάθημα στην λίστα με τα μαθήματα.";
                        throw new CustomErrorException(myJFrame, msg, true);
                    }
                }
                rowIndex++;
            }
            file.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + fileName + "' δεν βρέθηκε.",
               "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
            excel6 = false;
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + fileName + "'.","Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
            excel6 = false;
        } catch (Exception e){
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
        if(excel6){
            return true;
        }
        return false;
    }
    
    /**
     * Εντοπισμός όλων των μαθημάτων που έχουν καταχωρηθεί στο βασικό αρχείο.
     * 
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία (String).
     * @return Την λίστα courses με όλα τα μαθήματα (List<Course>).
     */
    public List<Course> readCourses(String filename){
        int rowIndex = 0;
        boolean error = false;
        try (FileInputStream file = new FileInputStream(new File(filename))) {
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet5);
            int lastRow = s.GetLastRow();
            List<Course> courses = new ArrayList<>();
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0).trim();
                    String cellB = s.GetCellString(rowIndex, 1).trim();
                    String cellC = s.GetCellString(rowIndex, 2).trim();
                    String cellD = s.GetCellString(rowIndex, 3).trim();
                    String cellE = s.GetCellString(rowIndex, 4).trim();
                    if (checkIfValid(cellA) && checkIfValid(cellC) &&
                            checkIfValid(cellD) && checkIfValid(cellE)){
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
                        if (dupliicate){
                            file.close();
                            error = true;
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                            + sheet5 + "' στην γραμμή " + (rowIndex + 1) + ". Βρέθηκε διπλοεγγραφή του μαθήματος '" + cellA + "'.";
                            throw new CustomErrorException(myJFrame, msg, true);
                        }
                        if(cellB.equals("-")){
                            cellB = "";
                        }
                        if(!cellD.equals("ΧΕΙΜΕΡΙΝΟ" ) && !cellD.equals("ΕΑΡΙΝΟ")){
                            file.close();
                            error = true;
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                            + sheet5 + "' στην γραμμή " + (rowIndex + 1) + " στην στήλη 'D'. Η στήλη μπορεί να είναι"
                            + " συμπληρωμένη μόνο με τα λεκτικά 'ΕΑΡΙΝΟ' ή 'ΧΕΙΜΕΡΙΝΟ'.";
                            throw new CustomErrorException(myJFrame, msg, true);
                        }
                        if (cellE.equals("+")){
                            Course tmp = new Course(cellA,cellB,cellC,cellD,true);
                            courses.add(tmp);
                        }else if (cellE.equals("-")){
                            Course tmp = new Course(cellA,cellB,cellC,cellD,false);
                            courses.add(tmp);
                        }else{
                            file.close();
                            error = true;
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '"
                            + fileName + "' στο φύλλο '" + sheet5 + "' στην γραμμή "
                            + (rowIndex + 1) + " στην στήλη 'E'. Η στήλη μπορεί να είναι"
                            + " συμπληρωμένη μόνο με τα σύμβολα '+' και '-'.";
                            throw new CustomErrorException(myJFrame, msg, true);
                         }
                    }
                    else{ 
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet5 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι ο τα δεδομένα στα κελιά "
                                + "είναι τύπου string | string/'' | string | 'ΧΕΙΜΕΡΙΝΟ'/'ΕΑΡΙΝΟ' | '+'/'-'";
                        throw new CustomErrorException(myJFrame, msg, true);
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
                    + " του αρχείου '" + filename + "'.","Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    /**
     * Εντοπισμός όλων των αιθουσών που έχουν καταχωρηθεί στο βασικό αρχείο.
     * 
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @return Μία λίστα με αντικείμενα τύπου Classroom (List<Classroom>).
     */
    public List<Classroom> readClassrooms(String filename){
        int rowIndex = 0;
        boolean error = false;
        try (FileInputStream file = new FileInputStream(new File(filename))) {
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet4);
            int lastRow = s.GetLastRow();
            List<Classroom> classrooms = new ArrayList<>();
            
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0);
                    String cellB = s.GetCellString(rowIndex, 1);
                    int cellC = -1;
                    try{
                        cellC = (int) s.GetCellNumeric(rowIndex, 2);
                    }catch (SheetExc e){
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                           + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι στην στήλη 'C' στο κελί υπάρχει ακέραιος αριθμός μεγαλύτερος ή ίσος του 0.";
                           throw new CustomErrorException(myJFrame, msg, true);
                    }
                    String cellD = s.GetCellString(rowIndex, 3);
                    cellA = cellA.trim();
                    cellB = cellB.trim();
                    cellD = cellD.trim();
                    if (cellD.equals("+") || cellD == "-"){
                        if(cellC >= 0){
                            if (checkIfValid(cellA) && checkIfValid(cellB) && checkIfValid(cellD)){
                                Classroom tmp = new Classroom(cellA, cellB, cellC, cellD);
                                classrooms.add(tmp);
                            }else{
                                file.close();
                                error = true;
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                                + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι "
                                        + "τα δεδομένα στα κελιά είναι τύπου 'A' και 'Β' είναι "
                                        + "έγκυρα λεκτικά διαφορετικά του κενού.";
                                throw new CustomErrorException(myJFrame, msg, true);
                            }
                        }else{
                           file.close();
                           error = true;
                           String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                           + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι στην στήλη 'C' στο κελί υπάρχει ακέραιος αριθμός μεγαλύτερος ή ίσος του 0.";
                           throw new CustomErrorException(myJFrame, msg, true);
                        }
                    }else{
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι στην στήλη 'D' οι χαρακτήρες στο κελί είναι μεταξύ των συμβόλων '+' "
                                + "για αίθουσες εργαστηρίου και '-' για αίθουσες διαλέξεων.";
                        throw new CustomErrorException(myJFrame, msg, true);
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
                    + " του αρχείου '" + filename + "'.","Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    /**
     * Εντοπισμός όλων των ημερομηνιών της εξεταστικής που έχουν καταχωρηθεί στο
     * βασικό αρχείο.
     * 
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @return Μία λίστα με τις ημερομηνίες τις οποίες θα διεξαχθεί η εξεταστική
     * (List<String>).
     */
    public List<String> readDates(String filename){
        int rowIndex = 0;
        boolean error = false;
        try (FileInputStream file = new FileInputStream(new File(filename))) {
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet3);
            List<String> dates = new ArrayList<>();
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            int lastRow = s.GetLastRow();
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    Date date;
                    try {
                        date = inputDateFormat.parse(s.GetCellDate(rowIndex, 0).toString());
                    } catch (ParseException ex) {
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet3 + "' στην γραμμή " + (rowIndex + 1) + ". Ο τύπος δεδομένων της ημερομηνίας δεν είναι σωστός."
                                + " Καταχωρήστε ημερομηνίες με το μορφότυπο 'HH/MM/ΕΕΕΕ' (π.χ. 01/12/2021).";
                        throw new CustomErrorException(myJFrame, msg, true);
                    } catch (SheetExc e){
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet3 + "' στην γραμμή " + (rowIndex + 1) + ". Ο τύπος δεδομένων της ημερομηνίας δεν είναι σωστός."
                                + " Καταχωρήστε ημερομηνίες με το μορφότυπο 'HH/MM/ΕΕΕΕ' (π.χ. 01/12/2021).";
                        throw new CustomErrorException(myJFrame, msg, true);
                    }
                    String cellA = outputDateFormat.format(date).trim();
                    if (checkIfValid(cellA) && !dates.contains(cellA)){
                        dates.add(cellA);
                    }else{
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet3 + "' στην γραμμή " + (rowIndex + 1) + ". Ο τύπος δεδομένων της ημερομηνίας δεν είναι σωστός."
                                + " Καταχωρήστε ημερομηνίες με το μορφότυπο 'HH/MM/ΕΕΕΕ' (π.χ. 01/12/2021).";
                        throw new CustomErrorException(myJFrame, msg, true);
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
                    + " του αρχείου '" + filename + "'.","Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet3 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    /**
     * Εντοπισμός όλων των διαστημάτων εξέτασης που έχουν καταχωρηθεί στο βασικό αρχείο.
     * 
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @return Μία λίστα με Strings που αναπαριστούν τα χρονικά διαστήματα των
     * εξετάσεων (List<String>).
     */
    public List<String> readTimeslots(String filename){
        int rowIndex = 0;
        boolean error = false;
        try (FileInputStream file = new FileInputStream(new File(filename))) {
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet2);
            List<String> timeslots = new ArrayList<>();
            rowIndex = 0;
            int lastRow = s.GetLastRow();
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0);
                    if(checkIfValid(cellA) && !timeslots.contains(cellA)){
                        timeslots.add(cellA);
                    }else{
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet2 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι τα δεδομένα είναι τύπος string.";
                        throw new CustomErrorException(myJFrame, msg, true);
                    }
                }
                rowIndex++;
            }
            file.close();
            return timeslots;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" +filename + "' δεν βρέθηκε.",
               "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + filename + "'.","Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet2 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    /**
     * Εντοπισμός όλων των καθηγητών που έχουν καταχωρηθεί στο βασικό αρχείο.
     * 
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @return Μία λίστα με αντικείμενα τύπου Professor (List<Professor>).
     */
    public List<Professor> readProfs(String filename){
        int rowIndex = 0;
        boolean error = false;
        try (FileInputStream file = new FileInputStream(new File(filename))) {
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet1);
            int lastRow = s.GetLastRow();
            List<Professor> profs = new ArrayList<>();

            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0).trim();
                    String cellB = s.GetCellString(rowIndex, 1).trim();
                    String cellC = s.GetCellString(rowIndex, 2).trim();
                    if (checkIfValid(cellA) && checkIfValid(cellB)){
                        boolean duplicate = false;
                        if (profs.isEmpty()){
                            duplicate = false;
                        }else{
                            for (Professor tmp : profs){
                                if (checkDuplicateProfessor(tmp,cellA,cellB)){
                                    file.close();
                                    duplicate = true;
                                    error = true;
                                    String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                                    + sheet1 + "' στην γραμμή " + (rowIndex + 1) + ". Εντοπίστηκε διπλοεγγραφή καθηγητή.";
                                    throw new CustomErrorException(myJFrame, msg, true);
                                }
                            }
                        }
                        if (!duplicate){
                            Professor prof = new Professor(cellA, cellB, cellC);
                            profs.add(prof);
                        }else{
                            file.close();
                            duplicate = true;
                            error = true;
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                            + sheet1 + "' στην γραμμή " + (rowIndex + 1) + ". Εντοπίστηκε διπλοεγγραφή καθηγητή.";
                            throw new CustomErrorException(myJFrame, msg, true);
                        }
                    }
                    else{
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet1 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι ο τα δεδομένα στα κελιά "
                                + "είναι τύπου string | string | string/''";
                        throw new CustomErrorException(myJFrame, msg, false);
                    }
                }
                rowIndex++;
            }
            file.close();
            return profs;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
                   "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);

        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                        + " του αρχείου '" + filename + "'.","Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet1 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
            }
        }       
        return null;
    }
    
    /**
     * Δημιουργεί δύο template αρχεία Excel για την καταχώρηση διαθεσιμότητας καθηγητών και αιθουσών.
     * Τα αρχεία περιλαμβάνουν φύλλα για κάθε καθηγητή και αίθουσα αντίστοιχα, με επικεφαλίδες
     * σε κάθε στήλη τα χρονικά διαστήματα χρονικά διαστήματα των εξετάσεων και
     * στην 1η στήλη σε κάθε γραμμή την ημερομηνία εξέτασης.
     *
     * @param uniqueProfs Λίστα μοναδικών αντικειμένων καθηγητών για τους οποίους 
     * θα φτιαχτεί από ένα φύλλο στον καθένα (List<Professor>).
     * @param timeslots Λίστα strings με τα διαστήματα εξέτασης που θα χρησιμοποιηθούν
     * ως headers στις στήλες του κάθε φύλλου(List<String>).
     * @param dates Λίστα strings με τις ημερομηνίες της εξεταστικής που θα 
     * χρησιμοποιθούν ως headers για τις γραμμές του κάθε φύλλου (List<String>).
     * @param classrooms Λίστα αντικειμένων αιθουσών για τις οποίες θα φτιαχτεί
     * από ένα φύλλο για την κάθε μία (List<Course>).
     */
    public void createTemplate(List<Professor> uniqueProfs, List<String> timeslots, List<String> dates,List<Classroom> classrooms) throws FileNotFoundException, IOException, SheetExc{
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        CellStyles cs = new CellStyles();
        //System.out.println(def.getFolderPath() + "\\" + def.getGenericFile());
        /*
        try (FileInputStream file = new FileInputStream(new File(def.getFolderPath() + "\\" + def.getGenericFile()))) {
            XlsxSheet f = new XlsxSheet("toyloymplioy");
            f.SelectSheet("toyloymplioy");
            f.SetCell(0, 0, "hi");
        }*/
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            for (Professor professor : uniqueProfs) {
                // Δημιουργία φύλλου (sheet) για κάθε καθηγητή
                String sheetName = professor.getProfSurname()+ " " + professor.getProfFirstname();
                XSSFSheet sheet = workbook.createSheet(sheetName);
                Row timeslotRow = sheet.createRow(0);
                Cell firstCell = timeslotRow.createCell(0);
                firstCell.setCellValue("Συμπληρώστε τα κελία με '+' και '-' \nανάλογα την διαθεσιμότητα του καθηγητή");
                // Προσθήκη των χρονικών διαστημάτων (timeslots) ως headers στις στήλες
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
                String greekDayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR")).toUpperCase();
                String greekDayNameWithoutAccents = Normalizer.normalize(greekDayName, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                dateCell.setCellValue(greekDayNameWithoutAccents + " " + tmp);
            }
            for (Row row : sheet) {
                for (Cell cell : row) {
                    cell.setCellStyle(cs.getTemplateStyle(workbook));
                    }
                }
                sheet.autoSizeColumn(0);
            }

            // Αποθήκευση αρχείου προς συμπλήρωση για τους καθηγητές
            try (FileOutputStream outputStream = new FileOutputStream(def.getFolderPath() + "\\" + def.getProducedProfessorsAvailabilityFile())) {
                workbook.write(outputStream);
            }
                logger.appendLogger("Η δημιουργία του template για τους καθηγητές ολοκληρώθηκε επιτυχώς.");
            }catch (Exception e){
                logger.appendLogger("Η δημιουργία του template για τους καθηγητές απέτυχε.");
            }
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            for (Classroom classroom : classrooms) {
                // Δημιουργία ενός φύλλου (sheet) για κάθε αίθουσα
                String sheetName = classroom.getClassroomName();
                XSSFSheet sheet = workbook.createSheet(sheetName);
                Row timeslotRow = sheet.createRow(0);
                Cell firstCell = timeslotRow.createCell(0);
                firstCell.setCellValue("Συμπληρώστε τα κελία με '+' και '-' \nανάλογα την διαθεσιμότητα της αίθουσας");
                // Προσθήκη των χρονικών διαστημάτων (timeslots) ως headers στις στήλες
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
                        String greekDayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR")).toUpperCase();
                        String greekDayNameWithoutAccents = Normalizer.normalize(greekDayName, Normalizer.Form.NFD)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                        dateCell.setCellValue(greekDayNameWithoutAccents + " " + tmp);
                        
                }
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        cell.setCellStyle(cs.getTemplateStyle(workbook));
                    }
                }
                sheet.autoSizeColumn(0);
            }
            // Αποθήκευση αρχείου προς συμπλήρωση για τις αίθουσες
            try (FileOutputStream outputStream1 = new FileOutputStream(def.getFolderPath() + "\\" + def.getProducedClassroomsAvailabilityFile())) {
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
     * Η συνάρτηση καλεί δύο μεθόδους της κλάσης ExcelManager ώστε να 
     * πραγματοποιηθεί ανάγνωση των συμπληρωμένων αρχείων διαθεσιμοτήτων για τους
     * καθηγητές και τις αίθουσες. 
     * 
     * @return Επιστρέφει true ή false ανάλογα με το εάν η διαδικασσία
     * έχει ολοκληρωθεί επιτυχώς ή όχι (boolean).
     */
    public boolean readAvailabilityTemplates(){
        try {
            if(addProfessorsAvailability(profs, timeslots.size())){
                if(addClassroomsAvailability(classrooms, timeslots.size())){
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }
    
    /**
     * Συμπληρώνει για όλη την λίστα των καθηγητών, την διαθεσιμότητά τους από 
     * το συμπληρωμένο φύλλο που παράχθηκε από το πρόγραμμα σε προηγούμενο βήμα. 
     * 
     * @param professors Η λίστα με τους καθηγητές που θα προστεθεί η διαθεσιμότητά τους (List<Professor>).
     * @param lastColumn Το μέγεθος της λίστας timeslots ή το πλήθος των διαφορετικών (int).
     * χρονικών πλαισίων.
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία (String).
     * @return true μόνο και μόνο όταν η διαδικασία ολοκληρωθεί χωρίς κανένα 
     * πρόβλημα (boolean).
     */    
    private boolean addProfessorsAvailability(List<Professor> professors,int lastColumn) throws SheetExc{
        int rowIndex = 0;
        int colIndex = 0;
        String sheetName="";
        String curCell="";
        String filename = def.getFolderPath() + "\\" + def.getProfessorsAvailabilityFile();
        XlsxSheet sheet = null;
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("el", "GR"));
        symbols.setWeekdays(weekdays);
        try (FileInputStream file = new FileInputStream(new File(filename))) {
            sheet = new XlsxSheet(filename);
            for (Professor professor : professors){
                List<String> uniqueDates = new ArrayList<String>();
                sheetName = professor.getProfSurname() + " " + professor.getProfFirstname();
                sheet.SelectSheet(sheetName);
                int lastRow = sheet.GetLastRow();
                List<Availability> availabilityList = new ArrayList<>();
                List<String> tmpTimeslots = new ArrayList<>();
                for (colIndex = 1; colIndex <= lastColumn; colIndex++){
                    String timeslot = sheet.GetCellString(0,colIndex).trim();
                    if(!timeslots.contains(timeslot)){
                        file.close();
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                        filename + "' στο φύλλο '" + sheetName + "' στην γραμμή 1" + 
                        ". Βρέθηκε χρονικό διάστημα το οποίο δεν υπάρχει στο βασικό"
                        + " αρχείο πληροφοριών.";
                        throw new CustomErrorException(myJFrame, msg, true);
                    }else if (tmpTimeslots.contains(timeslot)){
                        file.close();
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                        filename + "' στο φύλλο '" + sheetName + "' στην γραμμή 1" + 
                        ". Βρέθηκε διπλοεγγραφή χρονικού διαστήματος.";
                        throw new CustomErrorException(myJFrame, msg, true);
                    }else{
                        tmpTimeslots.add(timeslot);
                    }
                }
                for (rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    String cellDate = sheet.GetCellString(rowIndex, 0);
                    Matcher matcher = patternForDate.matcher(cellDate);
                    if (matcher.find()) {
                        cellDate = matcher.group();
                        if(!dates.contains(cellDate)){
                            file.close();
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                                filename + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) + 
                                ". Βρέθηκε ημερομηνία που δεν υπάρχει στο αρχείο"
                                    + " με τις ημερομηνίες της εξεταστικής.";
                            throw new CustomErrorException(myJFrame, msg, true);
                        }
                        if(uniqueDates.contains(cellDate)){
                            file.close();
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                                filename + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) + 
                                ". Βρέθηκε διπλοεγγραφή ημερομηνίας, παρακαλώ πολύ"
                                    + " ελέγξτε τα δεδομένα σας.";
                            throw new CustomErrorException(myJFrame, msg, true);
                        }
                        uniqueDates.add(cellDate);
                    } else {
                        file.close();
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                                filename + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) + 
                                ". Βρέθηκε μη έγκυρη ημερομηνία, παρακαλούμε ελέγξτε"
                                + " τα δεδομένα εκ νέου και επιβεβαιώστε ότι"
                                + " η ημερομηνία είναι γραμμένη με την μορφή"
                                + " 'ΗΜΕΡΑ ηη/μμ/ΕΕΕΕ'.";
                        throw new CustomErrorException(myJFrame, msg, true);
                    }

                    for (colIndex = 1; colIndex <= lastColumn; colIndex++){
                        String timeslot = sheet.GetCellString(0,colIndex);
                        curCell = sheet.GetCellString(rowIndex, colIndex);
                        if (curCell.equals("+")){
                            Availability tmp = new Availability(cellDate, timeslot, 1);
                            availabilityList.add(tmp);
                        }else if (curCell.equals("-")){
                            Availability tmp = new Availability(cellDate, timeslot, 0);
                            availabilityList.add(tmp);
                        }else{
                            file.close();
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                                filename + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) + 
                                ". Βρέθηκε μη αποδεκτή τιμή. Παρακαλούμε επιβεβαιώστε πως" +
                                " τα κελιά είναι συμπληρωμένα είτε με τον χαρακτήρα '+' είτε" +
                                " με τον χαρακτήρα '-'.";
                            throw new CustomErrorException(myJFrame, msg, true);
                        }
                    }
                }
                if (!availabilityList.isEmpty()){
                    professor.setAvailability(availabilityList);
                }
            }
            file.close();
            return true;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα αρχεία διαθεσιμότητας καθηγητών."
                    + "Όνομα φύλλου: '"+  sheetName + "'\n" + ex, "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα αρχεία διαθεσιμότητας καθηγητών"
                    + " στο φύλλο '" + sheetName + "' στην γραμμή:στήλη " + rowIndex + ":" + colIndex + "." + ex, "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    /**
     * Συμπληρώνει για όλη την λίστα των αιθουσών, την διαθεσιμότητά τους από το
     * συμπληρωμένο φύλλο που παράχθηκε από το πρόγραμμα σε προηγούμενο βήμα. 
     * 
     * @param classrooms Η λίστα με τις αίθουσες που θα προστεθεί η διαθεσιμότητά τους (List<Classroom>).
     * @param lastColumn Το μέγεθος της λίστας timeslots ή το πλήθος των διαφορετικών
     * χρονικών πλαισίων (int).
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία (String).
     */
    private boolean addClassroomsAvailability(List<Classroom> classrooms, int lastColumn){
        int rowIndex = 0;
        int colIndex = 0;
        String filename = def.getFolderPath() + "\\" + def.getClassroomsAvailabilityFile();
        String sheetName="";
        String curCell="";
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("el", "GR"));
        symbols.setWeekdays(weekdays);
        try (FileInputStream file = new FileInputStream(new File(filename))) {
            XlsxSheet sheet = new XlsxSheet(filename);
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEEE dd/MM/yyyy", symbols);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (Classroom classroom : classrooms){
                sheetName = classroom.getClassroomName();
                sheet.SelectSheet(sheetName);
                int lastRow = sheet.GetLastRow();
                List<String> tmpTimeslots = new ArrayList<>();
                for (colIndex = 1; colIndex <= lastColumn; colIndex++){
                    String timeslot = sheet.GetCellString(0,colIndex).trim();
                    if(!timeslots.contains(timeslot)){
                        file.close();
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                        filename + "' στο φύλλο '" + sheetName + "' στην γραμμή 1" + 
                        ". Βρέθηκε χρονικό διάστημα το οποίο δεν υπάρχει στο βασικό"
                        + " αρχείο πληροφοριών.";
                        throw new CustomErrorException(myJFrame, msg, true);
                    }else if (tmpTimeslots.contains(timeslot)){
                        file.close();
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                        filename + "' στο φύλλο '" + sheetName + "' στην γραμμή 1" + 
                        ". Βρέθηκε διπλοεγγραφή χρονικού διαστήματος.";
                        throw new CustomErrorException(myJFrame, msg, true);

                    }else{
                        tmpTimeslots.add(timeslot);
                    }
                }
                List<Availability> availabilityList = new ArrayList<>();
                for (rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    String cellDate = sheet.GetCellString(rowIndex, 0);
                    Matcher matcher = patternForDate.matcher(cellDate);
                    if (matcher.find()) {
                        cellDate = matcher.group();
                        if(!dates.contains(cellDate)){
                            file.close();
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                                filename + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) + 
                                ". Βρέθηκε ημερομηνία που δεν υπάρχει στο αρχείο"
                                    + " με τις ημερομηνίες της εξεταστικής.";
                        throw new CustomErrorException(myJFrame, msg, true);
                        }
                    } else {
                        file.close();
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                                filename + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) + 
                                ". Βρέθηκε μη έγκυρη ημερομηνία, παρακαλούμε ελέγξτε"
                                + " τα δεδομένα εκ νέου και επιβεβαιώστε ότι"
                                + " η ημερομηνία είναι γραμμένη με την μορφή"
                                + " 'ΗΜΕΡΑ ηη/μμ/ΕΕΕΕ'.";
                        throw new CustomErrorException(myJFrame, msg, true);
                    }

                    for (colIndex = 1; colIndex <= lastColumn; colIndex++){
                        String timeslot = sheet.GetCellString(0,colIndex);
                        curCell = sheet.GetCellString(rowIndex, colIndex).trim();
                        if (curCell.equals("+")){
                            Availability tmp = new Availability(cellDate, timeslot, 1);
                            availabilityList.add(tmp);
                        }else if (curCell.equals("-")){
                            Availability tmp = new Availability(cellDate, timeslot, 0);
                            availabilityList.add(tmp);
                        }else{
                            file.close();
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + 
                                filename + "' στο φύλλο '" + sheetName + "' στην γραμμή " + (rowIndex + 1) + 
                                ". Βρέθηκε μη αποδεκτή τιμή. Παρακαλούμε επιβεβαιώστε πως" +
                                " τα κελιά είναι συμπληρωμένα είτε με τον χαρακτήρα '+' είτε" +
                                " με τον χαρακτήρα '-'.";
                            throw new CustomErrorException(myJFrame, msg, true);
                        }
                    }
                }
                if (!availabilityList.isEmpty()){
                    classroom.setAvailability(availabilityList);
                }
            }
            file.close();
            return true;
            
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα αρχεία διαθεσιμότητας καθηγητών."
                    + "Όνομα φύλλου: '"+  sheetName + "'\n" + ex, "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα αρχεία διαθεσιμότητας αιθουσών"
                    + " στο φύλλο '" + sheetName + "' στην γραμμή:στήλη " + rowIndex + ":" + colIndex + ".", "Μήνυμα Σφάλματος", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private Professor getProfessor(String profName){
        for(Professor prf : profs){
            if(prf.getProfSurname().equals(profName)){
                return prf;
            }
        }
        return null;
    }
    
    /**
     * Συνάρτηση όπου ελέγχει εάν βρέθηκαν με την σωστή σειρά οι καθηγητές 
     * στο φύλλο καθηγητών-μαθημάτων.
     * @param hasProfA Εάν βρέθηκε ο 1ος καθηγητής (Boolean).
     * @param hasProfB Εάν βρέθηκε ο 2ος καθηγητής (Boolean)
     * @param hasProfC Εάν βρέθηκε ο 3ος καθηγητής (Boolean)
     * @param hasProfD Εάν βρέθηκε ο 4ος καθηγητής (Boolean)
     * @return true εάν οι καθηγητές τοποθετήθηκαν με την σωστή σειρά στο φύλλο
     * και false σε αντίθετη περίπτωση (Boolean).
     */
    private boolean checkProfessorsOrder(boolean hasProfA, boolean hasProfB, boolean hasProfC, boolean hasProfD){
        if(hasProfA && !hasProfB && !hasProfC && !hasProfD){
            return true;
        }else if(hasProfA && hasProfB && !hasProfC && !hasProfD){
            return true;
        }
        else if(hasProfA && hasProfB && hasProfC && !hasProfD){
            return true;
        }else if(hasProfA && hasProfB && hasProfC && hasProfD){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Συνάρτηση όπου ελέγχει για διπλοεγγραφή καθηγητή.
     * 
     * @param prof Αντικείμενο καθηγητή (Professor).
     * @param cellA Πληροφορία από 1η στήλη (String).
     * @param cellB Πληροφορία από 2η στήλη (String).
     * @return εάν υπάρχει ήδη ή όχι (boolean).
     */
    private boolean checkDuplicateProfessor(Professor prof, String cellA, String cellB){
        if(prof.getProfSurname().equals(cellA) && prof.getProfFirstname().equals(cellB)){
            return true;
        }
        return false;
    }
    
    /**
     * Συνάρτηση όπου ελέγχει εάν το string ανήκει σε επώνυμο καθηγητή στην 
     * λίστα από τους διαβασμένους καθηγητές.
     * 
     * @param cell Το περιεχόμενο του κελιού (String).
     * @return true εφόσον το string ταιριάζει με επώνυμο καθηγητή και false 
     * εάν όχι (boolean).
     */
    private boolean checkIfProfessorExists(String cell){
        for (Professor prf : this.profs){
            if (prf.getProfSurname().equals(cell)){
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Συνάρτηση που ελέγχει εάν οι το string που έχει δοθεί ως παράμετρο είναι
     * είτε κενό ('') είτε έγκυρος καθηγητής από το φύλλο καθηγητών.
     * 
     * @param cell Το περιεχόμενο ενός κελιού (String).
     * @return true εάν η τιμή είναι καθηγητής 2 αποδεκτές κατηγορίες και false
     * εάν όχι (boolean).
     */
    private boolean checkIfProfIsValid(String cell){
        if(cell.equals("") || checkIfProfessorExists(cell)){
            return true;
        }
        return false;
    }
    
    public List<Course> getValidCourses(){
        List<Course> tmp = new ArrayList(courses);
        tmp = utils.filterOutNotExaminedCourses(courses);
        tmp = utils.filterOutCoursesWithNoExaminers(courses);
        return tmp;
    }
    
    /**
     * Συνάρτηση όπου ελέγχει για διπλοεγγραφή μαθήματος.
     * 
     * @param course Αντικείμενο καθηγητή (Course).
     * @param cellA Πληροφορία από 1η στήλη (String).
     * @return εάν υπάρχει ήδη ή όχι (boolean).
     */
    private boolean checkDuplicateCourse(Course course, String cellA){
        if(course.getCourseName().equals(cellA)){
            return true;
        }
        return false;
    }
    
    /**
     * Συνάρτηση όπου ελέγχει για έγκυρη πληροφορία.
     * 
     * @param s Το string προς έλεγχο (String).
     * @return εάν είναι έγκυρο ή όχι (boolean).
     */
    private boolean checkIfValid(String s){
        if(s != null && !s.equals("") && !s.equals(" ")){
            return true;
        }
        return false;
    }
    
    /**
     * Η μέθοδος αυτή αφαιρεί τα μαθήματα που δεν έχουν κάποιον εξεταστή
     * (δεν υπάρχει δηλαδή σχέση μαθήματος - εξεταστών στο φύλλο 'COURSES_PROFESSORS'.
     * Τα μαθήματα αυτά θεωρούμε πως δεν εξετάζονται).
     * 
     * @param coursesList Η λίστα με τα μαθήματα προς επεξεργασία (List<Course>).
     * @return copy Η λίστα μετά την επεξεργασία (List<Course>).
     */
    private List<Course> removeCoursesWithNoExaminers(List<Course> coursesList){
        List<Course> copy = new ArrayList<>(coursesList);
        for (Course course : copy){
            int i = 0;
            for (Professor prof : course.getExaminers()){
                i = i + 1;
            }
            if (i == 0){
                copy.remove(course);
            }
        }
        return copy;
    }
}