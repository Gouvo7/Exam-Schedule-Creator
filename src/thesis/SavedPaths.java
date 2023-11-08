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
    //static final String importFolderPath = "C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ";
    //static final String importFilePath = "C:\\Users\\gouvo\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\1) General.xlsx";
    static String importFolderPath = "C:\\Users\\dbuser1\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ";
    static String importFilePath = "C:\\Users\\dbuser1\\OneDrive\\Documents\\ΠΤΥΧΙΑΚΗ\\1) General.xlsx";
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
}