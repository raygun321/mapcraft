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
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Material;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import mapcraft.map.SimpleWorld;
import org.mapcraft.api.block.BlockFace;
import org.mapcraft.api.gui.render.ImmutableFace;
import org.mapcraft.api.gui.render.RenderPart;
import org.mapcraft.api.material.BlockMaterial;
import org.mapcraft.api.math.BitSize;

/**
 *
 * @author rmalot
 */
public class Chunk {
    public static final BitSize BLOCKS = new BitSize(4);
    public static final int CHUNK_SIZE=16;
    public static final float RENDER_SIZE=0.5f;
    
    private final Block[][][] blocks;
    private final MeshView meshView;
    private DrawMode drawMode;
    private Material blockMaterial;
    
    // Should this be static? Are we just going to have one ChunkManager?
    private ChunkManager manager;
    
    private String metadata;
    
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
        
        manager.addChunksToRebuildList(manager.getChunksAroundPoint(position, 1));
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

        manager.getWorld().generateChunk(this);

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
    
    //TODO: Needs to move to material
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
    
    /*TODO: Extract logic into a Mesher
        RenderPart is a rectangle.
        Blocks have a BlockMaterial
        BlockMaterials have RenderParts.
        Mesher takes BlockMaterials and composes the Mesh
       
     */   
    public void rebuildMesh() {
        // Create the mesh...
        TriangleMesh mesh = (TriangleMesh) meshView.getMesh();
        mesh.getFaces().clear();
        mesh.getTexCoords().clear();
        mesh.getPoints().clear();

        Integer faceCount = 0;
        for(int x = 0; x < CHUNK_SIZE; x++) {
            for(int y = 0; y < CHUNK_SIZE; y++) {
                for(int z = 0; z < CHUNK_SIZE; z++) {
                    Block curBlock = blocks[x][y][z];
                    if(curBlock != null && curBlock.isActive()) {
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
                            faceMap.put(faceCount, curBlock);
                            faceCount += AddCubeToMesh(mesh, x, y, z, curBlock.getBlockType(), xNeg, xPos, yNeg, yPos, zNeg, zPos);

                            activeBlockCount++;
                        }
                    }
                }
            }
        }
        shouldRender = activeBlockCount > 0;
    }
    
    //TODO: Extract logic into a Mesher
    private int AddCubeToMesh(TriangleMesh mesh, int x, int y, int z, BlockType type,
            boolean xNeg, boolean xPos, boolean yNeg, boolean yPos, boolean zNeg, boolean zPos) {
        int tc = getTextureCoordFromBlockType(type);
        int numFaces =0;
        
        boolean[] shouldRenderFace = new boolean[6];
        shouldRenderFace[0] = !yPos;
        shouldRenderFace[1] = !xNeg;
        shouldRenderFace[2] = !zNeg;
        shouldRenderFace[3] = !xPos;
        shouldRenderFace[4] = !zPos;
        shouldRenderFace[5] = !yNeg;
    
        BlockMaterial mat = manager.getMaterialManager().getMaterialForType(type);
        for(int i=0; i< 6; i++) {
            if(shouldRenderFace[i]) {
                int baseTc = mesh.getTexCoords().size() / mesh.getTexCoordElementSize();
                int basePts = mesh.getPoints().size() / mesh.getPointElementSize();
                RenderPart part = mat.getRenderPartForFace(BlockFace.getFaceByInt(i));
                for(Point2D point : part.getTextureCoordinates()) {
                    mesh.getTexCoords().addAll((float) point.getX(), (float) point.getY());
                }

                for(Point3D point : part.getPoints()) {
                    mesh.getPoints().addAll((float)(x+point.getX()), (float)(y+point.getY()), (float)(z+point.getZ()));
                }
                
                for(ImmutableFace face : part.getFaces()) {
                    mesh.getFaces().addAll(face.getVertexIndex(0)+basePts, face.getTextureIndex(0)+baseTc, 
                            face.getVertexIndex(1)+basePts, face.getTextureIndex(1)+baseTc, 
                            face.getVertexIndex(2)+basePts, face.getTextureIndex(2)+baseTc);
                    numFaces++;
                }
            }
        }

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
    
    public void setBlock(int x, int y, int z, Block theBlock) {
        blocks[x][y][z] = theBlock;
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

    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
        
        //TODO: Deal with change of metadata. Deactivate/Active Nodes. Metadata should be a patch from the generated world.
    }
    
}
