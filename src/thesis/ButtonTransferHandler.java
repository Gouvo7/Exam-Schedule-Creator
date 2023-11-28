package thesis;

import javax.swing.TransferHandler;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;

/**
 *
 * @author gouvo
 */

public class ButtonTransferHandler extends TransferHandler {
    private final String courseName;

    public ButtonTransferHandler(String courseName) {
        this.courseName = courseName;
    }

    @Override
    protected Transferable createTransferable(javax.swing.JComponent c) {
        // Transfer the course name as a StringSelection
        return new StringSelection(courseName);
    }

    @Override
    public int getSourceActions(javax.swing.JComponent c) {
        return TransferHandler.MOVE;
    }
}
