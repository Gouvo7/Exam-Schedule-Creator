package thesis;

import java.util.ArrayList;
import java.util.List;

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
    Classroom(String code, String name, int seats, String type){
        classroomCode = code;
        classroomName = name;
        classroomSeats = seats;
        if (type.equals("+")){
            isLabClassroom = true;
        }else{
            isLabClassroom = false;
        }
        availability = new ArrayList<>();
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