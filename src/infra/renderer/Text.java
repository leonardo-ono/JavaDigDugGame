package infra.renderer;

import infra.Resource;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Text (renderer) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Text {
    
    private static final Font FONT;
    
    static {
        FONT = Resource.getFont("PressStart2P-Regular").deriveFont(8.0f);
    }
    
    public static void draw(
            Graphics2D g, String text, int col, int row, Color color) {
        
        g.setFont(FONT);
        g.setColor(color);
        g.drawString(text, col * 8, (row + 1) * 8);
    }
    
}
