/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.DrawMode;
import mapcraft.map.World;
import mapcraft.primitives.CameraManager;
import org.mapcraft.api.material.BlockMaterialManager;

/**
 *
 * @author rmalot
 */
public class ChunkManager {
    // Up to 63 bits of width+height+depth. Leave a bit for sign for each.
    public static final int MAX_WIDTH = 1024;
    public static final int MAX_HEIGHT = 1024;
    public static final int MAX_DEPTH = 1024;
    private static final int ASYNC_NUM_CHUNKS_PER_FRAME = 10;
    
    // Lots of list of chunks
    private final List<Chunk> chunkLoadList;          // List of chunks to load - cleared in load phase and populated in visibility phase
    private final List<Chunk> chunkSetupList;         // List of chunks to setup - cleared in setup phase
    private final List<Chunk> chunkRebuildList;       // List of chunks to rebuild - cleared in rebuild phase
    private final List<Chunk> chunkUpdateFlagsList;   // List of chunks to update flags - populated in rebuild phase
    private final List<Chunk> chunkUnloadList;        // List of chunks to upload - cleared in unload phase
    private final List<Chunk> chunkVisibilityList;    // List of chunks that can be seen
    private final List<Chunk> chunkTempVisibilityList;    // List of chunks that can be seen
    private final List<Chunk> chunkRenderList;        // List of chunks to render - cleared in render phase
    private final Map<Node, Chunk> chunkNodeMap;      // Map of chunks addressable by their meshView nodes.
   
    private final ChunkLoader loader;
    private final BlockMaterialManager materialManager;
    private final Group scene;

    private Point3D cameraPosition;
    private Point3D cameraView;
    private boolean forceVisibilityUpdate;
    
    private DrawMode drawMode;
    
    private World world;
    
    public ChunkManager(Group scene) {
        chunkLoadList = new ArrayList<>();
        chunkSetupList = new ArrayList<>();
        chunkRebuildList = new ArrayList<>();
        chunkUpdateFlagsList = new ArrayList<>();
        chunkUnloadList = new ArrayList<>();
        chunkVisibilityList = new ArrayList<>();
        chunkTempVisibilityList = new ArrayList<>();
        chunkRenderList = new ArrayList<>();
        chunkNodeMap = new HashMap<>();
        
//        loader = new SampleChunkLoader();
        loader = new TextureChunkLoader();
        this.scene = scene;
        
        forceVisibilityUpdate = false;
        cameraPosition = Point3D.ZERO;
        cameraView = Point3D.ZERO;
        
        drawMode = DrawMode.FILL;
        
        world = new World(new Double(Math.random()*Long.MAX_VALUE).longValue());
//        world = new World(1000000L);
        materialManager = new BlockMaterialManager();
    }
        
    public void update(CameraManager cameraManager) {
        Point3D centerOfView = cameraManager.getRoot().localToScene(Point3D.ZERO);
        
        // If camera is underground - move it up
        Double yVal = world.getValue(centerOfView.getX(), centerOfView.getZ());
        if(centerOfView.getY() < yVal) {
            double y = cameraManager.getRoot().getTranslateY();
            y += yVal - centerOfView.getY() + 2.0;
        }

        Camera camera = cameraManager.getCamera();
        Point3D camPosition = camera.localToScene(Point3D.ZERO);
        Point3D camView = camera.localToScene(0.0, 0.0, 1.0);        
        
        // What is this supposed to do?
        // updateAsyncChunker();
//        System.out.println("updateLoadList");
        updateLoadList();
//        System.out.println("updateSetupList");
        updateSetupList();
//        System.out.println("updateRebuildList");
        updateRebuildList();
        //updateFlagsList();
  //      System.out.println("updateUnloadList");
        updateUnloadList();
 //       System.out.println("updateVisibilityList");
        updateVisibilityList(camPosition);
//        System.out.println("updateFlagsList");
        updateFlagsList();
	
        if(cameraPosition != camPosition || cameraView != camView)
        {
//            System.out.println("updateRenderList");
            updateRenderList(cameraManager);
        }

        cameraPosition = camPosition;
        cameraView = camView;
    }
    
    /**
     * Unknown Captain.
     */
    private void updateAsyncChunker() {
        //TODO: Need to implement something here
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * 
     */
    private void updateLoadList() {
        int numberOfChunksLoaded = 0;
        
        for(Chunk currentChunk : chunkLoadList) {
            if(numberOfChunksLoaded == ASYNC_NUM_CHUNKS_PER_FRAME) {
                break;
            }
            
            if(!currentChunk.isLoaded()) {
                currentChunk.load();
                
                numberOfChunksLoaded++;
                forceVisibilityUpdate = true;
                
                System.out.println("updateLoadList current Position: " + currentChunk.getPosition());
                
                for(Chunk nextChunk : getChunksAroundPoint(currentChunk.getPosition(), 1)) {
                    if(nextChunk != null && nextChunk.isLoaded() && nextChunk.isSetup() ) {
                        chunkRebuildList.add(nextChunk);
                    }
                }
            }
        }

        // Clear the load list (every frame)
        chunkLoadList.clear();
    }
    
    private void updateSetupList() {
        // Setup any chunks that have not already been setup
        for(Chunk currentChunk : chunkSetupList) {
            if(currentChunk.isLoaded() && !currentChunk.isSetup()) {
                currentChunk.setup();
                
                if(currentChunk.isSetup()) {
                    // Only force the visibility update if we actually setup the chunk,
                    // some chunks wait in the pre-setup stage...
                    forceVisibilityUpdate = true;
                }
            }
        }
        
        // Clear the setup list (every frame)
        chunkSetupList.clear();
    }
    
    private void updateRebuildList() {
        // Rebuild any chunks that are in the rebuild chunk list
        int numberChunksRebuiltThisFrame = 0;
        
        for(Chunk currentChunk : chunkRebuildList) {
            if(numberChunksRebuiltThisFrame == ASYNC_NUM_CHUNKS_PER_FRAME) {
                break;
            }
            
            if(currentChunk.isLoaded() && currentChunk.isSetup()) {
                currentChunk.rebuildMesh();
                
                // If we rebuild a chunk, add it to the list of chunks that need 
                // their render flags updated
                // since we might now be empty or surrounded
                chunkUpdateFlagsList.add(currentChunk);
                
                // Also add our neighbours since they might now be surrounded too (If we have neighbours)
                for(Chunk tempChunk : getChunksAroundPoint(currentChunk.getPosition(), 1)) {
                    if(tempChunk != null) 
                        chunkUpdateFlagsList.add(tempChunk);
                }
                
                // Only rebuild a certain number of chunks per frame
                numberChunksRebuiltThisFrame++;
                forceVisibilityUpdate = true;
            }
        }
        
        // Clear the rebuild list
        chunkRebuildList.clear();
    }             

    private void updateFlagsList() {
        for(Chunk currentChunk : chunkVisibilityList) {
            if(!currentChunk.isLoaded()) {
                chunkLoadList.add(currentChunk);
            } else if (!currentChunk.isSetup()) {
                chunkSetupList.add(currentChunk);
            }
        }

        // If we rebuild a chunk, add it to the list of chunks that need 
                // their render flags updated
                // since we might now be empty or surrounded        
        for(Chunk currentChunk : chunkUpdateFlagsList) {
            currentChunk.setup();
        }
        chunkUpdateFlagsList.clear();
                
    }
    
    private void updateUnloadList() {
        for(Chunk currentChunk : chunkUnloadList) {
            if(currentChunk.isLoaded()) {
                currentChunk.unload();
                
                forceVisibilityUpdate = true;
            }
        }
        // Clear the unload list (every frame)
        chunkUnloadList.clear();
    }
    
    /* TODO: This is the update phase that brings most of the ChunkManager 
     * concepts to life. The visibility update phase is used to update the 
     * VisibilityList with all the potential blocks that might be visible 
     * to the current camera. Also the visibility list is used to update 
     * all the other list, for only chunks that are visible can be set to 
     * load,setup,rebuild and render.
     */
    private void updateVisibilityList(Point3D newCameraPosition) {
        double threshold = 10*Chunk.CHUNK_SIZE;
        Chunk theChunk;
        Point3D thePoint;

        chunkUnloadList.addAll(chunkVisibilityList);
        chunkTempVisibilityList.clear();
        for(int x = -4; x < 5; x++) {
            for(int y = -2; y < 2; y++) {
                for(int z = -4; z < 5; z++) {
                    thePoint = new Point3D(x*16, y*16, z*16);
                    theChunk = getChunkAtPoint(thePoint);
                    // If we have the chunk - don't remove it.
                    if(theChunk != null) {
                        chunkUnloadList.remove(theChunk);
                    }

                    // Is chunk isn't recognized ... we will need to load it.
                    if(theChunk == null || !theChunk.isLoaded()) {
                        System.out.println("Geting chunk at " + thePoint);
                        theChunk = loader.getChunkByLocation(thePoint);
                        theChunk.setManager(this);
                        chunkVisibilityList.add(theChunk);
                    }
                    chunkTempVisibilityList.add(theChunk);
                }
            }
        }
        
        chunkUnloadList.addAll(chunkVisibilityList);
        chunkUnloadList.removeAll(chunkTempVisibilityList);
        chunkTempVisibilityList.clear();
        
//        chunkVisibilityList.clear();
//        Chunk tempChunk;
//        Point3D thePoint;
//        
//        for(int x = -4; x < 5; x++) {
//            for(int y = -2; y < 2; y++) {
//                for(int z = -4; z < 5; z++) {
//                    thePoint = new Point3D(x*16, y*16, z*16);
//                    System.out.println("Geting chunk at " + thePoint);
//                    tempChunk = loader.getChunkByLocation(thePoint);
//                    tempChunk.setManager(this);
//                    chunkVisibilityList.add(tempChunk);
//                }
//            }
//        }
        
        chunkNodeMap.clear();
        for(Chunk chunk : chunkVisibilityList) {
            if(chunk.shouldRender())
                chunkNodeMap.put(chunk.getMeshView(), chunk);
        }
        
//        // Only update visibility list if forced ... or if we cross a boundary.
//        if(!forceVisibilityUpdate) {
//            // Check to see if boundary was crossed.
//            long current_id = getChunkId(cameraPosition);
//            long new_id = getChunkId(newCameraPosition);
//            if(current_id != new_id) {
//                // Might change this later
//                forceVisibilityUpdate = true;
//            }
//        }
//        if(forceVisibilityUpdate) {
//            // Remove the chunks that are too far away
//            for(Chunk currentChunk : chunkVisibilityList) {
//                if(currentChunk.getPosition().distance(newCameraPosition) > threshold) {
//                    chunkUnloadList.add(currentChunk);
//                }
//            }
//            chunkVisibilityList.removeAll(chunkUnloadList);
//
//            // Add in chunks
//            // TODO: Don't clear unless forced
//            chunkVisibilityList.clear();
//            for(int x = 0; x < 10*Chunk.CHUNK_SIZE; x = x + Chunk.CHUNK_SIZE) {
//                for(int y = 0; y < 10*Chunk.CHUNK_SIZE; y = y + Chunk.CHUNK_SIZE) {
//                    for(int z = 0; z < 10*Chunk.CHUNK_SIZE; z = z + Chunk.CHUNK_SIZE) {
//                        Chunk tempChunk;
//                        Point3D thePoint = new Point3D(newCameraPosition.getX()+x, newCameraPosition.getY()+y, newCameraPosition.getZ()+z);
//                        System.out.println("Geting chunk at [" + thePoint.getX() + "," + thePoint.getY() + "," + thePoint.getZ() + "]");
//                        tempChunk = loader.getChunkByLocation(thePoint);
//                        tempChunk.setManager(this);
//                        chunkVisibilityList.add(tempChunk);
//                        
//                        if(x>0) {
//                            thePoint = new Point3D(newCameraPosition.getX()-x, newCameraPosition.getY()+y, newCameraPosition.getZ()+z);
//                            tempChunk = loader.getChunkByLocation(thePoint);
//                            tempChunk.setManager(this);
//                            chunkVisibilityList.add(tempChunk);
//                            if(y>0) {
//                                thePoint = new Point3D(newCameraPosition.getX()-x, newCameraPosition.getY()-y, newCameraPosition.getZ()+z);
//                                tempChunk = loader.getChunkByLocation(thePoint);
//                                tempChunk.setManager(this);
//                                chunkVisibilityList.add(tempChunk);
//                                if(z>0) {
//                                    thePoint = new Point3D(newCameraPosition.getX()-x, newCameraPosition.getY()-y, newCameraPosition.getZ()-z);
//                                    tempChunk = loader.getChunkByLocation(thePoint);
//                                    tempChunk.setManager(this);
//                                    chunkVisibilityList.add(tempChunk);
//                                }
//                            } else if(z>0) {
//                                thePoint = new Point3D(newCameraPosition.getX()-x, newCameraPosition.getY()+y, newCameraPosition.getZ()-z);
//                                tempChunk = loader.getChunkByLocation(thePoint);
//                                tempChunk.setManager(this);
//                                chunkVisibilityList.add(tempChunk);
//                            }
//                        }
//                        if(y>0) {
//                            thePoint = new Point3D(newCameraPosition.getX()+x, newCameraPosition.getY()-y, newCameraPosition.getZ()+z);
//                            tempChunk = loader.getChunkByLocation(thePoint);
//                            tempChunk.setManager(this);
//                            chunkVisibilityList.add(tempChunk);
//                            if(z>0) {
//                                thePoint = new Point3D(newCameraPosition.getX()+x, newCameraPosition.getY()-y, newCameraPosition.getZ()-z);
//                                tempChunk = loader.getChunkByLocation(thePoint);
//                                tempChunk.setManager(this);
//                                chunkVisibilityList.add(tempChunk);
//                            }
//                        }
//                        if(z>0) {
//                            thePoint = new Point3D(newCameraPosition.getX()+x, newCameraPosition.getY()+y, newCameraPosition.getZ()-z);
//                            tempChunk = loader.getChunkByLocation(thePoint);
//                            tempChunk.setManager(this);
//                            chunkVisibilityList.add(tempChunk);
//                        }
//                    }
//                }
//            }            
//        }
    }
    
    private void updateRenderList(CameraManager cameraManager){
        // Clear the render list each frame BEFORE we do our tests to see what chunks should be rendered
        chunkRenderList.clear();
        
        for(Chunk currentChunk : chunkVisibilityList) {
            if(currentChunk != null) {
                if(currentChunk.isLoaded() && currentChunk.isSetup()) {
                    // Early flags check so we don't always have to do the frustum check...
                    if(currentChunk.shouldRender()) {
                        // Check if this chunk is inside the camera frustum
//                        if(cameraManager.isCubeInFrustrum(currentChunk.getBounds())) {
                            chunkRenderList.add(currentChunk);
                            
                            if(!currentChunk.isInScene()) {
                                currentChunk.render();
                            }
//                        }                        
                    }
                }
            }
        }
    }

    private Chunk getChunk(double x, double y, double z) {
        return getChunk(getChunkId(x, y, z));
    }

    public Chunk getChunkAtPoint(Point3D point) {
        return getChunk(getChunkId(point));
    }

    public Chunk getChunk(long id) {
        return loader.getChunkById(id);
    }
    
    public static long getChunkId(double x, double y, double z) {
        Double xd = (x/Chunk.CHUNK_SIZE);
        long xl = xd.longValue() + MAX_WIDTH / 2;
        
        Double yd = (y/Chunk.CHUNK_SIZE);
        long yl = yd.longValue() + MAX_HEIGHT / 2;
        
        Double zd = (z/Chunk.CHUNK_SIZE);
        long zl = zd.longValue() + MAX_DEPTH / 2;
        return xl + MAX_HEIGHT * ( yl + zl * MAX_DEPTH);
    }
    
    public static long getChunkId(Point3D point) {
        return getChunkId(point.getX(), point.getY(), point.getZ());
    }
    
    public static Point3D getChunkPosition(long id) {
        // If sizes are not the same - smallest has to be innermost
        long xd = ((id % MAX_HEIGHT) - (MAX_WIDTH / 2)) * Chunk.CHUNK_SIZE ;
        id -= xd;
        long yd = (((id / MAX_HEIGHT) % MAX_DEPTH) - (MAX_HEIGHT / 2)) * Chunk.CHUNK_SIZE;
        id -= yd;
        long zd = (((id / MAX_HEIGHT / MAX_DEPTH )) - (MAX_DEPTH / 2)) * Chunk.CHUNK_SIZE;
        return new Point3D(xd, yd, zd);
    }

    public void setDrawMode(DrawMode drawMode) {
        this.drawMode = drawMode;
    }

    public DrawMode getDrawMode() {
        return this.drawMode;
    }

    public Group getScene() {
        return scene;
    }

    public Chunk getChunkByNode(Node intersectedNode) {
        // Only nodes that are visibile will be clickable
        return chunkNodeMap.get(intersectedNode);
    }
    
    public ChunkLoader getLoader() {
        return loader;
    }
    
    public World getWorld() {
        return world;
    }
    
    public List<Chunk> getChunksAroundPoint(Point3D point, int radius) {
        List<Chunk> chunkList = new ArrayList<>();
        int chunkRadius = radius * Chunk.CHUNK_SIZE;
        double sqRadius = chunkRadius * chunkRadius;
        
        for(double x = point.getX() - chunkRadius; 
                x < point.getX() + chunkRadius; 
                x += 16) {
            for(double y = point.getY() - chunkRadius; 
                    y < point.getY() + chunkRadius; 
                    y += 16) {
                for(double z = point.getZ() - chunkRadius; 
                        z < point.getZ() + chunkRadius; 
                        z += 16) {
                    double distance = (point.getX() - x) * (point.getX() - x) + (point.getY() - y) * (point.getY() - y) + (point.getZ() - z) * (point.getZ() - z);
                    if(distance <= sqRadius) {
                        chunkList.add(getChunkAtPoint(new Point3D(x, y, z)));
                    }
                }
            }
        }
        return chunkList;
    }

    void addChunksToRebuildList(List<Chunk> chunksAroundPoint) {
        chunkRebuildList.addAll(chunksAroundPoint);
    }

    public BlockMaterialManager getMaterialManager() {
        return materialManager;
    }
 }
