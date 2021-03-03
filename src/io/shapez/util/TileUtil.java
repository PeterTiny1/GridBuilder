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

public class TileUtil {

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

    public static void placeEntity(int cX, int cY, Tile item, Rotation rotation, Image tileTexture) {
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
            if (item == Tile.Belt)
                SoundManager.playSound(Resources.beltPlaceSound);
            else
                SoundManager.playSound(Resources.generic_placeTileSound);
            currentChunk.contents[offX][offY] = new Entity(item, tileTexture, rotation, cX, cY);

            Board.usedChunks.add(currentChunk);
        }

        //deleteInvalidTile(item, currentChunk, offX, offY);

    }
}
