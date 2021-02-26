package io.shapez;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Resources {
    public static BufferedImage belt;
    public static Image missingTexture;

    static {
        try {
            belt = ImageIO.read(new File("src/resources/tiles/belt.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
