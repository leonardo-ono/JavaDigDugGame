package infra;

import static infra.Settings.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

/**
 * Audio class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Audio {
    
    private static final int MAX_SIMULTANEOUS_SOUNDS = 10;
    private static final List<SoundPlayer> SOUND_PLAYERS = 
        new ArrayList<SoundPlayer>();
    
    private static boolean soundInitialized;
    private static boolean disabled;

    public static boolean isSoundInitialized() {
        return soundInitialized;
    }
    
    public static void start() {
        initialize();
    }

    public static boolean isDisabled() {
        return disabled;
    }

    public static void disable() {
        disabled = true;
        stopAllSounds();
    }

    public static void enable() {
        disabled = false;
    }
    
    public static void initialize() {
        soundInitialized = true;
        for (int i = 0; i < MAX_SIMULTANEOUS_SOUNDS; i++) {
            SoundPlayer soundPlayer = new SoundPlayer();
            if (!soundPlayer.initialize()) {
                soundInitialized = false;
                return;
            }
            SOUND_PLAYERS.add(soundPlayer);
        }
    }
    
    public static void stopAllSounds() {
        if (!soundInitialized) {
            return;
        }
        SOUND_PLAYERS.forEach((soundPlayer) -> {
            soundPlayer.stop();
        });
    }

    public static void stopSound(String soundName) {
        if (!soundInitialized) {
            return;
        }
        try {
            for (SoundPlayer soundPlayer : SOUND_PLAYERS) {
                if (!soundPlayer.isFree() && 
                    soundPlayer.getCurrentSoundId().equals(soundName)) {
                    
                    soundPlayer.stop();
                    return;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
            soundInitialized = false;
        }
    }
    
    public static SoundPlayer playSound(String soundName) {
        return playSound(soundName, false);
    }
    
    public static SoundPlayer playSound(String soundName, boolean loop) {
        if (!soundInitialized || disabled) {
            return null;
        }
        try {
            for (SoundPlayer soundPlayer : SOUND_PLAYERS) {
                if (soundPlayer.isFree()) {
                    soundPlayer.play(
                            Resource.getSound(soundName), soundName, loop);
                    
                    return soundPlayer;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
            soundInitialized = false;
        }
        return null;
    }

    private static class SoundInfo {
        String currentSoundId;
        byte[] currentSound;
        int currentPos;
        boolean looped;
        boolean paused;
        boolean finished = true;
    }
    
    /*
     * SoundPlayer class.
     */
    public static class SoundPlayer implements Runnable {
        
        private static final int BUFFER_SIZE = 500;
        
        private Thread thread;
        private boolean running;
        private SourceDataLine line;
        
        private final SoundInfo currentSoundInfo = new SoundInfo();
        private final SoundInfo nextSoundInfo = new SoundInfo();
        private boolean hasNextSound;
        
        public SoundPlayer() {
        }

        public String getCurrentSoundId() {
            return currentSoundInfo != null 
                    ? currentSoundInfo.currentSoundId : "";
        }

        public boolean initialize() {
            try {
                createLine();
                line.open();
                line.start();
                running = true;
                thread = new Thread(this);
                thread.start();
            } catch (Exception ex) {
                return false;
            }
            return true;
        }
        
        private void createLine() throws Exception {
            Mixer mixer = AudioSystem.getMixer(null);
            SourceDataLine.Info sourceDataLineInfo = new DataLine.Info(
                    SourceDataLine.class, SOUND_AUDIO_FORMAT, BUFFER_SIZE);
            
            line = (SourceDataLine) mixer.getLine(sourceDataLineInfo);
        }

        public synchronized boolean isFree() {
            return currentSoundInfo.finished && !hasNextSound;
        }

        public synchronized boolean isPlaying() {
            return !currentSoundInfo.finished && !currentSoundInfo.paused;
        }
        
        public synchronized void play(
                byte[] sound, String soundId, boolean looped) {
            
            play(sound, soundId, looped, false);
        }
        
        public synchronized void play(
                byte[] sound, String soundId, boolean looped, boolean paused) {
            
            nextSoundInfo.currentPos = 0;
            nextSoundInfo.currentSoundId = soundId;
            nextSoundInfo.currentSound = sound;
            nextSoundInfo.looped = looped;
            nextSoundInfo.paused = paused;
            nextSoundInfo.finished = false;
            hasNextSound = true;
            notify();
        }

        public synchronized void stop() {
            currentSoundInfo.finished = true;
        }
        
        public synchronized void pause() {
            if (!currentSoundInfo.paused) {
                currentSoundInfo.paused = true;
            }
        }
        
        public synchronized void resume() {
            if (currentSoundInfo.paused) {
                currentSoundInfo.paused = false;
                notify();
            }
        }

        @Override
        public void run() {
            while (running) {
                synchronized (this) {
                    if (hasNextSound) {
                        currentSoundInfo.currentPos = nextSoundInfo.currentPos;
                        currentSoundInfo.currentSoundId 
                                = nextSoundInfo.currentSoundId;
                        
                        currentSoundInfo.currentSound 
                                = nextSoundInfo.currentSound;
                        
                        currentSoundInfo.looped = nextSoundInfo.looped;
                        currentSoundInfo.paused = nextSoundInfo.paused;
                        currentSoundInfo.finished = nextSoundInfo.finished;
                        hasNextSound = false;
                    }
                }
                if (currentSoundInfo.finished) {
                    line.flush();
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                        }
                    }
                }
                
                synchronized (this) {
                    if (currentSoundInfo.paused) {
                        line.drain();
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                        }
                    }
                }
                
                if (!currentSoundInfo.finished) {
                    line.write(currentSoundInfo.currentSound
                            , currentSoundInfo.currentPos, BUFFER_SIZE);
                    
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                    }
                    
                    currentSoundInfo.currentPos += BUFFER_SIZE;
                    
                    int progress 
                        = currentSoundInfo.currentSound.length - BUFFER_SIZE;
                    
                    if (currentSoundInfo.currentPos  >= progress) {
                        
                        if (!currentSoundInfo.looped) {
                            currentSoundInfo.finished = true;
                        }
                        else {
                            currentSoundInfo.currentPos = 0;
                        }
                    }
                    
                }
            }
        }
        
    }

}
