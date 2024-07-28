package thesis;

import models.Course;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση ExamCoursesFromFinalSchedule αντιπροσωπεύει ένα μάθημα μαζί με την θέση
 * στην οποία βρίσκεται στο διαβασμένο πρόγραμμα εξεταστικής.
 */
public class ExamCoursesFromFinalSchedule {

    private Course course;
    private int rowIndex;
    private int colIndex;   
    
    /**
     * Κατασκευαστής για την αρχικοποίηση ενός αντικειμένου ExamCoursesFromFinalSchedule 
     * με συγκεκριμένο μάθημα και θέση.
     *
     * @param crs Το μάθημα (Course)
     * @param x Η γραμμή που βρίσκεται το μάθημα στο πρόγραμμα (integer)
     * @param y Η στήλη που βρίσκεται το μάθημα στο πρόγραμμα (integer)
     */
    public ExamCoursesFromFinalSchedule(Course crs, int x, int y){
        course = new Course(crs);
        rowIndex = x;
        colIndex = y;
    }
    
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course crs) {
        this.course = crs;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }     
}