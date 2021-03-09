package io.shapez.ui;

import io.shapez.core.Resources;
import io.shapez.game.Board;
import io.shapez.managers.SerializeManager;
import io.shapez.managers.SettingsManager;
import io.shapez.managers.SoundManager;
import io.shapez.util.TileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TopPanel extends JPanel {
    JButton settingsButton = new JButton();
    JButton saveButton = new JButton();
    JButton loadButton = new JButton();
    JButton clearButton = new JButton();

    public static JLabel selectedILabel_Name = new JLabel();
    public static JLabel selectedILabel_Description = new JLabel();
    public static JLabel selectedILabel_Hotkey = new JLabel();

    void showSettings() {
        SoundManager.playSound(Resources.uiClickSound);
        SettingsManager.initSettingsWnd();
    }

    public TopPanel(Board board) {
        setOpaque(false);
        setLayout(new BorderLayout());
        JPanel L_innerPanel = new JPanel();
        JPanel L_morePanel = new JPanel();
        L_innerPanel.setOpaque(false);
        L_innerPanel.setLayout(new BoxLayout(L_innerPanel, BoxLayout.Y_AXIS));

        L_morePanel.setOpaque(false);
        L_morePanel.setLayout(new BoxLayout(L_morePanel, BoxLayout.Y_AXIS));

        Dimension d = new Dimension(70, 70);

        settingsButton.addActionListener(e ->
                showSettings()
        );
        settingsButton.setFocusable(false);
        settingsButton.setIcon(new ImageIcon(Resources.settingsImage.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
        settingsButton.setPreferredSize(d);
        settingsButton.setMaximumSize(d);

        saveButton.addActionListener((ActionEvent e) -> SerializeManager.saveAll(Board.usedChunks));
        saveButton.setFocusable(false);
        saveButton.setIcon(new ImageIcon(Resources.missingTexture.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
        saveButton.setPreferredSize(d);
        saveButton.setMaximumSize(d);

        loadButton.addActionListener((ActionEvent e) -> SerializeManager.loadAll(board));
        loadButton.setFocusable(false);
        loadButton.setIcon(new ImageIcon(Resources.missingTexture.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
        loadButton.setPreferredSize(d);
        loadButton.setMaximumSize(d);

        clearButton.addActionListener((ActionEvent e) -> board.__clearAll());
        clearButton.setFocusable(false);
        clearButton.setIcon(new ImageIcon(Resources.missingTexture.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
        clearButton.setPreferredSize(d);
        clearButton.setMaximumSize(d);

        selectedILabel_Name.setFocusable(false);
        selectedILabel_Name.setText("");
        selectedILabel_Name.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        selectedILabel_Description.setFocusable(false);
        selectedILabel_Description.setText("");
        selectedILabel_Description.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        selectedILabel_Hotkey.setFocusable(false);
        selectedILabel_Hotkey.setText("");
        selectedILabel_Hotkey.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));

        L_innerPanel.add(selectedILabel_Name, BorderLayout.WEST);
        L_innerPanel.add(selectedILabel_Description, BorderLayout.WEST);
        L_innerPanel.add(selectedILabel_Hotkey, BorderLayout.SOUTH);

       // L_morePanel.add(saveButton, BorderLayout.NORTH);
       // L_morePanel.add(loadButton, BorderLayout.NORTH);
      //  L_morePanel.add(clearButton, BorderLayout.NORTH);
        // todo: leave this to me lol
        add(settingsButton, BorderLayout.EAST);


        add(L_innerPanel);
        //add(L_morePanel);

    }
}
