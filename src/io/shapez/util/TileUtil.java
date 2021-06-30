package io.shapez.util;

import io.shapez.Application;
import io.shapez.core.Direction;
import io.shapez.core.Resources;
import io.shapez.core.Tile;
import io.shapez.game.*;
import io.shapez.game.platform.SoundManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileUtil {

    public static Tile[] networkEntities = {
            Tile.Belt,
            Tile.Rotator,
            Tile.Miner
    };

    public static void clearAll() {
        //for (Chunk chunk : Board.usedChunks) {
        for (int _i = 0; _i < Application.usedChunks.size(); _i++) {
            final MapChunk chunk = Application.usedChunks.get(_i);
            System.out.println(Application.usedChunks.size());
            for (int i = 0; i < chunk.lowerLayer.length; i++) {
                for (int j = 0; j < chunk.lowerLayer.length; j++) {
                    final BaseItem item = chunk.lowerLayer[i][j];
                    if (item != null/* && (color == Color.RED || color == Color.GREEN || color == Color.BLUE || color == Color.LIGHT_GRAY)*/) {
                        chunk.lowerLayer[i][j] = null;
                    }
                }
            }
            for (int x = 0; x < chunk.contents.length; x++) {
                for (int y = 0; y < chunk.contents.length; y++) {
                    if (chunk.contents[x][y] != null)
                        chunk.contents[x][y] = null;
                }
            }
            for (int x = 0; x < chunk.lowerLayer.length; x++) {
                for (int y = 0; y < chunk.lowerLayer.length; y++) {
                    if (chunk.lowerLayer[x][y] != null)
                        chunk.lowerLayer[x][y] = null;
                }
            }
        }
    }

//    public static BufferedImage rotateImageByDegrees(final BufferedImage bimg, final double angle) {
//        final int w = bimg.getWidth();
//        final int h = bimg.getHeight();
//        final BufferedImage bufimage = new BufferedImage(w, h, bimg.getType());
//        final Graphics2D g2d = bufimage.createGraphics();
//        g2d.rotate(Math.toRadians(angle), w >> 1, h >> 1);
//        g2d.drawImage(bimg, null, 0, 0);
//        g2d.dispose();
//        return bufimage;
//    }

    public static Image getTileTexture(final Tile tile) {
        final BufferedImage a;
        if (tile == null) {
            return Resources.missingTexture;
        }
        switch (tile) {
            case Belt:
                a = Resources.belt;
                break;
            case Miner:
                a = Resources.miner;
                break;
            case Rotator:
                a = Resources.rotator;
                break;
            case Trash:
                return Resources.trash; // cant be rotated
            case DEBUG_LowerLayer:
                return Resources.solidRed;
            default:
                return Resources.missingTexture;
        }

        return a;
    }

    public static void forcePlace(final GameRoot root, final int cX, final int cY, final Tile item, final Direction direction) {
        final int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cX % GlobalConfig.mapChunkSize;
        final int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cY % GlobalConfig.mapChunkSize;
        final MapChunk currentChunk = root.map.getChunkAtTile(offX, offY);
        currentChunk.contents[offX][offY] = null;
        currentChunk.contents[offX][offY] = new Entity(item, direction, cX, cY);
    }

    public static void placeEntity(final GameRoot root, final int cX, final int cY, final Tile item, final Direction direction) {
        final MapChunk currentChunk = root.map.getChunkAtTile(cX, cY);
        final int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cX % GlobalConfig.mapChunkSize;
        final int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cY % GlobalConfig.mapChunkSize;

        if (currentChunk.contents[offX][offY] != null) {
            currentChunk.contents[offX][offY] = null;
            currentChunk.contents[offX][offY] = new Entity(item, direction, cX, cY);
            Application.usedChunks.add(currentChunk);
            return;
        }

        if (currentChunk.contents[offX][offY] == null) {
            SoundManager.playSound(item == Tile.Belt ? Resources.beltPlaceSound : Resources.generic_placeTileSound);
            currentChunk.contents[offX][offY] = new Entity(item, direction, cX, cY);
            Application.usedChunks.add(currentChunk);
        }

        if (TileUtil.checkSpecialProperties(currentChunk, offX, offY, item)) {
            currentChunk.contents[offX][offY] = null;
        }
    }

    public static boolean checkSpecialProperties(final MapChunk currentChunk, final int offX, final int offY, final Tile item) {
        // Return:  0 if all is ok
        //          1 if tile should be removed (invalid placement)
        //          2 if (...) to be continued for later versions
        if (item == Tile.Miner) {
            /* chunk border */
            return currentChunk.lowerLayer[offX][offY] == null;
        }
        return false;
    }

    public static boolean checkInvalidTile(final Tile item, final Tile selecteditem, final MapChunk currentChunk, final int offX, final int offY) {
        final boolean result;
        result = TileUtil.checkSpecialProperties(currentChunk, offX, offY, item);
        return result;
    }
}
