package thesis;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JToolBar.Separator;

/**
 *
 * @author gouvo
 */
public class CourseClassroomsPanel extends JPanel {

    private ScheduledCourse sc;
    private Course course;
    private List<Classroom> classrooms;
    private String date;
    private String timeslot;
    private JLabel totalSeatsLabel;
    private int totalSeats = 0;

    public CourseClassroomsPanel(Course crs, List<Classroom> cls, String dt, String ts) {
        this.course = crs;
        this.classrooms = new ArrayList<>(cls);
        this.date = dt;
        this.timeslot = ts;
        this.sc = new ScheduledCourse(course, date, timeslot, classrooms);
        this.setLayout(new BorderLayout());
        createComponents();
    }
    
    public ScheduledCourse getSc() {
        return sc;
    }

    public void setSc(ScheduledCourse sc) {
        this.sc = sc;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
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

    public JLabel getTotalSeatsLabel() {
        return totalSeatsLabel;
    }

    public void setTotalSeatsLabel(JLabel totalSeatsLabel) {
        this.totalSeatsLabel = totalSeatsLabel;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    private void createComponents() {
        // Course label with number of students
        JLabel courseLabel = new JLabel(sc.getCourse().getCourseName() + " (" + sc.getCourse().getApproxStudents() + ")");
        courseLabel.setLayout(new FlowLayout(FlowLayout.LEADING));
        this.add(courseLabel, BorderLayout.NORTH);
        this.add(new Separator());

        // Panel for checkboxes
        JPanel checkboxesPanel = new JPanel();
        checkboxesPanel.setLayout(new BoxLayout(checkboxesPanel, BoxLayout.Y_AXIS));
        for (Classroom classroom : classrooms) {
            JCheckBox classroomCheckbox = new JCheckBox(classroom.getClassroomName() + " (" + classroom.getClassroomSeats() + ")");
            int res = classroom.isAvailable(date, timeslot);
            if (res == 1){
                classroomCheckbox.setEnabled(true);
            }else{
                classroomCheckbox.setEnabled(false);
            }
            classroomCheckbox.addActionListener(e -> {
                // Check if the classroom is selected or deselected
                if (classroomCheckbox.isSelected()) {
                    totalSeats += classroom.getClassroomSeats();
                } else {
                    totalSeats -= classroom.getClassroomSeats();
                }
                updateTotalSeatsDisplay();
            });
            checkboxesPanel.add(classroomCheckbox);
        }
        
        this.add(checkboxesPanel, BorderLayout.CENTER);
        
       //ImagePanel imagePanel1 = new ImagePanel("accept.png");
       //this.add(imagePanel1, BorderLayout.CENTER);
        // Total seats label
        totalSeatsLabel = new JLabel("Σύνολο: 0 θέσεις");
        this.add(totalSeatsLabel, BorderLayout.SOUTH);
        this.add(new Separator());
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane();
    }

    private void updateTotalSeatsDisplay() {
        totalSeatsLabel.setText("Σύνολο: " + totalSeats + " θέσεις");
    }
    
}

class ImagePanel extends JPanel {

public ImagePanel(String fileName) {
        try {
            BufferedImage image = ImageIO.read((getClass().getResource("/Assets/" + fileName)));
        } catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }

}