/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.map;

import javafx.scene.layout.Region;
import mapcraft.block.Block;
import mapcraft.block.Chunk;
import org.mapcraft.api.geo.LoadOption;

/**
 *
 * @author rmalot
 */
public interface World {
    public long getSeed();
    public int getHeightRange();
    public int getWaterLevel();
    public int getSurfaceHeightAt(double x, double z);
    public void generateChunk(Chunk theChunk);
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ, LoadOption loadopt);
    public Region getRegionFromChunk(int chunkX, int chunkY, int chunkZ, LoadOption loadopt);
    public Block getBlock(double x, double y, double z);
    public String getName();
}
