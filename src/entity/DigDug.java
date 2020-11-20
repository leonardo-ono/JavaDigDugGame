package entity;

import infra.Actor;
import infra.Audio;
import infra.Audio.SoundPlayer;
import infra.Direction;
import static infra.Direction.*;
import infra.GameInfo;
import infra.Input;
import infra.State;
import infra.StateManager;
import infra.Underground;
import java.awt.event.KeyEvent;
import scene.Stage;

/**
 * DigDug class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class DigDug extends Actor {
    
    private SoundPlayer music;
    private Direction currentDirection = IDLE;
    private final Harpoon harpoon;
    private Rock rock;
    
    public DigDug(Stage stage, Harpoon harpoon) {
        super(stage, "digdug", 16);
        this.harpoon = harpoon;
        zorder = 4;
    }

    public SoundPlayer getMusic() {
        return music;
    }

    public void setMusic(SoundPlayer music) {
        this.music = music;
    }

    public Harpoon getHarpoon() {
        return harpoon;
    }

    public Rock getRock() {
        return rock;
    }

    @Override
    public void init() {
        animationPlayer.addAnimation("idle", 0, 0, 0, false);
        animationPlayer.addAnimation("idle_with_drill", 2, 2, 0, false);
        animationPlayer.addAnimation("walking", 0, 1, 0.5, true);
        animationPlayer.addAnimation("walking_with_drill", 2, 3, 0.5, true);
        animationPlayer.addAnimation("pumping", 4, 5, 0.125, false);
        animationPlayer.addAnimation("firing", 6, 6, 0, false);
        animationPlayer.addAnimation("hit_by_rock", 7, 7, 0, false);
        animationPlayer.addAnimation("dying", 8, 13, 0.125, false);
        animationPlayer.play("walking");
        stateManager.addState(new Idle(stateManager, this));
        stateManager.addState(new Ready(stateManager, this));
        stateManager.addState(new Walking(stateManager, this));
        stateManager.addState(new Firing(stateManager, this));
        stateManager.addState(new Pumping(stateManager, this));
        stateManager.addState(new HitByRock(stateManager, this));
        stateManager.addState(new HitByEnemy(stateManager, this));
        stateManager.addState(new Dying(stateManager, this));
        stateManager.switchTo("ready");
    }
    
    /**
     * Ready (State) class.
     */
    private class Ready extends Walking {
        
        public Ready(StateManager stateManager, DigDug owner) {
            super(stateManager, "ready", owner);
        }

        @Override
        public void onEnter() {
            if (GameInfo.isFirstPlay()) {
                x = 200;
                y = 32;
            }
            else {
                x = 120;
                y = 151;
                currentDirection = DOWN;
                desiredDirection = IDLE;
                animationPlayer.setDirection(RIGHT);
                moveRequested = true;                
                for (int cy = 32; cy <= 152; cy++) {
                    Underground.dig(120, cy, false);
                }
            }
        }

        @Override
        protected void handleInput() {
            if (!GameInfo.isFirstPlay()) {
                return;
            }
            
            if ((int) y == 32 && (int) x > 128) {
                desiredDirection = LEFT;
                moveRequested = true;                
            } 
            else if ((int) y == 32 && (int) x <= 128) {
                desiredDirection = DOWN;
                moveRequested = true;                
            } 
            else if ((int) y < 151 && (int) x == 120) {
                desiredDirection = DOWN;
                moveRequested = true;                
            }
            else if ((int) y == 151 && (int) x == 120) {
                desiredDirection = IDLE;
                animationPlayer.setDirection(RIGHT);
                moveRequested = true;                
            }
        }
        
    }

    /**
     * Walking [and Idle] (State) class.
     */
    private class Walking extends State<DigDug> {

        protected Direction desiredDirection = IDLE;
        protected boolean moveRequested;
        protected boolean drilling;
        
        public Walking(StateManager stateManager, String name, DigDug owner) {
            super(stateManager, name, owner);
        }

        public Walking(StateManager stateManager, DigDug owner) {
            super(stateManager, "walking", owner);
        }
        
        protected void handleInput() {
            if (Input.isKeyPressed(KeyEvent.VK_LEFT)) {
                desiredDirection = LEFT;
                moveRequested = true;
            }
            else if (Input.isKeyPressed(KeyEvent.VK_RIGHT)) {
                desiredDirection = RIGHT;
                moveRequested = true;
            }
            else if (Input.isKeyPressed(KeyEvent.VK_UP)) {
                desiredDirection = UP;
                moveRequested = true;
            }
            else if (Input.isKeyPressed(KeyEvent.VK_DOWN)) {
                desiredDirection = DOWN;
                moveRequested = true;
            }

            if (Input.isKeyJustPressed(KeyEvent.VK_SPACE)) {
                owner.getStateManager().switchTo("firing");
            }        

            if (Input.isKeyJustPressed(KeyEvent.VK_A)) {
                owner.getScene().getManager().switchTo("stage");
            }        
        }
        
        @Override
        public void update() {
            moveRequested = false;

            handleInput();

            if (moveRequested) {
                if (desiredDirection == currentDirection.getOpposite()) {
                    currentDirection = desiredDirection;
                }
                
                boolean moved = false;
                
                if (currentDirection == LEFT
                        && !scene.isBlockedByRock((int) (owner.getX() - 1)
                                , (int) owner.getY())
                        && Underground.canMoveTo((int) (owner.getX() - 1)
                                , (int) owner.getY())) {

                    owner.translate(-1, 0);
                    moved = true;
                }
                else if (currentDirection == RIGHT
                        && !scene.isBlockedByRock((int) (owner.getX() + 1)
                                , (int) owner.getY())                        
                        && Underground.canMoveTo((int) (owner.getX() + 1)
                                , (int) owner.getY())) {

                    owner.translate(1, 0);
                    moved = true;
                }
                else if (currentDirection == UP
                        && !scene.isBlockedByRock((int) owner.getX()
                                , (int) (owner.getY() - 1))                        
                        && Underground.canMoveTo((int) owner.getX()
                                , (int) (owner.getY() - 1))) {

                    owner.translate(0, -1);
                    moved = true;
                }
                else if (currentDirection == DOWN
                        && !scene.isBlockedByRock((int) owner.getX()
                                , (int) (owner.getY() + 1))
                        && Underground.canMoveTo((int) owner.getX()
                                , (int) (owner.getY() + 1))) {

                    owner.translate(0, 1);
                    moved = true;
                }

                if (!moved || Underground.canMoveTo((int) owner.getX()
                        , (int) owner.getY(), currentDirection, 6)) {
                    
                    drilling = false;
                    owner.getAnimationPlayer().play("walking");
                }
                else {
                    drilling = true;
                    owner.getAnimationPlayer().play("walking_with_drill");
                }

                Underground.dig((int) owner.getX(), (int) owner.getY()
                        , !(this instanceof Ready));

                if (Underground.isCrossing(
                        (int) owner.getX(), (int) owner.getY())) {
                    
                    currentDirection = desiredDirection;
                }

                if ((int) y <= 32 
                    && (currentDirection == UP || currentDirection == DOWN)) {
                    
                    owner.getAnimationPlayer().setDirection(RIGHT);
                }
                else {
                    owner.getAnimationPlayer().setDirection(currentDirection);
                }
                
                if (music != null) {
                    music.resume();
                }
            }
            else {
                if (drilling) {
                    owner.getAnimationPlayer().play("idle_with_drill");
                }
                else {
                    owner.getAnimationPlayer().play("idle");
                }
                
                if (music != null) {
                    music.pause();
                }
            }
        }

    }
    
    /**
     * Firing [harpoon] (State) class.
     */
    private class Firing extends State<DigDug> {

        public Firing(StateManager stateManager, DigDug owner) {
            super(stateManager, "firing", owner);
        }

        @Override
        public void onEnter() {
            owner.getAnimationPlayer().play("firing");
            harpoon.fire(x, y, currentDirection);
        }
        
        @Override
        public void update() {
            if (!harpoon.isFiring()) {
                owner.getStateManager().switchTo("walking");
            }
        }

    }

    /**
     * Pumping (State) class.
     */
    private class Pumping extends State<DigDug> {

        public Pumping(StateManager stateManager, DigDug owner) {
            super(stateManager, "pumping", owner);
        }

        @Override
        public void onEnter() {
            owner.getAnimationPlayer().play("pumping", true);
        }
        
        @Override
        public void update() {
            if (harpoon.getEnemy() != null
                    && owner.getAnimationPlayer().isFinished()
                    && Input.isKeyJustPressed(KeyEvent.VK_SPACE)) {
                
                harpoon.getEnemy().inflate();
                owner.getAnimationPlayer().play("pumping", true);
                Audio.playSound("pumping");
            }
            else if (harpoon.getEnemy() == null) {
                owner.getStateManager().switchTo("walking");
            }
            else if (Input.isKeyJustPressed(KeyEvent.VK_LEFT)
                    || Input.isKeyJustPressed(KeyEvent.VK_RIGHT)
                    || Input.isKeyJustPressed(KeyEvent.VK_UP)
                    || Input.isKeyJustPressed(KeyEvent.VK_DOWN)) {
                
                harpoon.cancel();
                owner.getStateManager().switchTo("walking");
            }
        }

    }
    
    /**
     * HitByRock (State) class.
     */
    private class HitByRock extends State<DigDug> {
        
        public HitByRock(StateManager stateManager, DigDug owner) {
            super(stateManager, "hit_by_rock", owner);
        }

        @Override
        public void onEnter() {
            alive = false;
            scene.deactivateAllEnemies();
            owner.getAnimationPlayer().setDirection(RIGHT);
            owner.getAnimationPlayer().play("hit_by_rock");
            if (music != null) {
                music.pause();
            }
            harpoon.cancel();
        }
        
        @Override
        public void update() {
            y = rock.getY() + 6;
            if (rock.isDestroyed()) {
                owner.getStateManager().switchTo("dying");
                rock = null;
            }
        }

    }
    
    /**
     * HitByEnemy (State) class.
     */
    private class HitByEnemy extends State<DigDug> {
        
        private long startTime;
        
        public HitByEnemy(StateManager stateManager, DigDug owner) {
            super(stateManager, "hit_by_enemy", owner);
        }

        @Override
        public void onEnter() {
            alive = false;
            scene.deactivateAllEnemies();
            if (music != null) {
                music.pause();
            }
            owner.getAnimationPlayer().pause();
            Audio.playSound("enemy_touched_digdug");
            startTime = System.currentTimeMillis();
            harpoon.cancel();
        }
        
        @Override
        public void update() {
            long currentTime = System.currentTimeMillis();
            if (currentTime > startTime + 1000) {
                owner.getStateManager().switchTo("dying");
            }
        }

    }
    
    /**
     * Dying (State) class.
     */
    private class Dying extends State<DigDug> {
        
        public Dying(StateManager stateManager, DigDug owner) {
            super(stateManager, "dying", owner);
        }

        @Override
        public void onEnter() {
            scene.destroyAllStageEntities();
            owner.getAnimationPlayer().play("dying", true);
            Audio.playSound("digdug_disappearing");
        }
        
        @Override
        public void update() {
            if (owner.getAnimationPlayer().isFinished()) {
                destroy();
            }
        }

    }

    public void hitByRock(Rock rock) {
        this.rock = rock;
        stateManager.switchTo("hit_by_rock");
    }

    public void hitByEnemy() {
        stateManager.switchTo("hit_by_enemy");
    }

    public void startPumping() {
        stateManager.switchTo("pumping");
    }
    
    public void revive() {
        x = 120;
        y = 152;        
        music = null;
        rock = null;
        alive = true;
        destroyed = false;
        harpoon.cancel();
        stateManager.switchTo("ready");
    }
    
}
