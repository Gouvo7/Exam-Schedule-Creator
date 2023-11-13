package thesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private static JFrame myJFrame;
    private Logs logger;
    private SavedPaths paths;
    
    // Δήλωση των στατικών ονομασιών των φύλλων του excel προς επεξεργασία.
    static final String sheet1 = "PROFESSORS_LIST";
    static final String sheet2 = "TIMESLOTS";
    static final String sheet3 = "DATES";
    static final String sheet4 = "CLASSROOMS";
    static final String sheet5 = "COURSES_LIST";
    static final String sheet6 = "COURSES_PROFESSORS";
    
    Generator(JFrame x, String y){
        myJFrame = x;
        fileName = y;
        logger = new Logs();
        paths = new SavedPaths();
    }
    
    /**
     * Η κλάση doThings():
     * 1) Διαβάζει το αρχείο από το μονοπάτι που καθορίζει ο χρήστης από την εφαρμογή.
     * 2) Αποθηκεύει τα δεδομένα σε αντικείμενα κατάλληλου τύπου
     * 3) Παράγει δύο νέα αρχεία όπου το 1ο συμπληρώνεται από τους καθηγητές και 
     * αφορά την διαθεσιμότητά τους για τις ημερομηνίες της εξεταστικής. Για κάθε 
     * καθηγητή παράγεται ένα φύλλο, με γραμμές για τις ημερομηνίες και τα χρονικά 
     * διαστήματα μεταξύ των ωρών λειτουργίας του Πανεπιστημίου. Το 2ο αρχείο
     * συμπληρώνεται από τον υπεύθυνο σύνταξης του προγράμματος εξεταστικής και
     * αφορά την διαθεσιμότητα των αιθουσών για τις ημερομηνίες της εξεταστικής.
     */
    public void doThings(){

        boolean excel1,excel2,excel3, excel4, excel5 = false;
        List<Professor> profs = new ArrayList<>();
        try{
            profs.addAll(getProfs(paths.getImportFilePath()));
            excel1 = true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '" + sheet1 + "'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        
        List<String> timeslots = new ArrayList<>();
        try{
            timeslots.addAll(getTimeslots(paths.getImportFilePath()));
            excel2 = true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '" + sheet2 + "'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (String timeSlots : timeslots) {
            System.out.println(timeSlots);
        }
        
        HashMap<String, String> dates = new HashMap<>();
        try{
            HashMap<String, String> tmp = 
                new HashMap<String,String>(getDates(paths.getImportFilePath()));
            for (Map.Entry<String, String> entry : tmp.entrySet()) {
                dates.put(entry.getKey(), entry.getValue());
            }
            excel3 = true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '" + sheet3 + "'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Classroom> classrooms = new ArrayList<>();
        try{
            classrooms.addAll(getClassrooms(paths.getImportFilePath()));
            excel4 = true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '" + sheet4 + "'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        
        try{
            classrooms.addAll(getClassrooms(paths.getImportFilePath()));
            excel4 = true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '" + sheet4 + "'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return ;
        }

        List<Course> courses = new ArrayList<>();
        try{
            courses.addAll(getCourses(paths.getImportFilePath(), profs));
            excel5 = true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '" + sheet5 + "'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        
        if (excel1 && excel2 && excel3 && excel4 && excel5){
        
            try {
                createTemplate(profs,timeslots,dates,classrooms, paths.getImportFilePath());
                logger.appendLogger("Η δημιουργία template ολοκληρώθηκε επιτυχώς.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(myJFrame, "Το αρχείο προς συμπλήρωση δεν μπορεί να"
                        + " δημιουργηθεί. Παρακαλώ ελέγξτε τα μηνύματα λάθους.",
                   "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
                logger.appendLogger("Η δημιουργία template απέτυχε.");
            }
        }
                
       // readTemplate(profs,timeslots,dates,classrooms, distinctProfessors, paths.getImportFilePath());
    }
    
    /**
     * Η μέθοδος διαβάζει το κύριο excel που εμπεριέχει πληροφορίες για τους καθηγητές,
     * τα μαθήματα, τις σχέσεις τους κ.α. Έπειτα, καλεί 2 μεθόδους που συμπληρώνουν
     * στα αντικείμενα καθηγητών και αιθουσών την διαθεσιμότητά τους με βάση τα συμπληρωμένα
     * template.
     */
    public void readTemplates(){
        boolean excel1,excel2,excel3, excel4, excel5 = false;
        List<Professor> profs = new ArrayList<>();
        try{
            profs.addAll(getProfs(paths.getImportFilePath()));
            excel1 = true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '"+sheet1+"'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        
        List<String> timeslots = new ArrayList<>();
        try{
            timeslots.addAll(getTimeslots(paths.getImportFilePath()));
            excel2 = true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '"+sheet2+"'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        HashMap<String, String> dates = new HashMap<>();
        try{
            HashMap<String, String> tmp = 
                new HashMap<String,String>(getDates(paths.getImportFilePath()));
            for (Map.Entry<String, String> entry : tmp.entrySet()) {
                dates.put(entry.getKey(), entry.getValue());
            }
            excel3 = true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '"+sheet3+"'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Classroom> classrooms = new ArrayList<>();
        try{
            classrooms.addAll(getClassrooms(paths.getImportFilePath()));
            excel4 = true;
        }catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '"+sheet4+"'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return ;
        }

        if (excel1 && excel2 && excel3 && excel4){
            //addAvailability(profs, timeslots, paths.getImportFilePath());
            System.out.println(paths.getImportFilePath1());
            addProfessorsAvailability(profs, timeslots.size(), paths.getImportFilePath1());
            addClassroomsAvailability(classrooms, timeslots.size(), paths.getImportFilePath2());
        }
    }
    
    /**
     *
     * @param professors
     * @param lastColumn
     * @param filename
     * @return
     */
    public List<Professor> addProfessorsAvailability(List<Professor> professors,int lastColumn, String filename){
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            for (Professor professor : professors){
                System.out.println(professor.getProfSurname() + " " + professor.getProfFirstname());
            }
            for (Professor professor : professors){
                String sheetName = professor.getProfSurname() + " " + professor.getProfFirstname();
                s.SelectSheet(sheetName);
                System.out.println("Καθητητής: " + sheetName);
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
                            return null;
                        }
                    }
                }
                if (!availabilityList.isEmpty()){
                    professor.setAvailability(availabilityList);
                }
            }
            file.close();
            return null;
            
        } catch (SheetExc exName) {
            JOptionPane.showMessageDialog(myJFrame, "Αδερφέ δεν βρήκα το sheet που μου έδωσες.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public List<Professor> addClassroomsAvailability(List<Classroom> classrooms,int lastColumn, String filename){
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            for (Classroom classroom : classrooms){
                String sheetName = classroom.getClassroomName();
                s.SelectSheet(sheetName);
                System.out.println("Αίθουσα: " + sheetName);
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
                            System.out.println("Γαμήθηκες");
                            return null;
                        }
                    }
                }
                if (!availabilityList.isEmpty()){
                    classroom.setAvailability(availabilityList);
                }
                classroom.prinAvailable();
            }
            file.close();
            return null;
            
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Χάσαμε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public List<Classroom> getCourses(String filename, List<Professor> profs){
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet5);
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
                        file.close();
                        break;
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
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα εντοπισμού του φύλλου '" + sheet4 + "'."
                    ,"Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * Εντοπισμός όλων των αιθουσών που είναι καταχωρημένες.
     * @param f - Η ονομασία του αρχείου προς ανάγνωση.
     * @return List<Classroom> - είναι μία λίστα από αντικείμενα Classroom τα οποία
     * εμπεριέχουν πληροφορίες για κάθε αίθουσα.
     */
    public List<Classroom> getClassrooms(String filename){
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
                        file.close();
                        break;
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
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα εντοπισμού του φύλλου '" + sheet4 + "'."
                    ,"Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * 
     * @param f - Η ονομασία του αρχείου προς ανάγνωση.
     * @return List<Classroom> - είναι μία λίστα από αντικείμενα Classroom τα οποία
     * εμπεριέχουν πληροφορίες για κάθε αίθουσα.
     */
    public HashMap<String, String> getDates(String filename){
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
                        return dates;
                    }
                    String cellA = outputDateFormat.format(date);
                    String cellB = s.GetCellString(rowIndex, 1);
                    cellA = cellA.trim();
                    cellB =cellB.trim();
                    if (checkIfEmpty(cellA) && checkIfEmpty(cellB)){
                        dates.put(cellA, cellB);
                    }else{
                        file.close();
                        return dates;
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
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα εντοπισμού του φύλλου '" + sheet3 + "'."
                    ,"Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public List<String> getTimeslots(String filename){
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
                    if(cellA != null){
                        timeslots.add(cellA);
                    }else{
                        break;
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
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα εντοπισμού του φύλλου '" + sheet2 + "'."
                    ,"Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public List<Professor> getProfs(String filename){
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
                    String cellA = s.GetCellString(rowIndex, 0);
                    String cellB = s.GetCellString(rowIndex, 1);
                    String cellC = s.GetCellString(rowIndex, 2);
                    cellA = cellA.trim();
                    cellB = cellB.trim();
                    cellC = cellC.trim();
                    if (cellA != null && cellB != null && cellC != null){
                        boolean dupliicate = false;
                        if (profs.isEmpty()){
                            dupliicate = false;
                        }else{
                        for (Professor tmp : profs){
                            if (checkDuplicateProfessor(tmp,cellA,cellB,cellC)){
                                dupliicate = true;
                                break;
                            }
                        }
                        }
                        
                        if (!dupliicate){
                            Professor prof = new Professor(cellA, cellB, cellC);
                            profs.add(prof);
                        }
                    }
                    else{
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
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα εντοπισμού του φύλλου '" + sheet1 + "'."
                    ,"Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public boolean checkDuplicateProfessor(Professor tmp, String A, String B, String C){
        if(tmp.getProfSurname().equals(A) && tmp.getProfFirstname().equals(B) && tmp.getProfField().equals(C) ){
            System.out.println("Βρήκα διπλό!");
            return true;
        }
        return false;
    }
    
    public boolean checkIfEmpty(String s){
        if(s!=null && !s.equals("") && !s.equals(" ")){
            return true;
        }else{
            return false;
        }
    }
    
    public void createTemplate(List<Professor> uniqueProfs, List<String> timeslots, HashMap<String, String> dates,List<Classroom> classrooms, String filename) throws IOException {
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle style = getStyle(workbook);
            for (Professor professor : uniqueProfs) {
                // Create a sheet for each professor
                String sheetName = professor.getProfSurname()+ " " + professor.getProfFirstname();
                    XSSFSheet sheet = workbook.createSheet(sheetName);
                
                
                    // Add timeslots to the first row
                    Row timeslotRow = sheet.createRow(0);
                    for (int i = 0; i < timeslots.size(); i++) {
                        Cell cell = timeslotRow.createCell(i+1);
                        cell.setCellValue(timeslots.get(i));
                        sheet.autoSizeColumn(i+1);
                    }

                    // Add dates and time slots to the sheet
                    int rowIndex = 1;
                    for (Map.Entry<String, String> entry : dates.entrySet()) {
                        String date = entry.getKey();
                        String day = entry.getValue();
                        Row row = sheet.createRow(rowIndex++);
                        Cell dateCell = row.createCell(0);
                        dateCell.setCellValue(day + "\n" + date);
                        // Add time slots for each date
                        for (int i = 0; i < timeslots.size(); i++) {
                            Cell timeSlotCell = row.createCell(i + 1);
                        }
                    }
                    for (Row row : sheet) {
                        for (Cell cell : row) {
                            cell.setCellStyle(style);
                        }
                    }
                    sheet.autoSizeColumn(0);
            }

            // Αποθήκευση αρχείου προς συμπλήρωση για τους καθηγητές.
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
            
                // Δημιουργία ενός φύλλου (sheet) για κάθε τάξη.
                String sheetName = classroom.getClassroomName();
                XSSFSheet sheet = workbook.createSheet(sheetName);

                // Προσθήκη των χρονικών διαστημάτων (timeslots) ως headers στις στήλες.
                Row timeslotRow = sheet.createRow(0);
                for (int i = 0; i < timeslots.size(); i++) {
                    Cell cell = timeslotRow.createCell(i + 1);
                    cell.setCellValue(timeslots.get(i));
                    sheet.autoSizeColumn(i+1);
                }

                // Προσθήκη των ημερομηνιών στις γραμμές της 1ης στήλης.
                int rowIndex = 1;
                for (Map.Entry<String, String> entry : dates.entrySet()) {
                    String date = entry.getKey();
                    String day = entry.getValue();
                    Row row = sheet.createRow(rowIndex++);
                    Cell dateCell = row.createCell(0);
                    dateCell.setCellValue(day + " " + date);

                    for (int i = 0; i < timeslots.size(); i++) {
                        row.createCell(i + 1);
                    }
                }
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        cell.setCellStyle(style);
                    }
                }
                sheet.autoSizeColumn(0);
            }
            // Αποθήκευση αρχείου προς συμπλήρωση για τις τάξεις.
            try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\class.xlsx")) {
                workbook.write(outputStream);
            }
            logger.appendLogger("Η δημιουργία του template για τις αίθουσες ολοκληρώθηκε επιτυχώς.");
        }catch (Exception e){
            logger.appendLogger("Η δημιουργία του template για τις αίθουσες απέτυχε.");
        }
        System.out.println(logger.getLoggerTxt());
        JOptionPane.showMessageDialog(myJFrame,logger.getLoggerTxt(),
               "Μήνυμα Εφαρμογής", JOptionPane.INFORMATION_MESSAGE);

    }

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
}
