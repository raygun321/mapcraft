/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.primitives;

import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.PerspectiveCamera;

/**
 *
 * @author rmalot
 */
public interface Frustum {
    public void init(PerspectiveCamera camera);
    public void update(PerspectiveCamera camera);
    
    public boolean isPointInside(Point3D point);
    public boolean isSphereInside(Point3D center, double radius);
    public boolean isBoxInside(Bounds box);
    
    public Point3D getNearPoint();
    public Point3D getFarPoint();
}
