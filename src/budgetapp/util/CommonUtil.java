package budgetapp.util;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * This class contains common methods needed throughout the project.
 */
public class CommonUtil {
    
    /**
     * This method sets a status message for a label.
     * 
     * @param label - the label to set
     * @param message - the message to display
     * @param success - if true, message is in green, otherwise red
     */
    public final static void displayMessage(Label label, String message, boolean success) {
        label.setText(message);
        label.setTextFill(success? Color.GREEN : Color.RED);
        label.setAlignment(Pos.CENTER);
    }
}
