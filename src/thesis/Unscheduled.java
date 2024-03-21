package thesis;

import java.util.ArrayList;
import java.util.List;

/**
 * Η κλάση χρησιμοποιείται για την αποθήκευση των μαθημάτων που δεν έχουν τοποθετηθεί στον πίνακα excel
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */
public class Unscheduled {

    private List<Course> courses;
    private String msg;
    
    Unscheduled(){
        courses = new ArrayList<>();
    }
    
    Unscheduled(Unscheduled un){
        courses = new ArrayList<>(un.getCourses());
        msg = un.getMsg();
    }
    
    Unscheduled(List<Course> courses){
        this.courses = courses;
    }
    
    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
    
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public void removeCourseFromUnscheduledList(Course crs){
        for(Course tmp : courses){
            if (tmp.getCourseName().equals(crs.getCourseName()) && tmp.getCourseSem().equals(crs.getCourseSem())){
                courses.remove(tmp);
                return;
            }
        }
    }
    
    public void addCourseToUnscheduledList(Course crs){
        if (!courses.contains(crs)){
            courses.add(crs);
        }
    }
    
    public void printCourses(){
        for(Course crs : courses){
            System.out.println("Unscheduled - CourseName: " + crs.getCourseName());
        }
    }
}
