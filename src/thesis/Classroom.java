package thesis;

/**
 *
 * @author gouvo
 */

public class Classroom {
    private String classroomCode;
    private String classroomName;
    private int classroomSeats;
    private boolean isLabClassroom;
    
    Classroom(String a, String b, int c, String d){
        classroomCode = a;
        classroomName = b;
        classroomSeats = c;
        if (d.equals("+")){
            isLabClassroom = true;
        }else{
            isLabClassroom = false;
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
}
