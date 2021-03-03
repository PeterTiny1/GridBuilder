package io.shapez.managers;

import io.shapez.core.Rotation;
import io.shapez.core.Tile;
import io.shapez.game.Board;
import io.shapez.game.Chunk;
import io.shapez.game.Entity;
import io.shapez.util.TileUtil;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class SerializeManager {


    public static void loadAll(Board toReload) {
        // Start loading...
        System.out.println("Loading chunks in unsafe environment...");
        try {
            TileUtil.clearAll(toReload);
            FileInputStream fs = new FileInputStream(SystemPathManager.saveFile);
            DataInputStream ds = new DataInputStream(fs);

            // EXTREMELY busy loop
            while (ds.available() > 0) {
                // 4 bytes per entity
                // Tile type, Image texture, Rotations.cRotations rotation, int x, int y
                int i = 0;
                while (i < SystemPathManager.saveFile.length() / 4) {
                    // variables marked with _ are temporary and used for save game checking
                    Tile type;
                    int _type;

                    Image tex;

                    Rotation rot;
                    int _rot;

                    int x;
                    int y;
                    x = ds.readByte();
                    y = ds.readByte();
                    _type = ds.readByte();
                    _rot = ds.readByte();


                    if(_type > Tile.values().length || _rot > Rotation.values().length){
                        // Tile is corrupted,edited,hacked or from weird game version
                        System.err.println("Invalid save game data");
                        //continue;
                        break; // completely stop loading everything if one tile is invalid.... not a good approach but continue; causes issues
                    }
                    type = Tile.valueOf(_type);
                    rot = Rotation.valueOf(_rot);
                    tex = TileUtil.getTileTexture(type,rot);
                    TileUtil.placeEntity(x, y, type, rot, tex, true); // Suppress audio!
                    i++;
                }
            }
            System.out.println("Finished loading chunks");
            toReload.repaint();
            ds.close();
            fs.close();
        } catch (Exception e) {
            System.err.println("!!! Error loading chunks !!! (" + e.getMessage() + ")");
        }
    }

    public static void saveAll(ArrayList<Chunk> chunks) {
        // Start saving...
        System.out.println("Saving chunks in unsafe environment...");
        try {
            FileOutputStream fs = new FileOutputStream(SystemPathManager.saveFile);
            DataOutputStream ds = new DataOutputStream(fs);

            // EXTREMELY busy loop

            for (Chunk chunk : chunks) {
                if (chunk == null) continue;
                for (int x = 0; x < chunk.contents.length; x++) {
                    for (int y = 0; y < chunk.contents.length; y++) {
                        Entity entity = chunk.contents[x][y];
                        if (entity == null) continue;
                        ds.writeByte(entity.x);
                        ds.writeByte(entity.y);
                        ds.writeByte(entity.tile.getValue());
                        ds.writeByte(entity.rotation.getValue());
                    }
                }
            }
            ds.flush();
            ds.close();
            System.out.println("Finished saving chunks");
        } catch (Exception e) {
            System.err.println("!!! Error saving chunks !!! (" + e.getMessage() + ")");
        }
    }
}
