package infra;

import infra.renderer.AnimationPlayer;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import scene.Stage;

/**
 * Actor class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public abstract class Actor extends Entity<Stage> implements Collidable {

    protected double x;
    protected double y;
    protected final StateManager<Actor> stateManager;
    protected final AnimationPlayer animationPlayer;
    protected final Rectangle collider;
    protected double colliderRadius = 5;
    protected boolean alive = true;
    
    public Actor(Stage stage, String spriteResource, int spriteSize) {
        super(stage);
        animationPlayer = new AnimationPlayer(spriteResource, spriteSize);
        stateManager = new StateManager<>();
        collider = new Rectangle();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public double getColliderRadius() {
        return colliderRadius;
    }

    public void setColliderRadius(double colliderRadius) {
        this.colliderRadius = colliderRadius;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public AnimationPlayer getAnimationPlayer() {
        return animationPlayer;
    }

    @Override
    public Rectangle getCollider() {
        return collider;
    }

    public boolean isAlive() {
        return alive;
    }
    
    @Override
    public void update() {
        stateManager.update();
        animationPlayer.update();
    }

    @Override
    public void draw(Graphics2D g) {
        stateManager.draw(g);
        animationPlayer.draw(g, (int) x, (int) y);
    }    

    @Override
    public void updateCollider() {
        collider.setBounds((int) (x - colliderRadius)
                , (int) (y - colliderRadius), (int) (2 * colliderRadius)
                , (int) (2 * colliderRadius));
    }
    
    @Override
    public boolean collides(Collidable otherCollider) {
        updateCollider();
        otherCollider.updateCollider();
        return collider.intersects(otherCollider.getCollider());
    }

    @Override
    public void onCollision(Collidable other) {
        // implement your code here
    }

    /**
     * Idle (State) class.
     */
    protected class Idle extends State<Actor> {
        
        public Idle(StateManager stateManager, Actor owner) {
            super(stateManager, "idle", owner);
        }

        @Override
        public void onEnter() {
            owner.getAnimationPlayer().play("idle");
        }

    }
    
}
