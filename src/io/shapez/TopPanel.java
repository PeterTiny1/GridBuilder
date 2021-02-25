package io.shapez;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class TopPanel extends JPanel {
    JButton settingsButton = new JButton();

    public TopPanel() throws IOException {

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        Dimension d = new Dimension(70, 70);
        BufferedImage settingsImage = ImageIO.read(new File("src/resources/settings.png"));
        settingsButton.setFocusable(false);
        settingsButton.setIcon(new ImageIcon(settingsImage.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
        settingsButton.setAlignmentY(JComponent.TOP_ALIGNMENT);
        settingsButton.setPreferredSize(d);
        settingsButton.setMaximumSize(d);
        add(settingsButton);
    }
}
