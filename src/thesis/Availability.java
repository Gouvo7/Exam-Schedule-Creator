package thesis;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση Availability χειρίζεται την αποθήκευση και διαχείριση της διαθεσιμότητας
 * για καθηγητές και αίθουσες. Χρησιμοποιεί έναν ακέραιο για να εκφράσει τη διαθεσιμότητα,
 * όπου 0 σημαίνει μη διαθέσιμο και 1 διαθέσιμο.
 */
public class Availability{

    private String date;
    private String timeSlot;
    private int isAvailable;

    public Availability(String x, String y, int z) {
        date = x;
        timeSlot = y;
        isAvailable = z;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(int isAvailable) {
        this.isAvailable = isAvailable;
    }
}