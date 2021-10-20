package io.shapez.util;

import io.shapez.Application;
import io.shapez.core.Resources;
import io.shapez.core.Tile;
import io.shapez.game.EntityTutorial;
import io.shapez.platform.SoundManager;
import io.shapez.ui.BottomPanel;
import io.shapez.ui.TopPanel;

import static io.shapez.managers.providers.MiscProvider.*;

public class UIUtil {

    public static String getProcTitle(byte type) {
        return moreWndName.concat(switch (type) {
            case OP_SAVE -> " - Saving";
            case OP_LOAD -> " - Loading";
            case OP_CLEAR -> " - Clearing";
            default -> "";
        });
    }

    public static void updateButtonAppearance() {
        BottomPanel.beltButton.setSelected(Application.item == Tile.Belt);
        BottomPanel.minerButton.setSelected(Application.item == Tile.Miner);
        BottomPanel.rotatorButton.setSelected(Application.item == Tile.Rotator);
        BottomPanel.trashButton.setSelected(Application.item == Tile.Trash);
        TopPanel.selectedILabel_Name.setText(EntityTutorial.GetTitle(Application.item));
        TopPanel.selectedILabel_Description.setText(EntityTutorial.GetDescription(Application.item));
        TopPanel.selectedILabel_Hotkey.setText(EntityTutorial.GetHotkey(Application.item));
        SoundManager.playSound(Resources.uiClickSound);

    }
}
