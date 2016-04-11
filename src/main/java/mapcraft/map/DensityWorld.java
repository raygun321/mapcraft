/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.map;

import javafx.geometry.Point3D;
import javafx.scene.layout.Region;
import mapcraft.block.Block;
import mapcraft.block.BlockType;
import mapcraft.block.Chunk;
import org.mapcraft.api.geo.LoadOption;

/**
 *
 * @author rmalot
 */
public class DensityWorld implements World {
    private final long seed;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;
    private final int octaves;
    private final double roughness;
    private final double scale;
    private SimpleNoiseOctave sno = new SimpleNoiseOctave();
    
    private final int heightRange;
    private final int waterLevel;
    
    public DensityWorld (Long seed) {
        this(seed, 3, 0.4d, 0.005d);
    }
    
    public DensityWorld (Long seed, int octaves, double roughness, double scale) {
        this.seed = seed;
        offsetX = seed.intValue();
        seed = seed >>> 32;
        offsetY = seed.intValue();
        offsetZ = 0;
        this.roughness = roughness;
        this.scale = scale;
        this.octaves = octaves;
        
        heightRange = 64;
        waterLevel = 24;
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
    
    public void generateChunk(Chunk theChunk) {
        Point3D position = theChunk.getPosition();
        
        for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for(int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                BlockType previousType = BlockType.Stone;
                for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                    double currentLevel = y+position.getY();
                    // Should be between -1 and 1
                    // Multiply by the height range (density range of -64 to 64)
                    // Subtract the y level... density should increase as you go down and decrease as you go up
                    // Add the water level... This should even out the density to be an average of 0 at sea level.
                    // Add 5 more ... to raise the level to 0.
                    // Density below 0 should be Air or Water. Density above 0 should be various solids.
                    Double density = (getValue(x+position.getX(), currentLevel, z+position.getZ())) - currentLevel + waterLevel + 5.0;
                    BlockType type = getBlockTypeBasedOnDensityAndHeight(density, currentLevel);
                    
                    if(type.equals(BlockType.Default) && currentLevel < waterLevel) {
                        type = BlockType.Water;                        
                    }
                    
                    if(type.equals(BlockType.Default) && previousType.equals(BlockType.Dirt)) {
                        Block tempBlock = new Block(BlockType.Grass);
                        tempBlock.setActive(true);
                        theChunk.setBlock(x, y-1, z, tempBlock);
                        
                        previousType = BlockType.Grass;
                    }
                    
                    
                    Block tempBlock = new Block(type);
                    if(!type.equals(BlockType.Default)) {
                        tempBlock.setActive(true);                    
                    } else {
                        tempBlock.setActive(false);
                    }
                    theChunk.setBlock(x, y, z, tempBlock);
                    
                    previousType = type;
                }
            }
        }
    }
    
    private BlockType getBlockTypeBasedOnDensityAndHeight(double density, double height) {
        BlockType result = BlockType.Default;
        
        if(density >= 0.0) {
            if(density > 15) result = BlockType.Stone;
            if(density > 6) result = BlockType.Dirt;
            if(density > 5) result = BlockType.Sand;
            else result = BlockType.Dirt;
            
            if(density < 2 && height < waterLevel + 3.0) {
                result = BlockType.Sand;
            }
        }

        return result;
    }
    
    private double getValue(double x, double y, double z) {
        return sno.octavedNoise(x + offsetX, y + offsetY, z + offsetZ, octaves, roughness, scale) * heightRange;
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

    @Override
    public int getSurfaceHeightAt(double x, double z) {
        int surfaceLevel = 0;
        for(int yy = 0; yy < heightRange; yy++) {
            Double density = getValue(x, yy, z) - yy + waterLevel + 5.0;
            if(density >= 0.0)
                surfaceLevel = yy;
        }
        return surfaceLevel;
    }
}
