package thesis;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση Logs είναι υπεύθυνη για την καταγραφή ενός ιστορικού (log) όλων των
 * προβλημάτων που μπορεί να αντιμετωπίσει ο χρήστης κατά την λειτουργία του προγράμματος.
 */
public class Logs{
    
    private String log;
    private int index;
    
    public Logs(){
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
        index = 0;
    }
}
