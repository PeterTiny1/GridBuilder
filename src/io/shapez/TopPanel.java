package io.shapez;

import io.shapez.managers.SettingsManager;
import io.shapez.managers.SoundManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class TopPanel extends JPanel {
    JButton settingsButton = new JButton();
    public static JLabel selectedILabel_Name = new JLabel();
    public static JLabel selectedILabel_Description = new JLabel();
    public static JLabel selectedILabel_Hotkey = new JLabel();

    void showSettings(){
        SoundManager.playSound(Resources.uiClickSound);
        SettingsManager.initSettingsWnd();
    }
    public TopPanel() throws IOException {
        setOpaque(false);
        setLayout(new BorderLayout());
        JPanel L_innerPanel = new JPanel();
        L_innerPanel.setOpaque(false);
        L_innerPanel.setLayout(new BoxLayout(L_innerPanel, BoxLayout.Y_AXIS));

        Dimension d = new Dimension(70, 70);
        BufferedImage settingsImage = ImageIO.read(new File("src/resources/ui/settings.png"));
        settingsButton.addActionListener(e ->
         showSettings()
        );
        settingsButton.setFocusable(false);
        settingsButton.setIcon(new ImageIcon(settingsImage.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
        settingsButton.setPreferredSize(d);
        settingsButton.setMaximumSize(d);

        selectedILabel_Name.setFocusable(false);
        selectedILabel_Name.setText("");
        selectedILabel_Name.setFont(new Font(Font.SANS_SERIF,  Font.PLAIN, 30));
        selectedILabel_Description.setFocusable(false);
        selectedILabel_Description.setText("");
        selectedILabel_Description.setFont(new Font(Font.SANS_SERIF,  Font.PLAIN, 15));
        selectedILabel_Hotkey.setFocusable(false);
        selectedILabel_Hotkey.setText("");
        selectedILabel_Hotkey.setFont(new Font(Font.SANS_SERIF,  Font.PLAIN, 15));

        L_innerPanel.add(selectedILabel_Name, BorderLayout.WEST);
        L_innerPanel.add(selectedILabel_Description, BorderLayout.WEST);
        L_innerPanel.add(selectedILabel_Hotkey, BorderLayout.SOUTH);

        add(settingsButton, BorderLayout.EAST);
        add(L_innerPanel);

    }
}
