package io.shapez.game.systems;

import io.shapez.core.Direction;
import io.shapez.game.Component;
import io.shapez.game.GameSystemWithFilter;
import io.shapez.game.components.BeltComponent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BeltSystem extends GameSystemWithFilter {
    private final HashMap<Direction, ArrayList<Image>> beltAnimations;
    private final HashMap<Direction, Image> beltSprites;
    final byte BELT_ANIM_COUNT = 14;

    public BeltSystem() throws IOException {
        super(new Component[]{new BeltComponent(null)});
        this.beltSprites = new HashMap<Direction, Image>() {{
            put(Direction.Top, ImageIO.read(getClass().getResource("/sprites/belt/built/forward_0.png")));
            put(Direction.Left, ImageIO.read(getClass().getResource("/sprites/belt/built/left_0.png")));
            put(Direction.Right, ImageIO.read(getClass().getResource("/sprites/belt/left_0.png")));
        }};
        this.beltAnimations = new HashMap<>() {{
            put(Direction.Top, new ArrayList<>());
            put(Direction.Left, new ArrayList<>());
            put(Direction.Right, new ArrayList<>());
        }};
        for (int i = 0; i < BELT_ANIM_COUNT; i++) {
            this.beltAnimations.get(Direction.Top).add(ImageIO.read(getClass().getResource("sprites/belt/built/forward_" + i + ".png")));
            this.beltAnimations.get(Direction.Left).add(ImageIO.read(getClass().getResource("sprites/belt/built/forward_" + i + ".png")));
            this.beltAnimations.get(Direction.Right).add(ImageIO.read(getClass().getResource("sprites/belt/built/forward_" + i + ".png")));
        }
    }
}
