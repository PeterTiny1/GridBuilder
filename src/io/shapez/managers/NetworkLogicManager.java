package io.shapez.managers;

import io.shapez.core.Resources;
import io.shapez.core.Rotation;
import io.shapez.core.Tile;
import io.shapez.game.*;
import io.shapez.util.TileUtil;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

public class NetworkLogicManager {

    public static boolean isNetworkCompatible(Entity ent){
        return Arrays.asList(TileUtil.networkEntities).contains(ent.tile);
    }

    public static void updateLogic(){
        // TODO: Optimize
        // INFO: Proof of concept
        for (Iterator<Chunk> iterator = Board.usedChunks.iterator(); iterator.hasNext(); ) {
            Chunk chunk = iterator.next();
            for (int x = 0; x < GlobalConfig.mapChunkSize; x++) {
                for (int y = 0; y < GlobalConfig.mapChunkSize; y++) {
                    Entity Enttile = chunk.contents[x][y];
                    if(Enttile == null && chunk.movingContents[x][y] != null){
                        chunk.movingContents[x][y] = null; // Disconnected from system
                    }
                    if(Enttile != null/* && isNetworkCompatible(tile)*/ && chunk.movingContents[x][y]==null){
                        Tile t = Enttile.tile;
                        //System.out.println("Found compatible tile at " + x + " " + y);


                        // Up: x, y-1
                        // Right: x+1, y
                        // Down: x, y+1
                        // Left: x-1, y

                        if(t == Tile.Belt){
                            //System.out.println(chunk.contents[x][y-1].tile.toString());
                            // Check for belt rotation (move direction)
                            //if(chunk.movingContents[x][y-1] != null &&
                            //   chunk.movingContents[x+1][y] != null &&
                            //   chunk.movingContents[x][y+1] != null &&
                            //   chunk.movingContents[x-1][y] != null){
                                double movingX,movingY;
                            switch (Enttile.rotation) {
                                case Up:
                                    if(chunk.contents[x][y-1] != null && chunk.contents[x][y-1].tile == Tile.Belt && chunk.movingContents[x][y+1] != null){
                                        // So theres another belt.. continue
                                        chunk.movingContents[x][y] = null;
                                        chunk.movingContents[x][y+1] = null;
                                        chunk.movingContents[x][y-1] = new MovingEntity(t, Resources.missingTexture, Rotation.Up, x, y);
                                        break;
                                    }
                                    // Theres no belt... the item will get stuck at the end of the belt system until next belt is placed
                                    break;
                                case Right:

                                    if(chunk.contents[x+1][y] != null && chunk.contents[x+1][y].tile == Tile.Belt && chunk.movingContents[x-1][y] != null){
                                        // So theres another belt.. continue
                                        chunk.movingContents[x][y] = null;
                                        chunk.movingContents[x-1][y] = null;
                                        chunk.movingContents[x+1][y] = new MovingEntity(t, Resources.missingTexture, Rotation.Up, x, y);
                                        break;
                                    }
                                    break;
                                case Down:
                                    if(chunk.contents[x][y+1] != null && chunk.contents[x][y+1].tile == Tile.Belt && chunk.movingContents[x][y-1] != null){
                                        // So theres another belt.. continue
                                        chunk.movingContents[x][y] = null;
                                        chunk.movingContents[x][y-1] = null;
                                        chunk.movingContents[x][y+1] = new MovingEntity(t, Resources.missingTexture, Rotation.Up, x, y);
                                        break;
                                    }
                                    break;
                                case Left:
                                    if(chunk.contents[x-1][y] != null && chunk.contents[x-1][y].tile == Tile.Belt && chunk.movingContents[x+1][y] != null){
                                        // So theres another belt.. continue
                                        chunk.movingContents[x][y] = null;
                                        chunk.movingContents[x+1][y] = null;
                                        chunk.movingContents[x-1][y] = new MovingEntity(t, Resources.missingTexture, Rotation.Up, x, y);
                                        break;
                                    }
                                    break;
                            }
                        }
                        else if(t == Tile.Miner){
                            // Check for miner rotation (spew direction)
                            switch (Enttile.rotation) {
                                case Up:
                                    //if(chunk.contents[x][y-1] != null)
                                    //if(chunk.contents[x][y-1].tile == Tile.Belt)
                                    if(chunk.contents[x][y-1] != null && chunk.contents[x][y-1].tile == Tile.Belt && chunk.contents[x][y-1].rotation == Enttile.rotation){
                                    chunk.movingContents[x][y-1] = new MovingEntity(t, Resources.missingTexture, Rotation.Up, x, y);
                                    }
                                    break;
                                case Right:
                                    //if(chunk.contents[x+1][y] != null)
                                    //if(chunk.contents[x+1][y].tile == Tile.Belt)
                                    if(chunk.contents[x+1][y] != null && chunk.contents[x+1][y].tile == Tile.Belt && chunk.contents[x+1][y].rotation == Enttile.rotation) {
                                    chunk.movingContents[x + 1][y] = new MovingEntity(t, Resources.missingTexture, Rotation.Up, x, y);
                                    }
                                    break;
                                case Down:
                                    //if(chunk.contents[x][y+1] != null)
                                    //if(chunk.contents[x][y+1].tile == Tile.Belt)
                                    if(chunk.contents[x][y+1] != null && chunk.contents[x][y+1].tile == Tile.Belt && chunk.contents[x][y+1].rotation == Enttile.rotation){
                                    chunk.movingContents[x][y+1] = new MovingEntity(t, Resources.missingTexture, Rotation.Up, x, y);
                                    }
                                    break;
                                case Left:
                                    //if(chunk.contents[x-1][y] != null)
                                    //if(chunk.contents[x-1][y].tile == Tile.Belt)
                                    if(chunk.contents[x-1][y] != null && chunk.contents[x-1][y].tile == Tile.Belt && chunk.contents[x-1][y].rotation == Enttile.rotation) {
                                    chunk.movingContents[x-1][y] = new MovingEntity(t, Resources.missingTexture, Rotation.Up, x, y);
                                    }
                                    break;
                            }
                            // k done
                        }

                    }
                }
            }
        }
    }
}
