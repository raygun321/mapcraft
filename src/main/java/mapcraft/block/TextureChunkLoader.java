/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.block;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import org.mapcraft.api.material.BlockMaterialManager;

/**
 *
 * @author rmalot
 */
public class TextureChunkLoader implements ChunkLoader {
    Map<Long,Chunk> chunkMap = new HashMap<>();
    
    Material material;
    
    public TextureChunkLoader() {
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(new Image(getClass().getResourceAsStream("/images/terrain.png")));
        mat.setSpecularPower(5.0);

        material = mat;
    }
    
    @Override
    public Chunk getChunkByLocation(Point3D position) {
        long id = ChunkManager.getChunkId(position);

        return getChunkById(id);
    }

    @Override
    public Chunk getChunkById(long id) {
        if(chunkMap.containsKey(id) ) {
            return chunkMap.get(id);
        } else {
            Chunk thisChunk = new Chunk(ChunkManager.getChunkPosition(id));
            thisChunk.setMaterial(material);
            chunkMap.put(id, thisChunk );
            return thisChunk;
        }
    }
    
    
}
