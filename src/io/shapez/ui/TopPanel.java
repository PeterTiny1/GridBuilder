package io.shapez.ui;

import io.shapez.core.Resources;
import io.shapez.managers.SettingsManager;
import io.shapez.game.platform.SoundManager;

import javax.swing.*;
import java.awt.*;

import static io.shapez.managers.providers.MiscProvider.defDimensionBtn;

public class TopPanel extends JPanel {
    final JButton settingsButton = new JButton();

    public static final JLabel selectedILabel_Name = new JLabel();
    public static final JLabel selectedILabel_Description = new JLabel();
    public static final JLabel selectedILabel_Hotkey = new JLabel();

    void showSettings() {
        SoundManager.playSound(Resources.uiClickSound);
        // SettingsManager.initSettingsWnd();
        SettingsManager.showSettingswnd();
    }

    public TopPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        JPanel L_innerPanel = new JPanel();
        JPanel L_morePanel = new JPanel();
        L_innerPanel.setOpaque(false);
        L_innerPanel.setLayout(new BoxLayout(L_innerPanel, BoxLayout.Y_AXIS));

        L_morePanel.setOpaque(false);
        L_morePanel.setLayout(new BoxLayout(L_morePanel, BoxLayout.Y_AXIS));


        settingsButton.addActionListener(e ->
                showSettings()
        );
        settingsButton.setFocusable(false);
        settingsButton.setIcon(new ImageIcon(Resources.settingsImage.getScaledInstance(defDimensionBtn.width, defDimensionBtn.height, Image.SCALE_SMOOTH)));
        settingsButton.setPreferredSize(defDimensionBtn);
        settingsButton.setMaximumSize(defDimensionBtn);


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


        add(settingsButton, BorderLayout.EAST);


        add(L_innerPanel);
        //add(L_morePanel);

    }
}
