package thesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.util.IOUtils;
import org.gmele.general.sheets.exception.SheetExc;

/**
 * Η κλάση MainForm είναι η κλάση που χρησιμοποιείται για το γραφικό περιβάλλον 
 * (user interface - UI) της εφαρμογής. (Προς το παρόν διαχειρίζεται και όλο το πρόγραμμα).
 * @author gouvo
 */
public class MainForm extends javax.swing.JFrame {

    /**
     * Ο κατασκευαστής της κεντρικής φόρμας MainForm.
     */
    public MainForm(){
        initComponents();
        this.setLocationRelativeTo(null);
        this.show(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        loadBtn = new javax.swing.JButton();
        produceBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        jLabel1.setText("Output Path");

        jLabel4.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        jLabel4.setText("Input Path");

        jTextField1.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        jTextField1.setText("C:\\\\Users\\\\gouvo\\\\OneDrive\\\\Documents\\\\ΠΤΥΧΙΑΚΗ\\\\1) General.xlsx");
        jTextField1.setToolTipText("");
        jTextField1.setEnabled(false);

        jTextField2.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        jTextField2.setText("C:\\\\Users\\\\gouvo\\\\OneDrive\\\\Documents\\\\ΠΤΥΧΙΑΚΗ\\\\");
            jTextField2.setEnabled(false);

            loadBtn.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
            loadBtn.setLabel("Load Excels");
            loadBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    loadBtnActionPerformed(evt);
                }
            });

            produceBtn.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
            produceBtn.setLabel("Produce Excel");
            produceBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    produceBtnActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(103, 103, 103)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel4)
                        .addComponent(jLabel1))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextField2)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(layout.createSequentialGroup()
                    .addGap(247, 247, 247)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(produceBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(loadBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(25, 25, 25)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                    .addComponent(produceBtn)
                    .addGap(74, 74, 74)
                    .addComponent(loadBtn)
                    .addGap(65, 65, 65))
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void loadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBtnActionPerformed
        String path = jTextField1.getText();
        System.out.println(path);
        ExcelManager a = new ExcelManager(this,path);
        try {
            boolean hasRead = a.readTemplates();
            if (hasRead){
                SceduleManager b = new SceduleManager(a);
            }else { //throw new SheetExc(); 
                System.out.println("Ta gamhsame");
            }
            System.out.println("Edw mpainw?");
        } catch (SheetExc ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_loadBtnActionPerformed

    private void produceBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_produceBtnActionPerformed
        String path = jTextField1.getText();
        System.out.println(path);
        ExcelManager a = new ExcelManager(this,path);
        a.createExcels();
    }//GEN-LAST:event_produceBtnActionPerformed

    public static void main(String[] args) throws FileNotFoundException, IOException {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        /**
         * Εμφάνιση και εκτέλεση του κώδικα της φόρμας.
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JButton loadBtn;
    private javax.swing.JButton produceBtn;
    // End of variables declaration//GEN-END:variables
    
}


