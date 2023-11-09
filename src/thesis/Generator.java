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
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * Η κλάση είναι υπεύθυνη για την δημιουργία των κατάλληλων αρχείων προς συμπλήρωση
 * από τους καθηγητες αλλά και τον υπεύθυνο για την σύνταξη του προγράμματος εξεταστικής.
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
    static final String sheet5 = "COURSES_PROFESSORS";
    
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
        List<String> distinctProfessors = new ArrayList<>();
        try{
            distinctProfessors.addAll(findUniqueProfessors(paths.getImportFilePath()));
            excel5 = true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '"+sheet5+"'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        
        if (excel1 && excel2 && excel3 && excel4 && excel5){
            //addAvailability(profs, timeslots, paths.getImportFilePath());
            addProfessorsAvailability(profs, timeslots.size(), "C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\TEST2.xlsx");
            addClassroomsAvailability(classrooms, timeslots.size(), "C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\TEST3.xlsx");
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
                String sheetName = professor.getProfSurname() + " " + professor.getProfFirstname();
                s.SelectSheet(sheetName);
                System.out.println("Καθητητής: " + sheetName);
                int lastRow = s.GetLastRow();
                List<Availability> availabilityList = new ArrayList<>();
                for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    System.out.println("FIX");
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
                    + " του φύλλου: '"+sheet2+"'.",
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
        List<String> distinctProfessors = new ArrayList<>();
        try{
            distinctProfessors.addAll(findUniqueProfessors(paths.getImportFilePath()));
            excel5 = true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '"+sheet5+"'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        if (excel1 && excel2 && excel3 && excel4 && excel5){
        
            try {
                createTemplate(profs,timeslots,dates,classrooms, distinctProfessors, paths.getImportFilePath());
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
     * Εντοπισμός όλων των καθηγητών από το φύλλο που είναι σημειωμένες οι σχέσεις
     * μάθημα-καθηγητής/ες εξέτασης.
     * @param f είναι το όνομα του αρχείου που θα αναγνωστεί
     * @return HashMap<String,String> dates είναι ένα HashMap με την μεταβλητή 
     * κλειδί να είναι η ημερομηνία και την δευτερεόυσα να είναι η μέρα της εβδομάδας.
     */
    public List<String> findUniqueProfessors(String filename){
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet5);
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            List<String> distinctLastNames = new ArrayList<>();
            for (int rowNum = 1; rowNum <= lastRow; rowNum++) {

                // Get professor names from columns B, C, D, and E
                for (int colNum = 1; colNum <= 4; colNum++) {
                    String cell = s.GetCellString(rowNum, colNum);
                    if (cell != null) {
                        String professorName = cell.trim();
                        if (!professorName.isEmpty() && !professorName.equals("0") && !professorName.equals("-")) {

                            String[] nameParts = professorName.split(" ");
                            if (nameParts.length >= 1) {
                                String lastName = nameParts[nameParts.length - 1];
                                if (!distinctLastNames.contains(lastName)) {
                                    distinctLastNames.add(lastName);
                                }
                            }
                        }
                    }
                }
            }

            file.close();
            return distinctLastNames;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '" + filename + "' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '" + filename + "'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα εντοπισμού του φύλλου '" + sheet5 + "'."
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
                    if (check(cellA) && check(cellB)){
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
                        Professor tmp = new Professor(cellA, cellB, cellC);
                        profs.add(tmp);
                    }
                    else{
                        break;
                        //rowIndex = 1000000;
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
    
    public boolean check(String s){
        if(s!=null && !s.equals("") && !s.equals(" ")){
            return true;
        }else{
            return false;
        }
    }
    
    public void createTemplate(List<Professor> profs, List<String> timeslots, HashMap<String, String> dates,List<Classroom> classrooms,List<String> uniqueProfs, String filename) throws IOException {
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle style = getStyle(workbook);
            for (Professor professor : profs) {
                // Create a sheet for each professor
                String sheetName = professor.getProfSurname()+ " " + professor.getProfFirstname();
                if (uniqueProfs.contains(professor.getProfSurname())){
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
                else{
                    logger.appendLogger(logger.getIndexString() + "Ο καθηγητής " + professor.getProfSurname() +
                            " δεν βρέθηκε σε αντιστοιχία με κάποιο μάθημα.");
                }
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
    
    public void readTemplate(List<Professor> profs, List<String> timeslots, HashMap<String, String> dates,List<Classroom> classrooms,List<String> uniqueProfs, String filename){
        
        List<String> distinctProfessorsNew = new ArrayList<>();
        // READ PROFS_COURSES
        try{
            distinctProfessorsNew.addAll(findUniqueProfessors(fileName));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(myJFrame, "Πρόβλημα ανάγνωσης των δεδομένων"
                    + " του φύλλου: '"+sheet5+"'.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        for (String x : distinctProfessorsNew){
            if (!uniqueProfs.contains(x)){
                JOptionPane.showMessageDialog(myJFrame, "Ο καθηγητής " + x +
                        " δεν υπάρχει φύλλο στο excel. Ελέγξτε τα δεδομένα και δοκιμάστε ξανά."
                        ,"Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
            }
            else
                return;
        }
        try {
            FileInputStream file = new FileInputStream(new File("C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\TEST2.xlsx"));
        } catch (FileNotFoundException ex) {
            logger.appendLogger("T");
        }
        //XlsxSheet s = new XlsxSheet(f);
        for (String x : distinctProfessorsNew){
            
        }
        
        /*
        public List<Professor> getProfs(String f){
        try{
            FileInputStream file = new FileInputStream(new File(f));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(f);
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
                        Professor tmp = new Professor(cellA, cellB, cellC);
                        profs.add(tmp);
                    }
                    else{
                        break;
                        //rowIndex = 1000000;
                    }
                }
                rowIndex++;
            }
            for (Professor tmp : profs){
                tmp.printText();
            }
            file.close();
            return profs;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "Το αρχείο '"+f+"' δεν βρέθηκε.",
               "Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά το άνοιγμα"
                    + " του αρχείου '"+f+"'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "Σφάλμα κατά την ανάγνωση"
                    + " του αρχείου '"+f+"'.","Μήνυμα Λάθους", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
     */   
    }
    class Logs{
        private String log;
        private int index;
        Logs(){
            log = "";
            index = 0;
        }
        
        public String getLoggerTxt(){
            return log;
        }
        public void appendLogger(String x){
            log = log + x + "\n";
        }
        
        public int getIndex(){
            index = index + 1;
            return index;
        }
        public String getIndexString(){
            index = index + 1;
            return index+") ";
        }
        
        public void loggerClear(){
            log = "";
        }
    }
}
