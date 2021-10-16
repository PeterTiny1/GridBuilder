package io.shapez.ui;

import io.shapez.Application;
import io.shapez.core.Tile;
import io.shapez.util.UIUtil;

import javax.swing.*;
import java.awt.*;

import static io.shapez.core.Resources.*;
import static io.shapez.managers.providers.MiscProvider.defDimensionBtn;

public class BottomPanel extends JPanel {
    private final Application application;
    public static final JButton beltButton = new JButton();
    public static final JButton minerButton = new JButton();
    public static final JButton trashButton = new JButton();
    public static final JButton rotatorButton = new JButton();
//    public static  JButton lowerLayerButton = new JButton();

    public BottomPanel(final Application application) {
        this.application = application;
        setOpaque(false);
        setDoubleBuffered(true);
        final JPanel innerPanel = new JPanel();
        innerPanel.setOpaque(false);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));


        BottomPanel.beltButton.addActionListener(e ->
                selectItem(Tile.Belt));
        BottomPanel.beltButton.setFocusable(false);
        BottomPanel.beltButton.setIcon(new ImageIcon(ui_beltImage.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        BottomPanel.beltButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        BottomPanel.beltButton.setPreferredSize(defDimensionBtn);
        BottomPanel.beltButton.setMaximumSize(defDimensionBtn);
        innerPanel.add(BottomPanel.beltButton, BorderLayout.SOUTH);

        BottomPanel.minerButton.addActionListener(e ->
                selectItem(Tile.Miner)
        );
        BottomPanel.minerButton.setFocusable(false);
        BottomPanel.minerButton.setIcon(new ImageIcon(ui_minerImage.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        BottomPanel.minerButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        BottomPanel.minerButton.setPreferredSize(defDimensionBtn);
        BottomPanel.minerButton.setMaximumSize(defDimensionBtn);
        innerPanel.add(BottomPanel.minerButton, BorderLayout.SOUTH);

        BottomPanel.trashButton.addActionListener(e -> selectItem(Tile.Trash));
        BottomPanel.trashButton.setFocusable(false);
        BottomPanel.trashButton.setIcon(new ImageIcon(ui_trashImage.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        BottomPanel.trashButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        BottomPanel.trashButton.setPreferredSize(defDimensionBtn);
        BottomPanel.trashButton.setMaximumSize(defDimensionBtn);
        innerPanel.add(BottomPanel.trashButton, BorderLayout.SOUTH);

        BottomPanel.rotatorButton.addActionListener(e -> selectItem(Tile.Rotator));
        BottomPanel.rotatorButton.setFocusable(false);
        BottomPanel.rotatorButton.setIcon(new ImageIcon(ui_rotatorImage.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        BottomPanel.rotatorButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        BottomPanel.rotatorButton.setPreferredSize(defDimensionBtn);
        BottomPanel.rotatorButton.setMaximumSize(defDimensionBtn);
        innerPanel.add(BottomPanel.rotatorButton, BorderLayout.SOUTH);

        this.add(innerPanel);
    }


    public void selectItem(final Tile item) {
        if (item != null) {
            if (Application.item == item) {
                Application.item = Tile.None;
                application.hasItemSelected = false;
                System.out.println("Already selected. Now selected: " + Application.item);
                UIUtil.updateButtonAppearance();
                return;
            }
            Application.item = item;
            application.hasItemSelected = Application.item != Tile.None;
            System.out.println("Selected: " + item);
            UIUtil.updateButtonAppearance();
        }
    }
}
