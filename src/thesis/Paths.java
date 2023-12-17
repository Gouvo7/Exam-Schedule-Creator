package thesis;


/**
 * Η κλάση Paths είναι υπεύθυνη για την αποθήκευση των πληροφοριών που αφορούν διαδρομές (paths) αρχείων ή φακέλων του προγράμματος
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */
public class Paths {
    static String importFolderPath = "C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ";
    static String importFilePath = "C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\1) General.xlsx";
    static String importFilePath1 = "C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\TEST2.xlsx";
    static String importFilePath2 = "C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\TEST3.xlsx";
    
    static String sheet1 = "PROFESSORS_LIST"; 
    static String sheet2 = "TIMESLOTS";
    static String sheet3 = "DATES";
    static String sheet4 = "CLASSROOMS";
    static String sheet5 = "COURSES_LIST";
    static String sheet6 = "COURSES_PROFESSORS";
    
    public void Paths(){
    }

    public void setImportFolderPath(String tmp){
        importFolderPath = tmp;
    }
    
    public String getImportFolderPath(){
        return importFolderPath;
    }
    
    public void setImportFilePath(String tmp){
        importFilePath = tmp;
    }
    
    public String getImportFilePath(){
        return importFilePath;
    }
    
    public String getImportFilePath1(){
        return importFilePath1;
    }
    public String getImportFilePath2(){
        return importFilePath2;
    }
    
    public String getSheet1() {
        return sheet1;
    }

    public static void setSheet1(String sheet1) {
        Paths.sheet1 = sheet1;
    }

    public String getSheet2() {
        return sheet2;
    }

    public static void setSheet2(String sheet2) {
        Paths.sheet2 = sheet2;
    }

    public String getSheet3() {
        return sheet3;
    }

    public static void setSheet3(String sheet3) {
        Paths.sheet3 = sheet3;
    }

    public String getSheet4() {
        return sheet4;
    }

    public static void setSheet4(String sheet4) {
        Paths.sheet4 = sheet4;
    }

    public String getSheet5() {
        return sheet5;
    }

    public static void setSheet5(String sheet5) {
        Paths.sheet5 = sheet5;
    }

    public String getSheet6() {
        return sheet6;
    }
    
    public static void setSheet6(String sheet6) {
        Paths.sheet6 = sheet6;
    }
}