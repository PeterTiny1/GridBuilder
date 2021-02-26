package io.shapez;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Board extends JPanel implements ActionListener, MouseWheelListener, KeyListener, MouseMotionListener, MouseListener {
    // UI
    public CenterPanel centerPanel = new CenterPanel(this);
    public TopPanel topPanel = new TopPanel();

    private int scale = 40;
    private int offsetX, offsetY;
    public final ArrayList<Character> pressedKeys = new ArrayList<>();
    public ArrayList<Chunk> usedChunks = new ArrayList<>();
    private int gridOffsetX, gridOffsetY;
    private int previousMX, previousMY;
    public boolean hasItemSelected = false;
    private boolean mouseDown = false;

    public Items item;
    public Rotation rotation = Rotation.Up;
    private boolean shiftPressed;

    private Rectangle heldItem;

    public Board() throws IOException {
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
        add(topPanel, BorderLayout.NORTH);
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
        DrawGrid(g);
        DrawSelected();
        if (hasItemSelected) {
            g.drawImage(getTileTexture(item), heldItem.x, heldItem.y, heldItem.width, heldItem.height, null);
        }
    }

    private void DrawSelected() {

    }

    private void DrawGrid(Graphics g) {
        Vector leftTopTile = new Vector(-gridOffsetX, -gridOffsetY);
        Vector rightBottomTile = new Vector(getWidth() / (float) scale - gridOffsetX, getHeight() / (float) scale - gridOffsetY);
        Chunk leftTopChunk = GlobalConfig.map.getChunkAtTile((int) leftTopTile.x, (int) leftTopTile.y);
        Chunk rightBottomChunk = GlobalConfig.map.getChunkAtTile((int) rightBottomTile.x, (int) rightBottomTile.y);

        int c1x = leftTopChunk.x;
        int c1y = leftTopChunk.y;
        int c2x = rightBottomChunk.x;
        int c2y = rightBottomChunk.y;

        for (int x = c1x - 1; x < c2x + 1; x++) {
            for (int y = c1y - 1; y < c2y + 1; y++) {
                Chunk currentChunk = GlobalConfig.map.getChunk(x, y);
                currentChunk.drawChunk(g, offsetX, offsetY, gridOffsetX, gridOffsetY, scale, usedChunks.contains(currentChunk));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int moveValue = (shiftPressed) ? 10 : 5;
        if (pressedKeys.size() > 0) {
            for (Character key : pressedKeys) {
                switch (key) {
                    case 'S':
                        changeOffset(0, -moveValue);
                        break;
                    case 'W':
                        changeOffset(0, moveValue);
                        break;
                    case 'A':
                        changeOffset(moveValue, 0);
                        break;
                    case 'D':
                        changeOffset(-moveValue, 0);
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
        if (hasItemSelected) {
            heldItem = new Rectangle(previousMX - (scale / 2), previousMY - (scale / 2), scale - 2, scale - 2);
        }
        changeOffset(0, 0);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        shiftPressed |= e.getKeyCode() == KeyEvent.VK_SHIFT;
        if (!pressedKeys.contains(Character.toUpperCase(e.getKeyChar()))) {
            pressedKeys.add(Character.toUpperCase(e.getKeyChar()));
            if (Character.toUpperCase(e.getKeyChar()) == 'R') rotation = getNextRotation(rotation);
        }
    }

    private Rotation getNextRotation(Rotation rotation) {
        switch (rotation) {
            case Up:
                return Rotation.Right;
            case Right:
                return Rotation.Down;
            case Down:
                return Rotation.Left;
            case Left:
                return Rotation.Up;
            default:
                return rotation;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        shiftPressed &= e.getKeyCode() != KeyEvent.VK_SHIFT;
        if (pressedKeys.contains(Character.toUpperCase(e.getKeyChar()))) {
            pressedKeys.remove((Character) Character.toUpperCase(e.getKeyChar()));
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
        if (SwingUtilities.isRightMouseButton(e)) {
            int cX = (e.getX() - offsetX) / scale - gridOffsetX;
            int cY = (e.getY() - offsetY) / scale - gridOffsetY;
            clearTile(cX, cY);
            repaint();
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (!hasItemSelected) changeOffset(e.getX() - previousMX, e.getY() - previousMY);
            else {
                heldItem = new Rectangle(e.getX() - (scale / 2), e.getY() - (scale / 2), scale - 2, scale - 2);
            }
            repaint();
        }
        previousMX = e.getX();
        previousMY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        previousMX = e.getX();
        previousMY = e.getY();

        if (hasItemSelected) {
            heldItem = new Rectangle(e.getX() - (scale / 2), e.getY() - (scale / 2), scale - 2, scale - 2);
            repaint();
        }
    }

    private Image getTileTexture(Items tile) {
        BufferedImage a;
        if (tile == Items.Belt) {
            a = Resources.belt;
        } else {
            return Resources.missingTexture;
        }
        Graphics2D g2d = a.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate(a.getWidth() / 2, a.getHeight() / 2);
        switch (rotation) {
            case Down:
                at.rotate(Math.toRadians(180));
                g2d.setTransform(at);
                return a;
            case Left:
                at.rotate(Math.toRadians(-90));
                g2d.setTransform(at);
                return a;
            case Right:
                at.rotate(Math.toRadians(90));
                g2d.setTransform(at);
                return a;
            default:
                return a;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int cX = (e.getX() - offsetX) / scale - gridOffsetX;
        int cY = (e.getY() - offsetY) / scale - gridOffsetY;
        mouseDown = SwingUtilities.isLeftMouseButton(e);
        if (SwingUtilities.isRightMouseButton(e)) {
            if (item != Items.None) {
                item = Items.None;
                hasItemSelected = false;
                centerPanel.updateButtonAppearance();
            } else {
                clearTile(cX, cY);
            }
            repaint();
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (hasItemSelected) {
                placeEntity(e.getX(), e.getY(), item, rotation, getTileTexture(item));
            }
        }
    }

    private void placeEntity(int x, int y, Items item, Rotation rotation, Image tileTexture) {
        int cX = (x - offsetX) / scale - gridOffsetX;
        int cY = (y - offsetY) / scale - gridOffsetY;

        Chunk currentChunk = GlobalConfig.map.getChunkAtTile(cX, cY);

        int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + 16 : cX % GlobalConfig.mapChunkSize;
        int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + 16 : cY % GlobalConfig.mapChunkSize;

        if (currentChunk.contents[offX][offY] == null) {
            currentChunk.contents[offX][offY] = new Entity(item, tileTexture, rotation, cX, cY);
            usedChunks.add(currentChunk);
        }
    }

    private void clearTile(int cX, int cY) {
        Chunk chunk = GlobalConfig.map.getChunkAtTile(cX, cY);
        int offX = cX % GlobalConfig.mapChunkSize;
        int offY = cY % GlobalConfig.mapChunkSize;
        offX = offX < 0 ? offX + 16 : offX;
        offY = offY < 0 ? offY + 16 : offY;
        if (chunk.contents[offX][offY] != null) {
            chunk.contents[offX][offY] = null;
            for (int x = 0; x < GlobalConfig.mapChunkSize; x++) {
                for (int y = 0; y < GlobalConfig.mapChunkSize; y++) {
                    if (usedChunks.contains(chunk)) {
                        if (chunk.contents[x][y] != null) {
                            usedChunks.remove(chunk);
                        }
                    }
                }
            }
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = !SwingUtilities.isLeftMouseButton(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
