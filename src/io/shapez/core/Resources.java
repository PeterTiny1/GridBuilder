package io.shapez.core;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Resources {

    // Logo/UI
    public static ImageIcon logo;


    // Textures
    public static BufferedImage belt;
    public static BufferedImage miner;
    public static BufferedImage trash;
    public static Image missingTexture;
    public static BufferedImage vignette;


    // Sounds
    public static File beltPlaceSound; // WIP
    public static File generic_placeTileSound;
    public static File generic_destroyTileSound;
    public static File uiClickSound;
    public static File uiSuccessSound;
    public static File uiDenySound;


    // Make sure to run with these jvm options:
//    -Dsun.java2d.d3d=true -Dsun.java2d.translaccel=true -Dsun.java2d.ddforcevram=true -Dsun.java2d.accthreshold=1

    // This will provide a insane performance boost, but on low-end systems it might overwhelm the hardware
    static {
        try {
            logo = new ImageIcon("src/resources/ui/logo.png");

            beltPlaceSound = new File("src/resources/sound/place_belt.wav");
            generic_placeTileSound = new File("src/resources/sound/place_building.wav");
            generic_destroyTileSound = new File("src/resources/sound/destroy_building.wav");
            uiClickSound = new File("src/resources/sound/uiclick.wav");
            uiSuccessSound = new File("src/resources/sound/uisuccess.wav");
            uiDenySound = new File("src/resources/sound/uierror.wav");

            vignette = ImageIO.read(new File("src/resources/vignette.lossless.png"));
            missingTexture = ImageIO.read(new File("src/resources/missing.png"));
            belt = ImageIO.read(new File("src/resources/tiles/belt.png"));
            miner = ImageIO.read(new File("src/resources/tiles/miner.png"));
            trash = ImageIO.read(new File("src/resources/tiles/trash.png"));
        } catch (IOException e) {
            System.out.println("Resource(s) missing");
            // e.printStackTrace();
        }
    }
}
