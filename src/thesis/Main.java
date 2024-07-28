package thesis;

import com.formdev.flatlaf.FlatLightLaf;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            UIManager.setLookAndFeel(new FlatLightLaf());
            MainForm a = new MainForm();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}