package infra;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static infra.Settings.CANVAS_WIDTH;
import static infra.Settings.CANVAS_HEIGHT;

/**
 * Underground class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Underground {
    
    private static final int PATH = Color.WHITE.getRGB();
    private static final int DIGGED = Color.BLUE.getRGB();
    private static final int[] BLACK_TRANSPARENT = new int[] {0, 0, 0, 0};
    
    private static final BufferedImage[] IMAGES = new BufferedImage[4];
    private static final BufferedImage IMAGE;
    private static final Graphics2D IBG;
    private static final BufferedImage PATH_MASK_ORIGINAL;
    
    private static final BufferedImage PATH_MASK;
    private static Graphics2D pmg;
    
    private static final Composite DIG_COMPOSITE 
            = AlphaComposite.getInstance(AlphaComposite.CLEAR);
    
    private static final BufferedImage SCORE_MASK;
    private static Graphics2D smg;
    private static int digScore;
    
    static {
        for (int i = 0; i < 4; i++) {
            IMAGES[i] = Resource.getImage("underground_" + i);
        }
        
        IMAGE = new BufferedImage(CANVAS_WIDTH + 32, CANVAS_HEIGHT
                , BufferedImage.TYPE_INT_ARGB);
        
        IBG = (Graphics2D) IMAGE.getGraphics();
        PATH_MASK_ORIGINAL = Resource.getImage("path_mask");
        
        PATH_MASK = new BufferedImage(PATH_MASK_ORIGINAL.getWidth()
                , PATH_MASK_ORIGINAL.getHeight(), BufferedImage.TYPE_INT_ARGB);

        SCORE_MASK = new BufferedImage(PATH_MASK_ORIGINAL.getWidth()
                , PATH_MASK_ORIGINAL.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    public static BufferedImage getIMAGE() {
        return IMAGE;
    }

    public static BufferedImage getPATH_MASK() {
        return PATH_MASK;
    }

    public static int getDigScore() {
        return (digScore / 16) * 10;
    }
    
    // level < 0 -> title
    public static void reset(int level) {
        pmg = (Graphics2D) PATH_MASK.getGraphics();
        pmg.drawImage(PATH_MASK_ORIGINAL, 0, 0, null);
        pmg.setComposite(AlphaComposite.SrcIn);
        pmg.setColor(Color.BLUE);

        digScore = 0;
        smg = (Graphics2D) SCORE_MASK.getGraphics();
        smg.setColor(Color.BLACK);
        smg.fillRect(0, 0, SCORE_MASK.getWidth(), SCORE_MASK.getHeight());
        smg.setColor(Color.WHITE);
        smg.fillRect(0, 0, SCORE_MASK.getWidth(), 40);
        
        int i = ((level - 1) / 4) % 3;
        if (level < 0) {
            i = 3;
        }
        IBG.drawImage(IMAGES[i], 0, 0, null);
    }
    
    // check if (x, y) is under movable path
    public static boolean canMoveTo(int x, int y) {

        // check out of bounds
        if (x < 24 || x > 232 || y < 32 || y > 264) {
            return false;
        }

        int state = PATH_MASK.getRGB(x, y);
        return state == PATH || state == DIGGED;
    }

    // check if (x, y) is under movable path and if it was already been dug
    public static boolean canMoveTo(
            int x, int y, Direction direction, int radius) {
        
        int dx = x + direction.getDx() * radius;
        int dy = y + direction.getDy() * radius;
        
        if (dx < 0 || dx > PATH_MASK.getWidth() - 1 
                || dy < 0 || dy > PATH_MASK.getHeight() - 1) {
            
            return false;
        }
        
        int state = PATH_MASK.getRGB(dx, dy);
        return state == DIGGED;
    }
    
    public static void dig(int x, int y, boolean enableDigScore) {
        if (SCORE_MASK.getRGB(x, y) == Color.BLACK.getRGB()) {
            SCORE_MASK.setRGB(x, y, Color.WHITE.getRGB());
            if (enableDigScore) {
                digScore += 1;
                GameInfo.checkExtraLife();
            }
        }
        
        pmg.fillRect(x - 6, y - 6, 13, 13);
        createTunnel(x, y);
        eraseDirtyRegion(x, y);
    }
    
    private static void createTunnel(double px, double py) {
        Composite oc = IBG.getComposite();
        IBG.setComposite(DIG_COMPOSITE);
        
        for (int x = (int) (px - 5); x < (int) (px + 5); x++) {
            int h = ((x + 3) >> 1) % 2;
            IBG.drawLine(x, (int) (py - 6 - h), x, (int) (py + 6 - h));
        }
        
        for (int y = (int) (py - 5); y < (int) (py + 5); y++) {
            int w = (y >> 1) % 2;
            IBG.drawLine((int) (px - 6 - w), y, (int) (px + 6 - w), y);
        }
        
        IBG.setComposite(oc);
    }
    
    private static void eraseDirtyRegion(int digX, int digY) {
        int startX = digX / 16;
        int startY = digY / 16 - 3;
        int endX = startX + 2;
        int endY = startY + 2;
        
        // clear corners
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                int px = x * 16 - 2;
                int py = (y + 3) * 16 - 2;
                
                //bg.setColor(Color.GREEN);
                //bg.fillRect(px, py, 3, 3);
                
                for (int iy = 0; iy < 4; iy++) {
                    for (int ix = 0; ix < 4; ix++) {
                        
                        if (ix < 3 && iy == 3) {
                            continue;
                        }
                        
                        boolean checkLeft = 
                            IMAGE.getRGB(px + ix - 4, py + iy) == 0;

                        boolean checkRight = 
                            IMAGE.getRGB(px + ix + 4, py + iy) == 0;

                        boolean checkTop = 
                            IMAGE.getRGB(px + ix, py + iy - 4) == 0;

                        boolean checkBottom = 
                            IMAGE.getRGB(px + ix, py + iy + 4) == 0;

                        if (checkLeft && checkRight 
                                && checkTop && checkBottom) {
                            
                            IMAGE.getAlphaRaster().setPixel(
                                    px + ix, py + iy, BLACK_TRANSPARENT);
                        }
                    }
                }
            }
        }
        
        // clear inner region
        for (int y = startY; y < endY; y++) {
            outer:
            for (int x = startX; x < endX; x++) {
                int px = x * 16 - 8;
                int py = (y + 3) * 16 - 8;
                
                //bg.setColor(Color.ORANGE);
                //bg.fillRect(px, py, 16, 16);
                
                for (int i = 0; i < 16; i ++) {
                    boolean stY1 = PATH_MASK.getRGB(px + i, py) != DIGGED;
                    boolean stY2 = PATH_MASK.getRGB(px + i, py + 16) != DIGGED;
                    boolean stX1 = PATH_MASK.getRGB(px, py + i) != DIGGED;
                    boolean stX2 = PATH_MASK.getRGB(px + 16, py + i) != DIGGED;
                    if (stY1 || stY2 || stX1 || stX2) {
                        continue outer;
                    }
                }
                IBG.setColor(Color.BLACK);
                IBG.fillRect(px + 1, py + 1, 15, 15);
            }
        }
    }
 
    public static boolean isCrossing(int x, int y) {
        int left = PATH_MASK.getRGB(x + 1, y);
        int right = PATH_MASK.getRGB(x - 1, y);
        int top = PATH_MASK.getRGB(x, y - 1);
        int down = PATH_MASK.getRGB(x, y + 1);
        int r = (left == PATH || left == DIGGED) ? 1 : 0;
        r += (down == PATH || down == DIGGED) ? 2 : 0;
        r += (right == PATH || right == DIGGED) ? 4 : 0;
        r += (top == PATH || top == DIGGED) ? 8 : 0;
        return (r != 5 && r != 10);
    }
    
}
