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
        
        heightRange = 32;
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
    
    public void generateChunk(Chunk theChunk) {
        Point3D position = theChunk.getPosition();
        
        for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for(int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                Double yVal = (getValue(x+position.getX(), z+position.getZ())) - position.getY();
                
                int yLevel = yVal.intValue();
                if(yLevel < 0) yLevel = 0;
                
                // Below is active
                for(int y = 0; y < Chunk.CHUNK_SIZE && y < yLevel; y++ ) {
                    Block tempBlock = new Block(getBlockTypeBasedOnHeight(y + position.getY()));
                    tempBlock.setActive(true);
                    theChunk.setBlock(x, y, z, tempBlock);
                }
                // If near the waterline - replace the top block with sand.
                if(yLevel < Chunk.CHUNK_SIZE && yLevel > 0 && 
                        (yLevel + position.getY()) < (getWaterLevel()+2) && 
                        (yLevel + position.getY()) > (getWaterLevel()-4)) {
                    int yLowerLevel = yLevel - 3;
                    if(yLowerLevel < 0) yLowerLevel = 0;
                    int yUpperLevel = yLevel;
                    if(yUpperLevel > Chunk.CHUNK_SIZE) yUpperLevel = Chunk.CHUNK_SIZE;

                    for(int y = yLowerLevel; y < yUpperLevel; y++) {
                        Block tempBlock = new Block(BlockType.Sand);
                        tempBlock.setActive(true);
                        theChunk.setBlock(x, y, z, tempBlock);
                    }
                }
                
                // Above is not active
                for(int y = yLevel; y < Chunk.CHUNK_SIZE; y++) {
                    // Fill with water or air?
                    if(y + position.getY() < getWaterLevel()) {
                        Block tempBlock = new Block(BlockType.Water);
                        tempBlock.setActive(true);
                        theChunk.setBlock(x, y, z, tempBlock);
                    } else {
                        Block tempBlock = new Block(BlockType.Default);
                        tempBlock.setActive(false);
                        theChunk.setBlock(x, y, z, tempBlock);
                    }
                }
            }
        }
    }
    
    private BlockType getBlockTypeBasedOnHeight(double height) {
        BlockType result = BlockType.Stone;

        if(height > 10.0)
            result = BlockType.Stone;
        else if (height > 1.0)
            result = BlockType.Grass;
        else if (height > -5)
            result = BlockType.Dirt;

        return result;
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
