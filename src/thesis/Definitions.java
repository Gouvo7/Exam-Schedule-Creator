package thesis;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση Definitions είναι υπεύθυνη για την διαχείριση και την αποθήκευση πληροφοριών 
 * που αφορούν διαδρομές (paths) αρχείων ή φακέλων του προγράμματος. Επιπλέον, περιλαμβάνει
 * τις ονομασίες των αρχείων και των φύλλων του excel που θα χρησιμοποιηθούν. 
 * Τέλος, κληρονομεί από την κλάση JFrame μεθόδους ώστε να γίνει η πρώτη αρχικοποίηση
 * στο μονοπάτι του προγράμματος (εάν χρειάζεται).
 */
public class Definitions extends JFrame{
    
    private static String folderPath = "";
    private static String settingsFile = "config.txt";
    private static String genericFile = "Scheduler_input.xlsx";
    private static String producedProfessorsAvailabilityFile = "generated_professors_availability.xlsx";
    private static String producedClassroomsAvailabilityFile = "generated_classrooms_availability.xlsx";
    private static String professorsAvailabilityFile = "filled_professors_availability.xlsx";
    private static String classroomsAvailabilityFile = "filled_classrooms_availability.xlsx";
    private static String examScheduleFile = "ΠΡΟΓΡΑΜΜΑ.xlsx";
    
    private static String sheet1 = "ΚΑΘΗΓΗΤΕΣ"; 
    private static String sheet2 = "ΧΡΟΝΙΚΑ_ΔΙΑΣΤΗΜΑΤΑ";
    private static String sheet3 = "ΗΜΕΡΟΜΗΝΙΕΣ";
    private static String sheet4 = "ΑΙΘΟΥΣΕΣ";
    private static String sheet5 = "ΜΑΘΗΜΑΤΑ";
    private static String sheet6 = "ΜΑΘΗΜΑΤΑ_ΚΑΘΗΓΗΤΕΣ";
    private static String sheet7 = "ΠΡΟΓΡΑΜΜΑ ΕΞΕΤΑΣΤΙΚΗΣ";
    private static String sheet8 = "ΠΡΟΓΡΑΜΜΑ ΑΙΘΟΥΣΩΝ";
    
    private String[] weekdays = { "ΔΕΥΤΕΡΑ", "ΤΡΙΤΗ", "ΤΕΤΑΡΤΗ", "ΠΕΜΠΤΗ", "ΠΑΡΑΣΚΕΥΗ", "ΣΑΒΒΑΤΟ", "ΚΥΡΙΑΚΗ" };
    private List<String> greekSemesters = Arrays.asList("Α", "Β", "Γ", "Δ", "Ε", "Ζ", "Η","ΣΤ", "ΕΧ1", "ΕΧ2", "ΕΧ3", "ΕΧΓ", "ΕΕ1", "ΕΕ2", "ΕΕ3", "ΕΕΓ", "ΕΧΑ");

    public void Definitions(){
    }
    
    /**
     * Έναρξη της διαδικασίας εκκίνησης του προγράμματος. Ελέγχεται εάν το αρχείο 
     * ρυθμίσεων υπάρχει και εκκινεί την εφαρμογή ανάλογα με την ύπαρξή του. Σε
     * περίπτωση που δεν υπάρχει, το πρόγραμμα θα καλωσορίσει τον χρήστη στην εφαρμογή
     * και θα τον παροτρύνει να ορίσει έναν φάκελο ως workind directory.
     */
    public void startProcess(){
        if (!configFileExists()){
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Καλωσήρθατε στην εφαρμογή!"
                    + " Παρακαλώ ορίστε τον φάκελο αρχείων εισόδου/εξόδου για την εκκίνηση του σχεδιαστή προγράμματος εξετάσεων.", "Μήνυμα Εφαρμογής", JOptionPane.OK_OPTION))
            {
                promptUserForFolder();
            }else{
                System.exit(0);
            }
        }else{
            loadWorkingDirectory();
        }
    }
    
    public void Definitions(String x){
        this.folderPath = x;
    }

    public void setSettingsFile(String tmp){
        settingsFile = tmp;
    }
    
    public String getSettingsFile(){
        return settingsFile;
    }
    
    public void setFolderPath(String tmp){
        folderPath = tmp;
    }
    
    public String getFolderPath(){
        return folderPath;
    }
    
    public void setGenericFile(String tmp){
        genericFile = tmp;
    }
    
    public String getGenericFile(){
        return genericFile;
    }
    
    public void setConfigFile(String tmp){
        settingsFile = tmp;
    }
    
    public String getConfigFile(){
        return settingsFile;
    }
    
    public String getProducedProfessorsAvailabilityFile(){
        return producedProfessorsAvailabilityFile;
    }
    public String getProducedClassroomsAvailabilityFile(){
        return producedClassroomsAvailabilityFile;
    }
    
    public String getProfessorsAvailabilityFile(){
        return professorsAvailabilityFile;
    }
    public String getClassroomsAvailabilityFile(){
        return classroomsAvailabilityFile;
    }
    
    public void setExamScheduleFile(String x){
        examScheduleFile = x;
    }
    public String getExamScheduleFile(){
        return examScheduleFile;
    }
    
    public String getSheet1() {
        return sheet1;
    }

    public static void setSheet1(String sheet1) {
        Definitions.sheet1 = sheet1;
    }

    public String getSheet2() {
        return sheet2;
    }

    public static void setSheet2(String sheet2) {
        Definitions.sheet2 = sheet2;
    }

    public String getSheet3() {
        return sheet3;
    }

    public static void setSheet3(String sheet3) {
        Definitions.sheet3 = sheet3;
    }

    public String getSheet4() {
        return sheet4;
    }

    public static void setSheet4(String sheet4) {
        Definitions.sheet4 = sheet4;
    }

    public String getSheet5() {
        return sheet5;
    }

    public static void setSheet5(String sheet5) {
        Definitions.sheet5 = sheet5;
    }

    public String getSheet6() {
        return sheet6;
    }
    
    public static void setSheet6(String sheet6) {
        Definitions.sheet6 = sheet6;
    }
    
    public String getSheet7() {
        return sheet7;
    }
    
    public static void setSheet7(String sheet7) {
        Definitions.sheet7 = sheet7;
    }
    
    public String getSheet8() {
        return sheet8;
    }
    
    public static void setSheet8(String sheet8) {
        Definitions.sheet8 = sheet8;
    }
    
    public String[] getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String[] weekdays) {
        this.weekdays = weekdays;
    }

    public List<String> getGreekSemesters() {
        return greekSemesters;
    }

    public void setGreekSemesters(List<String> greekSemesters) {
        this.greekSemesters = greekSemesters;
    }
    
    /**
     * Ελέγχει εάν το αρχείο ρυθμίσεων υπάρχει.
     *
     * @return true αν το αρχείο ρυθμίσεων υπάρχει, αλλιώς false (boolean)
     */
    private static boolean configFileExists(){
        return Files.exists(Paths.get(settingsFile));
    }
    
    /**
     * Ελέγχει εάν το αρχείο προγράμματος εξετάσεων υπάρχει στον τρέχοντα φάκελο.
     *
     * @return true αν το αρχείο προγράμματος εξετάσεων υπάρχει, αλλιώς false (boolean).
     */
    public boolean examScheduleFileExists(){
        return Files.exists(Paths.get(folderPath + "\\" + examScheduleFile ));
    }
    
    /**
     * Επιστρέφει τον τρέχον κατάλογο της εφαρμογής.
     *
     * @return Ο τρέχον κατάλογος.
     */
    public String CurrentDirectory(){
        return System.getProperty("user.dir");
    }
    
    /**
     * Ζητά από τον χρήστη να επιλέξει έναν φάκελο για αποθήκευση.
     */
    public void promptUserForFolder() {
        try{
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = fileChooser.getSelectedFile();
                folderPath = selectedFolder.toString();
                saveConfigFile();
            }
        }catch (Exception ex){
            JOptionPane.showMessageDialog(this, "Πρόβλημα κατά την διαδικασία ενημέρωσης του workind directory.", "Μήνυμα Εφαρμογής", JOptionPane.OK_OPTION);
        }
    }
    
    /**
     * Αποθηκεύει τον επιλεγμένο φάκελο στο αρχείο ρυθμίσεων.
     */
    public void saveConfigFile() {
        if(folderPath != null){
            try (PrintWriter out = new PrintWriter(settingsFile)) {
                out.println(folderPath);
            } catch (Exception e) {
                return;
            }
        }else{
            System.out.println("Cannot save a null folder path");
        }
    }

    /**
     * Φορτώνει τον τρέχον κατάλογο από το αρχείο ρυθμίσεων διαβάζοντας το αρχείο
     * ρυθμίσεων.
     */
    public void loadWorkingDirectory() {
        try (Scanner scanner = new Scanner(new File(settingsFile))) {
            if (scanner.hasNextLine()) {
                folderPath = Paths.get(scanner.nextLine()).toString();
            }
        } catch (Exception e) {
            folderPath = null;
        }
    }
}