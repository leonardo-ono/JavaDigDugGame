package infra.renderer;

import infra.GameInfo;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * HUD (renderer) class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class HUD {
    
    private static final Sprite LIVE_SPRITE;
    private static boolean blink1UP = true;
    
    static {
        LIVE_SPRITE = new Sprite("digdug", 16);
    }

    public static void setBlink1UP(boolean blink1UP) {
        HUD.blink1UP = blink1UP;
    }
    
    public static void draw(Graphics2D g) {
        if (!blink1UP || (blink1UP 
                && ((int) (System.nanoTime() * 0.000000002)) % 2 == 0)) {
            
            Text.draw(g, "1UP", 3 + 2, 0, Color.RED);
        }
        Text.draw(g, GameInfo.getScoreStr(), 1 + 2, 1, Color.WHITE);
        Text.draw(g, "HIGH SCORE", 9 + 2, 0, Color.RED);
        Text.draw(g, GameInfo.getHiscoreStr(), 11 + 2, 1, Color.WHITE);
        Text.draw(g, "ROUND", 22 + 2, 34, Color.WHITE);
        Text.draw(g, GameInfo.getStageStr(), 22 + 2, 35, Color.WHITE);
        LIVE_SPRITE.setFrame(2);
        for (int l = 0; l < GameInfo.getLives() - 1; l++) {
            LIVE_SPRITE.draw(g, l * 16 + 8 + 16, 280);
        }
    }
    
}
