package io.shapez;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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

    public byte rotIndex = 0; // wont be more than 127 anyway :P
    public Rotations.cRotations cRot = Rotations.cRotations.Up;

    private boolean shiftPressed;

    private Rectangle heldItem = new Rectangle(0,0,0,0);

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
            JOptionPane.showMessageDialog(null, "Unsupported styles. " +
                    "Your OS is not supported or registry may be broken. " +
                    "UI might not work like intended", "Style loading failed", JOptionPane.ERROR_MESSAGE);
        }
        try {

            SettingsManager.load(false);
        } catch (IOException e) {
            System.err.println("!!! Failed to load config !!!");
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
            if (Character.toUpperCase(e.getKeyChar()) == 'R') {
                // Change rotation
                rotIndex++;
                if(rotIndex == Rotations.cRotations.values().length){
                    rotIndex = 0;
                }
                System.out.println("Rot index; " + rotIndex);
                System.out.println("Rot from index; " + Rotations.cRotations.values()[rotIndex]);

                cRot = Rotations.cRotations.values()[rotIndex];
            }
        }
        char key = e.getKeyChar();
        int index = Character.getNumericValue(key);
        if(!Character.isLetter(key) && index > -1){
            if(index < Items.values().length){
            centerPanel.selectItem( Items.values()[index] );} else{
                centerPanel.selectItem(Items.None);
            }
            repaint();
        }
            switch (e.getKeyCode()) {
                // F1, F2....
                // 112,113,114...
                case 112:
                    centerPanel.setVisible(!centerPanel.isVisible());
                    topPanel.setVisible(!topPanel.isVisible());
                    break;
                case 113:
                    long t1 = System.nanoTime();
                    System.out.println("GC start...");
                    System.gc();
                    long t2 = System.nanoTime();
                    System.out.println("GC end\nGC took " + (t2 - t1)/1000000 + " ms");
                    break;
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
            clearTile(cX, cY,true);
            repaint();
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (!hasItemSelected) {changeOffset(e.getX() - previousMX, e.getY() - previousMY);}
            else {
               heldItem = new Rectangle(e.getX() - (scale / 2), e.getY() - (scale / 2), scale - 2, scale - 2);
               placeEntity(e.getX(), e.getY(), item, cRot, getTileTexture(item));
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
    private static BufferedImage rotateImageByDegrees(BufferedImage bimg, double angle) {
        int w = bimg.getWidth();
        int h = bimg.getHeight();
        BufferedImage bufimage = new BufferedImage(w, h, bimg.getType());
        Graphics2D g2d = bufimage.createGraphics();
        g2d.rotate(Math.toRadians(angle), w>>1, h>>1);
        g2d.drawImage(bimg, null, 0, 0);
        g2d.dispose();
        return bufimage;
    }


    private Image getTileTexture(Items tile) {
        BufferedImage a;
        switch (tile) {
            case Belt:
                a = Resources.belt;
                break;
            case Miner:
                a = Resources.miner;
                break;
            case Trash:
                return Resources.trash; // cant be rotated
            default:
                return Resources.missingTexture;
        }


        switch (cRot) {
            case Down:
                a = rotateImageByDegrees(a,180);
                break;
            case Left:
                a = rotateImageByDegrees(a,-90);
                break;
            case Right:
                a = rotateImageByDegrees(a,90);
                break;
        }

        return a;
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
                clearTile(cX, cY,true);
            }
            repaint();
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (hasItemSelected && item != Items.None) {
                SoundManager.playSound(item != Items.Belt ? Resources.generic_placeTileSound : Resources.beltPlaceSound);
                placeEntity(e.getX(), e.getY(), item, cRot, getTileTexture(item));}
            }
        }


    private byte checkSpecialProperties(Chunk currentChunk, int offX, int offY, Items item){
        // Return:  0 if all is ok
        //          1 if tile should be removed (invalid placement)
        //          2 if (...) to be continued for later versions
        switch (item) {
            case Miner:
                if(currentChunk.lowerLayer[offX][offY] == null || currentChunk.lowerLayer[offX][offY] == Color.gray /* chunk border */){
                    return 1;
                }
            default:
                return 0;
        }
    }

    private void placeEntity(int x, int y, Items item, Rotations.cRotations rotation, Image tileTexture) {
        int cX = (x - offsetX) / scale - gridOffsetX;
        int cY = (y - offsetY) / scale - gridOffsetY;

        Chunk currentChunk = GlobalConfig.map.getChunkAtTile(cX, cY);

        int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + 16 : cX % GlobalConfig.mapChunkSize;
        int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + 16 : cY % GlobalConfig.mapChunkSize;

        if(     currentChunk.contents[offX][offY] != null
                && currentChunk.contents[offX][offY].type != item
                && checkSpecialProperties(currentChunk,offX,offY,item) == 0) {

            //currentChunk.contents[offX][offY] = null;
            clearTile(offX,offY,false);
            currentChunk.contents[offX][offY] = new Entity(item, tileTexture, rotation, cX, cY);
            usedChunks.add(currentChunk);
            repaint();
            return;
        }

        if (currentChunk.contents[offX][offY] == null) {
            currentChunk.contents[offX][offY] = new Entity(item, tileTexture, rotation, cX, cY);
            usedChunks.add(currentChunk);
            if (item == Items.Belt)
            SoundManager.playSound(Resources.beltPlaceSound);
            else
            SoundManager.playSound(Resources.generic_placeTileSound);
        }

        deleteInvalidTile(item, currentChunk, offX, offY);
        repaint();
    }

    private byte deleteInvalidTile(Items item, Chunk currentChunk, int offX, int offY) {
        byte result = 0;
        result = checkSpecialProperties(currentChunk, offX, offY, item);

        if(result == 1){
            System.out.println(
                    "Tile of type " + item.toString() + "has invalid placement at " + offX + " " + offY + "\n" +
                            "Tile will be deleted"
            );
            if(currentChunk.contents[offX][offY].type == item && currentChunk.contents[offX][offY].type == this.item)
            currentChunk.contents[offX][offY] = null;
        }
        return result;
    }

    private void clearTile(int cX, int cY, boolean user) {
        Chunk chunk = GlobalConfig.map.getChunkAtTile(cX, cY);
        int offX = cX % GlobalConfig.mapChunkSize;
        int offY = cY % GlobalConfig.mapChunkSize;
        offX = offX < 0 ? offX + 16 : offX;
        offY = offY < 0 ? offY + 16 : offY;
        if (chunk.contents[offX][offY] != null) {
            chunk.contents[offX][offY] = null;
            for (int x = 0; x < GlobalConfig.mapChunkSize; x++) {
                for (int y = 0; y < GlobalConfig.mapChunkSize; y++) {
                    if (usedChunks.contains(chunk) && chunk.contents[x][y] != null) {
                        usedChunks.remove(chunk);
                    }
                }
            }
            SoundManager.playSound(Resources.generic_destroyTileSound);
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
