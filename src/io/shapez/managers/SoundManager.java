package io.shapez.managers;

import io.shapez.managers.SettingsManager;

import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {


    public static synchronized void playSound(final File str) {
        if(!SettingsManager.allowSound)return;
        // The wrapper thread is unnecessary, unless it blocks on the
// Clip finishing; see comments.
        new Thread(() -> {
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(str);
                Clip clip = AudioSystem.getClip();
                clip.open(stream);
                clip.start();
                clip.isActive();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }




}
