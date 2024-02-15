package thesis;

import java.util.ArrayList;
import java.util.List;

/**
 * Η κλάση χρησιμοποιείται για την αποθήκευση των μαθημάτων που έχουν τοποθετηθεί στον πίνακα excel
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */
public class ScheduledCourse {
    
    private Course course;
    private String date, timeslot;
    List<Classroom> classrooms;
    List<Classroom> selectedClassrooms;
    int currSeats;
    
    // if we set a course to be scheduled, whenever we check a classroom,
    // we need to also update the change of the classroom's availability
    // in the classrooms list but also in the 
    
    public ScheduledCourse(Course crs, String date, String timeslot, List<Classroom> clsrms){
        course = crs;
        this.date = date;
        this.timeslot = timeslot;
        if(clsrms!= null){
            classrooms = new ArrayList<>(clsrms);
        }
        this.selectedClassrooms = new ArrayList<>();
        currSeats = 0;
    }

    public void setCurrSeats(int currSeats) {
        this.currSeats = currSeats;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
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
    
    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    public List<Classroom> getSelectedClassrooms() {
        return selectedClassrooms;
    }

    public void setSelectedClassrooms(List<Classroom> selectedClassrooms) {
        this.selectedClassrooms = selectedClassrooms;
    }

    public int getCurrSeats() {
        return currSeats;
    }
}
