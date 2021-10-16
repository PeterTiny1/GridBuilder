package io.shapez.game;


import java.util.ArrayList;

public class SoundProxy {
    private final GameRoot root;
    ArrayList<Object> playing3DSounds = new ArrayList<>();
    ArrayList<Object> playingUiSounds = new ArrayList<>();

    public SoundProxy(final GameRoot root) {
        this.root = root;
    }
}
