package entity;

import infra.Audio;
import infra.Collidable;
import infra.Direction;
import infra.Enemy;
import infra.Entity;
import infra.Resource;
import infra.Underground;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import scene.Stage;

/**
 * Harpoon class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Harpoon extends Entity<Stage> implements Collidable {
    
    private static final int SPEED = 4;
    
    private final BufferedImage image;
    private final Rectangle collider;
    private double sourceX;
    private double sourceY;
    private double x;
    private double y;
    private Direction direction;
    private double angle;
    private boolean firing;
    private double length;
    private Enemy enemy;
    
    public Harpoon(Stage stage) {
        super(stage);
        image = Resource.getImage("harpoon");
        collider = new Rectangle();
        zorder = 3;
    }

    @Override
    public Rectangle getCollider() {
        return collider;
    }

    public boolean isFiring() {
        return firing;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    @Override
    public void update() {
        if (!firing || enemy != null) {
            return;
        }
        // take small steps to avoid tunneling problems with the terrain
        for (int i = 0; i < SPEED; i++) {
            int l = (length > 16 * 3) ? 16 * 3 : (int) length;
            x = sourceX + (l + 5) * direction.getDx();
            y = sourceY + (l + 5) * direction.getDy();

            length += 1;
            if (length > 16 * 4 
                    || !Underground.canMoveTo((int) x, (int) y, direction, 2)) {

                firing = false;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!firing) {
            return;
        }
        
        //g.setColor(Color.MAGENTA);
        //g.fillOval((int) (x - 3), (int) (y - 3), 6, 6);
        
        AffineTransform ato = g.getTransform();
        g.translate(sourceX, sourceY);
        g.rotate(angle);
        g.translate(0, -8);
        int l = (length > 16 * 3)? 16 * 3 : (int) length;
        int dx1 = 8;
        int dy1 = 0;
        int dx2 = dx1 + l;
        int dy2 = 16;
        int sx1 = 64 - l;
        int sy1 = 0;
        int sx2 = 64;
        int sy2 = 16;
        g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        g.setTransform(ato);
        
        // --- draw collider ---
        //updateCollider();
        //g.draw(collider);        
    }
    
    public void fire(double x, double y, Direction direction) {
        this.sourceX = x;
        this.sourceY = y;
        this.x = x;
        this.y = y;
        this.direction = direction;
        angle = Math.atan2(direction.getDy(), direction.getDx());
        length = 0;
        firing = true;
        Audio.playSound("harpoon");
    }

    @Override
    public void updateCollider() {
        if (!firing) {
            collider.setBounds(0, 0, 0, 0);
            return;
        }
        collider.setBounds((int) (x - 3), (int) (y - 3), 6, 6);
    }

    @Override
    public boolean collides(Collidable otherCollider) {
        updateCollider();
        otherCollider.updateCollider();
        return collider.intersects(otherCollider.getCollider());
    }

    @Override
    public void onCollision(Collidable other) {
        if (enemy == null && other instanceof Enemy) {
            Enemy enemyTmp = (Enemy) other;
            if (enemyTmp.isAlive() && !enemyTmp.isDestroyed()) {
                enemy = enemyTmp;
                enemy.hitByHarpoon();
                enemy.getDigdug().startPumping();
            }
        }
        
    }

    public void cancel() {
        destroyed = false;
        firing = false;
        enemy = null;
    }
    
}
