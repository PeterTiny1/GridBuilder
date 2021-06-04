package io.shapez.managers;

import io.shapez.core.Direction;
import io.shapez.core.Tile;
import io.shapez.Application;
import io.shapez.game.*;
import io.shapez.managers.providers.SystemPathProvider;
import io.shapez.ui.MoreWindow;
import io.shapez.util.DebugUtil;
import io.shapez.util.TileUtil;
import io.shapez.util.UIUtil;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static io.shapez.managers.providers.MiscProvider.OP_LOAD;
import static io.shapez.managers.providers.MiscProvider.OP_SAVE;

public class SerializeManager {

    public static long elapsed = 0;
    public static int chunkSize = 0;
    public static final byte bytesPerTile = 25;

    public static void loadAll(GameRoot root) {

        // Start loading...
        long t1 = System.nanoTime();
        elapsed = 0;
        chunkSize = Application.usedChunks.size();
        try {
            TileUtil.clearAll();
            FileInputStream fs = new FileInputStream(SystemPathProvider.saveFile);
            DataInputStream ds = new DataInputStream(fs);

            // EXTREMELY busy loop

            while (ds.available() > 0) {
                int i = 0;

                // Read header (once)
                chunkSize = ds.readInt();

                while (i < SystemPathProvider.saveFile.length() / bytesPerTile) {
                    // variables marked with _ are temporary and used for save game checking
                    if (i % (bytesPerTile * 2) != 0) {
                        elapsed++;
                    }
                    //Per tile (data structure)

                    //4 Bytes - Entity x
                    //4 Bytes - Entity y
                    //1 Byte - Entity tile type
                    //1 Byte - entity direction

                    //4 Bytes - LowerLayer x
                    //4 Bytes - LowerLayer y
                    //1 Byte - LowerLayer R
                    //1 Byte - LowerLayer G
                    //1 Byte - LowerLayer B

                    Tile type;
                    Image tex;
                    Direction rot;
                    int _type, _rot, tileX, tileY, lX, lY;
                    byte lR, lG, lB;

                    tileX = ds.readInt();
                    tileY = ds.readInt();
                    _type = ds.readByte();
                    _rot = ds.readByte();

                    lX = ds.readInt();
                    lY = ds.readInt();
                    lR = ds.readByte();
                    lG = ds.readByte();
                    lB = ds.readByte();

                    if (_type > Tile.values().length || _rot > Direction.values().length) {
                        // Tile is corrupted,edited,hacked or from weird game version
                        System.err.println("Invalid save game data");
                        break;
                    }
                    type = Tile.valueOf(_type);
                    rot = Direction.valueOf(_rot);
                    TileUtil.forcePlace(root, tileX, tileY, type, rot);

                    //for(Chunk chunk : Board.usedChunks){
                    //    chunk.lowerLayer[lX][lY] = new Color(lR, lG, lB);
                    //}


                    elapsed++;
                    MoreWindow.L_moreFrame.setTitle(UIUtil.getProcTitle(OP_LOAD) + " (" + elapsed + "/" + chunkSize + ")");
                    i++;
                }
            }
            ds.close();
            fs.close();
        } catch (Exception e) {
            if (e.getMessage() == null) System.out.println("what???");
            System.err.println("!!! Error loading chunks !!! (" + e.getMessage() + ")");
        }
        long t2 = System.nanoTime();
        DebugUtil.printTime("Loading", "ms", t1, t2);
    }

    public static void saveAll() {
        // Start saving...
        long t1 = System.nanoTime();
        elapsed = 0;
        chunkSize = Application.usedChunks.size();


        // Documentation:
        // Integer write - 4 bytes
        // Byte write - 1 byte
        // Amount of integer writes: 5
        // Amount of byte writes: 5
        try {

            FileOutputStream fs = new FileOutputStream(SystemPathProvider.saveFile);
            DataOutputStream ds = new DataOutputStream(fs);

            // Write header
            ds.writeInt(chunkSize);

            for (MapChunk chunk : Application.usedChunks) {
                if (chunk == null) continue;
                //for (int x = 0; x < chunk.contents.length; x++) {
                //    for (int y = 0; y < chunk.contents.length; y++) {
                //        Entity entity = chunk.contents[x][y];
                //        if (entity == null) continue;
                //        ds.writeInt(entity.x);
                //        ds.writeInt(entity.y);
                //        ds.writeByte(entity.tile.getValue());
                //        ds.writeByte(entity.direction.getValue());
                //    }
                //}
                // for (int i = 0; i <= chunk.lowerLayer.length; i++) {
                //    for (int j = 0; j <= chunk.lowerLayer.length; j++) {
                //        System.out.println(i + " " + j);
                //        Color color = chunk.lowerLayer[i][j];
                //        if(color == null)continue;
                //        ds.writeInt(i); // x
                //        ds.writeInt(j); // y
                //        ds.writeByte(color.getRed());     // r
                //        ds.writeByte(color.getGreen()); // g
                //        ds.writeByte(color.getBlue()); // b
                //        //ds.writeInt(entity.y);
                //        //ds.writeByte(entity.tile.getValue());
                //        //ds.writeByte(entity.direction.getValue());
                //    }
                //}
                for (byte i = 0; i < GlobalConfig.mapChunkSize; i++) {
                    for (byte j = 0; j < GlobalConfig.mapChunkSize; j++) {
                        // Better approach?
                        Entity entity = chunk.contents[i][j];
                        if (entity == null) continue;
                        ds.writeInt(entity.x);
                        ds.writeInt(entity.y);
                        ds.writeByte(entity.tile.getValue());
                        ds.writeByte(entity.direction.getValue());
                    }
                }
                for (byte i = 0; i < GlobalConfig.mapChunkSize; i++) {
                    for (byte j = 0; j < GlobalConfig.mapChunkSize; j++) {
                        // Better approach?
                        BaseItem item = chunk.lowerLayer[i][j];
                        if (item == null) continue;
                        ds.writeInt(i); // x
                        ds.writeInt(j); // y
//                        ds.writeByte(color.getRed());     // r TODO: Allow saving of this stuff
//                        ds.writeByte(color.getGreen()); // g
//                        ds.writeByte(color.getBlue()); // b
                    }
                }
                elapsed++;
                MoreWindow.L_moreFrame.setTitle(UIUtil.getProcTitle(OP_SAVE) + " (" + elapsed + "/" + chunkSize + ")");
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
