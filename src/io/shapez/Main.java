package io.shapez;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    public Main() {
        initUI();
    }

    private void initUI() {
        Board board = new Board();
        JScrollPane scrollPane = new JScrollPane(board);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        board.addMouseWheelListener(board);
        add(scrollPane);
        pack();
        setTitle("Grid Builder - Java Edition");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}
