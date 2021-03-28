package io.shapez.util;

import io.shapez.core.Direction;
import io.shapez.core.Resources;
import io.shapez.core.Tile;
import io.shapez.game.Board;
import io.shapez.game.Chunk;
import io.shapez.game.Entity;
import io.shapez.game.GlobalConfig;
import io.shapez.managers.SoundManager;

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
        for (int _i = 0; _i < Board.usedChunks.size(); _i++) {
            Chunk chunk = Board.usedChunks.get(_i);
            System.out.println(Board.usedChunks.size());
            for (int i = 0; i < chunk.lowerLayer.length; i++) {
                for (int j = 0; j < chunk.lowerLayer.length; j++) {
                    Color color = chunk.lowerLayer[i][j];
                    if (color != null/* && (color == Color.RED || color == Color.GREEN || color == Color.BLUE || color == Color.LIGHT_GRAY)*/) {
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
            for (int x = 0; x < chunk.movingContents.length; x++) {
                for (int y = 0; y < chunk.movingContents.length; y++) {
                    if (chunk.movingContents[x][y] != null)
                        chunk.movingContents[x][y] = null;
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

    public static Image getTileTexture(Tile tile, Direction rot) {
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
            case DEBUG_LowerLayer:
                return Resources.solidRed;
            default:
                return Resources.missingTexture;
        }
        a = switch (rot) {
            case Bottom -> rotateImageByDegrees(a, 180);
            case Left -> rotateImageByDegrees(a, -90);
            case Right -> rotateImageByDegrees(a, 90);
            default -> a;
        };

        return a;
    }

    public static void forcePlace(int cX, int cY, Tile item, Direction direction, Image tileTexture) {
        int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cX % GlobalConfig.mapChunkSize;
        int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cY % GlobalConfig.mapChunkSize;
        Chunk currentChunk = GlobalConfig.map.getChunkAtTile(offX, offY);
        currentChunk.contents[offX][offY] = null;
        currentChunk.contents[offX][offY] = new Entity(item, tileTexture, direction, cX, cY);
    }

    public static void placeEntity(int cX, int cY, Tile item, Direction direction, Image tileTexture) {
        Chunk currentChunk = GlobalConfig.map.getChunkAtTile(cX, cY);
        int offX = cX % GlobalConfig.mapChunkSize < 0 ? cX % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cX % GlobalConfig.mapChunkSize;
        int offY = cY % GlobalConfig.mapChunkSize < 0 ? cY % GlobalConfig.mapChunkSize + GlobalConfig.mapChunkSize : cY % GlobalConfig.mapChunkSize;

        if (currentChunk.contents[offX][offY] != null) {
            currentChunk.contents[offX][offY] = null;
            currentChunk.contents[offX][offY] = new Entity(item, tileTexture, direction, cX, cY);
            Board.usedChunks.add(currentChunk);
            return;
        }

        if (currentChunk.contents[offX][offY] == null) {
            SoundManager.playSound(item == Tile.Belt ? Resources.beltPlaceSound : Resources.generic_placeTileSound);
            currentChunk.contents[offX][offY] = new Entity(item, tileTexture, direction, cX, cY);
            Board.usedChunks.add(currentChunk);
        }

        if (checkSpecialProperties(currentChunk, offX, offY, item)) {
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
