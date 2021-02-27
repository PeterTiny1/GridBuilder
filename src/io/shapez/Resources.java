package io.shapez;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Resources {
    public static BufferedImage belt;
    public static BufferedImage miner;
    public static BufferedImage trash;
    public static Image missingTexture;

    static {
        try {
            missingTexture = ImageIO.read(new File("src/resources/missing.png"));
            belt = ImageIO.read(new File("src/resources/tiles/belt.png"));
            miner = ImageIO.read(new File("src/resources/tiles/miner.png"));
            trash = ImageIO.read(new File("src/resources/tiles/trash.png"));
        } catch (IOException e) {
            System.out.println("Texture(s) missing");
           // e.printStackTrace();
        }
    }
}
