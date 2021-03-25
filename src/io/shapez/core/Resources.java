package io.shapez.core;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Resources {

    // Logo/UI
    public static ImageIcon logo;
    public static BufferedImage settingsImage;
    public static BufferedImage saveImage;
    public static BufferedImage loadImage;
    public static BufferedImage clearImage;
    public static BufferedImage ui_beltImage;
    public static BufferedImage ui_minerImage;
    public static BufferedImage ui_trashImage;
    public static BufferedImage ui_rotatorImage;

    // Textures
    public static BufferedImage belt;
    public static BufferedImage miner;
    public static BufferedImage trash;
    public static BufferedImage rotator;
    public static Image missingTexture;
    public static BufferedImage vignette;


    // Sounds
    public static URL beltPlaceSound; // WIP
    public static URL generic_placeTileSound;
    public static URL generic_destroyTileSound;
    public static URL uiClickSound;
    public static URL uiSuccessSound;
    public static URL uiDenySound;


    // Make sure to run with these jvm options:
//    -Dsun.java2d.d3d=true -Dsun.java2d.translaccel=true -Dsun.java2d.ddforcevram=true -Dsun.java2d.accthreshold=1

    // This will provide a insane performance boost, but on low-end systems it might overwhelm the hardware
    static {
        try {
            logo = new ImageIcon(Resources.class.getResource("/ui/logo.png"));
            settingsImage = ImageIO.read(Resources.class.getResource("/ui/settings.png"));
            saveImage = ImageIO.read(Resources.class.getResource("/ui/save.png"));
            loadImage = ImageIO.read(Resources.class.getResource("/ui/open.png"));
            clearImage = ImageIO.read(Resources.class.getResource("/ui/remove.png"));
            ui_beltImage = ImageIO.read(Resources.class.getResource("/ui/belt.png"));
            ui_minerImage = ImageIO.read(Resources.class.getResource("/ui/miner.png"));
            ui_trashImage = ImageIO.read(Resources.class.getResource("/ui/trash.png"));
            ui_rotatorImage = ImageIO.read(Resources.class.getResource("/ui/rotator.png"));

            beltPlaceSound = Resources.class.getResource("/sound/place_belt.wav");
            generic_placeTileSound = Resources.class.getResource("/sound/place_building.wav");
            generic_destroyTileSound = Resources.class.getResource("/sound/destroy_building.wav");
            uiClickSound = Resources.class.getResource("/sound/uiclick.wav");
            uiSuccessSound = Resources.class.getResource("/sound/uisuccess.wav");
            uiDenySound = Resources.class.getResource("/sound/uierror.wav");

            vignette = ImageIO.read(Resources.class.getResource("/vignette.lossless.png"));
            missingTexture = ImageIO.read(Resources.class.getResource("/missing.png"));
            belt = ImageIO.read(Resources.class.getResource("/tiles/belt.png"));
            miner = ImageIO.read(Resources.class.getResource("/tiles/miner.png"));
            trash = ImageIO.read(Resources.class.getResource("/tiles/trash.png"));
            rotator = ImageIO.read(Resources.class.getResource("/tiles/rotator.png"));
        } catch (IOException e) {
            System.out.println("Resource(s) missing");
            // e.printStackTrace();
        }
    }
}
