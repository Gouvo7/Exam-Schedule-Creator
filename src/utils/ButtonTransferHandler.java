package thesis;

import javax.swing.TransferHandler;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import javax.swing.JComponent;

/**
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * Η κλάση ButtonTransferHandler υλοποιεί την κλάση TramsferHandler και ευθύνεται για την μετακίνηση των
 * αντικειμένων των μαθημάτων ως JButtons σε κελία στον πίνακα excel της φόρμας.
 */
public class ButtonTransferHandler extends TransferHandler {
    private final String buttonText;

    public ButtonTransferHandler(String buttonText) {
        this.buttonText = buttonText;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return new StringSelection(buttonText);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }
}
