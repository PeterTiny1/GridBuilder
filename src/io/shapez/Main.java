package io.shapez;


import io.shapez.core.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;

public class Main extends JFrame implements WindowFocusListener {
    Board board;

    public Main() throws IOException {
        initUI();
    }

    private void initUI() throws IOException {
        board = new Board(this);
        JScrollPane scrollPane = new JScrollPane(board);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        board.addMouseWheelListener(board);
        add(scrollPane);
        addWindowFocusListener(this);
        pack();
        setIconImage(Resources.logo.getImage());
        setTitle("GridBuilder - Java Edition");
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

    @Override
    public void windowGainedFocus(WindowEvent e) {

    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        board.pressedKeys.clear();
    }
}
