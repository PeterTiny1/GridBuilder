package io.shapez;

import io.shapez.core.Direction;
import io.shapez.core.Resources;
import io.shapez.core.Tile;
import io.shapez.core.Vector;
import io.shapez.game.*;
import io.shapez.game.items.ColorItem;
import io.shapez.game.platform.PlatformWrapperInterface;
import io.shapez.game.platform.SoundManager;
import io.shapez.game.profile.ApplicationSettings;
import io.shapez.game.savegame.Savegame;
import io.shapez.managers.SettingsManager;
import io.shapez.platform.PlatformWrapperImpl;
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
import java.util.Collections;
import java.util.Date;

import static io.shapez.managers.providers.MiscProvider.gameName;
import static io.shapez.managers.providers.MiscProvider.getRandomTitlebar;

public class Application extends JPanel implements ActionListener, MouseWheelListener, KeyListener, MouseMotionListener, MouseListener {
    // UI
    public final BottomPanel centerPanel = new BottomPanel(this);
    public final TopPanel topPanel = new TopPanel();
    public PlatformWrapperInterface platformWrapper;
    public final ApplicationSettings settings = new ApplicationSettings(this);
    public final boolean visible = true;
    public final RestrictionManager restrictionMgr = new RestrictionManager(this);

    private int scale = 40;
    private int offsetX, offsetY;
    public final ArrayList<Character> pressedKeys = new ArrayList<>();
    public static final java.util.List<MapChunk> usedChunks = Collections.synchronizedList(new ArrayList<>());
    private int gridOffsetX, gridOffsetY;
    private int previousMX, previousMY;
    public boolean hasItemSelected = false;

    public static Tile item;

    public byte rotIndex = 0; // wont be more than 127 anyway :P
    public Direction cRot = Direction.Top;

    private boolean shiftPressed;
    private boolean controlPressed;

    private Rectangle heldItem = new Rectangle(0, 0, 0, 0);
    public final Main window;
    final GameCore core = new GameCore(this);
    final Savegame savegame = null;
    public final Date date = new Date();
    private long time;

    public Application(final Main window) throws IOException {
        this.window = window;

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
        setDoubleBuffered(true);
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.SOUTH);
        final int b_HEIGHT = 500;
        final int b_WIDTH = 500;
        setPreferredSize(new Dimension(b_WIDTH, b_HEIGHT));


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);

        } catch (final ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace(); // your os is unsupported or registry is fucked if this happens
            JOptionPane.showMessageDialog(null, "Unsupported styles. " +
                    "Your OS is not supported or registry may be broken. " +
                    "UI might not work like intended", "Style loading failed", JOptionPane.ERROR_MESSAGE);
        }
        try {
            SettingsManager.loadSettings(false);
        } catch (final IOException e) {
            System.err.println("!!! Failed to load config !!!");
        }

        BuildingCodes.buildBuildingCodeCache();

        MoreWindow.application = this;
        core.initializeRoot(this.savegame);
        MoreWindow.init(core.root);

        SettingsManager.application = this;
        SettingsManager.initSettingsWnd();

        try {
            SettingsManager.loadSettings(false);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.platformWrapper = new PlatformWrapperImpl(this);
        this.core.initNewGame();
        this.core.root.logicInitialized = true;
        this.core.updateLogic();
        final javax.swing.Timer timer = new javax.swing.Timer(SettingsManager.tickrateScreen, this);
        timer.start();
    }

    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        DrawGrid(g2d);
        if (hasItemSelected) {
            g2d.drawImage(TileUtil.getTileTexture(Application.item, cRot), heldItem.x, heldItem.y, heldItem.width, heldItem.height, null);
        }
        try {
            draw(g2d);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        g2d.drawImage(Resources.vignette, 0, 0, getWidth(), getHeight(), null);
    }

    private void draw(final Graphics2D g2d) throws IOException {
        core.draw(g2d);
    }

    private void DrawGrid(final Graphics2D g2d) {
        final Vector leftTopTile = new Vector(-gridOffsetX, -gridOffsetY);
        final Vector rightBottomTile = new Vector(getWidth() / (float) scale - gridOffsetX, getHeight() / (float) scale - gridOffsetY);
        final MapChunk leftTopChunk = core.root.map.getChunkAtTile((int) leftTopTile.x - 1, (int) leftTopTile.y - 1);
        final MapChunk rightBottomChunk = core.root.map.getChunkAtTile((int) rightBottomTile.x + 1, (int) rightBottomTile.y + 1);

        final int c1x = leftTopChunk.x;
        final int c1y = leftTopChunk.y;
        final int c2x = rightBottomChunk.x;
        final int c2y = rightBottomChunk.y;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int x = c1x - 1; ++x < c2x + 1; ) {
            for (int y = c1y - 1; ++y < c2y + 1; ) {
                final MapChunk currentChunk = core.root.map.getChunk(x, y);
                currentChunk.drawChunk(g2d, offsetX, offsetY, gridOffsetX, gridOffsetY, scale, Application.usedChunks.contains(currentChunk));
            }
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final long currentTime = date.getTime();
        final int moveValue = (shiftPressed) ? 8 : 2;
        if (pressedKeys.size() > 0) {
            for (final Character key : pressedKeys) {
                switch (key) {
                    case 'S' -> changeOffset(0, -moveValue);
                    case 'W' -> changeOffset(0, moveValue);
                    case 'A' -> changeOffset(moveValue, 0);
                    case 'D' -> changeOffset(-moveValue, 0);
                }
            }
        }
//        NetworkLogicManager.updateLogic();
        onBackgroundTick(currentTime - time);
        time = currentTime;
        if (this.isRenderable()) {
            repaint();
        }
    }

    private void onBackgroundTick(final long dt) {
        if (this.isRenderable()) {
            return;
        }
        this.core.tick(dt);
    }

    public boolean isRenderable() {
        return visible;
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            if (scale < 120) {
                scale *= 1.2;
                changeOffset((int) ((getWidth() - getWidth() * 1.2) / 2), (int) ((getHeight() - getHeight() * 1.2) / 2));
            }
        } else if (scale > 5) {
            scale /= 1.2;

            changeOffset((int) Math.round((getWidth() - getWidth() / 1.2) / 2), (int) Math.round((getHeight() - getHeight() / 1.2) / 2));
        }
        heldItem = new Rectangle(previousMX - (scale / 2), previousMY - (scale / 2), scale - 2, scale - 2);
    }

    @Override
    public void keyTyped(final KeyEvent e) {

    }


    @Override
    public void keyPressed(final KeyEvent e) {
        shiftPressed |= e.getKeyCode() == KeyEvent.VK_SHIFT;
        controlPressed |= e.getKeyCode() == KeyEvent.VK_CONTROL;
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
            if (controlPressed && e.getKeyCode() == KeyEvent.VK_S) {
                System.out.println("It's supposed to save but I couldn't be bothered");
            }
            if (Character.toUpperCase(e.getKeyChar()) == 'Q') {
                selectTile(previousMX, previousMY);
            }
        }
        final char key = e.getKeyChar();
        final int index = Character.getNumericValue(key);
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
                final long t1 = System.nanoTime();
                System.out.println("GC start...");
                System.gc();
                final long t2 = System.nanoTime();
                System.out.println("GC end\nGC took " + (t2 - t1) / 1000000 + " ms");
            }
            case KeyEvent.VK_F3 -> {
                final int diagres = JOptionPane.showConfirmDialog(null, "(Benchmark) - This will overwrite a lot of tiles and you may lose progress. Continue?", "Benchmark", JOptionPane.YES_NO_OPTION);
                if (diagres != JOptionPane.YES_OPTION) break;
                int x = 0;
                while (x < 1000) {
                    int y = 0;
                    while (y < 1000) {
                        placeEntity(x, y, Application.item, cRot);
                        y++;
                    }
                    x++;
                }
            }
            case KeyEvent.VK_F7 -> MoreWindow.Show();
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

    private void selectTile(final int previousMX, final int previousMY) {
        final int cX = (previousMX - offsetX) / scale - gridOffsetX;
        final int cY = (previousMY - offsetY) / scale - gridOffsetY;

        final MapChunk chunk = core.root.map.getChunkAtTile(cX, cY);

        final int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cX % GlobalConfig.mapChunkSize;
        final int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cY % GlobalConfig.mapChunkSize;

        if (chunk.contents[offX][offY] != null && chunk.contents[offX][offY].tile != null) {
            hasItemSelected = true;
            Application.item = chunk.contents[offX][offY].tile;
            cRot = chunk.contents[offX][offY].direction;
            if (cRot != null) {
                rotIndex = (byte) cRot.getValue();
            }
            UIUtil.updateButtonAppearance();
        }
    }


    @Override
    public void keyReleased(final KeyEvent e) {
        shiftPressed &= e.getKeyCode() != KeyEvent.VK_SHIFT;
        controlPressed &= e.getKeyCode() != KeyEvent.VK_CONTROL;
        if (pressedKeys.contains(Character.toUpperCase(e.getKeyChar()))) {
            pressedKeys.remove((Character) Character.toUpperCase(e.getKeyChar()));
        }
    }

    public void changeOffset(final int x, final int y) {
        offsetX += x;
        gridOffsetX += offsetX / scale;
        offsetX %= scale;
        offsetY += y;
        gridOffsetY += offsetY / scale;
        offsetY %= scale;
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            final int cX = (e.getX() - offsetX) / scale - gridOffsetX;
            final int cY = (e.getY() - offsetY) / scale - gridOffsetY;
            clearTile(cX, cY);
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (!hasItemSelected && !controlPressed) {
                changeOffset(e.getX() - previousMX, e.getY() - previousMY);
            } else {
                heldItem = new Rectangle(e.getX() - (scale / 2), e.getY() - (scale / 2), scale - 2, scale - 2);
                placeEntity(e.getX(), e.getY(), Application.item, cRot);
            }
        }
        previousMX = e.getX();
        previousMY = e.getY();
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        previousMX = e.getX();
        previousMY = e.getY();


        heldItem = new Rectangle(e.getX() - (scale / 2), e.getY() - (scale / 2), scale - 2, scale - 2);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {
        final int cX = (e.getX() - offsetX) / scale - gridOffsetX;
        final int cY = (e.getY() - offsetY) / scale - gridOffsetY;
        if (SwingUtilities.isRightMouseButton(e)) {
            if (Application.item != Tile.None) {
                Application.item = Tile.None;
                hasItemSelected = false;
                UIUtil.updateButtonAppearance();
            } else {
                clearTile(cX, cY);
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (hasItemSelected && Application.item != Tile.None) {
                placeEntity(e.getX(), e.getY(), Application.item, cRot);
            }
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            scale = 40;
            changeOffset(0, 0);
        }
    }


    public void placeEntity(final int x, final int y, final Tile item, final Direction direction) {
        final int cX = (x - offsetX) / scale - gridOffsetX;
        final int cY = (y - offsetY) / scale - gridOffsetY;

        final MapChunk currentChunk = core.root.map.getChunkAtTile(cX, cY);

        final int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cX % GlobalConfig.mapChunkSize;
        final int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cY % GlobalConfig.mapChunkSize;

        if (TileUtil.checkInvalidTile(item, Application.item, currentChunk, offX, offY)) return;

        if (currentChunk.contents[offX][offY] != null
                && currentChunk.contents[offX][offY].tile != item
                && !TileUtil.checkSpecialProperties(currentChunk, offX, offY, item)) {

            //currentChunk.contents[offX][offY] = null;
            clearTile(offX, offY);

            if (item != Tile.DEBUG_LowerLayer) {
                core.root.systemMgr.belt.onEntityAdded(new Entity(item, direction, cX, cY));
                core.root.systemMgr.belt.updateSurroundingBeltPlacement(new Entity(item, direction, cX, cY));
                currentChunk.contents[offX][offY] = new Entity(item, direction, cX, cY);
            } else
                currentChunk.lowerLayer[offX][offY] = new ColorItem(Colors.red);

            Application.usedChunks.add(currentChunk);
            return;
        }

        if (currentChunk.contents[offX][offY] == null) {
            if (item == Tile.Belt)
                SoundManager.playSound(Resources.beltPlaceSound);
            else
                SoundManager.playSound(Resources.generic_placeTileSound);

            if (item == Tile.DEBUG_LowerLayer) {
                currentChunk.lowerLayer[offX][offY] = new ColorItem(Colors.red);
                Application.usedChunks.add(currentChunk);
                return;
            }
            currentChunk.contents[offX][offY] = new Entity(item, direction, cX, cY);
            core.root.systemMgr.belt.onEntityAdded(new Entity(item, direction, cX, cY));
            core.root.systemMgr.belt.updateSurroundingBeltPlacement(new Entity(item, direction, cX, cY));
            Application.usedChunks.add(currentChunk);
        }
    }


    private void clearTile(final int cX, final int cY) {
        final MapChunk chunk = core.root.map.getChunkAtTile(cX, cY);
        int offX = cX % GlobalConfig.mapChunkSize;
        int offY = cY % GlobalConfig.mapChunkSize;
        offX = offX < 0 ? offX + GlobalConfig.mapChunkSize : offX;
        offY = offY < 0 ? offY + GlobalConfig.mapChunkSize : offY;
        core.root.systemMgr.belt.onEntityDestroyed(new Entity(Tile.Belt, null, cX, cY));
        core.root.systemMgr.belt.updateSurroundingBeltPlacement(new Entity(Tile.Belt, null, cX, cY));
        if (chunk.contents[offX][offY] != null) {
            chunk.contents[offX][offY] = null;
            for (int x = 0; x < GlobalConfig.mapChunkSize; x++) {
                for (int y = 0; y < GlobalConfig.mapChunkSize; y++) {
                    if (Application.usedChunks.contains(chunk) && chunk.contents[x][y] != null) {
                        Application.usedChunks.remove(chunk);
                    }
                }
            }
            SoundManager.playSound(Resources.generic_destroyTileSound);
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {

    }
}
