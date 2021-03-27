package io.shapez.game;

import io.shapez.game.systems.BeltSystem;
import io.shapez.game.systems.ItemEjectorSystem;

import java.io.IOException;

public class GameSystemManager {
    GameSystemWithFilter[] systems;

    public GameSystemManager() throws IOException {
        systems = new GameSystemWithFilter[]{new BeltSystem(), new ItemEjectorSystem()};
    }
}
