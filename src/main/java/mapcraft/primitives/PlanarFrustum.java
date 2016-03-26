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
public class PlanarFrustum implements Frustum {    
    private Plane[] plane = new Plane[6];
    
    private Point3D nearPoint;
    private Point3D farPoint;
    
    @Override
    public void init(PerspectiveCamera camera) {
        double nearDist = camera.getNearClip();
        double farDist = camera.getFarClip();
        
        double nearHeight = Math.tan(Math.toRadians(camera.getFieldOfView())/2) * nearDist;
        double nearWidth = nearHeight * camera.getScene().getWidth() / camera.getScene().getHeight();
        
        Point3D position = camera.localToScene(Point3D.ZERO);
        Point3D zVec = camera.localToScene(0.0, 0.0, 1.0).subtract(position).normalize();
        Point3D xVec = camera.localToScene(1.0, 0.0, 0.0).subtract(position).normalize();
        Point3D yVec = camera.localToScene(0.0, -1.0, 0.0).subtract(position).normalize();
        
        nearPoint = position.add(zVec.multiply(nearDist));        
        Point3D nearHVec = yVec.multiply(nearHeight);
        Point3D nearWVec = xVec.multiply(nearWidth);

        farPoint =  position.add(zVec.multiply(farDist));
        
        plane[0] = new Plane(nearPoint, zVec.multiply(-1.0));
        plane[1] = new Plane(farPoint, zVec);
                
        Point3D tpPoint = nearPoint.add(nearHVec);
        Point3D tpNormal = tpPoint.subtract(position).normalize().crossProduct(xVec);
        plane[2] = new Plane(tpPoint, tpNormal);

        Point3D bpPoint = nearPoint.subtract(nearHVec);
        Point3D bpNormal = xVec.crossProduct( bpPoint.subtract(position).normalize() );
        plane[3] = new Plane(bpPoint, bpNormal);

        Point3D lpPoint = nearPoint.subtract(nearWVec);
        Point3D lpNormal = lpPoint.subtract(position).normalize().crossProduct(yVec);
        plane[4] = new Plane(lpPoint, lpNormal);

        Point3D rpPoint = nearPoint.add(nearWVec);
        Point3D rpNormal = yVec.crossProduct( lpPoint.subtract(position).normalize() );
        plane[5] = new Plane(rpPoint, rpNormal);
    }

    @Override
    public void update(PerspectiveCamera camera) {
        init(camera);
    }

    @Override
    public boolean isPointInside(Point3D point) {
        for(int i=0; i < 6; i++ ) {
            if(plane[i].sidedness(point) < 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSphereInside(Point3D center, double radius) {
        for(int i=0; i < 6; i++ ) {
            double distance = plane[i].distance(center);
            double negRadius = radius * -1.0;
            if(distance < negRadius)
                return false;
            // Could check for intersection here
            //else if (distance < radius)
            //    intersection
        }
        return true;
    }

    @Override
    public boolean isBoxInside(Bounds box) {
        return isBoxInsideCornerCheck(box);
    }
    
    private boolean isBoxInsideCornerCheck(Bounds box) {
        int out, in;
        Point3D corners[] = new Point3D[8];
        
        corners[0] = new Point3D(box.getMaxX(), box.getMaxY(), box.getMaxZ());
        corners[1] = new Point3D(box.getMinX(), box.getMaxY(), box.getMaxZ());
        corners[2] = new Point3D(box.getMaxX(), box.getMinY(), box.getMaxZ());
        corners[3] = new Point3D(box.getMinX(), box.getMinY(), box.getMaxZ());
        corners[4] = new Point3D(box.getMaxX(), box.getMaxY(), box.getMinZ());
        corners[5] = new Point3D(box.getMinX(), box.getMaxY(), box.getMinZ());
        corners[6] = new Point3D(box.getMaxX(), box.getMinY(), box.getMinZ());
        corners[7] = new Point3D(box.getMinX(), box.getMinY(), box.getMinZ());
        
        for(int i=0; i < 6; i++ ) {
            // Reset corner counters
            out = 0; in = 0;
            
            // Check each corner of the box
            // Only continue if a box has corners on the same side
            for(int k = 0; k < 8 && (in==0 || out==0); k++) {
                if(plane[i].sidedness(corners[k]) < 0 )
                    out++;
                else
                    in++;
            }
            
            // if all corners are out
            if(in == 0)
                return false;
        }
        return true;        
    }
    
    private boolean isBoxInsideNearCornerCheck(Bounds box) {
        double x, y, z;
        Point3D n;
        
        for(int i=0; i<6; i++) {
            x = box.getMinX();
            y = box.getMinY();
            z = box.getMinZ();
            
            n = plane[i].getNormal();
            if(n.getX() >= 0)
                x = box.getMaxX();
            if(n.getY() >= 0)
                y = box.getMaxY();
            if(n.getZ() >= 0)
                z = box.getMaxZ();
            
            if(plane[i].sidedness(new Point3D(x, y, z)) < 0)
                return false;
                
        }
        return true;
    }  

    @Override
    public Point3D getNearPoint() {
        return nearPoint;
    }

    @Override
    public Point3D getFarPoint() {
        return farPoint;
    }

}
