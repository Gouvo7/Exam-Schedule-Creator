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
}
