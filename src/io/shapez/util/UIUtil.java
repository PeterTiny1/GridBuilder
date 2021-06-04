package io.shapez.util;

import io.shapez.core.Resources;
import io.shapez.Application;
import io.shapez.game.EntityTutorial;
import io.shapez.game.platform.SoundManager;
import io.shapez.ui.BottomPanel;
import io.shapez.ui.TopPanel;

import static io.shapez.managers.providers.MiscProvider.*;

public class UIUtil {

    public static String getProcTitle(byte type) {
        return switch (type) {
            case OP_SAVE -> moreWndName.concat(" - Saving");
            case OP_LOAD -> moreWndName.concat(" - Loading");
            case OP_CLEAR -> moreWndName.concat(" - Clearing");
            default -> moreWndName;
            // I forgot Java doesnt have pointers XD
        };
    }

    public static void updateButtonAppearance() {
        switch (Application.item) {
            case None -> {
                BottomPanel.minerButton.setSelected(false);
                BottomPanel.beltButton.setSelected(false);
                BottomPanel.trashButton.setSelected(false);
                BottomPanel.rotatorButton.setSelected(false);
            }
            case Belt -> {
                BottomPanel.minerButton.setSelected(false);
                BottomPanel.trashButton.setSelected(false);
                BottomPanel.rotatorButton.setSelected(false);
                BottomPanel.beltButton.setSelected(true);
            }
            case Miner -> {
                BottomPanel.beltButton.setSelected(false);
                BottomPanel.trashButton.setSelected(false);
                BottomPanel.rotatorButton.setSelected(false);
                BottomPanel.minerButton.setSelected(true);
            }
            case Trash -> {
                BottomPanel.beltButton.setSelected(false);
                BottomPanel.minerButton.setSelected(false);
                BottomPanel.rotatorButton.setSelected(false);
                BottomPanel.trashButton.setSelected(true);
            }
            case Rotator -> {
                BottomPanel.minerButton.setSelected(false);
                BottomPanel.beltButton.setSelected(false);
                BottomPanel.trashButton.setSelected(false);
                BottomPanel.rotatorButton.setSelected(true);
            }
        }
        TopPanel.selectedILabel_Name.setText(EntityTutorial.GetTitle(Application.item));
        TopPanel.selectedILabel_Description.setText(EntityTutorial.GetDescription(Application.item));
        TopPanel.selectedILabel_Hotkey.setText(EntityTutorial.GetHotkey(Application.item));
        SoundManager.playSound(Resources.uiClickSound);

    }
}
