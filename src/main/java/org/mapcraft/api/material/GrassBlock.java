/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.material;

import javafx.scene.shape.Rectangle;
import mapcraft.block.Block;
import org.mapcraft.api.block.BlockFace;
import org.mapcraft.api.gui.render.RenderPart;

/**
 *
 * @author rmalot
 */
public class GrassBlock implements BlockMaterial {
    private final String name;
    private final RenderPart[] facePart = new RenderPart[6];

    public GrassBlock() {
        this.name = "Grass";
        
        RenderPart part = new RenderPart();
        part.setSource(new Rectangle(0.0, 0.0, 16.0/256.0, 16.0/256.0));
        part.setSprite(new Rectangle(0.0, 0.0, Block.BLOCK_RENDER_SIZE, Block.BLOCK_RENDER_SIZE));
        part.setFacing(BlockFace.TOP);
        facePart[BlockFace.getIntByFace(BlockFace.TOP)] = part;
        
        part = new RenderPart();
        part.setSource(new Rectangle(2.0/16.0, 0.0, 16.0/256.0, 16.0/256.0));
        part.setSprite(new Rectangle(0.0, 0.0, Block.BLOCK_RENDER_SIZE, Block.BLOCK_RENDER_SIZE));
        part.setFacing(BlockFace.BOTTOM);
        facePart[BlockFace.getIntByFace(BlockFace.BOTTOM)] = part;
        
        part = new RenderPart();
        part.setSource(new Rectangle(3.0/16.0, 0.0, 16.0/256.0, 16.0/256.0));
        part.setSprite(new Rectangle(0.0, 0.0, Block.BLOCK_RENDER_SIZE, Block.BLOCK_RENDER_SIZE));
        part.setFacing(BlockFace.NORTH);
        facePart[BlockFace.getIntByFace(BlockFace.NORTH)] = part;

        part = new RenderPart();
        part.setSource(new Rectangle(3.0/16.0, 0.0, 16.0/256.0, 16.0/256.0));
        part.setSprite(new Rectangle(0.0, 0.0, Block.BLOCK_RENDER_SIZE, Block.BLOCK_RENDER_SIZE));
        part.setFacing(BlockFace.SOUTH);
        facePart[BlockFace.getIntByFace(BlockFace.SOUTH)] = part;

        part = new RenderPart();
        part.setSource(new Rectangle(3.0/16.0, 0.0, 16.0/256.0, 16.0/256.0));
        part.setSprite(new Rectangle(0.0, 0.0, Block.BLOCK_RENDER_SIZE, Block.BLOCK_RENDER_SIZE));
        part.setFacing(BlockFace.EAST);
        facePart[BlockFace.getIntByFace(BlockFace.EAST)] = part;

        part = new RenderPart();
        part.setSource(new Rectangle(3.0/16.0, 0.0, 16.0/256.0, 16.0/256.0));
        part.setSprite(new Rectangle(0.0, 0.0, Block.BLOCK_RENDER_SIZE, Block.BLOCK_RENDER_SIZE));
        part.setFacing(BlockFace.WEST);
        facePart[BlockFace.getIntByFace(BlockFace.WEST)] = part;
    }
    
    @Override
    public RenderPart getRenderPartForFace(BlockFace face) {
        return facePart[BlockFace.getIntByFace(face)];
    }    
}
