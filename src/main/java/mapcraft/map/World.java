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
public class World {
    private final long seed;
    private final int offsetX;
    private final int offsetY;
    private final int octaves;
    private final double roughness;
    private final double scale;
    private SimpleNoiseOctave sno = new SimpleNoiseOctave();
    
    private final int heightRange;
    private final int waterLevel;
    
    public World (Long seed) {
        this(seed, 3, 0.4d, 0.005d);
    }
    
    public World (Long seed, int octaves, double roughness, double scale) {
        this.seed = seed;
        offsetX = seed.intValue();
        seed = seed >>> 32;
        offsetY = seed.intValue();
        this.roughness = roughness;
        this.scale = scale;
        this.octaves = octaves;
        
        heightRange = 64;
        waterLevel = -2;
    }
    
    public double getValue(double x, double y) {
        return sno.octavedNoise(x + offsetX, y + offsetY, octaves, roughness, scale) * heightRange;
    }
    
    public long getSeed() {
        return seed;
    }
    
    public int getHeightRange() {
        return heightRange;
    }
    
    public int getWaterLevel() {
        return waterLevel;
    }

    public Chunk getChunk(int chunkX, int chunkY, int chunkZ, LoadOption loadopt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Region getRegionFromChunk(int chunkX, int chunkY, int chunkZ, LoadOption loadopt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Block getBlock(double x, double y, double z) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
