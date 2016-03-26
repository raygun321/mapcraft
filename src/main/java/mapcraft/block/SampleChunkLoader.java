/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.block;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Point3D;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import mapcraft.map.SimpleNoiseOctave;

/**
 *
 * @author rmalot
 */
public class SampleChunkLoader implements ChunkLoader {
    Map<Long,Chunk> chunkMap = new HashMap<>();
    
    Material material;
    
    public SampleChunkLoader() {
        PhongMaterial mat = new PhongMaterial();
        
        // Create Image and ImageView objects
        WritableImage image = new WritableImage(15, 3);
        PixelWriter pixelWriter = image.getPixelWriter();
        
        Color[] colorArray = new Color[5]; 
        colorArray[0] = new Color(135.0/256, 135.0/256, 108.0/256, 1.0);   // Stone
        colorArray[1] = new Color(112.0/256, 170.0/256, 17.0/256, 1.0);   // Grass
        colorArray[2] = new Color(95.0/256, 163.0/256, 184.0/256, 0.5);   // Water
        colorArray[3] = new Color(230.0/256, 230.0/256, 142.0/256, 1.0);   // Sand
        colorArray[4] = new Color(112.0/256, 78.0/256, 16.0/256, 1.0);   // Dirt
        
        // Determine the color of each pixel in a specified row
        SimpleNoiseOctave sno = new SimpleNoiseOctave();
        for(int y=0; y<image.getHeight(); y++){
            for(int x=0; x<image.getWidth(); x++){
                pixelWriter.setColor(x,y,colorArray[x / 3]);
            }
        }
        mat.setDiffuseMap(image);
        mat.setSpecularPower(5.0);
        //mat.setDiffuseMap(new Image(getClass().getResourceAsStream("/images/colors.png")));
        material = mat;
    }
    
    @Override
    public Chunk getChunkByLocation(Point3D position) {
        long id = ChunkManager.getChunkId(position);
//        System.out.println("getChunkByLocation position:[" + position +  "]");
//        System.out.println("getChunkByLocation id:[" + id + "]");
//        System.out.println("getChunkByLocation getChunkPosition:[" + ChunkManager.getChunkPosition(id) + "]");

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
