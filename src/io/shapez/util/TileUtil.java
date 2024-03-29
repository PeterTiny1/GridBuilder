package io.shapez.util;

import io.shapez.core.Resources;
import io.shapez.core.Rotation;
import io.shapez.core.Tile;
import io.shapez.game.Board;
import io.shapez.game.Chunk;
import io.shapez.game.Entity;
import io.shapez.game.GlobalConfig;
import io.shapez.managers.SoundManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TileUtil {
    public static void clearAll(Board b)
    {
        ArrayList<Chunk> usedChunks = Board.usedChunks;
        for (Chunk chunk : usedChunks) {
            for (int x = 0; x < chunk.contents.length; x++) {
                for (int y = 0; y < chunk.contents.length; y++) {
                    if (chunk.contents[x][y] == null) continue;
                    chunk.contents[x][y] = null;
                }
            }
        }
        b.repaint();
    }
    public static BufferedImage rotateImageByDegrees(BufferedImage bimg, double angle) {
        int w = bimg.getWidth();
        int h = bimg.getHeight();
        BufferedImage bufimage = new BufferedImage(w, h, bimg.getType());
        Graphics2D g2d = bufimage.createGraphics();
        g2d.rotate(Math.toRadians(angle), w >> 1, h >> 1);
        g2d.drawImage(bimg, null, 0, 0);
        g2d.dispose();
        return bufimage;
    }

    public static Image getTileTexture(Tile tile, Rotation rot) {
        BufferedImage a;
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
            default:
                return Resources.missingTexture;
        }
        switch (rot) {
            case Down:
                a = rotateImageByDegrees(a, 180);
                break;
            case Left:
                a = rotateImageByDegrees(a, -90);
                break;
            case Right:
                a = rotateImageByDegrees(a, 90);
                break;
        }

        return a;
    }

    public static void placeEntity(int cX, int cY, Tile item, Rotation rotation, Image tileTexture, boolean suppress) {
        Chunk currentChunk = GlobalConfig.map.getChunkAtTile(cX, cY);
        int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + 16 : cX % GlobalConfig.mapChunkSize;
        int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + 16 : cY % GlobalConfig.mapChunkSize;

        if (currentChunk.contents[offX][offY] != null) {
            currentChunk.contents[offX][offY] = null;
            currentChunk.contents[offX][offY] = new Entity(item, tileTexture, rotation, cX, cY);
            Board.usedChunks.add(currentChunk);
            return;
        }

        if (currentChunk.contents[offX][offY] == null) {
            if (!suppress) {
                SoundManager.playSound(item == Tile.Belt ? Resources.beltPlaceSound : Resources.generic_placeTileSound);
            }
            currentChunk.contents[offX][offY] = new Entity(item, tileTexture, rotation, cX, cY);

            Board.usedChunks.add(currentChunk);
        }

        if(checkSpecialProperties(currentChunk, offX, offY, item)){
            currentChunk.contents[offX][offY] = null;
        }

    }

    public static boolean checkSpecialProperties(Chunk currentChunk, int offX, int offY, Tile item) {
        // Return:  0 if all is ok
        //          1 if tile should be removed (invalid placement)
        //          2 if (...) to be continued for later versions
        if (item == Tile.Miner) {
            /* chunk border */
            return currentChunk.lowerLayer[offX][offY] == null || currentChunk.lowerLayer[offX][offY] == Color.gray;
        }
        return false;
    }

    public static boolean checkInvalidTile(Tile item, Tile selecteditem, Chunk currentChunk, int offX, int offY) {
        boolean result;
        result = checkSpecialProperties(currentChunk, offX, offY, item);
        return result;
    }
}
