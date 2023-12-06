package thesis;

import javax.swing.TransferHandler;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import javax.swing.JComponent;

/**
 * @author gouvo
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
