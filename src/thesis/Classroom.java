package thesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
 * Η κλάση Classroom ευθύνεται για την αποθήκευση στοιχείων αιθουσών
 *
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */
public class Classroom implements Serializable{
    private String classroomCode;
    private String classroomName;
    private int classroomSeats;
    private boolean isLabClassroom;
    private List<Availability> availability;
    
    Classroom(String a, String b, int c, String d){
        classroomCode = a;
        classroomName = b;
        classroomSeats = c;
        if (d.equals("+")){
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
     * Η μέθοδος εκτυπώνει ολόκληρο το φύλλο διαθεσιμότητας για την αίθουσα
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