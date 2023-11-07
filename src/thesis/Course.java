package thesis;

/**
 *
 * @author gouvo
 */

public class Course {
    private String courseName;
    private String courseShort;
    private String courseSem;
    private boolean isExamined;
    
    Course(String a, String b, String c, boolean d){
        courseName = a;
        courseShort = b;
        courseSem = c;
        isExamined = d;
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
    
    
}
