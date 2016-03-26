/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.block;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Material;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import mapcraft.map.World;

/**
 *
 * @author rmalot
 */
public class Chunk {
    public static final int CHUNK_SIZE=16;
    public static final float RENDER_SIZE=0.5f;
    
    private final Block[][][] blocks;
    private final MeshView meshView;
    private DrawMode drawMode;
    private Material blockMaterial;
    
    private ChunkManager manager;
    
    /**
     * Position of the min corner (upper left corner, min Z)
     */
    private Point3D position;
    private Bounds bounds;
    
    private boolean isLoaded;
    private boolean isSetup;
    private boolean shouldRender;
    private boolean isInScene;
    
    private int activeBlockCount;
    
    Map<Integer,Block> faceMap = new HashMap<>();
    
    public Chunk(Point3D position) {
        this(position, null);
    }
    
    public Chunk(Point3D position, ChunkManager manager) {
        isLoaded = false;
        isSetup = false;
        shouldRender = false;
        isInScene = false;
        
        this.manager = manager;
        drawMode = DrawMode.FILL;
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        
        this.position = position;
        bounds = new BoundingBox(position.getX(), position.getY(), position.getZ(), getSideLength(), getSideLength(), getSideLength());

        meshView = new MeshView(new TriangleMesh());
        meshView.setDrawMode(drawMode);
        meshView.setMaterial(blockMaterial);
        
        meshView.setTranslateX(position.getX());
        meshView.setTranslateY(position.getY());
        meshView.setTranslateZ(position.getZ());
        
        activeBlockCount = 0;
        
        System.out.println("Chunk created at " + position);
    }
        
    public void update() {
        System.out.println("Chunk updating at " + position);
        rebuildMesh();
        Chunk nextChunk = manager.getChunkAtPoint(new Point3D(position.getX()+CHUNK_SIZE, position.getY(), position.getZ()));
        if(nextChunk != null && nextChunk.isLoaded()) {
            nextChunk.rebuildMesh();
        }
        nextChunk = manager.getChunkAtPoint(new Point3D(position.getX()-CHUNK_SIZE, position.getY(), position.getZ()));
        if(nextChunk != null && nextChunk.isLoaded()) {
            nextChunk.rebuildMesh();
        }
        nextChunk = manager.getChunkAtPoint(new Point3D(position.getX(), position.getY()-CHUNK_SIZE, position.getZ()));
        if(nextChunk != null && nextChunk.isLoaded()) {
            nextChunk.rebuildMesh();
        }
        nextChunk = manager.getChunkAtPoint(new Point3D(position.getX(), position.getY()+CHUNK_SIZE, position.getZ()));
        if(nextChunk != null && nextChunk.isLoaded()) {
            nextChunk.rebuildMesh();
        }
        nextChunk = manager.getChunkAtPoint(new Point3D(position.getX(), position.getY(), position.getZ()-CHUNK_SIZE));
        if(nextChunk != null && nextChunk.isLoaded()) {
            nextChunk.rebuildMesh();
        }
        nextChunk = manager.getChunkAtPoint(new Point3D(position.getX(), position.getY(), position.getZ()+CHUNK_SIZE));
        if(nextChunk != null && nextChunk.isLoaded()) {
            nextChunk.rebuildMesh();
        }
    }
    
    public void render() {
        System.out.println("Chunk rendering at " + position);
        if(manager != null) {
            manager.getScene().getChildren().add(meshView);
            isInScene = true;
        }
    }
    
    public void setup() {
        System.out.println("Chunk setup at " + position);

        rebuildMesh();
        
        isSetup = true;
    }

    public void load() {
        System.out.println("Chunk load at " + position);
        World world = manager.getWorld();
        
        for(int x = 0; x < CHUNK_SIZE; x++) {
            for(int z = 0; z < CHUNK_SIZE; z++) {
                Double yVal = (world.getValue(x+position.getX(), z+position.getZ())) - position.getY();
                
                int yLevel = yVal.intValue();
                if(yLevel < 0) yLevel = 0;
                
                // Below is active
                for(int y = 0; y < CHUNK_SIZE && y < yLevel; y++ ) {
                    Block tempBlock = new Block(getBlockTypeBasedOnHeight(y + position.getY()));
                    tempBlock.setActive(true);
                    blocks[x][y][z] = tempBlock;
                }
                // If near the waterline - replace the top block with sand.
                if(yLevel < CHUNK_SIZE && yLevel > 0 && 
                        (yLevel + position.getY()) < (world.getWaterLevel()+2) && 
                        (yLevel + position.getY()) > (world.getWaterLevel()-4)) {
                    int yLowerLevel = yLevel - 3;
                    if(yLowerLevel < 0) yLowerLevel = 0;
                    int yUpperLevel = yLevel;
                    if(yUpperLevel > CHUNK_SIZE) yUpperLevel = CHUNK_SIZE;

                    for(int y = yLowerLevel; y < yUpperLevel; y++) {
                        Block tempBlock = new Block(BlockType.Sand);
                        tempBlock.setActive(true);
                        blocks[x][y][z] = tempBlock;
                    }
                }
                
                // Above is not active
                for(int y = yLevel; y < CHUNK_SIZE; y++) {
                    // Fill with water or air?
                    if(y + position.getY() < world.getWaterLevel()) {
                        Block tempBlock = new Block(BlockType.Water);
                        tempBlock.setActive(true);
                        blocks[x][y][z] = tempBlock;
                    } else {
                        Block tempBlock = new Block(BlockType.Default);
                        tempBlock.setActive(false);
                        blocks[x][y][z] = tempBlock;
                    }
                }
            }
        }
        isLoaded = true;
    }
    
    public void unload() {
        System.out.println("Chunk unload at " + position);

        isLoaded = false;
        isSetup = false;
        shouldRender = false;
        isInScene = false;
 

        // Remove from scene
        if(manager != null) {
            manager.getScene().getChildren().remove(meshView);
        }

        // Empty the mesh
        TriangleMesh mesh = (TriangleMesh) meshView.getMesh();
        mesh.getFaces().clear();
        mesh.getTexCoords().clear();
        mesh.getPoints().clear();
        
        // Clear the blocks
        for(int x = 0; x < CHUNK_SIZE; x++) {
            for(int y = 0; y < CHUNK_SIZE; y++) {
                for(int z = 0; z < CHUNK_SIZE; z++) {
                    blocks[x][y][z] = null;
                }
            }
        }
    }
    
    private int getTextureCoordFromBlockType(BlockType type) {
        switch(type) {
            case Grass:
                return 1;
            case Dirt:
                return 4;
            case Water:
                return 2;
            case Stone:
                return 0;
            case Wood:
                return 0;
            case Sand:
                return 3;
        }
        
        return 1;
    }
    
    private BlockType getRandomBlockType() {
        int type = new Double( Math.random() * 6 ).intValue();
        switch(type) {
            case 2:
                return BlockType.Grass;
            case 1:
                return BlockType.Dirt;
            case 5:
                return BlockType.Water;
            case 0:
                return BlockType.Stone;
            case 3:
                return BlockType.Wood;
            case 4:
                return BlockType.Sand;
        }
        
        return BlockType.Default;
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
        
    public void rebuildMesh() {
        // Create the mesh...
        TriangleMesh mesh = (TriangleMesh) meshView.getMesh();
        mesh.getFaces().clear();
        mesh.getTexCoords().clear();
        mesh.getPoints().clear();
        
        // Using the same set for all
        mesh.getTexCoords().addAll(0.1f, 0.5f, // 0 red
                                   0.3f, 0.5f, // 1 green
                                   0.5f, 0.5f, // 2 blue
                                   0.7f, 0.5f, // 3 yellow
                                   0.9f, 0.5f  // 4 orange
                                   );

        Integer faceCount = 0;
        for(int x = 0; x < CHUNK_SIZE; x++) {
            for(int y = 0; y < CHUNK_SIZE; y++) {
                for(int z = 0; z < CHUNK_SIZE; z++) {
                    Block curBlock = blocks[x][y][z];
                    if(curBlock.isActive()) {
                        // Check if cube is hidden by other cubes
                        boolean def = false;
                        boolean xNeg = def;
                        boolean xPos = def;
                        boolean yNeg = def;
                        boolean yPos = def;
                        boolean zNeg = def;
                        boolean zPos = def;
                        
                        if(!curBlock.getBlockType().equals(BlockType.Water)) {
                            if(x>0) {            
                                xNeg = blocks[x-1][y][z].isBlocking();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX()-CHUNK_SIZE, position.getY(), position.getZ()));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    xNeg = nextChunk.blocks[CHUNK_SIZE - 1][y][z].isBlocking();
                                }
                            }
                            if(x<CHUNK_SIZE-1) {
                                xPos = blocks[x+1][y][z].isBlocking();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX()+CHUNK_SIZE, position.getY(), position.getZ()));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    xPos = nextChunk.blocks[0][y][z].isBlocking();
                                }
                            }

                            if(y>0) { 
                                yNeg = blocks[x][y-1][z].isBlocking();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX(), position.getY()-CHUNK_SIZE, position.getZ()));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    yNeg = nextChunk.blocks[x][CHUNK_SIZE - 1][z].isBlocking();
                                }
                            }
                            if(y<CHUNK_SIZE-1) {
                                yPos = blocks[x][y+1][z].isBlocking();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX(), position.getY()+CHUNK_SIZE, position.getZ()));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    yPos = nextChunk.blocks[x][0][z].isBlocking();
                                }
                            }

                            if(z>0) {
                                zNeg = blocks[x][y][z-1].isBlocking();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX(), position.getY(), position.getZ()-CHUNK_SIZE));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    zNeg = nextChunk.blocks[x][y][CHUNK_SIZE - 1].isBlocking();
                                }
                            }
                            if(z<CHUNK_SIZE-1) {
                                zPos = blocks[x][y][z+1].isBlocking();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX(), position.getY(), position.getZ()+CHUNK_SIZE));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    zPos = nextChunk.blocks[x][y][0].isBlocking();
                                }
                            }
                        } else {    // For water - only check for active
                            if(x>0) {            
                                xNeg = blocks[x-1][y][z].isActive();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX()-CHUNK_SIZE, position.getY(), position.getZ()));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    xNeg = nextChunk.blocks[CHUNK_SIZE - 1][y][z].isActive();
                                }
                            }
                            if(x<CHUNK_SIZE-1) {
                                xPos = blocks[x+1][y][z].isActive();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX()+CHUNK_SIZE, position.getY(), position.getZ()));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    xPos = nextChunk.blocks[0][y][z].isActive();
                                }
                            }

                            if(y>0) { 
                                yNeg = blocks[x][y-1][z].isActive();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX(), position.getY()-CHUNK_SIZE, position.getZ()));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    yNeg = nextChunk.blocks[x][CHUNK_SIZE - 1][z].isActive();
                                }
                            }
                            if(y<CHUNK_SIZE-1) {
                                yPos = blocks[x][y+1][z].isActive();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX(), position.getY()+CHUNK_SIZE, position.getZ()));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    yPos = nextChunk.blocks[x][0][z].isActive();
                                }
                            }

                            if(z>0) {
                                zNeg = blocks[x][y][z-1].isActive();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX(), position.getY(), position.getZ()-CHUNK_SIZE));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    zNeg = nextChunk.blocks[x][y][CHUNK_SIZE - 1].isActive();
                                }
                            }
                            if(z<CHUNK_SIZE-1) {
                                zPos = blocks[x][y][z+1].isActive();
                            } else {
                                Chunk nextChunk = manager.getChunkAtPoint(
                                        new Point3D(position.getX(), position.getY(), position.getZ()+CHUNK_SIZE));
                                if(nextChunk != null && nextChunk.isLoaded()) {
                                    zPos = nextChunk.blocks[x][y][0].isActive();
                                }
                            }
                        }
                        
                        if(!(xNeg && xPos && yNeg && yPos && zNeg && zPos)) {
                            // Only create mesh for visible active blocks
                            int textureCoord = getTextureCoordFromBlockType(curBlock.getBlockType());
                            faceMap.put(faceCount, curBlock);
                            faceCount += AddCubeToMesh(mesh, x, y, z, textureCoord, xNeg, xPos, yNeg, yPos, zNeg, zPos);

                            activeBlockCount++;
                        }
                    }
                }
            }
        }
        shouldRender = activeBlockCount > 0;
    }
    
    private int AddCubeToMesh(TriangleMesh mesh, int x, int y, int z, int tc,
            boolean xNeg, boolean xPos, boolean yNeg, boolean yPos, boolean zNeg, boolean zPos) {
        int numFaces =0;
        int numPts = mesh.getPoints().size() / mesh.getPointElementSize();
        
        mesh.getPoints().addAll(x-RENDER_SIZE, y-RENDER_SIZE, z+RENDER_SIZE,    //0 Left,  Top,    Back
                                x+RENDER_SIZE, y-RENDER_SIZE, z+RENDER_SIZE,    //1 Right, Top,    Back
                                x+RENDER_SIZE, y+RENDER_SIZE, z+RENDER_SIZE,    //2 Right, Bottom, Back
                                x-RENDER_SIZE, y+RENDER_SIZE, z+RENDER_SIZE,    //3 Left,  Bottom, Back
                                x+RENDER_SIZE, y-RENDER_SIZE, z-RENDER_SIZE,    //4 Right, Top,    Front
                                x-RENDER_SIZE, y-RENDER_SIZE, z-RENDER_SIZE,    //5 Left,  Top,    Front         
                                x-RENDER_SIZE, y+RENDER_SIZE, z-RENDER_SIZE,    //6 Left,  Bottom, Front
                                x+RENDER_SIZE, y+RENDER_SIZE, z-RENDER_SIZE);   //7 Right, Bottom, Front
        
//    
//      0    1
//    5    4
//      
//      3    2
//    6    7

        if(!yNeg) {
            mesh.getFaces().addAll(0+numPts,tc, 4+numPts,tc, 1+numPts,tc);   // Top
            mesh.getFaces().addAll(0+numPts,tc, 5+numPts,tc, 4+numPts,tc);
            numFaces += 2;
        }
        if(!zNeg) {
            mesh.getFaces().addAll(4+numPts,tc, 5+numPts,tc, 6+numPts,tc);   // Front
            mesh.getFaces().addAll(4+numPts,tc, 6+numPts,tc, 7+numPts,tc);
            numFaces += 2;
        }
        if(!xPos) {
            mesh.getFaces().addAll(1+numPts,tc, 4+numPts,tc, 7+numPts,tc);   // Right
            mesh.getFaces().addAll(1+numPts,tc, 7+numPts,tc, 2+numPts,tc);
            numFaces += 2;
        }
        if(!zPos) {
            mesh.getFaces().addAll(1+numPts,tc, 3+numPts,tc, 0+numPts,tc);   // Back
            mesh.getFaces().addAll(1+numPts,tc, 2+numPts,tc, 3+numPts,tc);
            numFaces += 2;
        }
        if(!xNeg) {
            mesh.getFaces().addAll(5+numPts,tc, 0+numPts,tc, 3+numPts,tc);   // Left
            mesh.getFaces().addAll(5+numPts,tc, 3+numPts,tc, 6+numPts,tc);
            numFaces += 2;
        }
        if(!yPos) {
            mesh.getFaces().addAll(6+numPts,tc, 3+numPts,tc, 2+numPts,tc);   // Bottom
            mesh.getFaces().addAll(6+numPts,tc, 2+numPts,tc, 7+numPts,tc);
            numFaces += 2;
        }
        
//        mesh.getFaces().addAll(0+numPts,tc, 4+numPts,tc, 1+numPts,tc,   // Top
//                               0+numPts,tc, 5+numPts,tc, 4+numPts,tc,
//                               4+numPts,tc, 5+numPts,tc, 6+numPts,tc,   // Front
//                               4+numPts,tc, 6+numPts,tc, 7+numPts,tc,
//                               1+numPts,tc, 4+numPts,tc, 7+numPts,tc,   // Right
//                               1+numPts,tc, 7+numPts,tc, 2+numPts,tc,
//                               1+numPts,tc, 3+numPts,tc, 0+numPts,tc,   // Back
//                               1+numPts,tc, 2+numPts,tc, 3+numPts,tc,
//                               5+numPts,tc, 0+numPts,tc, 3+numPts,tc,   // Left
//                               5+numPts,tc, 3+numPts,tc, 6+numPts,tc,
//                               6+numPts,tc, 3+numPts,tc, 2+numPts,tc,   // Bottom
//                               6+numPts,tc, 2+numPts,tc, 7+numPts,tc);
        return numFaces;
    }

    boolean isLoaded() {
        return isLoaded;
    }

    boolean isSetup() {
        return isSetup;
    }

    boolean shouldRender() {
        // If the mesh is not empty?
        return shouldRender;
    }

    public Point3D getPosition() {
        return position;
    }

    public ChunkManager getManager() {
        return manager;
    }

    public void setManager(ChunkManager manager) {
        this.manager = manager;
    }
    
    public MeshView getMeshView() {
        return meshView;
    }
    
    public double getSideLength() {
        return CHUNK_SIZE * RENDER_SIZE * 2;
    }
    
    public Bounds getBounds() {
        return bounds;
    }    
    
    public Block getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }
    
    public Block getBlockByFace(int faceNumber) {
        int originalFaceNumber = faceNumber;
        // Faces are added in twos.
        if(faceNumber % 2 == 1) faceNumber--;
        
        while(!faceMap.containsKey(faceNumber)) faceNumber -= 2;

        System.out.println("Face " + originalFaceNumber + " lowered to " + faceNumber);
        return faceMap.get(faceNumber);
    }
    
    public void setDrawMode(DrawMode mode) {
        this.drawMode = mode;
        meshView.setDrawMode(drawMode);
    }
    
    public void setMaterial(Material mat) {
        this.blockMaterial = mat;
        meshView.setMaterial(blockMaterial);
    }

    public boolean isInScene() {
        return isInScene;
    }

}
