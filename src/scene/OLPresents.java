package scene;

import infra.Scene;
import infra.StateManager;
import infra.renderer.Text;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * OLPresents (Scene) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class OLPresents extends Scene {
    
    private static class Cell {
        
        int col;
        int row;
        double accelRad = 0.025;
        double velRad = 0;
        double maxRadius;
        double currentRadius;
        long startTime;

        public Cell(int col, int row, int maxRadius, long startTime) {
            this.col = col;
            this.row = row;
            this.currentRadius = 0;
            this.maxRadius = maxRadius;
            this.startTime = System.currentTimeMillis() + startTime;
        }
        
        void update() {
            long currentTime = System.currentTimeMillis();
            if (currentTime > startTime) {
                velRad += accelRad;
                currentRadius += velRad;
                if (currentRadius > maxRadius) {
                    currentRadius = maxRadius;
                    velRad = -velRad * 0.55;
                }
            }
        }
        
        void draw(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.fillRect((int) (col * 16 - currentRadius + 8)
                    , (int) (row * 16 - currentRadius)
                    , (int) (2 * currentRadius), (int) (2 * currentRadius));
        }
        
    }
    
    private final List<Cell> cells = new ArrayList<>();
    
    private final String text = "                  PRESENTS";
    private double frame;
    
    public OLPresents(StateManager<Scene> scenes) {
        super(scenes, "ol_presents", null);
    }
    
    private void createAllCells() {
        cells.clear();
        
        //   (4,6)
        //  /
        // ABC K
        // B D J
        // C E I
        // DEFGHIJL
        
        cells.add(new Cell(4, 6, 8, 1000)); //A
        
        cells.add(new Cell(4, 7, 8, 1050)); //B
        cells.add(new Cell(5, 6, 8, 1100)); //B
        
        cells.add(new Cell(4, 8, 8, 1150)); //C
        cells.add(new Cell(6, 6, 8, 1200)); //C
        
        cells.add(new Cell(4, 9, 8, 1250)); //D
        cells.add(new Cell(6, 7, 8, 1300)); //D

        cells.add(new Cell(5, 9, 8, 1350)); //E
        cells.add(new Cell(6, 8, 8, 1400)); //E

        cells.add(new Cell(6, 9, 8, 1450)); //F
        
        cells.add(new Cell(7, 9, 4, 1500)); //G DOT
        
        cells.add(new Cell(8, 9, 8, 1550)); //H

        cells.add(new Cell(8, 8, 8, 1600)); //I
        cells.add(new Cell(9, 9, 8, 1650)); //I

        cells.add(new Cell(8, 7, 8, 1700)); //J
        cells.add(new Cell(10, 9, 8, 1750)); //J

        cells.add(new Cell(8, 6, 8, 1800)); //K
        
        cells.add(new Cell(11, 9, 4, 1850)); //L DOT
    }
    
    @Override
    public void onEnter() {
        frame = 0;
        createAllCells();
    }

    @Override
    public void update() {
        cells.forEach(cell -> cell.update());
        
        frame += 0.2;
        if (frame > 50) {
            manager.switchTo("title");
        }
    }

    @Override
    public void draw(Graphics2D g) {
        int endIndex = (int) frame;
        if (endIndex > 26) {
            endIndex = 26;
        }
        Text.draw(g, text.substring(0, endIndex), -6, 20, Color.WHITE);
        
        cells.forEach(cell -> cell.draw(g));
    }
    
}
