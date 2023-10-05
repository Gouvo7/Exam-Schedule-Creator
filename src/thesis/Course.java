package thesis;

/**
 *
 * @author gouvo
 */

public class Course {
    static String courseName;
    static String courseShort;
    static String courseSem;
    static boolean isExamined;
    
    Course(String a, String b, String c, boolean d){
        courseName = a;
        courseShort = b;
        courseSem = c;
        isExamined = d;
    }
}
