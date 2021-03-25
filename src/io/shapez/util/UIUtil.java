package io.shapez.util;

import io.shapez.core.Resources;
import io.shapez.game.Board;
import io.shapez.game.EntityTutorial;
import io.shapez.managers.SoundManager;
import io.shapez.ui.BottomPanel;
import io.shapez.ui.TopPanel;

import static io.shapez.managers.providers.MiscProvider.*;

public class UIUtil {

    public static String getProcTitle(byte type) {
        String msg = moreWndName;
        // I forgot Java doesnt have pointers XD
        switch (type) {
            case OP_SAVE -> msg = msg.concat(" - Saving");
            case OP_LOAD -> msg = msg.concat(" - Loading");
            case OP_CLEAR -> msg = msg.concat(" - Clearing");
        }
        return msg;
    }

    public static void updateButtonAppearance() {
        switch (Board.item) {
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
        TopPanel.selectedILabel_Name.setText(EntityTutorial.GetTitle(Board.item));
        TopPanel.selectedILabel_Description.setText(EntityTutorial.GetDescription(Board.item));
        TopPanel.selectedILabel_Hotkey.setText(EntityTutorial.GetHotkey(Board.item));
        SoundManager.playSound(Resources.uiClickSound);

    }
}
