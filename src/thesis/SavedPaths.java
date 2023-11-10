package thesis;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author gouvo
 */
public class SavedPaths {
    //static String importFolderPath = "C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ";
    //static String importFilePath = "C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\1) General.xlsx";
    static String importFolderPath = "C:\\Users\\dbuser1\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ";
    static String importFilePath = "C:\\Users\\dbuser1\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\1) General.xlsx";
    static String importFilePath1 = "C:\\Users\\dbuser1\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\TEST2.xlsx";
    static String importFilePath2 = "C:\\Users\\dbuser1\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\TEST3.xlsx";
    
    
    public void SavedPaths(){
        
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
}