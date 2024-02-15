package thesis;

import java.io.Serializable;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση Availability ευθύνεται για την διαχείριση της διαθεσιμότητας καθηγητών
 * και αιθουσών. Το isAvailable λαμβάνει τιμές:
 * 0 - Όχι.
 * 1 - Ναι.
 */
public class Availability implements Serializable{

    private String date;
    private String timeSlot;
    private int isAvailable;

    Availability(String x, String y, int z) {
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
