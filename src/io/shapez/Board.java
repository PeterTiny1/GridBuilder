package io.shapez;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTreeUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Board extends JPanel implements ActionListener, MouseWheelListener, KeyListener, MouseMotionListener, MouseListener {
    // UI
    public JButton beltButton = new JButton();


    private int scale = 40;
    private int offsetX, offsetY;
    private final ArrayList<Character> pressedKeys = new ArrayList<>();
    private int gridOffsetX, gridOffsetY;
    private int previousMX, previousMY;
    private boolean hasItemSelected = false;
    private enum Items{
        None,
        Belt
    }
    private Items Item;

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
        g = (Graphics2D)g; // cast graphics to graphics2d, this is not backwards compatible
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

            for (int i = 0, pressedKeysSize = pressedKeys.size(); i < pressedKeysSize; i++) {
                Character key = pressedKeys.get(i).toString().toUpperCase().charAt(0);
                switch (key) {

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
        if(hasItemSelected)return;

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
    public void resetKeys(){
        pressedKeys.clear();
    }
    public void UpdateButtonAppearance(){
        Border unselectedBorder = BorderFactory.createLineBorder(Color.lightGray);
        Border selectedBorder = BorderFactory.createLineBorder(Color.black);

        if(Item == Items.None){
            beltButton.setFocusable(false);
        }else if(Item == Items.Belt){
            resetKeys();
            beltButton.setFocusable(true);
            beltButton.grabFocus();
        }
    }
    public void SelectItem(Items item){
        if(Item == item){
            Item = Items.None;
            hasItemSelected = false;
            System.out.println("Already selected. Now selected: " + Item.toString());
            UpdateButtonAppearance();
            return;
        }
        Item = item;
        hasItemSelected = Item != Items.None;
        System.out.println("Selected: " + item.toString());
        UpdateButtonAppearance();
    }

    private class CenterPanel extends JPanel {
        public CenterPanel() throws IOException {

            setOpaque(false);
            JPanel innerPanel = new JPanel();
            innerPanel.setOpaque(false);
            innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));

            Dimension d = new Dimension(70, 70);
            BufferedImage imageBelt = ImageIO.read(new File("src/resources/belt.png"));

            beltButton.addActionListener(e ->
            {
                SelectItem(Items.Belt);
            });
            beltButton.setFocusable(false);
            beltButton.setIcon(new ImageIcon(imageBelt.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
            beltButton.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
            beltButton.setPreferredSize(d);
            beltButton.setMaximumSize(d);
            innerPanel.add(beltButton, BorderLayout.SOUTH);
            this.add(innerPanel);
        }
    }
}
