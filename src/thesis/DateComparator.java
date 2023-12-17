package thesis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Η κλάση DateComparator υλοποιεί την κλάση Comparator και είναι υπεύθυνη για την σύγκριση δύο συμβολοσειρών που αναπαριστούν ημερομηνίες με το μορφότυπο  'ηη/μμ/εεεε'. Υλοποιεί την διεπαφή Comparator για την σύγκριση.
 *
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */
public class DateComparator implements Comparator<String> {
    /**
     * Συγκρίνει δύο συμβολοσειρές ημερομηνιών με βάση τα αντίστοιχα αντικείμενα Date τους.
     * @param dateString1 Η πρώτη συμβολοσειρά ημερομηνίας προς σύγκριση.
     * @param dateString2 Η δεύτερη συμβολοσειρά ημερομηνίας προς σύγκριση.
     * @return ένα αρνητικό ακέραιο, μηδέν ή έναν θετικό ακέραιο όπως η πρώτη ημερομηνία είναι λιγότερο, ίση ή μεγαλύτερη από τη δεύτερη.
     */
    @Override
    public int compare(String dateString1, String dateString2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            // Parse the date strings into Date objects
            Date date1 = dateFormat.parse(dateString1);
            Date date2 = dateFormat.parse(dateString2);

            // Compare the Date objects
            return date1.compareTo(date2);
        } catch (ParseException e) {
            // Handle parsing exception
            e.printStackTrace();
            return 0; // or throw an exception, depending on your requirements
        }
    }
}
