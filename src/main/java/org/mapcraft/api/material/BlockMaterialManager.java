/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.material;

import java.util.HashMap;
import java.util.Map;
import mapcraft.block.BlockType;

/**
 *
 * @author rmalot
 */
public class BlockMaterialManager {
    Map<BlockType, BlockMaterial> materialMap = new HashMap<>();
    
    public BlockMaterialManager() {
        materialMap.put(BlockType.Default, new SolidBlock("Default", 1.0/16.0, 1.0/16.0));
        materialMap.put(BlockType.Grass, new GrassBlock());
        materialMap.put(BlockType.Dirt, new SolidBlock("Dirt", 2.0/16.0, 0.0));
        materialMap.put(BlockType.Water, new SolidBlock("Water", 12.0/16.0, 13.0/16.0));
        materialMap.put(BlockType.Stone, new SolidBlock("Stone", 1.0/16.0, 0.0));
        materialMap.put(BlockType.Wood, new SolidBlock("Wood", 4.0/16.0, 1.0/16.0));
        materialMap.put(BlockType.Sand, new SolidBlock("Sand", 2.0/16.0, 1.0/16.0));
    }
    
    public BlockMaterial getMaterialForType(BlockType type) {
        return materialMap.get(type);
    }
}
