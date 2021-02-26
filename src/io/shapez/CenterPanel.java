package io.shapez;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;

public class CenterPanel extends JPanel {
    private final Board board;
    public static JButton beltButton = new JButton();
    public static JButton minerButton = new JButton();

    public CenterPanel(Board board) throws IOException {
        this.board = board;
        setOpaque(false);
        JPanel innerPanel = new JPanel();
        innerPanel.setOpaque(false);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));

        Dimension d = new Dimension(70, 70);

        BufferedImage beltImage = ImageIO.read(new File("src/resources/ui/belt.png"));
        BufferedImage minerImage = ImageIO.read(new File("src/resources/ui/miner.png"));

        beltButton.addActionListener(e ->
                selectItem(Items.Belt));
        beltButton.setFocusable(false);
        beltButton.setIcon(new ImageIcon(beltImage.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
        beltButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        beltButton.setPreferredSize(d);
        beltButton.setMaximumSize(d);
        innerPanel.add(beltButton, BorderLayout.SOUTH);

        minerButton.addActionListener(e -> selectItem(Items.Miner));
        minerButton.setFocusable(false);
        minerButton.setIcon(new ImageIcon(minerImage.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
        minerButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        minerButton.setPreferredSize(d);
        minerButton.setMaximumSize(d);
        innerPanel.add(minerButton, BorderLayout.SOUTH);

        this.add(innerPanel);
    }

    public void updateButtonAppearance() {
        switch (board.item) {
            case None:
                minerButton.setSelected(false);
                beltButton.setSelected(false);
                break;
            case Belt:
                minerButton.setSelected(false);
                beltButton.setSelected(true);
                break;
            case Miner:
                beltButton.setSelected(false);
                minerButton.setSelected(true);
                break;
        }
    }

    public void selectItem(Items item) {
        if (board.item == item) {
            board.item = Items.None;
            board.hasItemSelected = false;
            System.out.println("Already selected. Now selected: " + board.item.toString());
            updateButtonAppearance();
            return;
        }
        board.item = item;
        board.hasItemSelected = board.item != Items.None;
        System.out.println("Selected: " + item.toString());
        updateButtonAppearance();
    }
}
