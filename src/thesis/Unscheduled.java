package thesis;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gouvo
 */
public class Unscheduled {
    private List<Course> courses;
    private String msg;
    
    Unscheduled(){
        courses = new ArrayList<>();
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
}
