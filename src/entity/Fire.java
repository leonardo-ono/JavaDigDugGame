package entity;

import infra.Audio;
import infra.Collidable;
import infra.Direction;
import infra.Entity;
import infra.Resource;
import infra.Underground;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import scene.Stage;

/**
 * (Fygar's) Fire class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Fire extends Entity<Stage> implements Collidable {
    
    private static final double SPEED = 0.5;
    
    private final BufferedImage[] images;
    private final Rectangle collider;
    private double sourceX;
    private double sourceY;
    private double x;
    private double y;
    private Direction direction;
    private double angle;
    private boolean firing;
    private double frame;
    private int availableLength;
    private boolean hit;
    
    public Fire(Stage stage) {
        super(stage);
        BufferedImage image = Resource.getImage("fygar_fire");
        images = new BufferedImage[3];
        images[0] = image.getSubimage(0, 0, 16, 16);
        images[1] = image.getSubimage(16, 0, 32, 16);
        images[2] = image.getSubimage(48, 0, 48, 16);
        collider = new Rectangle();
        zorder = 5;
    }

    @Override
    public Rectangle getCollider() {
        return collider;
    }

    public boolean isFiring() {
        return firing;
    }

    @Override
    public void update() {
        if (!firing || hit) {
            return;
        }
        frame += SPEED;
        if (frame >= 3) {
            firing = false;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!firing) {
            return;
        }
        
        AffineTransform ato = g.getTransform();
        g.translate(sourceX, sourceY);
        g.rotate(angle);
        g.translate(0, -8);
        
        boolean flipX = direction == Direction.DOWN;
        boolean flipY = direction == Direction.LEFT;
        
        int imageMaxWidth = ((int) frame + 1) * 16;
        int l = availableLength > imageMaxWidth 
                ? imageMaxWidth : availableLength;
        
        int dx1 = 8;
        int dy1 = 0;
        int dx2 = dx1 + l;
        int dy2 = 16;
        int sx1 = 0;
        int sy1 = 0;
        int sx2 = l;
        int sy2 = 16;
        
        if (flipX || flipY) {
            int tmp = sy1;
            sy1 = sy2;
            sy2 = tmp;
        }
        
        g.drawImage(images[(int) frame]
                , dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        
        g.setTransform(ato);
        
        // --- draw collider ---
        //updateCollider();
        //g.draw(collider);
    }
    
    public void fire(double x, double y, Direction direction) {
        this.sourceX = x;
        this.sourceY = y;
        this.direction = direction;
        angle = Math.atan2(direction.getDy(), direction.getDx());
        firing = true;
        hit = false;
        frame = 0;
        availableLength = 0;
        do {
            int l = (availableLength > 16 * 3) ? 16 * 3 : availableLength;
            x = sourceX + (l + 5) * direction.getDx();
            y = sourceY + (l + 5) * direction.getDy();
            availableLength += 1;
        }
        while (availableLength < 16 * 3 
                && Underground.canMoveTo((int) x, (int) y, direction, 2));
        //System.out.println("availableLength = " + availableLength);
        Audio.playSound("fygar_fire");
    }

    @Override
    public void updateCollider() {
        if (!firing) {
            collider.setBounds(0, 0, 0, 0);
            return;
        }
        
        int imageMaxWidth = ((int) frame + 1) * 16;
        int l = availableLength > imageMaxWidth 
                ? imageMaxWidth : availableLength;
        switch (direction) {
            case LEFT:
                collider.setBounds(
                        (int) (sourceX - 8 - l), (int) (sourceY - 8), l, 16);
                break;
            case RIGHT: 
                collider.setBounds(
                        (int) (sourceX + 8), (int) (sourceY - 8), l, 16);
                break;
            case UP: 
                collider.setBounds(
                        (int) (sourceX - 8), (int) (sourceY - 8 - l), 16, l);
                break;
            case DOWN: 
                collider.setBounds(
                        (int) (sourceX - 8), (int) (sourceY + 8), 16, l);
                break;
        }
    }

    @Override
    public boolean collides(Collidable otherCollider) {
        updateCollider();
        otherCollider.updateCollider();
        return collider.intersects(otherCollider.getCollider());
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof DigDug) {
            DigDug digdug = (DigDug) other;
            if (digdug.isAlive() && !digdug.isDestroyed()) {
                digdug.hitByEnemy();
                hit = true;
            }
        }
    }

    public void cancel() {
        firing = false;
        destroyed = false;
    }
    
}
