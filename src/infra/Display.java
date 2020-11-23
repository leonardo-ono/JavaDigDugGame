package infra;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static infra.Settings.CANVAS_WIDTH;
import static infra.Settings.CANVAS_HEIGHT;
import static infra.Settings.VIEWPORT_WIDTH;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.image.BufferStrategy;
import scene.HiscoreEnterName;
import scene.HiscoreTop;
import scene.Hiscores;
import scene.Initializing;
import scene.OLPresents;
import scene.Stage;
import scene.Title;
import scene.Title;

/**
 * Display class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Display extends Canvas {
    
    private BufferedImage backbuffer;
    private StateManager<Scene> scenes;
    private BufferStrategy bs;
    private boolean running;
    private Thread thread;
    
    public Display() {
        setBackground(Color.BLACK);
    }
    
    public void start() {
        createBufferStrategy(2);
        bs = getBufferStrategy();
        Audio.initialize();
        backbuffer = new BufferedImage(
                CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        initAllScenes();
        
        running = true;
        thread = new Thread(new MainLoop());
        thread.start();
        addKeyListener(new Input());
    }
    
    private void initAllScenes() {
        scenes = new StateManager<>();
        scenes.addState(new Initializing(scenes));
        scenes.addState(new OLPresents(scenes));
        scenes.addState(new Title(scenes));
        scenes.addState(new Hiscores(scenes));
        scenes.addState(new Stage(scenes));
        scenes.addState(new HiscoreTop(scenes));
        scenes.addState(new HiscoreEnterName(scenes));
        scenes.initAll();
        scenes.switchTo("initializing");
    }
    
    private class MainLoop implements Runnable {

        @Override
        public void run() {
            long currentTime = System.nanoTime();
            long lastTime = currentTime;
            long delta;
            long unprocessedTime = 0;
            boolean updated = false;
            while (running) {
                currentTime = System.nanoTime();
                delta = currentTime - lastTime;
                unprocessedTime += delta;
                lastTime = currentTime;
                while (unprocessedTime >= Settings.REFRESH_PERIOD) {
                    unprocessedTime -= Settings.REFRESH_PERIOD;
                    update();
                    updated = true;
                }
                if (updated) {
                    Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                    draw((Graphics2D) backbuffer.getGraphics());
                    g.drawImage(backbuffer, 0, 0
                            , VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT, null);
                    
                    g.dispose();
                    bs.show();
                    updated = false;
                }
                
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
        
    }
    
    private void update() {
        scenes.update();
    }

    private void draw(Graphics2D g) {
        g.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        g.translate(-16, 0);
        scenes.draw(g);
    }

}
