package infra;

import java.awt.Graphics2D;

/**
 * Entity class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Entity<T extends Scene> implements Comparable<Entity> {
    
    protected String id;
    protected T scene;
    protected boolean destroyed; 
    protected int zorder;
    
    public Entity(T scene) {
        this.scene = scene;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getScene() {
        return scene;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public int getZorder() {
        return zorder;
    }
    
    public void destroy() {
        destroyed = true;
    }
        
    public void init() {
    }
    
    public void update() {
    }

    public void draw(Graphics2D g) {
    }    

    @Override
    public int compareTo(Entity o) {
        return zorder - o.zorder;
    }
    
}
