package io.shapez.game;

import io.shapez.Main;
import io.shapez.core.*;
import io.shapez.game.buildings.MetaBeltBuilding;
import io.shapez.game.components.StaticMapEntityComponent;
import io.shapez.managers.NetworkLogicManager;
import io.shapez.managers.SettingsManager;
import io.shapez.managers.SoundManager;
import io.shapez.ui.BottomPanel;
import io.shapez.ui.MoreWindow;
import io.shapez.ui.TopPanel;
import io.shapez.util.TileUtil;
import io.shapez.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import static io.shapez.managers.providers.MiscProvider.gameName;
import static io.shapez.managers.providers.MiscProvider.getRandomTitlebar;

public class Board extends JPanel implements ActionListener, MouseWheelListener, KeyListener, MouseMotionListener, MouseListener {
    // UI
    public BottomPanel centerPanel = new BottomPanel(this);
    public TopPanel topPanel = new TopPanel();

    private int scale = 40;
    private int offsetX, offsetY;
    public final ArrayList<Character> pressedKeys = new ArrayList<>();
    public static ArrayList<Chunk> usedChunks = new ArrayList<>();
    private int gridOffsetX, gridOffsetY;
    private int previousMX, previousMY;
    public boolean hasItemSelected = false;

    public static Tile item;

    public byte rotIndex = 0; // wont be more than 127 anyway :P
    public Direction cRot = Direction.Top;

    private boolean shiftPressed;

    private Rectangle heldItem = new Rectangle(0, 0, 0, 0);
    public final Main window;
    GameSystemManager systemManager = new GameSystemManager();

    public Board(Main window) throws IOException {
        this.window = window;
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
            SettingsManager.loadSettings(false);
        } catch (IOException e) {
            System.err.println("!!! Failed to load config !!!");
        }

        MoreWindow.board = this;
        MoreWindow.Init();

        SettingsManager.board = this;
        SettingsManager.initSettingsWnd();

        try {
            SettingsManager.loadSettings(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initMetaBuildingRegistry();
        javax.swing.Timer timer = new javax.swing.Timer(SettingsManager.tickrateScreen, this);
        timer.start();
    }

    private void initMetaBuildingRegistry() {
        Layer defaultBuildingVariant = Layer.Regular;
        registerBuildingVariant(1, new MetaBeltBuilding(), defaultBuildingVariant, 0);
        registerBuildingVariant(2, new MetaBeltBuilding(), defaultBuildingVariant, 1);
        registerBuildingVariant(3, new MetaBeltBuilding(), defaultBuildingVariant, 2);
    }

    private void registerBuildingVariant(int code, MetaBuilding meta, Layer variant, int rotationVariant) {
        ArrayList<StaticMapEntityComponent.BuildingVariantIdentifier> gBuildingVariants = new ArrayList<>();
        gBuildingVariants.add(new StaticMapEntityComponent.BuildingVariantIdentifier(meta, variant, rotationVariant, meta.getDimensions(variant)));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        DrawGrid(g2d);
        if (hasItemSelected) {
            g2d.drawImage(TileUtil.getTileTexture(item, cRot), heldItem.x, heldItem.y, heldItem.width, heldItem.height, null);
        }
        g2d.drawImage(Resources.vignette, 0, 0, getWidth(), getHeight(), null);
    }

    private void DrawGrid(Graphics2D g2d) {
        Vector leftTopTile = new Vector(-gridOffsetX, -gridOffsetY);
        Vector rightBottomTile = new Vector(getWidth() / (float) scale - gridOffsetX, getHeight() / (float) scale - gridOffsetY);
        Chunk leftTopChunk = GlobalConfig.map.getChunkAtTile((int) leftTopTile.x - 1, (int) leftTopTile.y - 1);
        Chunk rightBottomChunk = GlobalConfig.map.getChunkAtTile((int) rightBottomTile.x + 1, (int) rightBottomTile.y + 1);

        int c1x = leftTopChunk.x;
        int c1y = leftTopChunk.y;
        int c2x = rightBottomChunk.x;
        int c2y = rightBottomChunk.y;

        for (int x = c1x - 1; ++x < c2x + 1; ) {
            for (int y = c1y - 1; ++y < c2y + 1; ) {
                Chunk currentChunk = GlobalConfig.map.getChunk(x, y);
                currentChunk.drawChunk(g2d, offsetX, offsetY, gridOffsetX, gridOffsetY, scale, usedChunks.contains(currentChunk));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int moveValue = (shiftPressed) ? 8 : 2;
        if (pressedKeys.size() > 0) {
            for (Character key : pressedKeys) {
                switch (key) {
                    case 'S' -> changeOffset(0, -moveValue);
                    case 'W' -> changeOffset(0, moveValue);
                    case 'A' -> changeOffset(moveValue, 0);
                    case 'D' -> changeOffset(-moveValue, 0);
                }
            }
        }
        NetworkLogicManager.updateLogic();
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            if (scale < 120) {
                scale *= 1.2;
                changeOffset((int) ((getWidth() - getWidth() * 1.2) / 2), (int) ((getHeight() - getHeight() * 1.2) / 2));
            }
        } else if (scale > 5) {
            scale /= 1.2;

            changeOffset((int) Math.round((getWidth() - getWidth() / 1.2) / 2), (int) Math.round((getHeight() - getHeight() / 1.2) / 2));
        }
        if (hasItemSelected) {
            heldItem = new Rectangle(previousMX - (scale / 2), previousMY - (scale / 2), scale - 2, scale - 2);
        }
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
                if (rotIndex == Direction.values().length) {
                    rotIndex = 0;
                }
                System.out.println("Rot index; " + rotIndex);
                System.out.println("Rot from index; " + Direction.values()[rotIndex]);

                cRot = Direction.values()[rotIndex];
            }
        }
        char key = e.getKeyChar();
        int index = Character.getNumericValue(key);
        if (!Character.isLetter(key) && index > -1) {
            if (index < Tile.values().length) {
                centerPanel.selectItem(Tile.values()[index]);
            } else {
                centerPanel.selectItem(Tile.None);
            }
        }
        // show save/load menu
        switch (e.getKeyCode()) {
            case KeyEvent.VK_F1 -> {
                centerPanel.setVisible(!centerPanel.isVisible());
                topPanel.setVisible(!topPanel.isVisible());
            }
            case KeyEvent.VK_F2 -> {
                long t1 = System.nanoTime();
                System.out.println("GC start...");
                System.gc();
                long t2 = System.nanoTime();
                System.out.println("GC end\nGC took " + (t2 - t1) / 1000000 + " ms");
            }
            case KeyEvent.VK_F3 -> {
                int diagres = JOptionPane.showConfirmDialog(null, "(Benchmark) - This will overwrite a lot of tiles and you may lose progress. Continue?", "Benchmark", JOptionPane.YES_NO_OPTION);
                if (diagres != JOptionPane.YES_OPTION) break;
                Image tex = TileUtil.getTileTexture(item, cRot);
                int x = 0;
                while (x < 1000) {
                    int y = 0;
                    while (y < 1000) {
                        placeEntity(x, y, item, cRot, tex);
                        y++;
                    }
                    x++;
                }
            }
            case KeyEvent.VK_F4 -> MoreWindow.Show();
            case KeyEvent.VK_F5 -> this.window.setTitle(gameName + getRandomTitlebar());
            case KeyEvent.VK_F6 -> {
                offsetX += Integer.MAX_VALUE;
                gridOffsetX += Integer.MAX_VALUE / scale;
                offsetX %= scale;
                offsetY += Integer.MAX_VALUE;
                gridOffsetY += Integer.MAX_VALUE / scale;
                offsetY %= scale;
            }
            case KeyEvent.VK_F11 -> {
                if (window.getExtendedState() == JFrame.MAXIMIZED_BOTH)
                    window.setExtendedState(JFrame.NORMAL);
                else
                    window.setExtendedState(JFrame.MAXIMIZED_BOTH);
                window.setVisible(false);
                window.dispose();
                window.setUndecorated(!window.isUndecorated());
                window.setVisible(true);
            }
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
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (!hasItemSelected) {
                changeOffset(e.getX() - previousMX, e.getY() - previousMY);
            } else {
                heldItem = new Rectangle(e.getX() - (scale / 2), e.getY() - (scale / 2), scale - 2, scale - 2);
                placeEntity(e.getX(), e.getY(), item, cRot, TileUtil.getTileTexture(item, cRot));
            }
        }
        previousMX = e.getX();
        previousMY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        previousMX = e.getX();
        previousMY = e.getY();


        heldItem = new Rectangle(e.getX() - (scale / 2), e.getY() - (scale / 2), scale - 2, scale - 2);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int cX = (e.getX() - offsetX) / scale - gridOffsetX;
        int cY = (e.getY() - offsetY) / scale - gridOffsetY;
        if (SwingUtilities.isRightMouseButton(e)) {
            if (item != Tile.None) {
                item = Tile.None;
                hasItemSelected = false;
                UIUtil.updateButtonAppearance();
            } else {
                clearTile(cX, cY);
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (hasItemSelected && item != Tile.None) {
                placeEntity(e.getX(), e.getY(), item, cRot, TileUtil.getTileTexture(item, cRot));
            }
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            scale = 40;
            changeOffset(0, 0);
        }
    }


    public void placeEntity(int x, int y, Tile item, Direction direction, Image tileTexture) {
        int cX = (x - offsetX) / scale - gridOffsetX;
        int cY = (y - offsetY) / scale - gridOffsetY;

        Chunk currentChunk = GlobalConfig.map.getChunkAtTile(cX, cY);

        int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cX % GlobalConfig.mapChunkSize;
        int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cY % GlobalConfig.mapChunkSize;

        if (TileUtil.checkInvalidTile(item, Board.item, currentChunk, offX, offY)) return;

        if (currentChunk.contents[offX][offY] != null
                && currentChunk.contents[offX][offY].tile != item
                && !TileUtil.checkSpecialProperties(currentChunk, offX, offY, item)) {

            //currentChunk.contents[offX][offY] = null;
            clearTile(offX, offY);

            if (item != Tile.DEBUG_LowerLayer)
                currentChunk.contents[offX][offY] = new Entity(item, tileTexture, direction, cX, cY);
            else
                currentChunk.lowerLayer[offX][offY] = Color.red;

            usedChunks.add(currentChunk);
            return;
        }

        if (currentChunk.contents[offX][offY] == null) {
            if (item == Tile.Belt)
                SoundManager.playSound(Resources.beltPlaceSound);
            else
                SoundManager.playSound(Resources.generic_placeTileSound);

            if (item == Tile.DEBUG_LowerLayer) {
                currentChunk.lowerLayer[offX][offY] = Color.red;
                usedChunks.add(currentChunk);
                return;
            }
            currentChunk.contents[offX][offY] = new Entity(item, tileTexture, direction, cX, cY);
            usedChunks.add(currentChunk);
        }
    }


    private void clearTile(int cX, int cY) {
        Chunk chunk = GlobalConfig.map.getChunkAtTile(cX, cY);
        int offX = cX % GlobalConfig.mapChunkSize;
        int offY = cY % GlobalConfig.mapChunkSize;
        offX = offX < 0 ? offX + GlobalConfig.mapChunkSize : offX;
        offY = offY < 0 ? offY + GlobalConfig.mapChunkSize : offY;
        systemManager.belt.onEntityDestroyed(new Entity(Tile.Belt, TileUtil.getTileTexture(Tile.Belt, Direction.Top), null, cX, cY));
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
        }
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

    public void __clearAll() {
        TileUtil.clearAll();
    }
}
