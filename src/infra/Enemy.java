package infra;

import entity.DigDug;
import entity.Harpoon;
import entity.Rock;
import static infra.Direction.DOWN;
import static infra.Direction.IDLE;
import static infra.Direction.LEFT;
import static infra.Direction.RIGHT;
import static infra.Direction.UP;
import infra.renderer.AnimationPlayer.Animation;
import infra.renderer.BonusPoints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import scene.Stage;

/**
 * Enemy base class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public abstract class Enemy extends Actor {
    
    protected final DigDug digdug;
    protected final Harpoon harpoon;
    protected Direction direction;
    protected Rock rock;
    
    protected double chasingSpeed = 1.05;
    protected double ghostSpeed = 0.5;
    protected double originalChasingSpeed = 1.05;
    protected double originalGhostSpeed = 0.5;
    
    protected int air = -1;
    protected long desinflateTime;
    protected double originalX;
    protected double originalY;
    protected double targetX;
    protected double targetY;
    protected boolean retreating;
    
    public Enemy(
            Stage stage, String spriteResource, int size, double x, double y) {
        
        super(stage, spriteResource, size);
        this.digdug = stage.getDigdug();
        this.harpoon = digdug.getHarpoon();
        this.x = x;
        this.y = y;
        this.originalX = x;
        this.originalY = y;
        direction = LEFT;
    }

    public double getOriginalX() {
        return originalX;
    }

    public double getOriginalY() {
        return originalY;
    }

    public double getChasingSpeed() {
        return chasingSpeed;
    }

    public void setChasingSpeed(double chasingSpeed) {
        this.chasingSpeed = chasingSpeed;
        this.originalChasingSpeed = chasingSpeed;
}

    public double getGhostSpeed() {
        return ghostSpeed;
    }

    public void setGhostSpeed(double ghostSpeed) {
        this.ghostSpeed = ghostSpeed;
        this.originalGhostSpeed = ghostSpeed;
    }

    @Override
    public void init() {
        animationPlayer.addAnimation("idle", 2, 2, 0, false);
        animationPlayer.addAnimation("walking", 2, 3, 0.1, true);
        animationPlayer.addAnimation("ghost", 4, 5, 0.1, true);
        animationPlayer.addAnimation("hit_by_rock", 6, 6, 0, false);
        animationPlayer.addAnimation("inflating", 7, 9, 0, false);
        animationPlayer.addAnimation("exploding", 10, 10, 0.1, false);
        stateManager.addState(new Idle(stateManager, this));
        stateManager.addState(new Chasing(stateManager, this));
        stateManager.addState(new Ghost(stateManager, this));
        stateManager.addState(new HitByRock(stateManager, this));
        stateManager.addState(new Inflating(stateManager, this));
        stateManager.addState(new Exploding(stateManager, this));
        stateManager.switchTo("idle");
    }

    public DigDug getDigdug() {
        return digdug;
    }

    public Rock getRock() {
        return rock;
    }
    
    /**
     * Chasing (State) class.
     */
    public class Chasing extends State<Enemy> {
        
        private double accumulatedSpeed;
        private boolean moving;
        private long ghostTime;
        
        public Chasing(StateManager stateManager, Enemy owner) {
            super(stateManager, "chasing", owner);
        }

        @Override
        public void onEnter() {
            direction = Direction.IDLE;
            moving = false;
            owner.getAnimationPlayer().play("walking");
            if (retreating) {
                ghostTime = System.currentTimeMillis() 
                        + 1000 + (int) (2000 * Math.random());
            }
            else {
                ghostTime = System.currentTimeMillis() 
                        + 3000 + (int) (3000 * Math.random());
            }
        }

        protected void updateTargetPosition() {
            if (retreating) {
                targetY = 32;
                targetX -= (0.25 + 0.25 * Math.random());
                if (targetX < 1 || (int) y == (int) targetY) {
                    targetX = 1;
                }
                if (targetX < 24 && (int) y > (int) targetY) {
                    targetX = 24;
                }
                if ((int) x == 8 && (int) y == 32) {
                    destroy();
                    GameInfo.removeEnemyId(id);
                }
            }
            else {
                targetX = digdug.getX();
                targetY = digdug.getY();
            }
        }
        
        @Override
        public void update() {
            updateTargetPosition();
            
            if (moving) {
                updateMoving();
            }
            else {
                chooseDirection();
            }
            
            long currentTime = System.currentTimeMillis();
            if (currentTime >= ghostTime) {
                if (!retreating || (retreating && y > 32)) {
                    owner.getStateManager().switchTo("ghost");
                }
                else {
                    ghostTime = System.currentTimeMillis() 
                            + 2000 + (int) (2000 * Math.random());
                }
            }             
        }

        private final List<Direction> availableDirections 
                = new ArrayList<Direction>();
        
        private final List<Direction> availableDirections2 
                = new ArrayList<Direction>();

        private void chooseDirection() {
            availableDirections.clear();

            if (Underground.canMoveTo(
                    (int) (owner.getX() - 1), (int) owner.getY(), LEFT, 6)) {
                
                availableDirections.add(LEFT);
            }
            if (Underground.canMoveTo(
                    (int) (owner.getX() + 1), (int) owner.getY(), RIGHT, 6)) {
                
                availableDirections.add(RIGHT);
            }
            if (Underground.canMoveTo(
                    (int) owner.getX(), (int) (owner.getY() - 1), UP, 6)) {
                
                availableDirections.add(UP);
            }
            if (Underground.canMoveTo(
                    (int) owner.getX(), (int) (owner.getY() + 1), DOWN, 6)) {
                
                availableDirections.add(DOWN);
            }

                
            availableDirections2.clear();

            // try to runaway if there is unstable rock above
            if (scene.checkUnstableRockAbove((int) x, (int) y)) {
                if (availableDirections.contains(LEFT)) {
                    availableDirections2.add(LEFT);
                }
                if (availableDirections.contains(RIGHT)) {
                    availableDirections2.add(RIGHT);
                }
                if (availableDirections.contains(DOWN)) {
                    availableDirections2.add(DOWN);
                }
            }
            else {
                if (direction != null 
                        && availableDirections.contains(direction.getOpposite()) 
                        && availableDirections.size() > 1) {

                    availableDirections.remove(direction.getOpposite());
                }        
                
                int difX = (int) (targetX - owner.getX());
                int difY = (int) (targetY - owner.getY());

                if (difX < 0 && availableDirections.contains(LEFT)) {
                    availableDirections2.add(LEFT);
                }
                if (difX > 0 && availableDirections.contains(RIGHT)) {
                    availableDirections2.add(RIGHT);
                }
                if (difY < 0 && availableDirections.contains(UP)) {
                    availableDirections2.add(UP);
                }
                if (difY > 0 && availableDirections.contains(DOWN)) {
                    availableDirections2.add(DOWN);
                }
            }

            if (availableDirections2.isEmpty()) {
                availableDirections2.addAll(availableDirections);
            }
            

            if (availableDirections2.size() > 0) {
                Collections.shuffle(availableDirections2);
                direction = availableDirections2.get(0);
            }
            else {
                direction = IDLE;
            }

            if (direction == LEFT || direction == RIGHT) {
                owner.getAnimationPlayer().setDirection(direction);
            }
            moving = true;
        }

        private void updateMoving() {
            accumulatedSpeed += chasingSpeed;
            while (accumulatedSpeed >= 1) {
                accumulatedSpeed -= 1;
                
                if (Underground.canMoveTo(
                    (int) (owner.getX() + direction.getDx())
                    , (int) (owner.getY() + direction.getDy()), direction, 6)) {

                    owner.translate(direction.getDx(), direction.getDy());
                }
                else {
                    moving = false;
                    break;
                }        

                if (Underground.isCrossing(
                        (int) owner.getX(), (int) owner.getY())) {

                    moving = false;
                    break;
                }
            }

        }

    }

    /**
     * Ghost (State) class.
     */
    private class Ghost extends State<Enemy> {
        
        private long endTime;
        
        public Ghost(StateManager stateManager, Enemy owner) {
            super(stateManager, "ghost", owner);
        }

        @Override
        public void onEnter() {
            owner.getAnimationPlayer().play("ghost");
            endTime = System.currentTimeMillis() + 3000
                    + (int) (3000 * Math.random());
        }
        
        protected void updateTargetPosition() {
            if (retreating) {
                targetY = 32;
                targetX -= (0.25 + 0.25 * Math.random());
                if (targetX < 1 || (int) y == (int) targetY) {
                    targetX = 1;
                }
                if (targetX < 24 && (int) y > (int) targetY) {
                    targetX = 24;
                }
                if ((int) x == 8 && (int) y == 32) {
                    destroy();
                    GameInfo.removeEnemyId(id);
                }
            }
            else {
                targetX = digdug.getX();
                targetY = digdug.getY();
            }
        }
        
        @Override
        public void update() {
            updateTargetPosition();
            
            double difX = targetX - x;
            double difY = targetY - y;
            
            x += Math.signum(difX) * ghostSpeed;
            y += Math.signum(difY) * ghostSpeed;

            //double distance2 = difX * difX + difY * difY;
            //distance2 < 50 * 50 &&
            
            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime 
                    && ((Underground.canMoveTo((int) x, (int) y, LEFT, 6)
                    && Underground.canMoveTo((int) x, (int) y, RIGHT, 6)) 
                    || (Underground.canMoveTo((int) x, (int) y, UP, 6)
                    && Underground.canMoveTo((int) x, (int) y, DOWN, 6)))) {
                
                owner.getStateManager().switchTo("chasing");
            }
            
            if (retreating && (int) y == 32) {
                owner.getStateManager().switchTo("chasing");
            }
        }

    }
    
    /**
     * HitByRock (State) class.
     */
    private class HitByRock extends State<Enemy> {
        
        public HitByRock(StateManager stateManager, Enemy owner) {
            super(stateManager, "hit_by_rock", owner);
        }

        @Override
        public void onEnter() {
            alive = false;
            owner.getAnimationPlayer().setDirection(Direction.RIGHT);
            owner.getAnimationPlayer().play("hit_by_rock");
            if (harpoon.getEnemy() == Enemy.this) {
                harpoon.cancel();
            }
        }
        
        @Override
        public void update() {
            y = rock.getY() + 6;
            if (!rock.isAlive()) {
                rock = null;
                destroy();
                GameInfo.removeEnemyId(id);
            }
        }

    }
    
    /**
     * Inflating (State) class.
     */
    private class Inflating extends State<Enemy> {
        
        private Animation animation;
        
        public Inflating(StateManager stateManager, Enemy owner) {
            super(stateManager, "inflating", owner);
        }

        @Override
        public void onEnter() {
            owner.getAnimationPlayer().play("inflating", true);
            owner.getAnimationPlayer().pause();
            animation = owner.getAnimationPlayer().getCurrentAnimation();
            desinflateTime = System.currentTimeMillis() + 1000;
        }
        
        @Override
        public void update() {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= desinflateTime) {
                desinflateTime = System.currentTimeMillis() + 1000;
                air -= 1;
            }
            
            if (air <= -1) {
                air = -1;
                
                if ((Underground.canMoveTo((int) x, (int) y, LEFT, 6)
                    && Underground.canMoveTo((int) x, (int) y, RIGHT, 6)) 
                    || (Underground.canMoveTo((int) x, (int) y, UP, 6)
                    && Underground.canMoveTo((int) x, (int) y, DOWN, 6))) {

                    owner.getStateManager().switchTo("chasing");
                }
                else {
                    owner.getStateManager().switchTo("ghost");
                }
                
                if (harpoon.getEnemy() == Enemy.this) {
                    harpoon.cancel();
                }
            }
            else {
                int airTmp = air < 0 ? 0 : air;
                animation.currentFrame = animation.startFrame + airTmp;
                if (animation.currentFrame > animation.endFrame) {
                    owner.getStateManager().switchTo("exploding");
                }
                else {
                    owner.getAnimationPlayer().getSprite()
                            .setFrame((int) animation.currentFrame);
                }
            }
        }

    }

    /**
     * Exploding (State) class.
     */
    private class Exploding extends State<Enemy> {
        
        public Exploding(StateManager stateManager, Enemy owner) {
            super(stateManager, "exploding", owner);
        }

        @Override
        public void onEnter() {
            alive = false;
            harpoon.cancel();
            owner.getAnimationPlayer().play("exploding", true);
            Audio.playSound("enemy_blown_out");
        }
        
        @Override
        public void update() {
            if (owner.getAnimationPlayer().isFinished()) {
                destroy();
                GameInfo.removeEnemyId(id);
                
                int point = getBonusPoint();
                BonusPoints.show(
                        point, (int) x, (int) y, BonusPoints.ENEMY_KILLED);
                GameInfo.addScore(point);
            }
        }

    }
    
    @Override
    public void onCollision(Collidable other) {
        if (other instanceof DigDug && digdug.isAlive()
            && !stateManager.getCurrentState().getName().equals("inflating")
            && !stateManager.getCurrentState().getName().equals("exploding")) {
            
            digdug.hitByEnemy();
            stateManager.switchTo("idle");
        }
    }

    public void hitByRock(Rock rock) {
        if (alive && !destroyed) {
            this.rock = rock;
            stateManager.switchTo("hit_by_rock");
        }
    }

    public void hitByHarpoon() {
        if (alive && !destroyed) {
            air += 1;
            stateManager.switchTo("inflating");
        }
    }

    public void inflate() {
        air += 1;
        desinflateTime = System.currentTimeMillis() + 1000;
    }
    
    public void retreat() {
        targetX = x;
        retreating = true;
        chasingSpeed = chasingSpeed * 1.05;
        ghostSpeed = ghostSpeed * 1.75;
    }
    
    public void revive() {
        x = originalX;
        y = originalY;
        retreating = false;
        chasingSpeed = originalChasingSpeed;
        ghostSpeed = originalGhostSpeed;
        air = -1;
        alive = true;
        destroyed = false;
        stateManager.switchTo("idle");
    }
    
    // needs to implement bonus code in inherited classes
    public abstract int getBonusPoint();

// debug draw targetX & targetY
//    @Override
//    public void draw(Graphics2D g) {
//        super.draw(g);
//        
//        g.setColor(Color.CYAN);
//        g.fillOval((int) (targetX - 3), (int) (targetY - 3), 6, 6);
//    }
    
}
