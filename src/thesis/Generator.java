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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
 *
 * @author gouvo
 * 
 * Η κλάση είναι υπεύθυνη για την δημιουργία και διαχείριση αντικειμένων αλλά και την
 * ανάγνωση/δημιουργία αρχείων .xlsx
 * @param - myJFrame - Το παράθυρο που το καλεί.
 * @param - fileName - Το όνομα του αρχείου προς ανάγνωση.
 */
public class Generator {
    
    
    private static String fileName;
    private static String sheet1, sheet2,sheet3, sheet4, sheet5, sheet6;
    private static JFrame myJFrame;
    private static Logs logger;
    private SavedPaths paths;
    
    // Δήλωση των στατικών ονομασιών των φύλλων του excel προς επεξεργασία.
    
    
    Generator(JFrame x, String y){
        myJFrame = x;
        fileName = y;
        logger = new Logs();
        paths = new SavedPaths();
        fileName = paths.getImportFilePath();
        sheet1 = paths.getSheet1();
        sheet2 = paths.getSheet2();
        sheet3 = paths.getSheet3();
        sheet4 = paths.getSheet4();
        sheet5 = paths.getSheet5();
        sheet6 = paths.getSheet6();
    }
    
    /**
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
     * ή κενά, θα προκληθεί ένα Exception και η διαδικασία θα διακοπεί.
     */
    public void createExcels(){
        try {
            boolean excel1, excel2, excel3, excel4, excel5 = false;
            
            List<Professor> profs = new ArrayList<>();
            profs = getProfs(fileName);
            excel1 = true;
            if (profs == null) {
                throw new Exception();
            }

            List<String> timeslots = new ArrayList<>();
            timeslots = getTimeslots(fileName);
            if (timeslots == null) {
                throw new Exception();
            }
            excel2 = true;

            HashMap<String, String> dates = new HashMap<>();
            HashMap<String, String> tmp
                    = new HashMap<String, String>(getDates(fileName));
            if (tmp == null) {
                throw new Exception();
            }
            for (Map.Entry<String, String> entry : tmp.entrySet()) {
                dates.put(entry.getKey(), entry.getValue());
            }
            excel3 = true;

            List<Classroom> classrooms = new ArrayList<>();
            classrooms = getClassrooms(fileName);
            if (classrooms == null) {
                throw new Exception();
            }
            excel4 = true;

            for (Classroom cls : classrooms) {
                System.out.println(cls.getClassroomName());
            }
            List<Course> courses = new ArrayList<>();
            courses = getCourses(fileName, profs);
            if (courses == null) {
                throw new Exception();
            }
            excel5 = true;

            if (excel1 && excel2 && excel3 && excel4 && excel5) {
                createTemplate(profs, timeslots, dates, classrooms);
                FileOutputStream f = new FileOutputStream(new File("myObjects.dat"));
                ObjectOutputStream o = new ObjectOutputStream(f);
                // Write objects to file
                o.writeObject(profs);
                o.writeObject(courses);
                o.writeObject(classrooms);
                o.writeObject(timeslots);
                o.writeObject(dates);
                o.close();
                f.close();
            }
            
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Η μέθοδος διαβάζει το κύριο excel που εμπεριέχει πληροφορίες για τους καθηγητές,
     * τα μαθήματα, τις σχέσεις τους κ.α. Έπειτα, καλεί 2 μεθόδους που συμπληρώνουν
     * στα αντικείμενα καθηγητών και αιθουσών την διαθεσιμότητά τους με βάση τα συμπληρωμένα
     * template.
     * 
     * @throws org.gmele.general.sheets.exception.SheetExc
     */
    public void readTemplates() throws SheetExc{
        try {
            
            FileInputStream fi = new FileInputStream(new File("myObjects.dat"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            List<Professor> profs = (List<Professor>) oi.readObject();
            List<Classroom> classrooms = (List<Classroom>) oi.readObject();
            List<Course> courses = (List<Course>) oi.readObject();
            List<String> timeslots = (List<String>) oi.readObject();
            HashMap<String, String> dates = (HashMap<String, String>) oi.readObject();
            oi.close();
            fi.close();
            System.out.println("\n\n\n\n\n\n\n\n");
            for (Professor tmp : profs){
                //System.out.println(tmp.getProfSurname());
            }
            for (int i=0; i<timeslots.size();i++){
                System.out.println(timeslots.get(i));
            }
            
            
            addProfessorsAvailability(profs, timeslots.size(), paths.getImportFilePath1());
            for (Professor prof : profs){
                prof.prinAvailable();
            }
            addClassroomsAvailability(classrooms, timeslots.size(), paths.getImportFilePath2());
            for (Classroom cls : classrooms){
                cls.prinAvailable();
            }
        }catch (Exception e){
            return;
        }
    }
    
    /**
     * Συμπληρώνει για όλη την λίστα των καθηγητών, την διαθεσιμότητά τους από το
     * συμπληρωμένο φύλλο που παράχθηκε από το πρόγραμμα σε προηγούμενο βήμα. 
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

            for (Professor professor : professors){
                String sheetName = professor.getProfSurname() + " " + professor.getProfFirstname();
                s.SelectSheet(sheetName);
                int lastRow = s.GetLastRow();
                List<Availability> availabilityList = new ArrayList<>();
                for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    String cellDate = s.GetCellString(rowIndex, 0);
                    for (int colIndex = 1; colIndex < lastColumn; colIndex++){
                        String timeslot = s.GetCellString(0,colIndex);
                        String curCell = s.GetCellString(rowIndex, colIndex).trim();
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
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα σημπληρωμένα αρχεία διαθεσιμότητας καθηγητών.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        
        return ;
    }
    
    /**
     * Συμπληρώνει για όλη την λίστα των αιθουσών, την διαθεσιμότητά τους από το
     * συμπληρωμένο φύλλο που παράχθηκε από το πρόγραμμα σε προηγούμενο βήμα. 
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
            for (Classroom classroom : classrooms){
                String sheetName = classroom.getClassroomName();
                s.SelectSheet(sheetName);
                int lastRow = s.GetLastRow();
                List<Availability> availabilityList = new ArrayList<>();
                for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    String cellDate = s.GetCellString(rowIndex, 0);
                    for (int colIndex = 1; colIndex < lastColumn; colIndex++){
                        String timeslot = s.GetCellString(0,colIndex);
                        String curCell = s.GetCellString(rowIndex, colIndex).trim();
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
                    classroom.setAvailability(availabilityList);
                }
                classroom.prinAvailable();
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
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα με τα σημπληρωμένα αρχεία διαθεσιμότητας αιθουσών.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return ;
    }
    
    /**
     * Ενημέρωση της λίστας των καθηγητών που εξετάζουν το μάθημα σε ολόκληρη την λίστα
     * αντικειμένων Course.
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
            int rowIndex = 0;
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
            rowIndex = 0;
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
                                            tmpCourse.getExaminers().add(prof);
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
    public List<Course> getCourses(String filename, List<Professor> profs) throws SheetExc{
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
    public List<Classroom> getClassrooms(String filename) throws SheetExc{
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
    public HashMap<String, String> getDates(String filename) throws SheetExc{
        try {
            FileInputStream file = new FileInputStream(new File(filename));
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet3);
            HashMap<String, String> dates = new HashMap<String, String>();
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
                    String cellA = outputDateFormat.format(date);
                    String cellB = s.GetCellString(rowIndex, 1);
                    cellA = cellA.trim();
                    cellB =cellB.trim();
                    if (checkIfValid(cellA) && checkIfValid(cellB)){
                        dates.put(cellA, cellB);
                    }else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
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
    public List<String> getTimeslots(String filename) throws SheetExc{
        try {
            FileInputStream file = new FileInputStream(new File(filename));
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet2);
            //Iterate through each rows one by one
            List<String> timeslots = new ArrayList<>();
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            while (rowIndex < lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0);
                    if(checkIfValid(cellA)){
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
    public List<Professor> getProfs(String filename) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
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
     * Έλεγχος για διπλοεγγραφή καθηγητή.
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
     * Έλεγχος για διπλοεγγραφή μαθήματος.
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
     * Έλεγχος για έγκυρη πληροφορία.
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
     * Δημιουργία των template αρχείων προς συμπλήρωση. 
     * 1 αρχείο αφορά τους καθηγητές και την διαθεσιμότητά τους για τις ημερομηνίες 
     * της εξεταστικής. Κάθε φύλλο θα συμπληρώνεται από τον κάθε καθηγητή ξεχωριστά.
     * 2 αρχείο αφορά τις αίθουσες και την διαθεσιμότητά τους για τις ημερομηνίες
     * της εξεταστικής. Τα φύλλα συμπληρώνονται από τον υπεύθυνο για το πρόγραμμα.
     * @param uniqueProfs Λίστα αντικειμένων καθηγητών (Professor).
     * @param timeslots Λίστα strings με τα διαστήματα εξέτασης. 
     * @param dates HashMap με τα ζευγάρια ημέρας - ημερομηνίας.
     * @param classrooms Λίστα αντικειμένων αιθουσών (Course).
     */
    public void createTemplate(List<Professor> uniqueProfs, List<String> timeslots, HashMap<String, String> dates,List<Classroom> classrooms){
        
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
                    for (Map.Entry<String, String> entry : dates.entrySet()) {
                        String date = entry.getKey();
                        String day = entry.getValue();
                        Row row = sheet.createRow(rowIndex++);
                        Cell dateCell = row.createCell(0);
                        dateCell.setCellValue(day + "\n" + date);
                        
                        //for (int i = 0; i < timeslots.size(); i++) {
                        //      row.createCell(i + 1);
                        //      }
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
                for (Map.Entry<String, String> entry : dates.entrySet()) {
                    String date = entry.getKey();
                    String day = entry.getValue();
                    Row row = sheet.createRow(rowIndex++);
                    Cell dateCell = row.createCell(0);
                    dateCell.setCellValue(day + " " + date);
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
    
    public void readObjects() throws FileNotFoundException, IOException, ClassNotFoundException{
        FileInputStream fi = new FileInputStream(new File("myObjects.dat"));
	ObjectInputStream oi = new ObjectInputStream(fi);
        List<Professor> pr1 = (List<Professor>) oi.readObject();
        List<Classroom> pr2 = (List<Classroom>) oi.readObject();
        List<Course> pr3 = (List<Course>) oi.readObject();
        
        System.out.println("\n\n\n\n\n\n\n\n");
        for (Professor tmp : pr1){
            System.out.println(tmp.getProfSurname());
        }
	oi.close();
	fi.close();
    }
}
