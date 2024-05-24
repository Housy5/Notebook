package housy.nb;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import javax.swing.JLabel;

public class Model implements Serializable {
    
    public static Font DEFAULT_FONT = new JLabel().getFont().deriveFont(Font.PLAIN, 14);
    
    public Font font = DEFAULT_FONT;
    public Color bgColor = Colors.DEFAULT_BG_COLOR;
    public Color fgColor = Color.GRAY;
    
}
