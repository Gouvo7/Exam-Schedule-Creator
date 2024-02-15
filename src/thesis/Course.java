package thesis;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση Course αντιπροσωπεύει κάθε μάθημα του τμήματος. Περιλαμβάνει πληροφορίες
 * όπως το όνομα του μαθήματος, τη συντομογραφία του, το εξάμηνο στο οποίο διδάσκεται,
 * εάν βρίσκεται σε χειμερινό ή εαρινό εξάμηνο, καθώς και αν εξετάζεται. Αποθηκεύει
 * επίσης τους εξεταστές του μαθήματος καθώς και τις αίθουσες διεξαγωγής της εξέτασης.
 */

public class Course{
    private String courseName;
    private String courseShort;
    private String courseSem;
    private String courseSeason;
    private boolean isExamined;
    private List<Professor> examiners;
    private List<Classroom> classrooms;
    private int approxStudents;

    /**
     * Κατασκευαστής για τη δημιουργία ενός νέου αντικειμένου Course.
     *
     * @param name Το όνομα του μαθήματος (String).
     * @param nameShort Η συντομογραφία του μαθήματος (String).
     * @param semester Το εξάμηνο διεξαγωγής του μαθήματος (String).
     * @param season Περίοδος εξέστασης (String 'ΕΑΡΙΝΟ'/'ΧΕΙΜΕΡΙΝΟ').
     * @param isExamined Εάν εξετάζεται το μάθημα ή όχι(boolean, true για '+', false για '-').
     */
    public Course(String name, String nameShort, String semester, String season, boolean isExamined){
        courseName = name;
        courseShort = nameShort;
        courseSem = semester;
        courseSeason = season;
        isExamined = isExamined;
        examiners = new ArrayList<>();
        classrooms = new ArrayList<>();
        approxStudents = 0;
    }
    
    /**
     * Ένας κατασκευαστής (copy-constrctor) ο οποίος δημιουργεί ένα νέο αντικείμενο
     * τύπου Cousrse με τιμές από ένα άλλο αντικείμενο Course.
     * 
     * @param course Αντικείμενο τύπου Course από το οποίο θα αντληθούν τα στοιχεία
     * που θα θέσουμε στο νέο αντικείμενο Course.
     */
    public Course(Course course){
        courseName = course.getCourseName();
        courseShort = course.getCourseShort();
        courseSem = course.getCourseSem();
        courseSeason = course.getCourseSeason();
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
    
    public void setCourseSeason(String s) {
        this.courseSeason = s;
    }
    
    public String getCourseSeason(){
        return this.courseSeason;
    }
    
    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    public int getApproxStudents() {
        return approxStudents;
    }

    public void setApproxStudents(int approxStudents) {
        this.approxStudents = approxStudents;
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
