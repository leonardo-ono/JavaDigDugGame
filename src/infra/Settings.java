package infra;

import javax.sound.sampled.AudioFormat;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_UNSIGNED;

/**
 * (Project) Settings class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Settings {
    
    public static final double REFRESH_PERIOD = 1000000000.0 / 30.0;

    // --- display ---
    
    public static final int CANVAS_WIDTH = 224;
    public static final int CANVAS_HEIGHT = 288;

    public static final int VIEWPORT_WIDTH = 224 * 2;
    public static final int VIEWPORT_HEIGHT = 288 * 2;
    
    // --- resources ---
    
    public static final AudioFormat SOUND_AUDIO_FORMAT 
            = new AudioFormat(PCM_UNSIGNED, 11025, 8, 1, 1, 11025, true);
    
    public static final String RES_IMAGE_FILE_EXT = ".png";
    public static final String RES_SOUND_FILE_EXT = ".wav"; 
    public static final String RES_FONT_FILE_EXT = ".ttf"; 
    public static final String RES_LEVEL_FILE_EXT = ".txt"; 
    public static final String RES_TITLE_ANIMATION_FILE_EXT = ".txt"; 
    
    public static final String RES_IMAGE_PATH = "/res/image/";
    public static final String RES_SOUND_PATH = "/res/sound/";
    public static final String RES_FONT_PATH = "/res/font/";
    public static final String RES_LEVEL_PATH = "/res/level/";
    public static final String RES_TITLE_ANIMATION_PATH = "/res/title_animation/";
    
}
