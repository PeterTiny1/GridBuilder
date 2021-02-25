package io.shapez;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class Board extends JPanel implements ActionListener, MouseWheelListener, KeyListener, MouseMotionListener, MouseListener {
    // UI
    public CenterPanel centerPanel = new CenterPanel(this);
    public TopPanel topPanel = new TopPanel();

    private int scale = 40;
    private int offsetX, offsetY;
    private final ArrayList<Character> pressedKeys = new ArrayList<>();
    private int gridOffsetX, gridOffsetY;
    private int previousMX, previousMY;
    public boolean hasItemSelected = false;

    public Items Item;

    public Board() throws IOException {
        initBoard();
    }

    private void initBoard() throws IOException {
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        setFocusable(true);
        requestFocusInWindow();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(topPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.SOUTH);
        int b_HEIGHT = 350;
        int b_WIDTH = 350;
        setPreferredSize(new Dimension(b_WIDTH, b_HEIGHT));
        int DELAY = 10;
        Timer timer = new Timer(DELAY, this);
        timer.start();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace(); // your os is unsupported or registry is fucked if this happens
            // todo: tell user that OS is unsupported
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        Graphics2D g2d = (Graphics2D) g; // cast graphics to graphics2d, this is not backwards compatible
        g.setColor(Color.BLACK);
        for (int x = offsetX; x < size.width; x += scale) {
            g.drawLine(x, 0, x, size.height);
        }
        for (int y = offsetY; y < size.height; y += scale) {
            g.drawLine(0, y, size.width, y);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (pressedKeys.size() > 0) {
            for (Character key : pressedKeys) {
                switch (Character.toUpperCase(key)) {
                    case 'S':
                        changeOffset(0, -5);
                        break;
                    case 'W':
                        changeOffset(0, 5);
                        break;
                    case 'A':
                        changeOffset(5, 0);
                        break;
                    case 'D':
                        changeOffset(-5, 0);
                        break;
                }
            }
            repaint();
        }

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            if (scale < 120) {
                scale *= 1.2;
            }
        } else if (scale > 5) {
            scale /= 1.2;
        }
        changeOffset(0, 0);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!pressedKeys.contains(e.getKeyChar())) {
            pressedKeys.add(e.getKeyChar());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (pressedKeys.contains(e.getKeyChar())) {
            pressedKeys.remove((Character) e.getKeyChar());
        }
    }

    public void changeOffset(int x, int y) {
        offsetX += x;
        gridOffsetX += offsetX / scale;
        offsetX %= scale;
        offsetY += y;
        gridOffsetY += offsetY / scale;
        offsetY %= scale;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!hasItemSelected) changeOffset(e.getX() - previousMX, e.getY() - previousMY);
        previousMX = e.getX();
        previousMY = e.getY();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        previousMX = e.getX();
        previousMY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
//        System.out.println("Mouse started clicking at" + e.getX() + " " + e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
