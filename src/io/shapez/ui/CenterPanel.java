package io.shapez.ui;

import io.shapez.core.Tile;
import io.shapez.core.Resources;
import io.shapez.game.Board;
import io.shapez.game.EntityTutorial;
import io.shapez.managers.SoundManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static io.shapez.core.Resources.*;
import static io.shapez.managers.providers.MiscProvider.*;

public class CenterPanel extends JPanel {
    private final Board board;
    public static JButton beltButton = new JButton();
    public static JButton minerButton = new JButton();
    public static JButton trashButton = new JButton();
    public static JButton rotatorButton = new JButton();

    public CenterPanel(Board board) throws IOException {
        this.board = board;
        setOpaque(false);
        JPanel innerPanel = new JPanel();
        innerPanel.setOpaque(false);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));

        beltButton.addActionListener(e ->
                selectItem(Tile.Belt));
        beltButton.setFocusable(false);
        beltButton.setIcon(new ImageIcon(ui_beltImage.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        beltButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        beltButton.setPreferredSize(defDimensionBtn);
        beltButton.setMaximumSize(defDimensionBtn);
        innerPanel.add(beltButton, BorderLayout.SOUTH);

        minerButton.addActionListener(e ->
                selectItem(Tile.Miner)
        );
        minerButton.setFocusable(false);
        minerButton.setIcon(new ImageIcon(ui_minerImage.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        minerButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        minerButton.setPreferredSize(defDimensionBtn);
        minerButton.setMaximumSize(defDimensionBtn);
        innerPanel.add(minerButton, BorderLayout.SOUTH);

        trashButton.addActionListener(e -> selectItem(Tile.Trash));
        trashButton.setFocusable(false);
        trashButton.setIcon(new ImageIcon(ui_trashImage.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        trashButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        trashButton.setPreferredSize(defDimensionBtn);
        trashButton.setMaximumSize(defDimensionBtn);
        innerPanel.add(trashButton, BorderLayout.SOUTH);

        rotatorButton.addActionListener(e -> selectItem(Tile.Rotator));
        rotatorButton.setFocusable(false);
        rotatorButton.setIcon(new ImageIcon(ui_rotatorImage.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        rotatorButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        rotatorButton.setPreferredSize(defDimensionBtn);
        rotatorButton.setMaximumSize(defDimensionBtn);
        innerPanel.add(rotatorButton, BorderLayout.SOUTH);

        this.add(innerPanel);
    }

    public void updateButtonAppearance() {
        switch (board.item) {
            case None:
                minerButton.setSelected(false);
                beltButton.setSelected(false);
                trashButton.setSelected(false);
                rotatorButton.setSelected(false);
                break;
            case Belt:
                minerButton.setSelected(false);
                trashButton.setSelected(false);
                rotatorButton.setSelected(false);
                beltButton.setSelected(true);
                break;
            case Miner:
                beltButton.setSelected(false);
                trashButton.setSelected(false);
                rotatorButton.setSelected(false);
                minerButton.setSelected(true);
                break;
            case Trash:
                beltButton.setSelected(false);
                minerButton.setSelected(false);
                rotatorButton.setSelected(false);
                trashButton.setSelected(true);
                break;
            case Rotator:
                minerButton.setSelected(false);
                beltButton.setSelected(false);
                trashButton.setSelected(false);
                rotatorButton.setSelected(true);
        }
        TopPanel.selectedILabel_Name.setText(EntityTutorial.GetTitle(board.item));
        TopPanel.selectedILabel_Description.setText(EntityTutorial.GetDescription(board.item));
        TopPanel.selectedILabel_Hotkey.setText(EntityTutorial.GetHotkey(board.item));
        SoundManager.playSound(Resources.uiClickSound);

    }

    public void selectItem(Tile item) {
        if (board.item == item) {
            board.item = Tile.None;
            board.hasItemSelected = false;
            System.out.println("Already selected. Now selected: " + board.item.toString());
            updateButtonAppearance();
            return;
        }
        board.item = item;
        board.hasItemSelected = board.item != Tile.None;
        System.out.println("Selected: " + item.toString());
        updateButtonAppearance();
    }
}
