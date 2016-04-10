/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.primitives;

import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import mapcraft.map.SimpleNoiseOctave;

/**
 *
 * @author rmalot
 */
public class CameraManager {
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    
    private final Sphere cameraSphere = new Sphere(0.1);
    private final Sphere fogSphere = new Sphere(64);
    private final PhongMaterial fogMat = new PhongMaterial();
    private final Sphere worldSphere = new Sphere(120);
    private final PhongMaterial worldMat = new PhongMaterial();
    private final Xform cameraXform = new Xform(); // Position
    private final Xform cameraXform1 = new Xform(); // Rotate
    private final Frustum frustum = new PlanarFrustum();

    public static final double CAMERA_INITIAL_DISTANCE = 0.0;
    public static final double CAMERA_INITIAL_X_ANGLE = 10.0;
    public static final double CAMERA_INITIAL_Y_ANGLE = 0.0;
    public static final double CAMERA_NEAR_CLIP = 0.1;
    public static final double CAMERA_FAR_CLIP = 1000.0;
        
    public void init() {
        /* Don't hide the inside of the world sphere */
        fogSphere.setCullFace(CullFace.NONE);
        
        // Create Image and ImageView objects
        WritableImage image = new WritableImage(256, 256);
        PixelWriter pixelWriter = image.getPixelWriter();
                
        // Determine the color of each pixel in a specified row
        // TODO: Get the texture to wrap...
        Color theColor;
        SimpleNoiseOctave sno = new SimpleNoiseOctave();
        for(int y=0; y<image.getHeight(); y++){
            for(int x=0; x<image.getWidth(); x++){
                double val = sno.octavedNoise(x, y, 3, 0.4d, 0.005d) + 1.0 / 2;
                if(val < 0.0) val = 0.0;
                if(val > 1.0) val = 1.0;
                theColor = new Color(val, val, val, 0.1);
                pixelWriter.setColor(x,y, theColor);
            }
        }
        fogMat.setDiffuseMap(image);
        fogMat.setSpecularPower(0.0);
        fogSphere.setMaterial(fogMat);        
        
        worldSphere.setCullFace(CullFace.NONE);
        worldMat.setDiffuseColor(Color.CORNFLOWERBLUE);
        worldSphere.setMaterial(worldMat);
        
        AmbientLight light=new AmbientLight(Color.IVORY);
        
        cameraXform.getChildren().add(camera);
        cameraXform.getChildren().add(cameraSphere);
        cameraXform.getChildren().add(worldSphere);
        cameraXform.getChildren().add(fogSphere);
        cameraXform.getChildren().add(light);
        
        cameraXform.getChildren().add(cameraXform1);
        cameraXform1.getChildren().add(camera);
        cameraXform1.setRotateZ(180.0);
 
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);

        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }
    
    public Node getRoot() {
        return cameraXform;
    }
    
    public PerspectiveCamera getCamera() {
        return camera;
    }

    public Xform getTXform(){
        return cameraXform;
    }

    public Xform getRotXform(){
        return cameraXform1;
    }
    
    public void update() {
        frustum.update(camera);
    }
    
    public boolean isCubeInFrustrum(Bounds boundingBox) {
        return frustum.isBoxInside(boundingBox);
    }
    
    public Frustum getFrustum() {
        return frustum;
    }
    
    public void reset() {
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        cameraXform.t.setX(0.0);
        cameraXform.t.setY(0.0);
        cameraXform.setRotateZ(180.0);
    }
    
    public Point3D getCameraOriginToScene() {
        return cameraXform.localToScene(Point3D.ZERO);
    }
    
}
