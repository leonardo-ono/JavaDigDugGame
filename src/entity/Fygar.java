package entity;

import static infra.Direction.*;
import infra.Enemy;
import infra.State;
import infra.StateManager;
import infra.Underground;
import scene.Stage;

/**
 * Fygar (Enemy) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Fygar extends Enemy {
    
    private final Fire fire;
    
    public Fygar(Stage stage, double x, double y) {
        super(stage, "fygar", 32, x, y);
        this.fire = new Fire(stage);
        zorder = 7;
    }

    public Fire getFire() {
        return fire;
    }
    
    @Override
    public void init() {
        super.init(); 
        animationPlayer.addAnimation("preparing_to_breathe", 1, 2, 0.5, true);
        animationPlayer.addAnimation("breathing_fire", 2, 2, 0, false);
        stateManager.removeState("chasing");
        stateManager.addState(new FygarChasing(stateManager, this));
        stateManager.addState(new PreparingToBreathe(stateManager, this));
        stateManager.addState(new BreathingFire(stateManager, this));
    }
    
    @Override
    public int getBonusPoint() {
        if (y <= 89) {
            return direction == LEFT || direction == RIGHT ? 400 : 200;
        }
        else if (y <= 153) {
            return direction == LEFT || direction == RIGHT ? 600 : 300;
        }
        else if (y <= 218) {
            return direction == LEFT || direction == RIGHT ? 800 : 400;
        }
        else {
            return direction == LEFT || direction == RIGHT ? 1000 : 500;
        }
    }

    /**
     * Fygar Chasing (State) class.
     */
    public class FygarChasing extends Chasing  {
        
        private long breatheTime;
        
        public FygarChasing(StateManager stateManager, Enemy owner) {
            super(stateManager, owner);
        }

        @Override
        public void onEnter() {
            super.onEnter();
            breatheTime = 1000 + System.currentTimeMillis() 
                    + (int) (3000 * Math.random());  
        }
        
        @Override
        public void update() {
            super.update();

            long currentTime = System.currentTimeMillis();
            if (currentTime >= breatheTime) {
                
                if (Underground.canMoveTo((int) x, (int) y, LEFT, 12)
                    && Underground.canMoveTo((int) x, (int) y, RIGHT, 12)
                    && (direction == LEFT || direction == RIGHT)) {
                    
                    owner.getStateManager().switchTo("preparing_to_breathe");
                    return;
                }
                else {
                    breatheTime = 1000 + System.currentTimeMillis() 
                            + (int) (3000 * Math.random());  
                }
            }             
        }
        
    }

    /**
     * PreparingToBreathe (State) class.
     */
    private class PreparingToBreathe extends State<Enemy> {
        
        private long endTime;
        
        public PreparingToBreathe(StateManager stateManager, Enemy owner) {
            super(stateManager, "preparing_to_breathe", owner);
        }

        @Override
        public void onEnter() {
            owner.getAnimationPlayer().play("preparing_to_breathe");
            endTime = System.currentTimeMillis() + 1000;
        }

        @Override
        public void update() {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime) {
                owner.getStateManager().switchTo("breathing_fire");
            }
        }

    }

    /**
     * BreathingFire (State) class.
     */
    private class BreathingFire extends State<Enemy> {
        
        public BreathingFire(StateManager stateManager, Enemy owner) {
            super(stateManager, "breathing_fire", owner);
        }

        @Override
        public void onEnter() {
            owner.getAnimationPlayer().play("breathing_fire");
            fire.fire(x, y, direction);
        }

        @Override
        public void update() {
            if (!fire.isFiring()) {
                owner.getStateManager().switchTo("chasing");
            }
        }

    }

    @Override
    public void revive() {
        super.revive(); 
        fire.cancel();
    }

}
