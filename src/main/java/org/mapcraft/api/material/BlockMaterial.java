/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.material;

import org.mapcraft.api.block.BlockFace;
import org.mapcraft.api.gui.render.RenderPart;

/**
 *
 * @author rmalot
 */
public interface BlockMaterial {
    // TODO: Include elements of the material
    // TODO: Include properties of the material, hardness, light, opacity, transparent, invisibility
    // TODO: Placeable... Physics... 
    // TODO: BlockFaces
    
    // TOP, BOTTOM, NORTH, SOUTH, EAST, WEST
    public RenderPart getRenderPartForFace(BlockFace face);
    
}
