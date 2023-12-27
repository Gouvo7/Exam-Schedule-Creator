package thesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
 * Η κλάση Course χρησιμοποιείται για την αποθήκευση στοιχείων μαθημάτων καθώς και τους εξεταστές αυτών.
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */
public class Course implements Serializable{
    private String courseName;
    private String courseShort;
    private String courseSem;
    private boolean isExamined;
    private List<Professor> examiners;
    private List<Classroom> classrooms;
    
    Course(String a, String b, String c, boolean d){
        courseName = a;
        courseShort = b;
        courseSem = c;
        isExamined = d;
        examiners = new ArrayList<>();
        classrooms = new ArrayList<>();
    }
    
    Course(Course course){
        courseName = course.getCourseName();
        courseShort = course.getCourseShort();
        courseSem = course.getCourseSem();
        isExamined = course.getIsExamined();
        examiners = new ArrayList<>(course.getExaminers());
        classrooms = new ArrayList<>();
    }

    public Course getCourse(){
        return this;
    }
    
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseShort() {
        return courseShort;
    }

    public void setCourseShort(String courseShort) {
        this.courseShort = courseShort;
    }

    public String getCourseSem() {
        return courseSem;
    }

    public void setCourseSem(String courseSem) {
        this.courseSem = courseSem;
    }
    
    public boolean getIsExamined() {
        return isExamined;
    }
    
    public void setIsExamined(boolean isExamined) {
        this.isExamined = isExamined;
    }
    
    public List<Professor> getExaminers() {
        return examiners;
    }

    

    public void setExaminers(List<Professor> examiners) {
        this.examiners = examiners;
    }
    
    public void addExaminer(Professor prof){
        examiners.add(prof);
    }

    /**
     * Εκτύπωση μαθήματος και εξεταστών.
     */
    /*
    public void printStatistics(){
        int i = 0;
        System.out.println("Course is:" + this.getCourseName() + examiners.size());
        for (Professor prof : examiners){
            System.out.println("Professor " + i + ": " + prof.getProfFirstname());
        }
    }*/
}
