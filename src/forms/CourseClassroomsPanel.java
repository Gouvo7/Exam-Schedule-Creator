package thesis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author gouvo
 */
public class CourseClassroomsPanel extends javax.swing.JPanel {

    private ScheduledCourse scheduledCourse;
    private List<Classroom> selectedClassrooms;
    private CustomJPanel jpanel;
    private int totalSeats = 0;
    private Dimension dimensionsCheckboxesPanels;
    private Dimension dimensionsFrame;
    private JLabel jPanelLabel;
    private ImageIcon jPanelIcon;

    
    public CourseClassroomsPanel(CourseClassroomsPanel ccp) {
        scheduledCourse = ccp.scheduledCourse;
        selectedClassrooms = ccp.getSelectedClassrooms();
    }
    
    public CourseClassroomsPanel(ScheduledCourse sc, List<Classroom> cls, String dt, String ts) {
        initComponents();
        scheduledCourse = sc;
        selectedClassrooms = new ArrayList<>();
        int classroomsCount = scheduledCourse.getClassrooms().size();
        int classroomsRows = 0;
        if(classroomsCount >= 0){
            classroomsRows = ((scheduledCourse.getClassrooms().size())/2) + 1;
        }
        this.setLayout(new BorderLayout());
        dimensionsFrame = new Dimension(340, 200);
        dimensionsCheckboxesPanels = new Dimension(200, 30 * classroomsRows);
        jPanel1.setLayout(new GridLayout(classroomsRows ,2, 20, 0));
        jPanel1.setSize(dimensionsCheckboxesPanels);
        
        jpanel = new CustomJPanel();
        
        totalSeatsLabel = new JLabel("Συνολικές καταχωρημένες θέσεις: "+ totalSeats + " θέσεις");
        jPanelIcon = new ImageIcon(getClass().getResource("/Assets/caution.png"));
        jpanel.setLayout(new BorderLayout());
        jpanel.setName("bottom_panel");
        createNewComponents();
    }

    public ScheduledCourse getScheduledCourse() {
        return scheduledCourse;
    }

    public void setScheduledCourse(ScheduledCourse sc) {
        this.scheduledCourse = sc;
    }

    public List<Classroom> getSelectedClassrooms() {
        return selectedClassrooms;
    }

    public void setSelectedClassrooms(List<Classroom> selectedClassrooms) {
        this.selectedClassrooms = selectedClassrooms;
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

    public String getScheduledCourseName(){
        return this.scheduledCourse.getScheduledCourse().getCourseName();
    }
    
    public String getScheduledCourseShortName(){
        return this.scheduledCourse.getScheduledCourse().getCourseShort();
    }
    
    private void createNewComponents() {
        String courseName = scheduledCourse.getScheduledCourse().getCourseName();
        String htmlMsg = "<html><div style='width: 40px; word-wrap: break-word'</body></html>";
        String res1 = String.format(htmlMsg, 100, courseName);
        
        String courseDetails = "<html>&nbsp;Περιόδου: " + scheduledCourse.getScheduledCourse().getCourseSeason() +
                           "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Εξάμηνο: " + scheduledCourse.getScheduledCourse().getCourseSem() +
                           "<br>&nbsp;Εκτιμώμενος αριθμός μαθητών: " + scheduledCourse.getScheduledCourse().getApproxStudents() + "</html>";
        lblCourseName.setText(courseDetails);
        lblCourseName.setForeground(Color.GRAY);
        for (Classroom classroom : scheduledCourse.getClassrooms()) {
            JCheckBox classroomCheckbox = new JCheckBox(classroom.getClassroomName() + " (" + classroom.getClassroomSeats() + ")");
            int res = classroom.isAvailable(scheduledCourse.getDate(), scheduledCourse.getTimeslot());
            if (res == 1){
                classroomCheckbox.setEnabled(true);
            }else{
               classroomCheckbox.setEnabled(false);
            }
            classroomCheckbox.putClientProperty("classroom", classroom);
            classroomCheckbox.addActionListener(e -> {
                if (classroomCheckbox.isSelected()) {
                    selectedClassrooms.add(classroom);
                    totalSeats += classroom.getClassroomSeats();
                } else {
                    selectedClassrooms.remove(classroom);
                    totalSeats -= classroom.getClassroomSeats();
                }
                updateTotalSeatsDisplay();
                updateIcon();
            });
            jPanel1.add(classroomCheckbox);
        }
//        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Assets/accept.png"));
//        Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
//        ImageIcon scaledIcon = new ImageIcon(scaledImage);
//        JLabel imageLabel = new JLabel(scaledIcon);
//        JLabel imageLabel1 = new JLabel("Συνολικές καταχωρημένες θέσεις: 0 θέσεις");
//        jpanel.add(imageLabel, BorderLayout.CENTER);
//        jpanel.add(imageLabel1, BorderLayout.WEST);
        //jpanel.
        this.add(lblCourseName, BorderLayout.NORTH);
        this.add(jPanel1, BorderLayout.CENTER);
        //this.add(jpanel, BorderLayout.SOUTH);
        this.add(totalSeatsLabel, BorderLayout.SOUTH);
        this.setSize(dimensionsFrame);
        this.setPreferredSize(dimensionsFrame);
        this.setMaximumSize(dimensionsFrame);
        
        this.setBorder(BorderFactory.createTitledBorder(courseName));
        this.revalidate();
        this.repaint();
    }

    private void updateTotalSeatsDisplay() {
        totalSeatsLabel.setText("Συνολικές καταχωρημένες θέσεις: "+ totalSeats + " θέσεις");
    }
    
    private void updateIcon(){
        if(totalSeats < scheduledCourse.getNeededSeats()){
            
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblCourseName = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        totalSeatsLabel = new javax.swing.JLabel();

        lblCourseName.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );

        totalSeatsLabel.setText("Συνολικές καταχωρημένες θέσεις: 0 θέσεις");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblCourseName, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(totalSeatsLabel)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCourseName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(218, Short.MAX_VALUE)
                    .addComponent(totalSeatsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(2, 2, 2)))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCourseName;
    private javax.swing.JLabel totalSeatsLabel;
    // End of variables declaration//GEN-END:variables
}

class CustomJPanel extends JPanel{
    String lblTotalSeats = "Συνολικός αριθμός εκτιμώμενων μαθητών: ";
    int intTotalSeats = 0;
    CustomJPanel(String tmp1, String tmp2){
        
    }
    CustomJPanel(){
        

    }
}