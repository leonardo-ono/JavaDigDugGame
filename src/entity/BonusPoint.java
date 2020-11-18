package entity;

import infra.Entity;
import infra.Resource;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * BonusPoint class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class BonusPoint extends Entity {
    
    private static final BufferedImage[] BITMAP_FONTS = new BufferedImage[2];
    
    private static final int[] FONT_START_POSITION_X 
            = { 0, 5, 8, 14, 20, 26, 32, 38, 44, 50 };
    
    private static final int[] FONT_DIGIT_WIDTH 
            = { 6, 4, 7, 7, 7, 7, 7, 7, 7, 7 };
    
    static {
        BITMAP_FONTS[0] = Resource.getImage("bonus_point_digits_0");
        BITMAP_FONTS[1] = Resource.getImage("bonus_point_digits_1");
    }
    
    private String pointStr;
    private int x;
    private int y;
    private int totalWidth;
    private int colorType;
    private long waitTime;
    private boolean free = true;
    
    public BonusPoint() {
        super(null);
    }
    
    public void spawn(int point, int x, int y, int colorType) {
        this.pointStr = "" + point;
        this.x = x;
        this.y = y;
        this.colorType = colorType;
        waitTime = System.currentTimeMillis() + 1000;
        free = false;
        totalWidth = 0;
        for (int i = 0; i < this.pointStr.length(); i++) {
            int digit = this.pointStr.charAt(i) - 48;
            totalWidth += FONT_DIGIT_WIDTH[digit] - 1; 
        }
    }

    public void hide() {
        free = true;
    }

    public boolean isFree() {
        return free;
    }
    
    @Override
    public void update() {
        if (free) {
            return;
        }
        if (System.currentTimeMillis() > waitTime) {
            free = true;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (free) {
            return;
        }
        int offsetX = 0;
        for (int i = 0; i < pointStr.length(); i++) {
            int digit = pointStr.charAt(i) - 48;
            int dx1 = x + offsetX - totalWidth / 2; 
            int dy1 = y - 4; 
            int dx2 = dx1 + FONT_DIGIT_WIDTH[digit]; 
            int dy2 = dy1 + 9; 
            int sx1 = FONT_START_POSITION_X[digit]; 
            int sy1 = 0;
            int sx2 = sx1 + FONT_DIGIT_WIDTH[digit]; 
            int sy2 = 9;
            g.drawImage(BITMAP_FONTS[colorType]
                    , dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
            
            offsetX += FONT_DIGIT_WIDTH[digit] - 1;
        }
    }
    
}
