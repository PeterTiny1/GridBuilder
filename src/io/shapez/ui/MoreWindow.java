package io.shapez.ui;

import io.shapez.core.Resources;
import io.shapez.game.Board;
import io.shapez.managers.SerializeManager;
import io.shapez.managers.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static io.shapez.managers.providers.MiscProvider.*;

public class MoreWindow extends JFrame {
    public static Board board;

    private static boolean initialised = false;

    public static JFrame L_moreFrame = new JFrame(moreWndName);
    public static JButton saveButton = new JButton();
    public static JButton loadButton = new JButton();
    public static JButton clearButton = new JButton();
    public static JPanel L_ioPanel = new JPanel();

    private static void ResetTitle(){
        L_moreFrame.setTitle(gameName);
    }
    public static void internalUI_SaveAllChunks(){
        L_moreFrame.setTitle(gameName + " - Saving...");
        SerializeManager.saveAll(Board.usedChunks);
        ResetTitle();
    }
    public static void internalUI_LoadAllChunks(){
        L_moreFrame.setTitle(gameName + " - Loading...");
        SerializeManager.loadAll(board);
        ResetTitle();
    }
    public static void internalUI_ClearAllChunks(){
        L_moreFrame.setTitle(gameName + " - Clearing...");
        board.__clearAll();
        ResetTitle();
    }
    public static void Init(){

        L_ioPanel.setOpaque(false);

        L_ioPanel.setLayout(new BoxLayout(L_ioPanel, BoxLayout.X_AXIS));

        saveButton.addActionListener((ActionEvent e) -> internalUI_SaveAllChunks());
        saveButton.setFocusable(false);
        saveButton.setIcon(new ImageIcon(Resources.saveImage.getScaledInstance(defDimension.width, defDimension.height, Image.SCALE_SMOOTH)));
        saveButton.setPreferredSize(defDimension);
        saveButton.setMaximumSize(defDimension);

        loadButton.addActionListener((ActionEvent e) -> internalUI_LoadAllChunks());
        loadButton.setFocusable(false);
        loadButton.setIcon(new ImageIcon(Resources.loadImage.getScaledInstance(defDimension.width, defDimension.height, Image.SCALE_SMOOTH)));
        loadButton.setPreferredSize(defDimension);
        loadButton.setMaximumSize(defDimension);

        clearButton.addActionListener((ActionEvent e) -> internalUI_ClearAllChunks());
        clearButton.setFocusable(false);
        clearButton.setIcon(new ImageIcon(Resources.clearImage.getScaledInstance(defDimension.width, defDimension.height, Image.SCALE_SMOOTH)));
        clearButton.setPreferredSize(defDimension);
        clearButton.setMaximumSize(defDimension);


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

    public static void Show(){
        if(!initialised){ System.err.println("Can\'t show without initialisation"); return; }
        L_moreFrame.pack();
        L_moreFrame.setLocationRelativeTo(null);

        board.window.setVisible(false);
        L_moreFrame.setResizable(false);

        L_moreFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                board.window.setVisible(true);
                SoundManager.playSound(Resources.uiClickSound); }
        });
        L_moreFrame.setSize(300, 100);
        L_moreFrame.setLocationRelativeTo(null);
        L_moreFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Free frame on close ...
        L_moreFrame.setIconImage(Resources.logo.getImage());

        // style 1 (exit on close) also terminates process...

        L_moreFrame.setVisible(true);
    }
}
