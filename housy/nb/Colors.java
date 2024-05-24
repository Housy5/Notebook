package housy.nb;

import java.awt.Color;

public class Colors {
    
    final static Color[] backgroundColors = new Color[6];
    
    static {
        backgroundColors[0] = new Color(0xff7eb9);
        backgroundColors[1] = new Color(0xff65a3);
        backgroundColors[2] = new Color(0x7afcff);
        backgroundColors[3] = new Color(0xfeff9c);
        backgroundColors[4] = new Color(0xfff740);
    }
    
    final static Color DEFAULT_BG_COLOR = backgroundColors[3];
    final static Color DEFAULT_FG_COLOR = Color.GRAY;
    
    static Color getSaveEffectColor(Color color) {
        return new Color(0x05c3DD);
    }
    
    static double calculateLuminance(Color c) {
        double r = c.getRed();
        double g = c.getGreen();
        double b = c.getBlue();
        
        return ((0.299 * r) + (0.587 * g) + (0.114 * b)) / 255;
    }

}
