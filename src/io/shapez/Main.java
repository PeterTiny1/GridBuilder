package io.shapez;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main extends JFrame {
    public Main() throws IOException {
        initUI();
    }

    private void initUI() throws IOException {
        Board board = new Board();
        JScrollPane scrollPane = new JScrollPane(board);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        board.addMouseWheelListener(board);
        add(scrollPane);
        pack();
        setTitle("Better version of Aurumaker's Grid Builder");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Main main = null;
            try {
                main = new Main();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert main != null;
            main.setVisible(true);
        });
    }
}
