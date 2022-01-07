package io.shapez.ui;

import io.shapez.Application;
import io.shapez.core.Tile;
import io.shapez.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static io.shapez.core.Resources.*;
import static io.shapez.managers.providers.MiscProvider.defDimensionBtn;

public class BottomPanel extends JPanel {
    public static final JButton beltButton = new JButton();
    public static final JButton minerButton = new JButton();
    public static final JButton trashButton = new JButton();
    public static final JButton rotatorButton = new JButton();
    private final Application application;
//    public static  JButton lowerLayerButton = new JButton();

    public BottomPanel(final Application application) {
        this.application = application;
        setOpaque(false);
        setDoubleBuffered(true);
        final JPanel innerPanel = new JPanel();
        innerPanel.setOpaque(false);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));

        initButton(beltButton, e -> selectItem(Tile.Belt), ui_beltImage);
        innerPanel.add(BottomPanel.beltButton, BorderLayout.SOUTH);

        initButton(minerButton, e -> selectItem(Tile.Miner), ui_minerImage);
        innerPanel.add(BottomPanel.minerButton, BorderLayout.SOUTH);

        initButton(trashButton, e -> selectItem(Tile.Trash), ui_trashImage);
        innerPanel.add(BottomPanel.trashButton, BorderLayout.SOUTH);

        initButton(rotatorButton, e -> selectItem(Tile.Rotator), ui_rotatorImage);
        innerPanel.add(BottomPanel.rotatorButton, BorderLayout.SOUTH);

        this.add(innerPanel);
    }

    void initButton(JButton button, ActionListener listener, Image icon) {
        button.addActionListener(listener);
        button.setFocusable(false);
        button.setIcon(new ImageIcon(icon.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        button.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        button.setPreferredSize(defDimensionBtn);
        button.setMaximumSize(defDimensionBtn);
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
