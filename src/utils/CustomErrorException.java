package utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση CustomErrorException χρησιμοποιείται για να ενημερώσουμε τον χρήστη
 * για κάποιο σφάλμα σε κάποια διαδικασία. Από την κλάση αυτή έχουμε την δυνατότητα
 * να τερματίσουμε ή να συνεχίσουμε την ροή του προγράμματος.
 */

public class CustomErrorException extends Exception{
    
    /**
    * Η εμφανίζει ένα μήνυμα λάθους με συγκεκριμένο format για οποιδήποτε string ως παράμετρο.
    * 
    * @param myJFrame Το αντικείμενο JFrame που χρησιμοποιείται από το βασικό
    * πρόγραμμα. (JFrame).
    * @param msg Το μήνυμα που επιθυμούμε να εμφανιστεί (String).
    */
    public CustomErrorException(JFrame myJFrame, String msg, boolean stopExecution){
        String htmlMsg = "<html><body style='width: %1spx'>%1s";
        htmlMsg = String.format(htmlMsg, 600, msg);
        JOptionPane.showMessageDialog(myJFrame, htmlMsg, "Μήνυμα λάθους", JOptionPane.ERROR_MESSAGE);
        if(stopExecution){
            System.exit(-1);
        }
    }
}