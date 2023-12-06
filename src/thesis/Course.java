package thesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
 * @author gouvo
 * Η κλάση Course χρησιμοποιείται για την αποθήκευση στοιχείων μαθημάτων καθώς και
 * τους εξεταστές αυτών.
 */

public class Course implements Serializable{
    private String courseName;
    private String courseShort;
    private String courseSem;
    private boolean isExamined;
    private List<Professor> examiners;
    
    Course(String a, String b, String c, boolean d){
        courseName = a;
        courseShort = b;
        courseSem = c;
        isExamined = d;
        examiners = new ArrayList<>();
    }
    
    Course(Course course){
        courseName = course.getCourseName();
        courseShort = course.getCourseShort();
        courseSem = course.getCourseSem();
        isExamined = course.isIsExamined();
        examiners = new ArrayList<>(course.getExaminers());
    }

    public Course getCourse(){
        return this;
    }
    
    /**
     * @return the courseName
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * @param courseName the courseName to set
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * @return the courseShort
     */
    public String getCourseShort() {
        return courseShort;
    }

    /**
     * @param courseShort the courseShort to set
     */
    public void setCourseShort(String courseShort) {
        this.courseShort = courseShort;
    }

    /**
     * @return the courseSem
     */
    public String getCourseSem() {
        return courseSem;
    }

    /**
     * @param courseSem the courseSem to set
     */
    public void setCourseSem(String courseSem) {
        this.courseSem = courseSem;
    }

    /**
     * @return the isExamined
     */
    public boolean isIsExamined() {
        return isExamined;
    }

    /**
     * @param isExamined the isExamined to set
     */
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
    
    public void printStatistics(){
        int i = 0;
        System.out.println("Course is:" + this.getCourseName() + examiners.size());
        for (Professor prof : examiners){
            System.out.println("Professor " + i + ": " + prof.getProfFirstname());
        }
    }
}
