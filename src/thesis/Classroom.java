package thesis;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gouvo
 */

public class Classroom {
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
    
    public void prinAvailable(){
        for (Availability x : availability){
            System.out.println(x.getDate() +  x.getTimeSlot() + x.getIsAvailable());
        }
    }
}
