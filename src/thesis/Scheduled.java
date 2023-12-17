package thesis;

import java.util.ArrayList;
import java.util.List;

/**
 * Η κλάση χρησιμοποιείται για την αποθήκευση των μαθημάτων που έχουν τοποθετηθεί στον πίνακα excel
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */
public class Scheduled {
    
    private List<Course> courses;
    private String date, timeslot;
    
    Scheduled(){
        courses = new ArrayList<>();
        date = null;
        timeslot = null;
    }

    public void addCourse(Course course, String date, String timeslot){
        courses.add(course);
        this.date = date;
        this.timeslot = timeslot;
    }
    
    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }
    
}
