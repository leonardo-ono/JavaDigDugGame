package infra.renderer;

import infra.Direction;
import static infra.Direction.*;
import infra.Resource;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Sprite class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Sprite {
    
    private final int sizeX;
    private final int sizeY;
    private final int originX;
    private final int originY;
    
    private final BufferedImage image;
    private int frameIndex;
    private Direction direction;
    private double angle = 0;
    
    public Sprite(String resource, int size) {
        this(resource, size, size);
    }
    
    public Sprite(String resource, int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.originX = sizeX / 2;
        this.originY = sizeY / 2;
        this.image = Resource.getImage(resource);
        frameIndex = 0;
    }
    
    public void setFrame(int frameIndex) {
        this.frameIndex = frameIndex;
    }
    
    public void draw(Graphics2D g, int x, int y) {
        int dx1 = 0;
        int dy1 = 0;
        int dx2 = sizeX;
        int dy2 = sizeY;
        if (direction == LEFT || direction == DOWN) {
            dx1 = sizeX;
            dx2 = 0;
        }
        int sx1 = frameIndex * sizeX;
        int sy1 = 0;
        int sx2 = sx1 + sizeX;
        int sy2 = sizeY;
        AffineTransform oat = g.getTransform();
        g.translate(originX + x - sizeX / 2, originY + y - sizeY / 2);
        g.rotate(angle);
        g.translate(-originX, -originY);
        g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        g.setTransform(oat);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        if (direction == RIGHT || direction == LEFT) {
            angle = 0;
        }
        else if (direction == UP || direction == DOWN) {
            angle = Math.toRadians(-90);
        }
    }
    
}
