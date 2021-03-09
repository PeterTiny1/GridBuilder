package io.shapez.util;

import io.shapez.core.Resources;
import io.shapez.game.Board;
import io.shapez.game.EntityTutorial;
import io.shapez.managers.SoundManager;
import io.shapez.ui.BottomPanel;
import io.shapez.ui.TopPanel;

public class UIUtil {
    public static void updateButtonAppearance() {
        switch (Board.item) {
            case None:
                BottomPanel.minerButton.setSelected(false);
                BottomPanel.beltButton.setSelected(false);
                BottomPanel.trashButton.setSelected(false);
                BottomPanel.rotatorButton.setSelected(false);
                break;
            case Belt:
                BottomPanel.minerButton.setSelected(false);
                BottomPanel.trashButton.setSelected(false);
                BottomPanel.rotatorButton.setSelected(false);
                BottomPanel.beltButton.setSelected(true);
                break;
            case Miner:
                BottomPanel.beltButton.setSelected(false);
                BottomPanel.trashButton.setSelected(false);
                BottomPanel.rotatorButton.setSelected(false);
                BottomPanel.minerButton.setSelected(true);
                break;
            case Trash:
                BottomPanel.beltButton.setSelected(false);
                BottomPanel.minerButton.setSelected(false);
                BottomPanel.rotatorButton.setSelected(false);
                BottomPanel.trashButton.setSelected(true);
                break;
            case Rotator:
                BottomPanel.minerButton.setSelected(false);
                BottomPanel.beltButton.setSelected(false);
                BottomPanel.trashButton.setSelected(false);
                BottomPanel.rotatorButton.setSelected(true);
        }
        TopPanel.selectedILabel_Name.setText(EntityTutorial.GetTitle(Board.item));
        TopPanel.selectedILabel_Description.setText(EntityTutorial.GetDescription(Board.item));
        TopPanel.selectedILabel_Hotkey.setText(EntityTutorial.GetHotkey(Board.item));
        SoundManager.playSound(Resources.uiClickSound);

    }
}
