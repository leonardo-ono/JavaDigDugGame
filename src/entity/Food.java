package entity;

import infra.Actor;
import infra.Audio;
import infra.Collidable;
import infra.GameInfo;
import infra.renderer.BonusPoints;
import java.util.HashMap;
import java.util.Map;
import scene.Stage;

/**
 * Food class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Food extends Actor {

    private static final Map<Integer, Type> TYPES = new HashMap<>();
    
    private static enum Type {
        
        CARROT(400, 1), 
        TURNIP(600, 2), 
        MUSHROOM(800, 3), 
        CUCUMBER(1000, 4, 5), 
        EGGPLANT(2000, 6, 7), 
        PEPPER(3000, 8, 9), 
        TOMATO(4000, 10, 11), 
        GARLIC(5000, 12, 13), 
        WATERMELON(6000, 14, 15), 
        GALAXIAN(7000, 16, 17), 
        PINEAPPLE(8000, 18);
    
        int point;

        private Type(int point, int ... stages) {
            this.point = point;
            setTypes(stages);
        }
        
        private void setTypes(int ... stages) {
            for (int stage : stages) {
               TYPES.put(stage, this);
            }
        }

        public int getPoint() {
            return point;
        }
        
    }

    private static Type getByStage(int stage) {
        if (stage > 18) {
            return Type.PINEAPPLE;
        }
        else {
            return TYPES.get(stage);
        }
    }
    
    private Type type;
    private long hideTime;
    
    public Food(Stage stage) {
        super(stage, "foods", 16);
        for (Type fruit : Type.values()) {
            animationPlayer.addAnimation(
                    fruit.name(), fruit.ordinal(), fruit.ordinal(), 0, false);
        }
        zorder = 1;
    }

    @Override
    public void update() {
        super.update();
        
        long currentTime = System.currentTimeMillis();
        if (currentTime >= hideTime) {
            destroy();
            alive = false;
        }
    }
    
    public void spawn(int x, int y, int stage) {
        this.x = x;
        this.y = y;
        type = getByStage(stage);
        destroyed = false;
        alive = true;
        animationPlayer.play(type.name());
        hideTime = System.currentTimeMillis() + 10000;
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof DigDug && !isDestroyed()) {
            DigDug digdug = (DigDug) other;
            if (digdug.isAlive() && !digdug.isDestroyed()) {
                alive = false;
                destroy();
                BonusPoints.show(type.getPoint(), (int) x, (int) y, 0);
                GameInfo.addScore(type.getPoint());
                Audio.playSound("food");
            }
        }
    }
    
}
