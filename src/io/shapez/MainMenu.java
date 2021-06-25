package io.shapez;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {
    Main parent;

    public MainMenu(final Main parent) {
        this.parent = parent;
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.5;
        constraints.weighty = 0.5;
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
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(new JLabel("<html>SHAPEZ.IO<br/>JAVA EDITION</html>", SwingConstants.CENTER), constraints);
        constraints.gridy = 1;
        add(loadGame, constraints);
        constraints.gridy = 2;
        add(startGame, constraints);
    }
}
