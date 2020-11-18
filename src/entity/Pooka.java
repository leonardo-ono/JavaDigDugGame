package entity;

import infra.Enemy;
import scene.Stage;

/**
 * Pooka (Enemy) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Pooka extends Enemy {

    public Pooka(Stage stage, double x, double y) {
        super(stage, "pooka", 32, x, y);
        zorder = 6;
    }

    @Override
    public int getBonusPoint() {
        if (y <= 89) {
            return 200;
        }
        else if (y <= 153) {
            return 300;
        }
        else if (y <= 218) {
            return 400;
        }
        else {
            return 500;
        }
    }
    
}
