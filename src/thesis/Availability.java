package thesis;

import thesis.Professor;

/**
 *
 * @author gouvo
 */

/*
int isAvailable
isAvailable == 0 -> Η/Ο αίθουσα/καθηγητής δεν είναι διαθέσιμη/ος/η
isAvailable == 1 -> Ο καθηγητής έχει δηλώσει πως μπορεί.
isAvailable == 2 -> Ο καθηγητής έχει δηλώσει πως μπορεί αλλά έχει δεσμευθεί από άλλο μάθημα.
 */
public class Availability {

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
