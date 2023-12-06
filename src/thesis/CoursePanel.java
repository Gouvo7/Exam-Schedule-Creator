package thesis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

/**
 * @author gouvo
 */
public class CoursePanel extends JPanel {
    private Course course;

    public CoursePanel(Course course) {
        this.course = course;
        setPreferredSize(new Dimension(250, 40));
        setBackground(Color.BLUE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setTransferHandler(new TransferHandler(course.getCourseName()));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, e, TransferHandler.COPY);
            }
        });
    }

    public Course getCourse() {
        return course;
    }
}
