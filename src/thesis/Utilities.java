package thesis;

import java.awt.Component;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JPanel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gmele.general.sheets.XlsxSheet;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση Utilities 
 */

public class Utilities {
    
    public Utilities(){
        
    }
    
    /**
     * Η συνάρτηση δέχεται ένα string ως παράμετρο και επιστρέφει το string χωρίς
     * τόνους στους ελληνικούς χαρακτήρες.
     * @param s Το string που θα υποστεί επεξεργασία (String).
     * @return  Το τροποποιημένο string (String).
     */
    public String removeAccents(String s) {
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
    
    /**
     * Συνάρτηση που αλλάζει αυτόματα το μέγεθος των στηλών του φύλλου excel.
     * @param sheet Το sheet του οποίου οι στήλες θα αλλάξουν μέγεθος (XSSFSheet).
     * @param columns Ο αριθμός των στηλών προς αλλαγή (integer).
     */
    public void autoSizeColumns(XSSFSheet sheet, int columns){
        for (int i = 0; i <= columns; i++){
            sheet.autoSizeColumn(i);
        }
    }
    
    public boolean checkDate(List<String> dates, String dt){
        if(dates.contains(dt))
            return true;
        return false;
    }
    
    public boolean checkTimeslot(List<String> timeslots, String ts){
        if(timeslots.contains(ts))
            return true;
        return false;
    }
    
    /**
     * Η συνάρτηση εφαρμόζει template στυλ κελιών σε συγκεκριμένες θέσεις
     * για να αποτυπώσει το τελικό excel προγράμματος εξετάσεων.
     * @param workbook Το αρχείο προς επεξεργασία (XSSFWorkbook).
     * @param sheet Το φύλλο προς επεξεργασία (XSSFSheet).
     */
    public void applyCellStyles(XSSFWorkbook workbook, XSSFSheet sheet){
        int curRow, curCol = 0;
        CellStyles cs = new CellStyles();
        for (Row row : sheet) {
            for (Cell cell : row) {
                curRow = cell.getRowIndex();
                curCol = cell.getColumnIndex();
                if (curRow > 0 && curCol >= 2){
                    cell.setCellStyle(cs.getFinalScheduleBasicStyle(workbook));
                }else if (curRow == 0 && curCol <= 1){
                    cell.setCellStyle(cs.getDateHeadersStyle(workbook));
                }else if (curRow > 0 && curCol < 2){
                    cell.setCellStyle(cs.getDateValuesStyle(workbook));
                }else if (curRow == 0 && curCol > 1){
                    cell.setCellStyle(cs.getTimeslotsHeadersStyle(workbook));
                }
            }
        }
    }
    
    /**
     * Συνάρτηση που εντοπίζει εάν υπάρχει ένα string στην λίστα με τα μαθήματα
     * 
     * @param cellValue Το λεκτικό με το όνομα του μαθήματος ή την συντομογραφία
     * του (String).
     * 
     * @return Επιστρέφει είτε το αντικείμενο του μαθήματος εάν βρέθηκε (Course), είτε null
     * εάν δεν υπάρχει.
     */
    public Course getCourse(List<Course> courses, String cellValue){
        for (Course tmp : courses){
            if (tmp.getCourseName().equals(cellValue) || 
                    tmp.getCourseShort().equals(cellValue)){
                return tmp;
            }
        }
        return null;
    }
    
    public ScheduledCourse getScheduledCourse(List<ScheduledCourse> scheduledCourses, Course course){
        for(ScheduledCourse crs : scheduledCourses){
            if(crs.getCourse().getCourseName().equals(course.getCourseName())){
                return crs;
            }
        }
        return null;
    }
    
    public String getSafeCellString(XlsxSheet sheet, int rowIndex, int colIndex) {
        try {
            return sheet.GetCellString(rowIndex, colIndex);
        } catch (Exception e) { // Σε περίπτωση που το κελί είναι κενό (null), επιστρέφω ένα κενό string.
            return "";
        }
    }
    
    public String getDateWithGreekFormat(String[] weekdays, String dateStr) {
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
    
    public String modifyDate(String inputDate, int daysToModify, char operation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate date = LocalDate.parse(inputDate, formatter);

            // Modify the date based on the operation
            if (operation == '+') {
                date = date.plusDays(daysToModify);
            } else if (operation == '-') {
                date = date.minusDays(daysToModify);
            } else {
                return null;
            }
            return date.format(formatter);

        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Η συνάρτηση γεμίζει το τελικό πρόγραμμα εξεταστικής με τα headers
     * από τις ημερομηνίες και τα timeslots.
     * @param workbook Το αρχείο προς επεξεργασία (XSSFWorkbook).
     * @param sheet Το φύλλο προς επεξεργασία (XSSFSheet).
     * @param timeslots Η λίστα με τα χρονικά διαστήματα (List<String>).
     * @param dates Η λίστα με τις ημερομηνίες εξέτασης (List<String>).
     */
    public void fillHeaders(XSSFWorkbook workbook, XSSFSheet sheet, List<String> timeslots, List<String> dates){
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
            String greekDayName = this.getGreekDayName(tmp);
            greekDayName = this.removeAccents(greekDayName);
            dayCell.setCellValue(greekDayName.toUpperCase());
            Cell dateCell = row.createCell(1);
            dateCell.setCellValue(tmp);
        }
    }
    
    //method to filter out not needed courses.
    public List<Course> getValidCourses(List<Course> courses){
        List<Course> validCoursesList = new ArrayList<>();
        for(Course crs : courses){
            if (crs.getApproxStudents() > 0 && crs.getIsExamined() == true){
                validCoursesList.add(crs);
            }
        }
        return validCoursesList;
    }
    
    public CourseClassroomsPanel findPanelForCourse(Course course, JPanel coursesClassroomsPanelContainer) {
        for (Component comp : coursesClassroomsPanelContainer.getComponents()) {
            if (comp instanceof CourseClassroomsPanel) {
                CourseClassroomsPanel panel = (CourseClassroomsPanel) comp;
                if (panel.getScheduledCourse().getCourse().equals(course)) {
                    return panel;
                }
            }
        }
        return null;
    }
}
