package io.shapez.ui;

import io.shapez.Application;
import io.shapez.core.Resources;
import io.shapez.game.GameRoot;
import io.shapez.managers.SerializeManager;
import io.shapez.platform.SoundManager;
import io.shapez.util.TileUtil;
import io.shapez.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static io.shapez.managers.providers.MiscProvider.*;

public class MoreWindow extends JFrame {
    public static final JFrame L_moreFrame = new JFrame(moreWndName);
    public static final JButton saveButton = new JButton();
    public static final JButton loadButton = new JButton();
    public static final JButton clearButton = new JButton();
    public static final JPanel L_ioPanel = new JPanel();
    public static Application application;
    private static boolean initialised = false;

    private static void ResetTitle() {
        L_moreFrame.setTitle(gameName);
    }

    public static void internalUI_SaveAllChunks() {
        SoundManager.playSound(Resources.uiClickSound);
        SerializeManager.saveAll();
        ResetTitle();
        SoundManager.playSound(Resources.uiSuccessSound);
    }

    public static void internalUI_LoadAllChunks(GameRoot root) {
        SoundManager.playSound(Resources.uiClickSound);
        SerializeManager.loadAll(root);
        ResetTitle();
        SoundManager.playSound(Resources.uiSuccessSound);
    }

    public static void internalUI_ClearAllChunks() {
        SoundManager.playSound(Resources.uiClickSound);
        L_moreFrame.setTitle(UIUtil.getProcTitle(OP_CLEAR));
        //board.__clearAll();
        TileUtil.clearAll();
        ResetTitle();
    }

    public static void init(GameRoot root) {

        L_ioPanel.setOpaque(false);

        L_ioPanel.setLayout(new BoxLayout(L_ioPanel, BoxLayout.X_AXIS));

        initButton(saveButton, (ActionEvent e) -> internalUI_SaveAllChunks(), Resources.saveImage);
        initButton(loadButton, (ActionEvent e) -> internalUI_LoadAllChunks(root), Resources.loadImage);
        initButton(clearButton, (ActionEvent e) -> internalUI_ClearAllChunks(), Resources.clearImage);

        L_ioPanel.add(saveButton, BorderLayout.NORTH);
        L_ioPanel.add(loadButton, BorderLayout.NORTH);
        L_ioPanel.add(clearButton, BorderLayout.NORTH);

        //add(L_ioPanel);

        L_moreFrame.add(L_ioPanel);


        L_moreFrame.setPreferredSize(defWndDimension);
        L_moreFrame.setMinimumSize(defWndDimension);
        L_moreFrame.setSize(defWndDimension);

        initialised = true;
    }

    static void initButton(JButton button, ActionListener listener, Image image) {
        button.addActionListener(listener);
        button.setFocusable(false);
        button.setIcon(new ImageIcon(image.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        button.setPreferredSize(defDimensionBtn);
        button.setMaximumSize(defDimensionBtn);
    }

    public static void Show() {
        if (!initialised) {
            System.err.println("Can't show without initialisation");
            return;
        }
        L_moreFrame.pack();
        L_moreFrame.setLocationRelativeTo(null);

        application.window.setVisible(false);
        L_moreFrame.setResizable(false);

        L_moreFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                application.window.setVisible(true);
                SoundManager.playSound(Resources.uiClickSound);
            }
        });
        L_moreFrame.setSize(300, 100);
        L_moreFrame.setLocationRelativeTo(null);
        L_moreFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Free frame on close ...
        L_moreFrame.setIconImage(Resources.logo.getImage());

        // style 1 (exit on close) also terminates process...

        L_moreFrame.setVisible(true);
    }
}
