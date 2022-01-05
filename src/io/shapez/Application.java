package io.shapez;

import io.shapez.core.Direction;
import io.shapez.core.Resources;
import io.shapez.core.Tile;
import io.shapez.core.Vector;
import io.shapez.game.*;
import io.shapez.game.items.ColorItem;
import io.shapez.game.profile.ApplicationSettings;
import io.shapez.game.savegame.Savegame;
import io.shapez.managers.SettingsManager;
import io.shapez.platform.*;
import io.shapez.platform.ad_providers.NoAdProvider;
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

import static io.shapez.managers.providers.MiscProvider.gameName;
import static io.shapez.managers.providers.MiscProvider.getRandomTitlebar;

public class Application extends JPanel implements ActionListener, MouseWheelListener, KeyListener, MouseMotionListener, MouseListener, FocusListener {
    // UI
    public final BottomPanel centerPanel = new BottomPanel(this);
    public final TopPanel topPanel = new TopPanel();
    public PlatformWrapper platformWrapper = new PlatformWrapperImpl(this);
    public final ApplicationSettings settings = new ApplicationSettings(this);
    public final boolean visible = true;
    public final RestrictionManager restrictionMgr = new RestrictionManager(this);
    public SoundInterface sound;
    public Vector mousePosition;
    public AdProvider adProvider;
    public GameAnalyticsInterface gameAnalytics;

    private int scale = 40;
    private int offsetX, offsetY;
    public final ArrayList<Character> pressedKeys = new ArrayList<>();
    public static final java.util.List<MapChunk> usedChunks = Collections.synchronizedList(new ArrayList<>());
    private int gridOffsetX, gridOffsetY;
    private int previousMX, previousMY;
    public boolean hasItemSelected = false;

    public static Tile item;

    public byte rotIndex = 0; // won't be more than 127 anyway :P
    public Direction cRot = Direction.Top;

    private boolean shiftPressed;
    private boolean controlPressed;

    private Rectangle heldItem = new Rectangle(0, 0, 0, 0);
    public final Main window;
    final GameCore core = new GameCore(this);
    final Savegame savegame = null;
    private long time;
    private Analytics analytics;

    public Application(final Main window) throws Exception {
        this.window = window;

        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
        setFocusable(true);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setDoubleBuffered(true);
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.SOUTH);


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
        this.initPlatformDependentInstances();
        this.core.initNewGame();
        this.core.root.logicInitialized = true;
        this.core.updateLogic();
        final Timer timer = new Timer(SettingsManager.tickrateScreen, this);
        timer.start();
    }

    private void initPlatformDependentInstances() {
        this.adProvider = new NoAdProvider(this);
        this.sound = new SoundImplBrowser(this);
        this.analytics = new GoogleAnalyticsImpl(this);
        this.gameAnalytics = new ShapezGameAnalytics(this);
    }

    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        try {
            core.draw(g2d);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        DrawGrid(g2d);
        if (hasItemSelected) {
            final double rotation = switch (cRot) {
                case Top -> Math.toRadians(0);
                case Right -> Math.toRadians(90);
                case Bottom -> Math.toRadians(180);
                case Left -> Math.toRadians(270);
            };
            g2d.translate(heldItem.x, heldItem.y);
            g2d.rotate(rotation, heldItem.width >> 1, heldItem.height >> 1);
            g2d.drawImage(TileUtil.getTileTexture(Application.item), 0, 0, heldItem.width, heldItem.height, null);
            g2d.rotate(-rotation, heldItem.width >> 1, heldItem.height >> 1);
            g2d.translate(-heldItem.x, -heldItem.y);

        }
        g2d.drawImage(Resources.vignette, 0, 0, getWidth(), getHeight(), null);
    }

    private void DrawGrid(final Graphics2D g2d) {
        final int centeredOffsetX = offsetX + getWidth() / 2;
        final int centeredOffsetY = offsetY + getHeight() / 2;
        final Vector leftTopTile = new Vector(-gridOffsetX - ((double) centeredOffsetX / scale), -gridOffsetY - ((double) centeredOffsetY / scale));
        final Vector rightBottomTile = new Vector(getWidth() / 2.0 / (double) scale - gridOffsetX, getHeight() / 2.0 / (double) scale - gridOffsetY);
        final MapChunk leftTopChunk = core.root.map.getChunkAtTile((int) leftTopTile.x - 1, (int) leftTopTile.y - 1);
        final MapChunk rightBottomChunk = core.root.map.getChunkAtTile((int) rightBottomTile.x + 1, (int) rightBottomTile.y + 1);

        final int c1x = leftTopChunk.x;
        final int c1y = leftTopChunk.y;
        final int c2x = rightBottomChunk.x;
        final int c2y = rightBottomChunk.y;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        for (int x = c1x - 1; ++x < c2x + 1; ) {
            for (int y = c1y - 1; ++y < c2y + 1; ) {
                final MapChunk currentChunk = core.root.map.getChunk(x, y);
                currentChunk.drawChunk(g2d, centeredOffsetX, centeredOffsetY, gridOffsetX, gridOffsetY, scale, Application.usedChunks.contains(currentChunk));
            }
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final long currentTime = System.currentTimeMillis();
        final int moveValue = (shiftPressed) ? 4 : 1;
        if (pressedKeys.size() > 0) {
            for (final Character key : pressedKeys) {
                switch (key) {
                    case 'S' -> changeOffset(0, (int) (-moveValue * (currentTime - time)));
                    case 'W' -> changeOffset(0, (int) (moveValue * (currentTime - time)));
                    case 'A' -> changeOffset((int) (moveValue * (currentTime - time)), 0);
                    case 'D' -> changeOffset((int) (-moveValue * (currentTime - time)), 0);
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
            }
        } else if (scale > 5) {
            scale /= 1.2;
        }
        this.core.root.camera.onMouseWheel(e);
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
        final int cX = Math.floorDiv(previousMX - getWidth() / 2 - offsetX, scale) - gridOffsetX;
        final int cY = Math.floorDiv(previousMY - getHeight() / 2 - offsetY, scale) - gridOffsetY;

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
            System.out.println("Rot index; " + rotIndex);
            System.out.println("Rot from index; " + Direction.values()[rotIndex]);
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
            final int cX = Math.floorDiv(e.getX() - (getWidth() / 2) - offsetX, scale) - gridOffsetX;
            final int cY = Math.floorDiv(e.getY() - (getHeight() / 2) - offsetY, scale) - gridOffsetY;
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
        this.mousePosition = new Vector(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        previousMX = e.getX();
        previousMY = e.getY();
        this.mousePosition = new Vector(e.getX(), e.getY());

        heldItem = new Rectangle(e.getX() - (scale / 2), e.getY() - (scale / 2), scale - 2, scale - 2);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {
        final int cX = Math.floorDiv(e.getX() - (getWidth() / 2) - offsetX, scale) - gridOffsetX;
        final int cY = Math.floorDiv(e.getY() - (getHeight() / 2) - offsetY, scale) - gridOffsetY;
        if (SwingUtilities.isRightMouseButton(e)) {
            hasItemSelected = false;
            if (Application.item != Tile.None) {
                Application.item = Tile.None;
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
        core.root.camera.onMouseDown(e);
    }


    public void placeEntity(final int x, final int y, final Tile item, final Direction direction) {
        final int cX = Math.floorDiv(x - (getWidth() / 2) - offsetX, scale) - gridOffsetX;
        final int cY = Math.floorDiv(y - (getHeight() / 2) - offsetY, scale) - gridOffsetY;

        final MapChunk currentChunk = core.root.map.getChunkAtTile(cX, cY);

        final int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cX % GlobalConfig.mapChunkSize;
        final int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cY % GlobalConfig.mapChunkSize;

        if (TileUtil.checkInvalidTile(item, Application.item, currentChunk, offX, offY)) return;

        if (currentChunk.contents[offX][offY] != null
                && !TileUtil.checkSpecialProperties(currentChunk, offX, offY, item)) {

            if (item != Tile.DEBUG_LowerLayer) {
                new Thread(() -> {
                    core.root.systemMgr.belt.onEntityAdded(new Entity(item, direction, cX, cY));
                    core.root.systemMgr.belt.updateSurroundingBeltPlacement(new Entity(item, direction, cX, cY));
                });
                if (currentChunk.contents[offX][offY].tile == Tile.Belt)
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

    @Override
    public void focusGained(final FocusEvent e) {

    }

    @Override
    public void focusLost(final FocusEvent e) {
        this.pressedKeys.clear();
        requestFocusInWindow();
    }
}
