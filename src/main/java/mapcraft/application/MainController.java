/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.application;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import static javafx.scene.input.KeyCode.L;
import javafx.scene.input.PickResult;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import mapcraft.block.Chunk;
import mapcraft.block.ChunkManager;
import mapcraft.primitives.CameraManager;

/**
 *
 * @author rmalot
 */
public class MainController {
        
    @FXML
    private AnchorPane drawPane;
    
    @FXML
    private Button drawModeButton;
    private boolean drawModeFillFlag = true;

    @FXML
    private Button contentButton;
    private boolean contentFlag = false;

    @FXML private Button resetButton;
    @FXML private Button activeButton;
    @FXML private Button deactiveButton;

    @FXML private Button cameraFOVButton;
    private int cameraFOVValue = 2;
    
    @FXML private Button updateButton;

    private ChunkManager chunkManager;
    
    private final CameraManager cameraManager = new CameraManager();
    
    private static final double CONTROL_MULTIPLIER = 0.1;    
    private static final double SHIFT_MULTIPLIER = 10.0;    
    private static final double MOUSE_SPEED = 0.1;    
    private static final double ROTATION_SPEED = 2.0;    
    private static final double TRACK_SPEED = 0.3;
    
    private double modifierFactor = 1.0;
        
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    
    private boolean mouseDragged = false;
    private PickResult clickedPick;
    
    private void handleMouse(Node scene, final Node root) {
        scene.setOnMouseReleased((event) -> {
            if(!mouseDragged) {
                if(clickedPick != null) {
                    System.out.println("PickResult: " + clickedPick);
                    System.out.println("IntersectedNode: " + clickedPick.getIntersectedNode());
                    System.out.println("IntersectedFace: " + clickedPick.getIntersectedFace());

                    if(clickedPick.getIntersectedFace() > 0) {
                        Chunk theChunk = chunkManager.getChunkByNode(clickedPick.getIntersectedNode());
                        theChunk.getBlockByFace(clickedPick.getIntersectedFace()).setActive(false);
                        theChunk.update();
                    }              
                }
                clickedPick = null;
            }
        });
        
        scene.setOnMousePressed((event) -> {
            mouseDragged = false;
            
            clickedPick = event.getPickResult();

            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
        });
        
        scene.setOnMouseDragged((event) -> {
//            System.out.println("Mouse Dragged");
            mouseDragged = true;
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX); 
            mouseDeltaY = (mousePosY - mouseOldY);

            double modifier = 0.5;

            if (event.isPrimaryButtonDown()) {
                cameraManager.getRotXform().ry.setAngle(cameraManager.getRotXform().ry.getAngle() +
                   mouseDeltaX*modifierFactor*modifier*ROTATION_SPEED);
                
                //Constrain X rotation to be upright.
                double newValue = cameraManager.getRotXform().rx.getAngle() -
                   mouseDeltaY*modifierFactor*modifier*ROTATION_SPEED;
                if(newValue > 90.0) newValue = 90.0;
                else if(newValue < -90.0) newValue = -90.0;
                cameraManager.getRotXform().rx.setAngle(newValue);
                
            }
            
        }); // setOnMouseDragged
    }
    
    private void handleKeyboard(Node scene, final Node root) {
        
        scene.setOnKeyPressed((event) -> {
//            System.out.println("Key Pressed!");
            
            Double modifier = 4.0;
            if (event.isMetaDown()) {
                modifier = 0.1;
            }
            if (event.isAltDown()) {
                modifier = 10.0;
            }
            
            Point3D result;
            switch (event.getCode()) {
                case D:
                    // Straffe Right to view. 
                    result = cameraManager.getRotXform().rx.transform(new Point3D(modifierFactor*modifier*TRACK_SPEED,0.0,0.0));
                    result = cameraManager.getRotXform().ry.transform(result);
                    result = cameraManager.getRotXform().rz.transform(result);
                    
                    cameraManager.getTXform().t.setX(cameraManager.getTXform().t.getX() + result.getX());
                    cameraManager.getTXform().t.setY(cameraManager.getTXform().t.getY() + result.getY());
                    cameraManager.getTXform().t.setZ(cameraManager.getTXform().t.getZ() + result.getZ());
                    
                    break;
                case W:
                    // Move the camera forward in line to the view.
                    result = cameraManager.getRotXform().rx.transform(new Point3D(0.0,0.0,modifierFactor*modifier*TRACK_SPEED));
                    result = cameraManager.getRotXform().ry.transform(result);
                    result = cameraManager.getRotXform().rz.transform(result);
                    
                    cameraManager.getTXform().t.setX(cameraManager.getTXform().t.getX() + result.getX());
                    cameraManager.getTXform().t.setY(cameraManager.getTXform().t.getY() + result.getY());
                    cameraManager.getTXform().t.setZ(cameraManager.getTXform().t.getZ() + result.getZ());
                    break;
                case A:
                    // Straffe Left to view.
                    result = cameraManager.getRotXform().rx.transform(new Point3D(modifierFactor*modifier*TRACK_SPEED,0.0,0.0));
                    result = cameraManager.getRotXform().ry.transform(result);
                    result = cameraManager.getRotXform().rz.transform(result);
                    
                    cameraManager.getTXform().t.setX(cameraManager.getTXform().t.getX() - result.getX());
                    cameraManager.getTXform().t.setY(cameraManager.getTXform().t.getY() - result.getY());
                    cameraManager.getTXform().t.setZ(cameraManager.getTXform().t.getZ() - result.getZ());

                    break;
                case S:
                    // Move the camera backward in line to the view.
                     result = cameraManager.getRotXform().rx.transform(new Point3D(0.0,0.0,modifierFactor*modifier*TRACK_SPEED));
                    result = cameraManager.getRotXform().ry.transform(result);
                    result = cameraManager.getRotXform().rz.transform(result);
                    
                    cameraManager.getTXform().t.setX(cameraManager.getTXform().t.getX() - result.getX());
                    cameraManager.getTXform().t.setY(cameraManager.getTXform().t.getY() - result.getY());
                    cameraManager.getTXform().t.setZ(cameraManager.getTXform().t.getZ() - result.getZ());
                    break;
                case DOWN:
                    // Move the camera down.
                    cameraManager.getTXform().t.setY(cameraManager.getTXform().t.getY() - 
                        modifierFactor*modifier*TRACK_SPEED);
                    break;
                case UP:
                    // Move the camera up.
                    cameraManager.getTXform().t.setY(cameraManager.getTXform().t.getY() + 
                        modifierFactor*modifier*TRACK_SPEED);
                    break;
                case Z: // Reset Angle
                    cameraManager.reset();
                    break;
                case L:
                    handleDrawModeToggle();
                    break;
            } // switch
            
            System.out.println("Camera Manager Xform.ry: " + cameraManager.getRotXform().ry);
            System.out.println("Camera Manager Xform.rx: " + cameraManager.getRotXform().rx);

        });
    }  //  handleKeyboard()

    @FXML
    private void handleResetButtonAction(ActionEvent event) {
        System.out.println("Clicked ResetButton!");
        cameraManager.reset();
    }
    
    @FXML
    private void handleUpdateButtonAction(ActionEvent event) {
        System.out.println("Clicked UpdateButton!");
        chunkManager.update(cameraManager);
    }
    
    @FXML
    private void handleDrawModeButtonAction(ActionEvent event) {
        System.out.println("Clicked DrawModeButton!");
        handleDrawModeToggle();
    }
    
    private void handleDrawModeToggle() {
        System.out.println("Toggling Draw Mode.");
        
        if(drawModeFillFlag) {
            chunkManager.setDrawMode(DrawMode.LINE);
            drawModeButton.setText("Fill");
        } else {
            chunkManager.setDrawMode(DrawMode.FILL);
            drawModeButton.setText("Line");
        }
        
        drawModeFillFlag = !drawModeFillFlag;
    }
    
    @FXML
    private void handleContentButtonAction(ActionEvent event) {
        System.out.println("Clicked ContentButton!");
        
        if(!contentFlag) {
            contentFlag = true;
            
            try {
//            drawPane.getChildren().add(createContent());
            } catch (Exception e) {
                System.out.println("Exception: " + e.toString());
            }
        }
    }

    @FXML
    private void handleActiveButtonAction(ActionEvent event) {
        System.out.println("Clicked Active Button!");
        
        for(int i = 0; i < 16; i++) {
            int x = new Double( Math.random() * Chunk.CHUNK_SIZE ).intValue();
            int y = new Double( Math.random() * Chunk.CHUNK_SIZE ).intValue();
            int z = new Double( Math.random() * Chunk.CHUNK_SIZE ).intValue();
            chunkManager.getChunk(1).getBlock(x, y, z).setActive(true);
        }
        chunkManager.getChunk(1).update();
    }    

    @FXML
    private void handleDeactiveButtonAction(ActionEvent event) {
        System.out.println("Clicked Deactive Button!");
        
        for(int i = 0; i < 128; i++) {
            int x = new Double( Math.random() * Chunk.CHUNK_SIZE ).intValue();
            int y = new Double( Math.random() * Chunk.CHUNK_SIZE ).intValue();
            int z = new Double( Math.random() * Chunk.CHUNK_SIZE ).intValue();
            chunkManager.getChunk(1).getBlock(x, y, z).setActive(false);
        }
        
        chunkManager.getChunk(1).update();
    }
    
    @FXML
    private void handleCameraFOVButtonAction(ActionEvent event) {
        System.out.println("Clicked Camera FOV Button!");
        
        cameraFOVValue++;
        if(cameraFOVValue > 5) {
            cameraFOVValue = 0;
        }
        
        switch(cameraFOVValue) {
            case 0:
                cameraFOVButton.setText("XTel");
                cameraManager.getCamera().setFieldOfView(5.0);
                break;
            case 1:
                cameraFOVButton.setText("Tele");
                cameraManager.getCamera().setFieldOfView(20.0);
                break;
            case 2:
                cameraFOVButton.setText("Narrow");
                cameraManager.getCamera().setFieldOfView(40.0);
                break;
            case 3:
                cameraFOVButton.setText("Normal");
                cameraManager.getCamera().setFieldOfView(62.0);
                break;
            case 4:
                cameraFOVButton.setText("Fish");
                cameraManager.getCamera().setFieldOfView(150.0);
                break;
            case 5:
                cameraFOVButton.setText("XFish");
                cameraManager.getCamera().setFieldOfView(175.0);
                break;
        }
    }
    
    @FXML
    public void initialize() {
        // Create and position camera
        cameraManager.init();
        
        // Build the Scene Graph
        Group root = new Group();  
        root.getChildren().add(cameraManager.getRoot());

        chunkManager = new ChunkManager(root);
        chunkManager.update(cameraManager);

        // Use a SubScene       
        SubScene subScene = new SubScene(root, 1024, 744, true, SceneAntialiasing.BALANCED );
        subScene.setFill(Color.ALICEBLUE);
        subScene.setCamera(cameraManager.getCamera());
        Group group = new Group();
        group.getChildren().add(subScene);
        
        handleKeyboard(drawPane.getParent(), root);
        handleMouse(drawPane, root);
        
        drawPane.getChildren().add(group);
        
//        AmbientLight light=new AmbientLight(Color.IVORY);
//        light.setTranslateX(-180);
//        light.setTranslateY(-90);
//        light.setTranslateZ(-120);
//        root.getChildren().add(light);
//        //light.getScope()
//
//        PointLight light2=new PointLight(Color.AQUA);
//        light2.setTranslateX(180);
//        light2.setTranslateY(190);
//        light2.setTranslateZ(180);
//        light2.getScope().addAll(box,sphere);
//
//        Group group = new Group(sphere, box);
//        ...
//        group.getChildren().addAll(light,light2);
        
        // Set up an animation timer to keep the chunk manager chugging along.
        new AnimationTimer() {
            long then;
            
            @Override
            public void handle(long now) {
                if(now > then) {
                    chunkManager.update(cameraManager);
                    then = now + 50;
                }
            }
        }.start();
        
//        // Create Image and ImageView objects
//        WritableImage image = new WritableImage(100, 100);
//        PixelWriter pixelWriter = image.getPixelWriter();
//        
//        // Determine the color of each pixel in a specified row
//        SimpleNoiseOctave sno = new SimpleNoiseOctave();
//        for(int y=0; y<image.getHeight(); y++){
//            for(int x=0; x<image.getWidth(); x++){
//                double red_noise = sno.octavedNoise(x, y,  3, 0.4d, 0.005d) / 2.0 + 1.0;
//                if (red_noise > 1.0) red_noise = 1.0;
//                if (red_noise < 0.0) red_noise = 0.0;
//                double green_noise = sno.octavedNoise(x, y,  4, 0.4d, 0.015d) / 2.0 + 1.0;
//                if (green_noise > 1.0) green_noise = 1.0;
//                if (green_noise < 0.0) green_noise = 0.0;
//                double blue_noise = sno.octavedNoise(x, y,  5, 0.4d, 0.105d) / 2.0 + 1.0;
//                if (blue_noise > 1.0) blue_noise = 1.0;
//                if (blue_noise < 0.0) blue_noise = 0.0;
//
//                Color color = new Color(red_noise, green_noise, blue_noise, 1.0);
//
//                pixelWriter.setColor(x,y,color);
//            }
//        }
//        
//        ImageView imageView = new ImageView();
//        imageView.setImage(image);
//      
//        // Display image on screen
//        root.getChildren().add(imageView);
    }
    
}
