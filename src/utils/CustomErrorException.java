package thesis;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author gouvo
 */
public class CustomErrorException extends Exception{
    /**
    * Η συνάρτηση εμφανίζει ένα μήνυμα λάθους με συγκεκριμένο format για 
    * οποιδήποτε string ως παράμετρο.
    * @param myJFrame Το αντικείμενο JFrame που χρησιμοποιείται από το βασικό
    * πρόγραμμα. (JFrame).
    * @param msg Το μήνυμα που επιθυμούμε να εμφανιστεί (String).
    */
    public CustomErrorException(JFrame myJFrame, String msg) throws CustomErrorException{
        try{
            String htmlMsg = "<html><body style='width: %1spx'>%1s";
            htmlMsg = String.format(htmlMsg, 600, msg);
            JOptionPane.showMessageDialog(myJFrame, htmlMsg, "Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
            return;
        }catch(Exception e){
        }
    }
}