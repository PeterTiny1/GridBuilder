package io.shapez;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Resources {

    // Textures
    public static BufferedImage belt;
    public static BufferedImage miner;
    public static BufferedImage trash;
    public static Image missingTexture;


    // Sounds
    public static File beltPlaceSound; // WIP
    public static File generic_placeTileSound;
    public static File generic_destroyTileSound;
    public static File uiClickSound;
    public static File uiSuccessSound;
    public static File uiDenySound;



    static {
        try {
            beltPlaceSound =   new File("src/resources/sound/place_belt.wav");
            generic_placeTileSound =   new File("src/resources/sound/place_building.wav");
            generic_destroyTileSound = new File("src/resources/sound/destroy_building.wav");
            uiClickSound =             new File("src/resources/sound/uiclick.wav");
            uiSuccessSound =           new File("src/resources/sound/uisuccess.wav");
            uiDenySound =              new File("src/resources/sound/uierror.wav");

            missingTexture =           ImageIO.read(new File("src/resources/missing.png"));
            belt =                     ImageIO.read(new File("src/resources/tiles/belt.png"));
            miner =                    ImageIO.read(new File("src/resources/tiles/miner.png"));
            trash =                    ImageIO.read(new File("src/resources/tiles/trash.png"));
        } catch (IOException e) {
            System.out.println("Resource(s) missing");
           // e.printStackTrace();
        }
    }
}
