package models;

import java.util.ArrayList;
import java.util.List;
import thesis.Availability;
import thesis.Availability;

/** 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση Classroom αντιπροσωπεύει κάθε αίθουσα διδασκαλίας. Περιλαμβάνει στοιχεία
 * όπως τον κωδικό, το όνομα, τον αριθμό των θέσεων και αν πρόκειται για αίθουσα 
 * θεωρίας ή εργαστηρίου. Επίσης, διαχειρίζεται τη διαθεσιμότητα της αίθουσας.
 */
public class Classroom{
    private String classroomCode;
    private String classroomName;
    private int classroomSeats;
    private boolean isLabClassroom;
    private List<Availability> availability;
    
    /**
     * Κατασκευαστής για τη δημιουργία ενός νέου αντικειμένου Classroom.
     *
     * @param code Ο κωδικός της αίθουσας (String).
     * @param name Το όνομα της αίθουσας (String).
     * @param seats Ο αριθμός των θέσεων στην αίθουσα (integer > 0).
     * @param type Τύπος αίθουσας (char '+' για εργαστηριακή, αλλιώς '-' για κανονική).
     */
    public Classroom(String code, String name, int seats, String type){
        classroomCode = code;
        classroomName = name;
        classroomSeats = seats;
        if (type.equals("+")){
            isLabClassroom = true;
        }else{
            isLabClassroom = false;
        }
        availability = new ArrayList<Availability>();
    }
    
    public Classroom(Classroom cls){
        if(cls != null){
            classroomCode = cls.getClassroomCode();
            classroomName = cls.getClassroomName();
            classroomSeats = cls.getClassroomSeats();
            isLabClassroom = cls.getClassroomType();
            List<Availability> availability = new ArrayList<Availability>(cls.getAvailabilityList());
        }
    }
    
    public String getClassroomCode(){
        return classroomCode;
    }
    
    public void setClassroomCode(String x){
        this.classroomCode = x;
    }
    
    public String getClassroomName(){
        return classroomName;
    }
    
    public void setClassroomName(String x){
        this.classroomName = x;
    }
    
    public int getClassroomSeats(){
        return classroomSeats;
    }
    
    public void setClassroomSeats(int x){
        this.classroomSeats = x;
    }
    
    public boolean getClassroomType(){
        return isLabClassroom;
    }
    
    public void setClassroomType(boolean x){
        this.isLabClassroom = x;
    }
    
    public void setAvailability(List<Availability> availability) {
        this.availability = availability;
    }
    
    public List<Availability> getAvailabilityList(){
        return this.availability;
    }
    
    /**
     * Η μέθοδος επιστρέφει την διαθεσιμότητα μίας αίθουσας για συγκεκριμένη
     * ημερομηνία και χρονική περίοδο
     * @param date Η ημερομηνία της διαθεσιμότητας προς έλεγχο
     * @param timeslot Η χρονική περίοδος της διαθεσιμότητας προς έλεγχο
     * @return 0 όταν η αίθουσα δεν είναι διαθέσιμη 
     *         1 όταν η αίθουσα είναι διαθέσιμη
     */
    public int isAvailable(String date, String timeslot){
        date.trim();
        timeslot.trim();
        for (Availability a : availability){
            if (a.getDate().equals(date) && a.getTimeSlot().equals(timeslot)){
                if(a.getIsAvailable() == 0){
                    return 0;
                }else if (a.getIsAvailable() == 1){
                    return 1;
                }else if (a.getIsAvailable() == 2){
                    return 2;
                }
            }
        }
        return -1;
    }
    
    /**
     * Εκτυπώνει την πλήρη διαθεσιμότητα της αίθουσας στην κονσόλα.
     * Η έξοδος περιλαμβάνει την ημερομηνία, το χρονικό διάστημα και τη διαθεσιμότητα
     * για κάθε διάστημα. Εκτυπώνεται το όνομα και ο κωδικός της αίθουσας
     * καθώς και αν είναι διαθέσιμη ή όχι για κάθε διάστημα.
     */
    public void printClassroomAvailability(){
        System.out.println("Διαθεσιμότητα αίθουσας " + this.getClassroomName() + " / " + this.getClassroomCode()+ ":");
        String tmp = null;
        for (Availability x : availability){
            if (x.getIsAvailable() == 1){
                tmp = "Ναι";
            }else{
                tmp = "Όχι";
            }
            System.out.println(x.getDate() + " " + x.getTimeSlot() + " - " + tmp);
            tmp = "";
        }
    }
}