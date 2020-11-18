package infra.renderer;

import infra.Resource;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Flowers class.
 * 
 * Renderer flowers according to current stage number.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Flowers {

    private static final int FLOWER_Y = 15;
    private static final BufferedImage[] IMAGES = new BufferedImage[3];
    private static int stage = 0;
    
    static {
        for (int i = 0; i < 3; i++) {
            IMAGES[i] = Resource.getImage("flower_" + i);
        }
    }

    public static int getStage() {
        return stage;
    }

    public static void setStage(int stage) {
        Flowers.stage = stage;
    }
    
    public static void draw(Graphics2D g) {
        int remainingStage = Flowers.stage;
        if (remainingStage > 68) {
            remainingStage = 68;
        }
        for (int i = 0; i < 14; i++) {
            BufferedImage image = null;
            if (remainingStage >= 10) {
                remainingStage -= 10;
                image = IMAGES[(remainingStage / 10) % 2];
            }
            else if (remainingStage > 0) {
                remainingStage--;
                image = IMAGES[2];
            }
            if (image == null) {
                break;
            }
            g.drawImage(image, (14 - i) * 16, FLOWER_Y, null);
        }
    }
    
}
