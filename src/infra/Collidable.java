package infra;

import java.awt.Rectangle;

/**
 * Collidable interface.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public interface Collidable {
    
    public Rectangle getCollider();
    public void updateCollider();
    public boolean collides(Collidable otherCollider);
    public void onCollision(Collidable other);
    
}
