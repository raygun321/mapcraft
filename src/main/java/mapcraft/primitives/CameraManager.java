/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.primitives;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.shape.Sphere;

/**
 *
 * @author rmalot
 */
public class CameraManager {
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    
    private final Sphere sphere = new Sphere(0.5);
    private final Xform cameraXform = new Xform(); // Rotate
    private final Xform cameraXform2 = new Xform(); // Translate X and Y
    private final Xform cameraXform3 = new Xform(); // Rotate Z
    private final Frustum frustum = new PlanarFrustum();

    public static final double CAMERA_INITIAL_DISTANCE = -20;
    public static final double CAMERA_INITIAL_X_ANGLE = 10.0;
    public static final double CAMERA_INITIAL_Y_ANGLE = 0.0;
    public static final double CAMERA_NEAR_CLIP = 0.1;
    public static final double CAMERA_FAR_CLIP = 100.0;
        
    public void init() {
        /* Sphere needs to be one level higher */
        cameraXform.getChildren().add(cameraXform2);
        cameraXform.getChildren().add(sphere);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);
 
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
    
    public Xform getXform(){
        return cameraXform;
    }

    public Xform getXform2(){
        return cameraXform2;
    }

    public Xform getXform3(){
        return cameraXform3;
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
        cameraXform2.t.setX(0.0);
        cameraXform2.t.setY(0.0);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }
    
}
