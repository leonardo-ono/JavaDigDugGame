package entity;

import infra.Actor;
import infra.Audio;
import infra.Collidable;
import infra.Direction;
import infra.Enemy;
import infra.GameInfo;
import infra.State;
import infra.StateManager;
import infra.renderer.AnimationPlayer.Animation;
import infra.renderer.BonusPoints;
import infra.Underground;
import scene.Stage;

/**
 * Rock class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Rock extends Actor {
    
    private static final int[] BONUS_TABLE= {
        0, 1000, 2500, 4000, 6000, 8000, 10000, 12000, 15000
    };
    
    private final DigDug digdug;
    private double originalX;
    private double originalY;
    private int actorsHitCount;
    
    private boolean inAction;
    
    public Rock(Stage stage, double x, double y) {
        super(stage, "rock", 16);
        this.digdug = stage.getDigdug();
        this.x = x;
        this.y = y;
        zorder = 2;
    }

    public boolean isInAction() {
        return inAction;
    }
    
    @Override
    public void init() {
        originalX = x;
        originalY = y;
        actorsHitCount = 0;
        animationPlayer.addAnimation("idle", 0, 0, 0, true);
        animationPlayer.addAnimation("shattering", 0, 3, 0.1, false);
        animationPlayer.play("idle");
        stateManager.addState(new Stable(stateManager, this));
        stateManager.addState(new Activated(stateManager, this));
        stateManager.addState(new Unstable(stateManager, this));
        stateManager.addState(new Falling(stateManager, this));
        stateManager.addState(new Shattering(stateManager, this));
        stateManager.addState(new Destroyed(stateManager, this));
        stateManager.switchTo("stable");
    }

    public double getOriginalX() {
        return originalX;
    }

    public double getOriginalY() {
        return originalY;
    }

    public boolean isBlocking(int dx, int dy) {
        return true;
    }

    /**
     * Stable (State) class.
     */
    private class Stable extends State<Rock> {

        public Stable(StateManager manager, Rock owner) {
            super(manager, "stable", owner);
        }

        @Override
        public void onEnter() {
            owner.getAnimationPlayer().play("idle");
        }

        @Override
        public void update() {
            if ((int) digdug.getX() == (int) x 
                    && (int) digdug.getY() == (int) (y + 16)) {

                owner.getStateManager().switchTo("activated");
            }
        }

    }
    
    /**
     * Activated (State) class.
     */
    private class Activated extends State<Rock> {

        public Activated(StateManager manager, Rock owner) {
            super(manager, "activated", owner);
        }

        @Override
        public void update() {
            if ((int) digdug.getX() != (int) x 
                    || (int) digdug.getY() != (int) (y + 16)) {

                owner.getStateManager().switchTo("unstable");
            }
        }

    }
    
    /**
     * Unstable (State) class.
     */
    private class Unstable extends State<Rock> {
        
        private long startTime;
        
        public Unstable(StateManager manager, Rock owner) {
            super(manager, "unstable", owner);
        }

        @Override
        public void onEnter() {
            originalX = x;
            startTime = System.currentTimeMillis();
            inAction = true;
        }
        
        @Override
        public void update() {
            long currentTime = System.currentTimeMillis();
            x = originalX + (int) (2 * Math.sin(currentTime * 0.05));
            if (currentTime > startTime + 1500) {
                x = originalX;
                owner.getStateManager().switchTo("falling");
            }
        }

    }

    /**
     * Falling (State) class.
     */
    private class Falling extends State<Rock> {
        
        private static final int SPEED = 4;
        
        public Falling(StateManager manager, Rock owner) {
            super(manager, "falling", owner);
        }

        @Override
        public void onEnter() {
            Audio.playSound("rock_falling");
        }
        
        @Override
        public void update() {
            // take small steps to avoid tunneling problems with the terrain
            for (int i = 1; i <= SPEED; i++) {
                if (Underground.canMoveTo(
                        (int) x, (int) (y + i), Direction.DOWN, 8)) {
                    
                    for (int i2 = 0; i2 < SPEED; i2++) {
                        y += 1;
                        if (y > originalY + 16) {
                            Underground.dig((int) x, (int) y, false);
                        }
                    }
                    return;
                }
            }            
            owner.getStateManager().switchTo("shattering");
        }

    }
    
    /**
     * Shattering (State) class.
     */
    private class Shattering extends State<Rock> {
        
        public Shattering(StateManager manager, Rock owner) {
            super(manager, "shattering", owner);
        }

        @Override
        public void onEnter() {
            owner.getAnimationPlayer().play("shattering", true);
            Audio.playSound("rock_hit_floor");
        }
        
        @Override
        public void update() {
            if (owner.getAnimationPlayer().isFinished()) {
                owner.getStateManager().switchTo("destroyed");
            }
            Animation currentAnimation 
                    = owner.getAnimationPlayer().getCurrentAnimation();
            if (alive && currentAnimation.currentFrame > 2) {
                alive = false;
            }
        }

    }

    /**
     * Destroyed (State) class.
     */
    private class Destroyed extends State<Rock> {
        
        public Destroyed(StateManager manager, Rock owner) {
            super(manager, "destroyed", owner);
        }

        @Override
        public void onEnter() {
            destroy();
            
            if (actorsHitCount >= 2) {
                if (GameInfo.isStageBonusAvailable()) {
                    GameInfo.consumeStageBonus();
                    scene.spawnFood();
                }
            }
            
            if (actorsHitCount > 0) {
                int bonusPoint = BONUS_TABLE[actorsHitCount];
                GameInfo.addScore(bonusPoint);
                BonusPoints.show(
                        bonusPoint, (int) x, (int) y, BonusPoints.ENEMY_KILLED);
            }
            
            inAction = false;
        }
        
    }

    @Override
    public void onCollision(Collidable other) {
        if (!stateManager.getCurrentState().getName().equals("falling")) {
            return;
        }
        
        if (other instanceof DigDug) {
            if (digdug.getRock() == null && digdug.isAlive()) {
                digdug.hitByRock(this);
                actorsHitCount++;
                Audio.playSound("squashed");
            }
        }
        else if (other instanceof Enemy) {
            Enemy enemy = (Enemy) other;
            if (enemy.getRock() == null && enemy.isAlive()) {
                enemy.hitByRock(this);
                actorsHitCount++;
                Audio.playSound("squashed");
            }
        }
    }
    
}
