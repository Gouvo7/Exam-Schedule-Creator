package thesis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Υλοποιεί την διεπαφή Comparator και είναι υπεύθυνη για την σύγκριση δύο συμβολοσειρών
 * που αναπαριστούν ημερομηνίες με την μορφή 'ηη/μμ/εεεε'.
 */
public class DateComparator implements Comparator<String> {
    /**
     * Η κλάση DateComparator συγκρίνει δύο συμβολοσειρές ημερομηνιών με βάση τα
     * αντίστοιχα αντικείμενα Date τους.
     * @param dateString1 Η πρώτη συμβολοσειρά ημερομηνίας προς σύγκριση (String).
     * @param dateString2 Η δεύτερη συμβολοσειρά ημερομηνίας προς σύγκριση (String).
     * @return ένας ακέραιος που δηλώνει τη σχέση των δύο ημερομηνιών: 
     *      α) αρνητικός αν η πρώτη είναι πριν 
     *      β) θετικός αν είναι μετά 
     *      γ) μηδέν αν είναι ίσες (integer)
     */
    @Override
    public int compare(String dateString1, String dateString2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date1 = dateFormat.parse(dateString1);
            Date date2 = dateFormat.parse(dateString2);
            return date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
