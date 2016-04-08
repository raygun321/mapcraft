/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.block;

import javafx.geometry.Point3D;

/**
 * The chunk loader creates chunks.
 * 
 * @author rmalot
 */
public interface ChunkLoader {
    public Chunk getChunkByLocation(Point3D position);
    public Chunk getChunkById(long id);
}
