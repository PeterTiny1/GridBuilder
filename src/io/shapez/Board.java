package io.shapez;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Board extends JPanel implements ActionListener, MouseWheelListener, KeyListener, MouseMotionListener, MouseListener {
    private Timer timer;
    private final int B_WIDTH = 350;
    private final int B_HEIGHT = 350;
    private final int DELAY = 10;
    private int scale = 40;
    private int offsetX, offsetY;
    private int oldLocationX, oldLocationY;
    private ArrayList<Character> pressedKeys = new ArrayList<>();
    private int gridOffsetX, gridOffsetY;
    private int previousMX, previousMY;

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        setFocusable(true);
        requestFocusInWindow();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        g.setColor(Color.BLACK);
        for (int x = 0; x < size.width; x += scale) {
            g.drawLine(x + offsetX, 0, x + offsetX, size.height);
        }
        for (int y = 0; y < size.height; y += scale) {
            g.drawLine(0, y + offsetY, size.width, y + offsetY);
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
                        changeOffset(-5, 0);;
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
    void updateMouse(int cleanX, int cleanY, int absoluteX, int absoluteY){
        changeOffset(cleanX,cleanY);
        oldLocationX = absoluteX;
        oldLocationY = absoluteY;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX(); int y = e.getY(); // avoid crazy many function calls
        updateMouse(x - oldLocationX, y - oldLocationY, x, y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println("Mouse moved to " + e.getX() + " " + e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX(); int y = e.getY(); // avoid crazy many function calls
        updateMouse(x - oldLocationX, y - oldLocationY, x, y);
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
