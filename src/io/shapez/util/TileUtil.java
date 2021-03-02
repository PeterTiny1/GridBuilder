package io.shapez.util;

import io.shapez.core.Rotation;
import io.shapez.core.Tile;
import io.shapez.core.Resources;
import io.shapez.game.Board;
import io.shapez.game.Chunk;
import io.shapez.game.Entity;
import io.shapez.game.GlobalConfig;
import io.shapez.managers.SoundManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileUtil {
    public static Image getTileTexture(Tile tile)
    {
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
        return a;
    }

    public static void placeEntity(int cX, int cY, Tile item, Rotation rotation, Image tileTexture){
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
