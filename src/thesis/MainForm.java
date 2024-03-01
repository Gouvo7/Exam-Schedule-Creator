package thesis;

import javax.swing.JOptionPane;

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
    
    public MainForm(){
        loadSettings();
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

        jLabel4.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        jLabel4.setText("Working Directory:");

        jTextField1.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        jTextField1.setText("C:\\\\Users\\\\gouvo\\\\OneDrive\\\\Documents\\\\ΠΤΥΧΙΑΚΗ\\\\1) General.xlsx");
        jTextField1.setToolTipText("");
        jTextField1.setEnabled(false);

        changeDirectoryBtn.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        changeDirectoryBtn.setText("Αλλαγή φάκελου");
        changeDirectoryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeDirectoryBtnActionPerformed(evt);
            }
        });

        produceTemplateBtn.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        produceTemplateBtn.setText("Δημιουργία Αρχείων Διαθεσιμότητας Καθηγητών");
        produceTemplateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                produceTemplateBtnActionPerformed(evt);
            }
        });

        loadUIBtn.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        loadUIBtn.setText("Επεξεργασία Προγράμματος");
        loadUIBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadUIBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(loadUIBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(produceTemplateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(changeDirectoryBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(242, 242, 242))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addComponent(changeDirectoryBtn)
                .addGap(40, 40, 40)
                .addComponent(produceTemplateBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(loadUIBtn)
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeDirectoryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeDirectoryBtnActionPerformed
        def.promptUserForFolder();
        jTextField1.setText(def.getFolderPath());
    }//GEN-LAST:event_changeDirectoryBtnActionPerformed

    private void produceTemplateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_produceTemplateBtnActionPerformed
        ExcelManager excelManager = new ExcelManager(this, def);
        excelManager.readGenericExcel();
        excelManager.createExcels();
    }//GEN-LAST:event_produceTemplateBtnActionPerformed

    private void loadUIBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadUIBtnActionPerformed
        
        ExcelManager excelManager = new ExcelManager(this, def);
        if(excelManager.readGenericExcel()){
            if (excelManager.readAvailabilityTemplates()){
                ScheduleManager b = new ScheduleManager(excelManager);
                if (def.examScheduleFileExists()){
                    if(!b.readExamSchedule()){
                        if (JOptionPane.showConfirmDialog(this, "Σφάλμα κατά την ανάγνωση των δεδομένων από το αρχείο του προγράμματος εξεταστικής." +
                            " Θέλετε να ξεκινήσετε ένα νέο κενό παράθυρο;", "Σφάλμα εφαρμογής", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                            b.startProcess(true);
                        }
                    }
                    b.startProcess(false);
                } else {
                    if (JOptionPane.showConfirmDialog(this, "Δεν βρέθηκε στο μονοπάτι: '" + def.getFolderPath() + " ' το" +
                        " αρχείο: '" + def.getExamScheduleFile() + "'. Θέλεις να ξεκινήσεις ένα πρόγραμμα εκ νέου;", "Σφάλμα εφαρμογής", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                        b.startProcess(true);
                    }
                }
            }else {
                //JOptionPane.showMessageDialog(this, "Πρόβλημα κατά την ανάγνωση των δεδομένων από τα συμπληρωμένα templates.", "Μήνυμα Εφαρμογής", JOptionPane.ERROR_MESSAGE);
            }
        }else {
            //JOptionPane.showMessageDialog(this, "Πρόβλημα κατά την ανάγνωση του βασικού αρχείου Excel.", "Μήνυμα Εφαρμογής", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_loadUIBtnActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton changeDirectoryBtn;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton loadUIBtn;
    private javax.swing.JButton produceTemplateBtn;
    // End of variables declaration//GEN-END:variables
}