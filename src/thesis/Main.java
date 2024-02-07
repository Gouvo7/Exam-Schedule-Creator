package thesis;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 *
 * Η κλάση MainForm είναι η κύρια κλάση του προγράμματος. Αποτελείται από ένα 
 * κεντρικό μενού, ένα απλό παράθυρο εφαρμογής που χρησιμοποιεί μια πολύ απλή 
 * και φιλική διεπαφή χρήστη. Διαχειρίζεται την ροή του προγράμματος.
 */
public class Main {
    
    public static void main (String[] args){
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            MainForm a = new MainForm();
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
