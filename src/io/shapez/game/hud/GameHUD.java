package io.shapez.game.hud;

import io.shapez.game.GameRoot;
import io.shapez.game.hud.parts.*;

public class GameHUD {
    private final GameRoot root;
    private HUDPartTutorialHints tutorialHints;
    private HUDInteractiveTutorial interactiveTutorial;
    private HUDVignetteOverlay vignetteOverlay;
    private HUDColorBlindHelper colorBlindHelper;
    HUDBuildingPlacer buildingPlacer;

    public GameHUD(final GameRoot root) {
        this.root = root;
    }

    public void initialize() {
        buildingPlacer = new HUDBuildingPlacer(this.root);
        if (this.root.app.settings.getAllSettings().offerHints) {
            this.tutorialHints = new HUDPartTutorialHints(this.root);
            this.interactiveTutorial = new HUDInteractiveTutorial(this.root);
        }

        if (this.root.app.settings.getAllSettings().vignette) {
            this.vignetteOverlay = new HUDVignetteOverlay(this.root);
        }

        if (this.root.app.settings.getAllSettings().enableColorBlindHelper) {
            this.colorBlindHelper = new HUDColorBlindHelper(this.root);
        }

        // TODO: Add elements to GUI
    }

    public boolean shouldPauseRendering() {
        return false;
    }

    public void update() {
//        if (!this.root.gameInitialised) {
//        }
//        for (String key : this.parts) {
//            this.parts.get
//        }

    }
}
