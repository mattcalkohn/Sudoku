/**
 *   RedirectingMenuItem allows for the menu items
 *      to send the action to the applet in order to
 *      be processed there without requiring a Frame
 */
import java.awt.*;

public class RedirectingMenuItem extends MenuItem {

    private Component event_handler;

    public RedirectingMenuItem(Component event_handler, String label) {
        super(label);
        this.event_handler = event_handler;
    }

    public boolean postEvent(Event e) {
        if (event_handler.isValid())
            return event_handler.postEvent(e);
        else return false;
    }

}
