package infra.renderer;

import entity.BonusPoint;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * BonusPoints class.
 * 
 * - show above player
 * - if food was picked up, the color is #0068de
 * - if the enemy was killed, the color is #00b8de
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class BonusPoints {
    
    private static final int MAX_NUMBER_OF_SCORE_POINTS = 5;
    private static final List<BonusPoint> POINTS = new ArrayList<>();
    
    public static final int FOOD_PICKED_UP = 0;
    public static final int ENEMY_KILLED = 1;
    
    static {
        for (int i = 0; i < MAX_NUMBER_OF_SCORE_POINTS; i++) {
            POINTS.add(new BonusPoint());
        }
    }
    
    public static void update() {
        POINTS.forEach(point -> point.update());
    }

    public static void draw(Graphics2D g) {
        POINTS.forEach(point -> point.draw(g));
    }
    
    // color = 0 or 1: 0 when food is picked up and 1 if enemy was killed
    public static void show(int point, int x, int y, int colorType) {
        for (BonusPoint bonusPoint : POINTS) {
            if (bonusPoint.isFree()) {
                bonusPoint.spawn(point, x, y, colorType);
                return;
            }
        }
    }
    
    public static void hideAll() {
        POINTS.forEach(point -> point.hide());
    }

}
