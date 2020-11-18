package scene;

import infra.Scene;
import infra.StateManager;

/**
 * Initializing (Scene) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Initializing extends Scene {
    
    private long waitTime;
    
    public Initializing(StateManager<Scene> scenes) {
        super(scenes, "initializing", null);
    }
    
    @Override
    public void onEnter() {
        waitTime = System.currentTimeMillis() + 3000;
    }

    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= waitTime) {
            manager.switchTo("ol_presents");
            // manager.switchTo("title");
        }
    }
    
}
