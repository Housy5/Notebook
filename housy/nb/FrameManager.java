package housy.nb;

import java.awt.Component;
import java.awt.Window;

public class FrameManager {
    
    public static void open(Window window, Component relative) {
        if (window instanceof Updatable win) {
            win.update();
        }
        window.setLocationRelativeTo(relative);
        window.setVisible(true);
    }
    
}
