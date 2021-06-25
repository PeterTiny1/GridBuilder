package io.shapez;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {
    Main parent;

    public MainMenu(final Main parent) {
        this.parent = parent;
        final int b_HEIGHT = 500;
        final int b_WIDTH = 500;
        setPreferredSize(new Dimension(b_WIDTH, b_HEIGHT));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);

        } catch (final ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace(); // your os is unsupported or registry is fucked if this happens
            JOptionPane.showMessageDialog(null, "Unsupported styles. " +
                    "Your OS is not supported or registry may be broken. " +
                    "UI might not work like intended", "Style loading failed", JOptionPane.ERROR_MESSAGE);
        }
        final JButton loadGame = new JButton("LOAD GAME");
        final JButton startGame = new JButton("NEW GAME");
        loadGame.addActionListener(e -> parent.initApplication());
        startGame.addActionListener(e -> parent.initApplication());
        add(new JLabel("<html>SHAPEZ.IO<br/>JAVA EDITION</html>", SwingConstants.CENTER), BorderLayout.NORTH);
        add(loadGame, BorderLayout.CENTER);
        add(startGame, BorderLayout.SOUTH);
    }
}
