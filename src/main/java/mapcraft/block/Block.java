/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.block;

/**
 *
 * @author rmalot
 */
public class Block {

    static final int BLOCK_RENDER_SIZE = 1;
    private BlockType type;
    private boolean active;
    private int blockID;
    
    public Block() {
        type = BlockType.Default;
        active = true;
    }
    
    public Block(BlockType type) {
        this.type = type;
        active = true;
    }

    public Block(boolean active) {
        this.type = BlockType.Default;
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public BlockType getBlockType() {
        return type;
    }
    
    public boolean isTranslucent() {
        if(type.equals(BlockType.Water)) return true;
        else return false;
    }
    
    /**
     * Mixture of Active and Translucent
     * 
     * @return 
     */
    public boolean isBlocking() {
        return isActive() && !isTranslucent();
    }
}
