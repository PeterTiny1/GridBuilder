package io.shapez.managers;

import io.shapez.core.Rotation;
import io.shapez.core.Tile;
import io.shapez.game.Board;
import io.shapez.game.Chunk;
import io.shapez.game.Entity;
import io.shapez.managers.providers.SystemPathProvider;
import io.shapez.util.DebugUtil;
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
        long t1 = System.nanoTime();
        try {
            TileUtil.clearAll(toReload);
            FileInputStream fs = new FileInputStream(SystemPathProvider.saveFile);
            DataInputStream ds = new DataInputStream(fs);

            // EXTREMELY busy loop
            while (ds.available() > 0) {
                // 4 bytes per entity
                // Tile type, Image texture, Rotations.cRotations rotation, int x, int y
                int i = 0;
                while (i < SystemPathProvider.saveFile.length() / 6) {
                    // variables marked with _ are temporary and used for save game checking
                    Tile type;
                    int _type;

                    Image tex;

                    Rotation rot;
                    int _rot;

                    int x;
                    int y;
                    x = ds.readInt();
                    y = ds.readInt();
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
            ds.close();
            fs.close();
        } catch (Exception e) {
            if(e.getMessage() == null)return;

            System.err.println("!!! Error loading chunks !!! (" + e.getMessage() + ")");
        }
        long t2 = System.nanoTime();
        DebugUtil.printTime("Loading", "ms", t1, t2);
    }

    public static void saveAll(ArrayList<Chunk> chunks) {
        // Start saving...
        long t1 = System.nanoTime();
        try {

            FileOutputStream fs = new FileOutputStream(SystemPathProvider.saveFile);
            DataOutputStream ds = new DataOutputStream(fs);

            // EXTREMELY busy loop

            for (Chunk chunk : chunks) {
                if (chunk == null) continue;
                for (int x = 0; x < chunk.contents.length; x++) {
                    for (int y = 0; y < chunk.contents.length; y++) {
                        Entity entity = chunk.contents[x][y];
                        if (entity == null) continue;
                        ds.writeInt(entity.x);
                        ds.writeInt(entity.y);
                        ds.writeByte(entity.tile.getValue());
                        ds.writeByte(entity.rotation.getValue());
                    }
                }
            }
            ds.flush();
            ds.close();
        } catch (Exception e) {
            System.err.println("!!! Error saving chunks !!! (" + e.getMessage() + ")");
        }
        long t2 = System.nanoTime();
        DebugUtil.printTime("Saving", "ms", t1, t2);
    }


}
