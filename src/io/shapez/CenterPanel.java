package io.shapez;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class CenterPanel extends JPanel {
    private final Board board;
    public JButton beltButton = new JButton();

    public CenterPanel(Board board) throws IOException {
        this.board = board;
        setOpaque(false);
        JPanel innerPanel = new JPanel();
        innerPanel.setOpaque(false);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));

        Dimension d = new Dimension(70, 70);
        BufferedImage beltImage = ImageIO.read(new File("src/resources/belt.png"));

        beltButton.addActionListener(e ->
                SelectItem(Items.Belt));
        beltButton.setFocusable(false);
        beltButton.setIcon(new ImageIcon(beltImage.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
        beltButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
        beltButton.setPreferredSize(d);
        beltButton.setMaximumSize(d);
        innerPanel.add(beltButton, BorderLayout.SOUTH);
        this.add(innerPanel);
    }

    public void UpdateButtonAppearance() {
        switch (board.Item) {
            case None:
                beltButton.setSelected(false);
                break;
            case Belt:
                beltButton.setSelected(true);
                break;
        }
    }

    public void SelectItem(Items item) {
        if (board.Item == item) {
            board.Item = Items.None;
            board.hasItemSelected = false;
            System.out.println("Already selected. Now selected: " + board.Item.toString());
            UpdateButtonAppearance();
            return;
        }
        board.Item = item;
        board.hasItemSelected = board.Item != Items.None;
        System.out.println("Selected: " + item.toString());
        UpdateButtonAppearance();
    }
}
