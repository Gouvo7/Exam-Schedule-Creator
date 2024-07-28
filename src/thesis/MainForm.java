package thesis;

import forms.ScheduleManager;
import java.awt.Cursor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import models.Course;
import org.gmele.general.sheets.exception.SheetExc;
import utils.Utilities;

/**
 * Η κλάση MainForm είναι η κύρια κλάση του προγράμματος.
 * Αποτελείται από ένα κεντρικό μενού, ένα απλό παράθυρο εφαρμογής που χρησιμοποιεί μια πολύ απλή και φιλική διεπαφή χρήστη. Διαχειρίζεται την ροή του προγράμματος.
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 */
public class MainForm extends javax.swing.JFrame {

    String folderPath;
    Definitions def;
    Utilities utils;
    
    public MainForm() throws IOException{
        loadSettings();
        utils = new Utilities();
        folderPath = def.getFolderPath();
        initComponents();
        jTextField1.setText(folderPath);
        this.setLocationRelativeTo(null);
        this.show(true);
    }
    
    public void loadSettings(){
        def = new Definitions();
        def.startProcess();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        changeDirectoryBtn = new javax.swing.JButton();
        produceTemplateBtn = new javax.swing.JButton();
        loadUIBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Σχεδιαστής Προγράμματος Εξετάσεων");
        setResizable(false);

        jLabel4.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        jLabel4.setText("Φάκελος αρχείων:");

        jTextField1.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        jTextField1.setToolTipText("");
        jTextField1.setEnabled(false);

        changeDirectoryBtn.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        changeDirectoryBtn.setText("Αλλαγή φάκελου δεδομένων");
        changeDirectoryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeDirectoryBtnActionPerformed(evt);
            }
        });

        produceTemplateBtn.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        produceTemplateBtn.setText("Δημιουργία Αρχείων Διαθεσιμότητας");
        produceTemplateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                produceTemplateBtnActionPerformed(evt);
            }
        });

        loadUIBtn.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        loadUIBtn.setText("Δημιουργία/Επεξεργασία Προγράμματος Εξετάσεων");
        loadUIBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadUIBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(changeDirectoryBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(produceTemplateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(125, 125, 125))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(loadUIBtn)
                        .addGap(73, 73, 73))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addComponent(changeDirectoryBtn)
                .addGap(18, 18, 18)
                .addComponent(produceTemplateBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(loadUIBtn)
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeDirectoryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeDirectoryBtnActionPerformed
        def.promptUserForFolder();
        jTextField1.setText(def.getFolderPath());
    }//GEN-LAST:event_changeDirectoryBtnActionPerformed

    private void produceTemplateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_produceTemplateBtnActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ExcelManager excelManager = new ExcelManager(this, def);
        excelManager.readGenericExcel();
        try {
            excelManager.createExcels();
        } catch (IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SheetExc ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_produceTemplateBtnActionPerformed

    private void loadUIBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadUIBtnActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ExcelManager excelManager = new ExcelManager(this, def);
        if(excelManager.readGenericExcel()){
            if (excelManager.readAvailabilityTemplates()){
                ScheduleManager a = new ScheduleManager(excelManager);
                if (def.examScheduleFileExists()){
                    List<Course> list = new ArrayList<>(excelManager.getCourses());
                    list = utils.filterOutNotExaminedCourses(list);
                    list = utils.filterOutCoursesWithNoExaminers(list);
                    if(!a.readExamScheduleExcel(list)){
                        setCursor(Cursor.getDefaultCursor());
                        if (JOptionPane.showConfirmDialog(this, "Σφάλμα κατά την ανάγνωση των δεδομένων από το αρχείο του προγράμματος εξεταστικής." +
                            " Θέλετε να ξεκινήσετε ένα νέο κενό παράθυρο;", "Σφάλμα εφαρμογής", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                            ScheduleManager b = new ScheduleManager(excelManager);
                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            b.startProcess(true);
                            //this.dispose();
                        }else{
                            return;
                        }
                    }else{
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        a.startProcess(false);
                    }
                } else {
                    setCursor(Cursor.getDefaultCursor());
                    if (JOptionPane.showConfirmDialog(this, "Δεν βρέθηκε στο μονοπάτι: '" + def.getFolderPath() + " ' το" +
                        " αρχείο: '" + def.getExamScheduleFile() + "'. Θέλετε να ξεκινήσετε ένα νέο κενό παράθυρο;", "Σφάλμα εφαρμογής", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        a.startProcess(true);
                        //this.dispose();
                    }else{
                        return;
                    }
                }
            }
        }
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_loadUIBtnActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton changeDirectoryBtn;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton loadUIBtn;
    private javax.swing.JButton produceTemplateBtn;
    // End of variables declaration//GEN-END:variables
}