package io.shapez;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {


    public static synchronized void playSound(final File str) {
        new Thread(new Runnable() {

            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(str);
                    Clip clip = AudioSystem.getClip();
                    clip.open(stream);
                    clip.start();
                    if(clip.isActive()){

                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }




}
