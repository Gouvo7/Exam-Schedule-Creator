package utils;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση LoadingPanel αναπαριστά το παράθυρο 
 */
public class LoadingPanel extends javax.swing.JPanel {

    /**
     * Creates new form LoadingPanel
     */
    public LoadingPanel() {
        initComponents();
        setLayout(new BorderLayout());
        Icon loadingIcon = new ImageIcon(getClass().getResource("/Assets/loading.gif"));
        JLabel lblLoading = new JLabel("Παρακαλώ περιμένετε...", loadingIcon, SwingConstants.CENTER);
        lblLoading.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblLoading.setHorizontalTextPosition(SwingConstants.CENTER);
        
        Font newFont = new Font("Arial", Font.BOLD, 54); // Example: Arial, Bold, 18pt
        lblLoading.setFont(newFont);

        add(lblLoading, BorderLayout.CENTER);
        setOpaque(true);
        //this.setBackground(new Color(255, 255, 255, 1));
        this.show();
        this.setSize(1200,750);
        this.repaint();
        this.revalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
