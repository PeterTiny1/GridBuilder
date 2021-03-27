package io.shapez.game.systems;

import io.shapez.core.Direction;
import io.shapez.game.Component;
import io.shapez.game.GameSystemWithFilter;
import io.shapez.game.components.BeltComponent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BeltSystem extends GameSystemWithFilter {
    private HashMap<Direction, ArrayList<BufferedImage>> beltAnimations;
    private HashMap<Direction, BufferedImage> beltSprites;
    final byte BELT_ANIM_COUNT = 14;

    public BeltSystem() throws IOException {
        super(new Component[]{new BeltComponent(null)});
        beltSprites = new HashMap<>() {{
            put(Direction.Top, ImageIO.read(BeltSystem.class.getResource("/sprites/forward_0.png")));
            put(Direction.Left, ImageIO.read(BeltSystem.class.getResource("/sprites/left_0.png")));
            put(Direction.Right, ImageIO.read(BeltSystem.class.getResource("/sprites/right_0.png")));
        }};
        beltAnimations = new HashMap<>() {{
            put(Direction.Top, new ArrayList<>());
            put(Direction.Left, new ArrayList<>());
            put(Direction.Right, new ArrayList<>());
        }};
        for (int i = 0; i < BELT_ANIM_COUNT; i++) {
            this.beltAnimations.get(Direction.Top).add(ImageIO.read(BeltSystem.class.getResource("/sprites/forward_" + i + ".png")));
            this.beltAnimations.get(Direction.Left).add(ImageIO.read(BeltSystem.class.getResource("/sprites/left_" + i + ".png")));
            this.beltAnimations.get(Direction.Right).add(ImageIO.read(BeltSystem.class.getResource("/sprites/right_" + i + ".png")));
        }
    }
}
