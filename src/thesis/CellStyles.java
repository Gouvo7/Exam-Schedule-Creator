package thesis;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση CellStyles ευθύνεται για την δημιουργία και την επιστροφή προκαθορισμένων στυλ κελιών.
 * Συγκεκριμένα, έχουν δημιουργηθεί οι απαραίτητες μέθοδοι για διαφορετικά είδη κελιών
 * που χρησιμοποιούνται για την δημιουργία των απαραίτητων excel αρχείων.
 */
public class CellStyles {
    
    /**
     * Όλες οι μέθοδοι λαμβάνουν ως είσοδο ένα αντικείμενο τύπου XSSFWorkbook και
     * επιστρέφουν ένα αντικείμενο τύπου CellStyle.
     * 
     * @param workbook Αντικείμενο τύπου XSSFWorkbook που θα λάβει την τροποποίηση.
     * @return style Το αντικείμενο τύπου CellStyle.
     */
    public CellStyle getTemplateStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
     /**
      * @param workbook Αντικείμενο τύπου XSSFWorkbook που θα λάβει την τροποποίηση.
      * @return style Το αντικείμενο τύπου CellStyle.
      */
    public CellStyle getDateHeadersStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.NONE);
        style.setBorderRight(BorderStyle.NONE);
        return style;
    }
    
    /**
     * @param workbook Αντικείμενο τύπου XSSFWorkbook που θα λάβει την τροποποίηση.
     * @return style Το αντικείμενο τύπου CellStyle.
     */
    public CellStyle getDateValuesStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.NONE);
        style.setBorderRight(BorderStyle.NONE);
        //XSSFColor headerCells = new XSSFColor(Color.decode("#A9A9A9"), new DefaultIndexedColorMap());
        //style.setFillForegroundColor(headerCells);
        return style;
    }
    
    /**
     * @param workbook Αντικείμενο τύπου XSSFWorkbook που θα λάβει την τροποποίηση.
     * @return style Το αντικείμενο τύπου CellStyle.
     */
    public CellStyle getTimeslotsHeadersStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.NONE);
        style.setBorderRight(BorderStyle.NONE);
        return style;
    }
    
    /**
     * @param workbook Αντικείμενο τύπου XSSFWorkbook που θα λάβει την τροποποίηση.
     * @return style Το αντικείμενο τύπου CellStyle.
     */
    public CellStyle getNoBorderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.NONE);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.NONE);
        style.setBorderRight(BorderStyle.NONE);
        style.setFillForegroundColor(IndexedColors.CORAL.index);
        return style;
    }
    
    /**
      * @param workbook Αντικείμενο τύπου XSSFWorkbook που θα λάβει την τροποποίηση.
      * @return style Το αντικείμενο τύπου CellStyle.
      */
    public CellStyle getFinalScheduleBasicStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        font.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }
}