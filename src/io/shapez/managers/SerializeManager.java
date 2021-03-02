package io.shapez.managers;

import io.shapez.core.Rotations;
import io.shapez.core.Tile;
import io.shapez.game.Chunk;
import io.shapez.game.Entity;
import io.shapez.util.TileUtil;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class SerializeManager {


    public static void loadAll(){
        // Start loading...
        System.out.println("Loading chunks in unsafe environment...");
        try {
            FileInputStream fs = new FileInputStream(SystemPathManager.saveFile);
            DataInputStream ds = new DataInputStream(fs);

            // EXTREMELY busy loop
            while(ds.available() > 0)
            {
                // 4 bytes per entity
                // Tile type, Image texture, Rotations.cRotations rotation, int x, int y
                int i = 0;
                while (i < SystemPathManager.saveFile.length() / 4) {
                    Tile type;
                    Image tex;
                    int x;
                    int y;
                    x = ds.readByte();
                    y = ds.readByte();
                    type = Tile.valueOf(ds.readByte());
                    //rot = cRotations.valueOf(ds.readByte());
                    ds.readByte(); // Im not sure how to implement loading rotation because of java's retarded access modifiers
                    tex = TileUtil.getTileTexture(type);

                    System.out.println("Type: " + type.toString());
                    System.out.println("Texture: " + tex.toString());
                    System.out.println("X: " + x);
                    System.out.println("Y: " + y);

                    //Entity entity = new Entity(type, tex, Rotations.cRotations.Up, x, y);
                    TileUtil.placeEntity(x,y, type, Rotations.cRotations.Up, tex);
                    i++;
                }
            }
            System.out.println("Finished loading chunks");
        }catch(Exception e){
            System.err.println("!!! Error loading chunks !!! (" + e.getMessage() + ")");
        }
    }
    public static void saveAll(ArrayList<Chunk> chunks)
    {
        // Start saving...
        System.out.println("Saving chunks in unsafe environment...");
        try {
            FileOutputStream fs = new FileOutputStream(SystemPathManager.saveFile);
            DataOutputStream ds = new DataOutputStream(fs);

            // EXTREMELY busy loop

            for (Chunk chunk : chunks) {
                if(chunk == null)continue;
                for (int x = 0; x < chunk.contents.length; x++) {
                    for (int y = 0; y < chunk.contents.length; y++) {
                        Entity entity = chunk.contents[x][y];
                        if(entity == null) continue;
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
        }catch(Exception e){
            System.err.println("!!! Error saving chunks !!! (" + e.getMessage() + ")");
        }
    }
}
