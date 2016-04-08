/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.block;

import javafx.geometry.Point3D;

/**
 *
 * @author rmalot
 */
public enum BlockFace {
    TOP(0x1, 0, 1, 0),
    BOTTOM(0x2, 0, -1, 0, TOP),
    NORTH(0x4, -1, 0, 0),
    SOUTH(0x8, 1, 0, 0, NORTH),
    EAST(0x10, 0, 0, -1),
    WEST(0x20, 0, 0, 1, EAST);

    private final byte mask;
    private final Point3D offset;
    private BlockFace opposite = this;
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("LeakingThisInConstructor")
    private BlockFace(int mask, int dx, int dy, int dz, BlockFace opposite) {
            this(mask, dx, dy, dz);
            this.opposite = opposite;
            opposite.opposite = this;
    }

    private BlockFace(int mask, int dx, int dy, int dz) {
            this.offset = new Point3D(dx, dy, dz);
            this.mask = (byte) mask;
    }

    public byte getMask() {
        return mask;
    }

    public Point3D getOffset() {
        return offset;
    }

    public BlockFace getOpposite() {
        return opposite;
    }
    
    public static BlockFace getFaceByInt(int face) {
        switch (face) {
            case 0:
                return TOP;
            case 1:
                return NORTH;
            case 2:
                return EAST;
            case 3:
                return SOUTH;
            case 4:
                return WEST;
            case 5:
                return BOTTOM;
        }
        return TOP;
    }
    
    public static int getIntByFace(BlockFace face) {
        switch (face) {
            case TOP:
                return 0;
            case NORTH:
                return 1;
            case EAST:
                return 2;
            case SOUTH:
                return 3;
            case WEST:
                return 4;
            case BOTTOM:
                return 5;
        }
        return 0;
    }
        
        
}
