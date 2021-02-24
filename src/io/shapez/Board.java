package io.shapez;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Board extends JPanel implements ActionListener, MouseWheelListener, KeyListener, MouseMotionListener, MouseListener {
    private int scale = 40;
    private int offsetX, offsetY;
    private final ArrayList<Character> pressedKeys = new ArrayList<>();
    private int gridOffsetX, gridOffsetY;
    private int previousMX, previousMY;

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
        add(new CenterPanel(), BorderLayout.SOUTH);
        int b_HEIGHT = 350;
        int b_WIDTH = 350;
        setPreferredSize(new Dimension(b_WIDTH, b_HEIGHT));
        int DELAY = 10;
        Timer timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
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
                switch (key) {
                    case 's':
                        changeOffset(0, -5);
                        break;
                    case 'w':
                        changeOffset(0, 5);
                        break;
                    case 'a':
                        changeOffset(5, 0);
                        break;
                    case 'd':
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
            scale *= 1.2;
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
        changeOffset(e.getX() - previousMX, e.getY() - previousMY);
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

    private static class CenterPanel extends JPanel {
        public CenterPanel() throws IOException {
            setOpaque(false);
            JPanel innerPanel = new JPanel();
            innerPanel.setOpaque(false);
            innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
            BufferedImage image = ImageIO.read(new File("src/resources/belt.png"));
            Dimension d = new Dimension(100, 100);
            Image scaled = image.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH);
            JButton button = new JButton();
            button.setIcon(new ImageIcon(scaled));
            button.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
            button.setPreferredSize(d);
            button.setMaximumSize(d);
            innerPanel.add(button, BorderLayout.SOUTH);
            this.add(innerPanel);
        }
    }
}
