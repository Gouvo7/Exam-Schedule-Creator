package thesis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 *
 * @author gouvo
 */
public class CourseClassroomsPanel extends javax.swing.JPanel {

    private ScheduledCourse scheduledCourse;
    private int totalSeats = 0;
    private Dimension dimensionsCheckboxesPanels;
    private Dimension dimensionsFrame;
    
    public CourseClassroomsPanel(ScheduledCourse sc, List<Classroom> cls, String dt, String ts) {
        initComponents();
        this.scheduledCourse = sc;
        int classroomsRows = ((scheduledCourse.getClassrooms().size())/2) + 1;
        this.setLayout(new BorderLayout());
        //this.setLayout(new GridBagLayout());
        dimensionsFrame = new Dimension(280, 200);
        dimensionsCheckboxesPanels = new Dimension(200, 30 * classroomsRows);
        jPanel1.setLayout(new GridLayout(classroomsRows ,2, 20, 0));
        jPanel1.setSize(dimensionsCheckboxesPanels);
        createComponents();
    }

    public ScheduledCourse getSc() {
        return scheduledCourse;
    }

    public void setSc(ScheduledCourse sc) {
        this.scheduledCourse = sc;
    }

    public ScheduledCourse getCourse() {
        return scheduledCourse;
    }

    public void setCourse(ScheduledCourse sc) {
        this.scheduledCourse = sc;
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
    
    private void createComponents() {
        String courseName = scheduledCourse.getScheduledCourse().getCourseName() + " '" + scheduledCourse.getScheduledCourse().getCourseSem() + "' (" + scheduledCourse.getScheduledCourse().getApproxStudents() + ")";
        String htmlMsg = "<html><body style='width: %1spx'>%2s</body></html>";
        String res1 = String.format(htmlMsg, 400, courseName);
        
        String courseDetails = "Περιόδου: " + scheduledCourse.getScheduledCourse().getCourseSeason() + "     Εξάμηνο: " + scheduledCourse.getScheduledCourse().getCourseSem();
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
            classroomCheckbox.addActionListener(e -> {
                if (classroomCheckbox.isSelected()) {
                    totalSeats += classroom.getClassroomSeats();
                } else {
                    totalSeats -= classroom.getClassroomSeats();
                }
                updateTotalSeatsDisplay();
            });
            jPanel1.add(classroomCheckbox);
        }
        this.add(lblCourseName, BorderLayout.NORTH);
        this.add(jPanel1, BorderLayout.CENTER);
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
            .addGap(0, 220, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );

        totalSeatsLabel.setText("Συνολικές καταχωρημένες θέσεις:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCourseName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(totalSeatsLabel)
                    .addContainerGap(24, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCourseName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(213, Short.MAX_VALUE)
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
