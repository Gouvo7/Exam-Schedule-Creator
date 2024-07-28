package forms;

import models.Classroom;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import thesis.Logs;
import thesis.ScheduledCourse;

/** 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση CourseClassroomsPanel είναι ένα panel για κάθε μάθημα στο δεξί μέροες του.
 * παραθύρου του ScheduleManager. Τα παράθυρα αυτά ευθύνονται για την επιλογή των
 * αιθουσών που θα καταληφθούν για την εξέταση ενός μαθήματος.
 */
public class CourseClassroomsPanel extends javax.swing.JPanel {

    private ScheduledCourse scheduledCourse;
    private List<Classroom> selectedClassrooms;
    private CustomJPanel bottomPanel;
    private int totalSeats = 0;
    private Logs log;
    private List<Classroom> classrooms;
    private Dimension dimensionsCheckboxesPanels;
    private Dimension dimensionsFrame;
    
    /**
     * Ο constructor της κλάσης ο οποίος απαιτεί τις ακόλουθες παραμέτρους για την
     * αρχικοποίησή του. Στον constructor επίσης αρχικοποιούνται μεταβλητές, και
     * θέτουμε παραμέτρους για το panel.
     * @param sc Το προγραμματισμένο μάθημα (ScheduledCourse).
     * @param cls Η λίστα με όλες τις αίθουσες (List<Classroom).
     */
    public CourseClassroomsPanel(ScheduledCourse sc, List<Classroom> cls) {
        initComponents();
        
        scheduledCourse = sc;
        selectedClassrooms = new ArrayList<>();
        classrooms = List.copyOf(cls);
        int classroomsCount = scheduledCourse.getClassrooms().size();
        
        // Υπολογισμός των γραμμών των αιθουσών που θα καταλάβει η λίστα αιθουσών.
        int classroomsRows = 0;
        if(classroomsCount >= 0){
            classroomsRows = ((scheduledCourse.getClassrooms().size())/2) + 1;
        }
        this.setLayout(new BorderLayout());
        dimensionsFrame = new Dimension(340, 200);
        dimensionsCheckboxesPanels = new Dimension(200, 30 * classroomsRows);
        classroomsPanel.setLayout(new GridLayout(classroomsRows ,2, 20, 0));
        classroomsPanel.setSize(dimensionsCheckboxesPanels);
        totalSeatsLabel = new JLabel("Επιλεγμένες θέσεις: "+ totalSeats + " θέσεις");
        bottomPanel = new CustomJPanel(scheduledCourse.getCourse().getApproxStudents());
        bottomPanel.setName("bottom_panel");
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
    
    /**
     * Μέθοδος που καλείται για την δημιουργία του panel του προγραμματισμένου
     * μαθήματος.
     */
    private void createNewComponents() {
        String courseName = scheduledCourse.getScheduledCourse().getCourseName();
        String courseDetails = "<html>Ημερομηνία: " + scheduledCourse.getDate() + "&nbsp;&nbsp;Ώρα: " + scheduledCourse.getTimeslot() + 
                            "<br>&nbsp;Περιόδου: " + scheduledCourse.getScheduledCourse().getCourseSeason() +
                           "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Εξάμηνο: " + scheduledCourse.getScheduledCourse().getCourseSem() +
                           "<br>&nbsp;Επιλεγμένες θέσεις: " + scheduledCourse.getScheduledCourse().getApproxStudents() + "</html>";
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
            classroomCheckbox.putClientProperty("classroom", classroom.getClassroomName());
            classroomCheckbox.putClientProperty("seats", classroom.getClassroomSeats());
            classroomCheckbox.addActionListener(e -> {
                if (classroomCheckbox.isSelected()) {
                    selectedClassrooms.add(classroom);
                    totalSeats += classroom.getClassroomSeats();
                    bottomPanel.addSeats(classroom.getClassroomSeats());
                } else {
                    selectedClassrooms.remove(classroom);
                    totalSeats -= classroom.getClassroomSeats();
                    bottomPanel.removeSeats(classroom.getClassroomSeats());
                }
            });
            classroomsPanel.add(classroomCheckbox);
        }
        this.add(lblCourseName, BorderLayout.NORTH);
        this.add(classroomsPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
        this.setSize(dimensionsFrame);
        this.setPreferredSize(dimensionsFrame);
        this.setMaximumSize(dimensionsFrame);
        this.setBorder(BorderFactory.createTitledBorder(courseName));
        this.revalidate();
        this.repaint();
    }
    
    /**
    * Επιλέγει τις αίθουσες από τη λίστα των αιθουσών και ενημερώνει την κατάσταση επιλογής των
    * αντίστοιχων checkboxes στο γραφικό περιβάλλον. Επίσης, προσθέτει τον αριθμό θέσεων της
    * κάθε επιλεγμένης αίθουσας στον συνολικό αριθμό θέσεων.
    *
    * @param classroomsList Η λίστα των ονομάτων των αιθουσών που πρέπει να επιλεχθούν.
    */
    public void selectClassrooms(List<String> classroomsList){
        for(Component comp : classroomsPanel.getComponents()){
            if (comp instanceof JCheckBox){
                JCheckBox checkbox = (JCheckBox) comp;
                String tmp1 = (String) checkbox.getClientProperty("classroom");
                if(classroomsList.contains(tmp1)){
                    Classroom classroom = getClassroomFromName(tmp1);
                    String date = this.getScheduledCourse().getDate();
                    String timeslot = this.getScheduledCourse().getTimeslot();
                    if(classroom.isAvailable(date, timeslot) == 1){
                        checkbox.setSelected(true);
                        int seats = (int) checkbox.getClientProperty("seats");
                        bottomPanel.addSeats(seats);
                        totalSeats += seats;
                    }
                }
            }
        }
    }
    
    /**
     *  Μέθοδος που επιστρέφει την αίθουσα αποτυπωμένη με μορφή String στην λίστα
     * με τις αίθουσες. 
     * 
     * @param classroomName Το όνομα της αίθουσας (String).
     * @return Εάν το όνομα ταιριάζει, τότε επιστρέφει το αντικείμενο της αίθουσας,
     * αλλιώς, επιστρέφει null (Classroom).
     */
    public Classroom getClassroomFromName(String classroomName){
        for(Classroom cls : classrooms){
            if(cls.getClassroomName().equals(classroomName)){
                return cls;
            }
        }
        return null;
    }
    
    /**
     * Μέθοδος για αυτόματη επιλογή διαθέσιμων αιθουσών
     */
    public void autoSelectNeededClassrooms() {
        int studentsToTakeExam = scheduledCourse.getCourse().getApproxStudents();
        if(bottomPanel.getCurrSeats() >= studentsToTakeExam){
            return;
        }
        for (Component comp : classroomsPanel.getComponents()) {
            if (comp instanceof JCheckBox) {
                JCheckBox checkbox = (JCheckBox) comp;
                if (!checkbox.isSelected() && checkbox.isEnabled()) {
                    checkbox.setSelected(true); // Automatically select the checkbox
                    String classroomName = (String) checkbox.getClientProperty("classroom");
                    Classroom classroom = findClassroom(classroomName);
                    totalSeats += classroom.getClassroomSeats();
                    bottomPanel.addSeats(classroom.getClassroomSeats());
                    selectedClassrooms.add(classroom);
                    if (studentsToTakeExam <= totalSeats) {
                        break; // Stop once enough seats are selected.
                    }
                }
            }
        }
    }

    /**
     * Μέθοδος για έυρεση του αντικειμένου αίθουσας που αντιστοιχηθεί με το 
     * εισαγόμενο λεκτικό
     * @param classroomName Το όνομα της αίθουσα (String).
     * @return Το αντικείμενο Classroom σε περίπτωση που βρεθεί ή null.
     */
    public Classroom findClassroom(String classroomName){
        for(Classroom classroom : classrooms){
            if(classroom.getClassroomName().equals(classroomName)){
                return classroom;
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblCourseName = new javax.swing.JLabel();
        classroomsPanel = new javax.swing.JPanel();
        totalSeatsLabel = new javax.swing.JLabel();

        lblCourseName.setText("0");

        javax.swing.GroupLayout classroomsPanelLayout = new javax.swing.GroupLayout(classroomsPanel);
        classroomsPanel.setLayout(classroomsPanelLayout);
        classroomsPanelLayout.setHorizontalGroup(
            classroomsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        classroomsPanelLayout.setVerticalGroup(
            classroomsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );

        totalSeatsLabel.setText("Επιλεγμένες  θέσεις: 0 θέσεις");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblCourseName, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                    .addComponent(classroomsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(classroomsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(218, Short.MAX_VALUE)
                    .addComponent(totalSeatsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(2, 2, 2)))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel classroomsPanel;
    private javax.swing.JLabel lblCourseName;
    private javax.swing.JLabel totalSeatsLabel;
    // End of variables declaration//GEN-END:variables
}

/**
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Κλάση που χρησιμοποιείται για την δημιουργία των JPanels επιλογής αιθουσών.
 */
class CustomJPanel extends JPanel{
    String lblTotalSeats = "Επιλεγμένες θέσεις: ";
    int currSeats = 0;
    int neededSeats = 0;
    JLabel lblSeats;
    ImageIcon warningIcon;
    ImageIcon validIcon;
    JLabel lblIcon;

    CustomJPanel(int x){
        lblSeats = new JLabel();
        neededSeats = x;
        lblSeats.setText(getSeatsString());
        initIcons();
        initPanel();
    }
    
    public int getCurrSeats(){
        return currSeats;
    }
    
    public String getSeatsString(){
        return lblTotalSeats + currSeats;
    }
    
    /**
     * Μέθοδος για την προσθήκη θέσεων στο λεκτικό που απεικονίζει τον αριθμό 
     * δεσμευμένων θέσσεων κατά έναν χ αριθμό.
     * @param x Ο αριθμός των αιθουσών που θα αυξηθεί.
     */
    public void addSeats(int x){
        currSeats = currSeats + x;
        lblSeats.setText(getSeatsString());
        updateTextAndIcon();
    }
    
    /**
     * Μέθοδος για την αφαίρεση θέσεων στο λεκτικό που απεικονίζει τον αριθμό 
     * δεσμευμένων θέσσεων κατά έναν χ αριθμό.
     * @param x Ο αριθμός των αιθουσών που θα μειωθεί.
     */
    public void removeSeats(int x){
        currSeats = currSeats - x;
        lblSeats.setText(getSeatsString());
        updateTextAndIcon();
    }
    
    /**
     * Μέθοδος για την δημιουργία των εικονιδίων
     */
    public void initIcons(){
        ImageIcon originalIcon1 = new ImageIcon(getClass().getResource("/Assets/caution.png"));
        Image scaledImage1 = originalIcon1.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        warningIcon = new ImageIcon(scaledImage1);
        
        ImageIcon originalIcon2 = new ImageIcon(getClass().getResource("/Assets/accept.png"));
        Image scaledImage2 = originalIcon2.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        validIcon = new ImageIcon(scaledImage2);
        if(currSeats == 0){
            lblIcon = new JLabel(warningIcon);
        }else{
            lblIcon = new JLabel(validIcon);
        }
    }
    
    public void initPanel(){
        this.setLayout(new BorderLayout());
        this.add(lblSeats, BorderLayout.WEST);
        this.add(lblIcon, BorderLayout.EAST);
        this.repaint();
        this.revalidate();
    }
    
    public void changeIcon(ImageIcon imageIcon){
        lblIcon.setIcon(imageIcon);
        this.revalidate();
        this.repaint();
    }
    
    public void updateTextAndIcon(){
        if(neededSeats <= currSeats && currSeats > 0){
            if(this.lblIcon.getIcon() == validIcon){
                return;
            }
            changeIcon(validIcon);
        }else{
            if(this.lblIcon.getIcon() == warningIcon){
                return;
            }
            changeIcon(warningIcon);
        }
    }
}