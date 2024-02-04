package thesis;

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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gmele.general.sheets.XlsxSheet;
import org.gmele.general.sheets.exception.SheetExc;

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
    
    private String[] weekdays = {"ΚΥΡΙΑΚΗ", "ΔΕΥΤΕΡΑ", "ΤΡΙΤΗ", "ΤΕΤΑΡΤΗ", "ΠΕΜΠΤΗ", "ΠΑΡΑΣΚΕΥΗ", "ΣΑΒΒΑΤΟ"};
    private static String fileName;
    private static String sheet1, sheet2,sheet3, sheet4, sheet5, sheet6;
    private boolean excel1, excel2, excel3, excel4, excel5, excel6;
    private static JFrame myJFrame;
    private static Logs logger;
    private Definitions def;
    private List<Professor> profs;
    private List<Course> courses;
    private List<Classroom> classrooms;
    private List<String> timeslots;
    private List<String> dates;
    
    /**
     * Κατασκευαστής του ExcelManager αντικειμένου στον οποίο οι περισσότερες
     * ιδιότητες του ExcelManager αρχικοποιούνται με βάση τις σταθερές από το
     * αντικείμενο της κλάσης Definitions
     * 
     * @param jFrame Το JFrame που χρησιμοποιείται για την εμφάνιση διαλογικών 
     * παραθύρων και μηνυμάτων (JFrame)
     * @param def Αντικείμενο της κλάσης Definitions που περιέχει ρυθμίσεις και 
     * πληροφορίες διαδρομών αρχείων (Definitions)
     */
    public ExcelManager(JFrame jFrame, Definitions def){
        myJFrame = jFrame;
        this.def = def;
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
    
    public void createExcels(){
        createTemplate(profs, timeslots, dates, classrooms);
    }

    /**
     * Μέθοδος για την ανάγνωση του βασικού αρχείου Excel που περιέχει τις γενικές
     * πληροφορίες για την εξεταστική. Συλλέγει δεδομένα για καθηγητές, μαθήματα,
     * αίθουσες, ημερομηνίες και χρονικά διαστήματα εξέτασης.
     * 
     * @return true εάν η όλων των φύλλων ήταν επιτυχής, αλλιώς false.
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
            courses = readCourses(fileName, profs);
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
            
            return false;
        }
    }
    
    /**
     * Η μέθοδος προσθέτει στα μαθήματα (αντικείμενα τύπου Course) τους καθηγητές που
     * εξετάζουν το κάθε μάθημα.
     * 
     * @param profs Η λίστα των καθηγητών List (<Professor>)
     * @param courses Η λίστα των μαθημάτων List (<Course>)
     * @param filename Το όνομα του αρχείου που περιέχει τις αντιστοιχίσεις καθηγητών σε μαθήματα.
     * @return true εάν η προσθήκη ήταν επιτυχής, αλλιώς false.
     * @throws SheetExc εάν υπάρχει σφάλμα στην επεξεργασία του φύλλου.
     */
    public boolean addProfsToCourses(List<Professor> profs, List<Course> courses) throws SheetExc{
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
                        throw new CustomErrorException(myJFrame, msg);
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
                    double profE1 = s.GetCellNumeric(rowIndex, 5);
                    int intValue = (int) profE1;
                    if (intValue < 0){
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Παρακαλώ πολύ εισάγεται ακέραιους αριθμούς > 0 στην στήλη με τον εκτιμώμενο αριθμό φοιτητών.";
                        excel6 = false;
                        throw new CustomErrorException(myJFrame, msg);
                    }
                    if(!checkIfCellIsValid(profA) || !checkIfCellIsValid(profB) || !checkIfCellIsValid(profC) || !checkIfCellIsValid(profD)){
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Παρακαλώ βεβαιωθείτε ότι στα πεδία οι τιμές είναι συμπληρωμένες είτε με έγκυρο όνομα καθηγητή είτε με '-'.";
                        excel6 = false;
                        throw new CustomErrorException(myJFrame, msg);
                    }
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
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!exists){
                        excel6 = false;
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + "."
                                + " Δεν βρέθηκε το μάθημα στην λίστα με τα μαθήματα.";
                        throw new CustomErrorException(myJFrame, msg);
                    }
                }
                rowIndex++;
            }
            file.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + fileName + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            excel6 = false;
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + fileName + "'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            excel6 = false;
        } catch (Exception e){
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet6 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
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
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     * @param profs Η λίστα με τους καθηγητές.
     * @return Μία λίστα με αντικείμενα τύπου Course.
     * @throws SheetExc 
     */
    public List<Course> readCourses(String filename, List<Professor> profs) throws SheetExc{
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
                    if (checkIfValid(cellA) && checkIfValid(cellB) && checkIfValid(cellC) &&
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
                        if(cellB.equals("-")){
                            cellB = "";
                        }
                        if(!cellD.equals("ΧΕΙΜΕΡΙΝΟ" ) && !cellD.equals("ΕΑΡΙΝΟ")){
                            file.close();
                            error = true;
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                            + sheet5 + "' στην γραμμή " + (rowIndex + 1) + " στην στήλη D. Η στήλη μπορεί να είναι"
                            + " συμπληρωμένη μόνο με τα λεκτικά 'ΕΑΡΙΝΟ' ή 'ΧΕΙΜΕΡΙΝΟ'.";
                            throw new CustomErrorException(myJFrame, msg);
                        }
                        if (!dupliicate){
                            if (cellE.equals("+")){
                                Course tmp = new Course(cellA,cellB,cellC,cellD,true);
                                courses.add(tmp);
                            }else if (cellE.equals("-")){
                                Course tmp = new Course(cellA,cellB,cellC,cellD,false);
                                courses.add(tmp);
                            }else{
                                file.close();
                                error = true;
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                                + sheet5 + "' στην γραμμή " + (rowIndex + 1) + " στην στήλη E. Η στήλη μπορεί να είναι συμπληρωμένη μόνο με τα σύμβολα "
                                        + "'+' και '-'.";
                                throw new CustomErrorException(myJFrame, msg);
                            }
                        }else{
                            file.close();
                            error = true;
                            String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                            + sheet5 + "' στην γραμμή " + (rowIndex + 1) + ". Βρέθηκε διπλοεγγραφή του μαθήματος '" + cellA + "'.";
                            throw new CustomErrorException(myJFrame, msg);
                        }
                    }
                    else{ 
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet5 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι ο τα δεδομένα στα κελιά είναι τύπου string | string/- | string | ΧΕΙΜΕΡΙΝΟ/ΕΑΡΙΝΟ | +/-";
                        throw new CustomErrorException(myJFrame, msg);
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
            
        } catch (Exception e){
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            }
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
                    int cellC = (int) s.GetCellNumeric(rowIndex, 2);
                    String cellD = s.GetCellString(rowIndex, 3);
                    cellA = cellA.trim();
                    cellB = cellB.trim();
                    cellD = cellD.trim();
                    if (cellD.equals("+") || cellD == "-"){
                        if(cellC > 0){
                            if (checkIfValid(cellA) && checkIfValid(cellB) && checkIfValid(cellD)){
                                Classroom tmp = new Classroom(cellA, cellB, cellC, cellD);
                                classrooms.add(tmp);
                            }else{
                                file.close();
                                error = true;
                                String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                                + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι ο τα δεδομένα στα κελιά είναι τύπου string | string | integer > 0 | +/-";
                                throw new CustomErrorException(myJFrame, msg);
                            }
                        }else{
                           file.close();
                           error = true;
                           String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                           + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι στην στήλη C στο κελί υπάρχει ακέραιος αριθμός μεγαλύτερος του 0.";
                           throw new CustomErrorException(myJFrame, msg);
                        }
                    }else{
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ". Ελέγξτε ότι στην στήλη D οι χαρακτήρες στο κελί είναι μεταξύ των συμβόλων '+' "
                                + "για αίθουσες εργαστηρίου και '-' για αίθουσες διαλέξεων.";
                        throw new CustomErrorException(myJFrame, msg);
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
        } catch (Exception e){
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet4 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            }
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
                        + sheet3 + "' στην γραμμή " + (rowIndex + 1) + ". Ο τύπος δεδομένων της ημερομηνίας δεν είναι σωστός"
                                + " (π.χ. '24/10/2023').";
                        throw new CustomErrorException(myJFrame, msg);
                    }
                    String cellA = outputDateFormat.format(date).trim();
                    if (checkIfValid(cellA) && !dates.contains(cellA)){
                        dates.add(cellA);
                    }else{
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet3 + "' στην γραμμή " + (rowIndex + 1) + ". Ο τύπος δεδομένων της ημερομηνίας δεν είναι σωστός"
                                + " (π.χ. '24/10/2023') ή βρέθηκε διπλοεγγραφή ημερομηνίας.";
                        throw new CustomErrorException(myJFrame, msg);
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
        } catch (Exception e){
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet3 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            }
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
                        throw new CustomErrorException(myJFrame, msg);
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
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet2 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            }
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
                                    throw new CustomErrorException(myJFrame, msg);
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
                            throw new CustomErrorException(myJFrame, msg);
                        }
                    }
                    else{
                        file.close();
                        error = true;
                        String msg = "Πρόβλημα με τα δεδομένα του αρχείου '" + fileName + "' στο φύλλο '"
                        + sheet1 + "' στην γραμμή " + (rowIndex + 1) + ". Εντοπίστηκν μη έγκυρα δεδομένα.";
                        throw new CustomErrorException(myJFrame, msg);
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
            if (!error){
                JOptionPane.showMessageDialog(myJFrame, "Σφάλμα στο αρχείο '" + fileName + "' στο φύλλο '" + sheet1 + "' στην γραμμή " + (rowIndex + 1) + ":\n" + e, "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            }
        }       
        return null;
    }
    
    /**
     * Δημιουργεί δύο template αρχεία Excel για την καταχώρηση διαθεσιμότητας καθηγητών και αιθουσών.
     * Τα αρχεία περιλαμβάνουν φύλλα για κάθε καθηγητή και αίθουσα αντίστοιχα, με επικεφαλίδες 
     * στις στήλες τα χρονικά διαστήματα χρονικά διαστήματα των εξετάσεων και
     * στην 1η στήλη σε κάθε γραμμή την ημερομηνία εξέτασης.
     *
     * @param uniqueProfs Λίστα μοναδικών αντικειμένων καθηγητών για τους οποίους 
     * θα φτιαχτεί από ένα φύλλο στον καθένα (List<Professor>)
     * @param timeslots Λίστα strings με τα διαστήματα εξέτασης που θα χρησιμοποιηθούν
     * ως headers στις στήλες του κάθε φύλλου(List<String>)
     * @param dates Λίστα strings με τις ημερομηνίες της εξεταστικής που θα 
     * χρησιμοποιθούν ως headers για τις γραμμές του κάθε φύλλου (List<String>)
     * @param classrooms Λίστα αντικειμένων αιθουσών για τις οποίες θα φτιαχτεί
     * από ένα φύλλο για την κάθε μία (List<Course>)
     * @param fileName
     */
    public void createTemplate(List<Professor> uniqueProfs, List<String> timeslots, List<String> dates,List<Classroom> classrooms){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        CellStyles cs = new CellStyles();
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
            try (FileOutputStream outputStream = new FileOutputStream(def.getFolderPath() + "\\" + "profs.xlsx")) {
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
            try (FileOutputStream outputStream1 = new FileOutputStream(def.getFolderPath() + "\\" + "class.xlsx")) {
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
    public boolean readAvailabilityTemplates() throws SheetExc{
        try {
            addProfessorsAvailability(profs, timeslots.size(), def.getFolderPath() + "\\" + def.getProfessorsAvailabilityFile());
            addClassroomsAvailability(classrooms, timeslots.size(), def.getFolderPath() + "\\" + def.getClassroomsAvailabilityFile());
            return true;
        }catch (Exception e){
            return false;
        }
    }
    
    /**
     * Συμπληρώνει για όλη την λίστα των καθηγητών, την διαθεσιμότητά τους από το συμπληρωμένο φύλλο που παράχθηκε από το πρόγραμμα σε προηγούμενο βήμα. 
     * @param professors Η λίστα με τους καθηγητές που θα προστεθεί η διαθεσιμότητά τους.
     * @param lastColumn Το μέγεθος της λίστας timeslots ή το πλήθος των διαφορετικών
     * χρονικών πλαισίων.
     * @param filename Το όνομα του αρχείου από το οποίο θα αντλήσουμε την πληροφορία.
     */    
    public void addProfessorsAvailability(List<Professor> professors,int lastColumn, String filename) throws SheetExc{
        int rowIndex = 0;
        int colIndex = 0;
        String sheetName="";
        String curCell="";
        XlsxSheet sheet = null;
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("el", "GR"));
        symbols.setWeekdays(weekdays);
        try (FileInputStream file = new FileInputStream(new File(filename))) {
            sheet = new XlsxSheet(filename);
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEEE dd/MM/yyyy", symbols);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("el", "GR"));
            for (Professor professor : professors){
                sheetName = professor.getProfSurname() + " " + professor.getProfFirstname();
                sheet.SelectSheet(sheetName);
                
                int lastRow = sheet.GetLastRow();
                List<Availability> availabilityList = new ArrayList<>();
                for (rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    String cellDate = sheet.GetCellString(rowIndex, 0);
                    Date date = inputFormat.parse(cellDate);
                    cellDate = outputFormat.format(date);
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
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα αρχεία διαθεσιμότητας καθηγητών"
                    + " στο φύλλο '" + sheetName + "' στην γραμμή:στήλη " + rowIndex + ":" + colIndex + ".", "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα αρχεία διαθεσιμότητας καθηγητών"
                    + " στο φύλλο '" + sheetName + "' στην γραμμή:στήλη " + rowIndex + ":" + colIndex + ".", "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
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
        int rowIndex = 0;
        int colIndex = 0;
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
                List<Availability> availabilityList = new ArrayList<>();
                for (rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    String cellDate = sheet.GetCellString(rowIndex, 0);
                    Date date = inputFormat.parse(cellDate);
                    cellDate = outputFormat.format(date);
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
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα συμπληρωμένα αρχεία διαθεσιμότητας αιθουσών"
                    + " στο φύλλο '" + sheetName + "' στην γραμμή:στήλη " + rowIndex + ":" + colIndex + ".", "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
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
    public boolean checkDuplicateProfessor(Professor prof, String cellA, String cellB){
        if(prof.getProfSurname().equals(cellA) && prof.getProfFirstname().equals(cellB)){
            return true;
        }
        return false;
    }
    
    public boolean checkIfProfessorExists(String cell){
        boolean exists = false;
        for (Professor prf : this.profs){
            if (prf.getProfSurname().equals(cell)){
                exists = true;
            }
        }
        return exists;
    }
    
    public boolean checkIfCellIsValid(String cell){
        if(cell.equals("-") || checkIfProfessorExists(cell)){
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
        }
        return false;
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

    private class CustomErrorException extends Exception{
        public CustomErrorException(JFrame myJFrame, String msg) {
            String htmlMsg = "<html><body style='width: %1spx'>%1s";
            htmlMsg = String.format(htmlMsg, 600, msg); // Set the width as needed for your message
            JOptionPane.showMessageDialog(myJFrame, htmlMsg, "Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}