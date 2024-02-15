package thesis;

import com.formdev.flatlaf.FlatLightLaf;
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
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel( new FlatLightLaf() );

            MainForm a = new MainForm();
        //} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
          } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}