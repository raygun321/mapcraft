/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.primitives;

import javafx.geometry.Point3D;

/**
 *
 * @author rmalot
 */
public class Plane {
    private final Point3D normal;
    private final Point3D unitNormal;
    private final double constant;
    
    private double denom;
    
    public static final double TOLERANCE = 0.0000001;
    
    /**
     * Constructor used to create a plane from a point and normal
     * 
     * @param pointOnPlane
     * @param normal 
     */
    public Plane(Point3D pointOnPlane, Point3D normal) {
        this.normal = normal;
        constant = normal.dotProduct(pointOnPlane) * -1.0;
        
        unitNormal = normal.normalize();
        denom = normal.getX() * normal.getX() + normal.getY() * normal.getY() + normal.getZ() * normal.getZ();
        denom = Math.sqrt(denom);
    }
    
    /**
     * Constructor used to create a plane from three points
     * 
     * The three points should be specified in a counter clockwise order (right hand rule)
     * 
     * @param point1
     * @param point2
     * @param point3 
     */
    public Plane(Point3D point1, Point3D point2, Point3D point3) {
        this(point2, point2.subtract(point1).crossProduct(point3.subtract(point1)));
//        Point3D vec1 = point2.subtract(point1);
//        Point3D vec2 = point3.subtract(point1);
//        
//        this(point2, vec1.crossProduct(vec2));
    }
    
    /**
     * Determines the distance from the plane to the point.
     * 
     * Can be negative if the point is below the plane.
     * 
     * @param point
     * @return 
     */
    public double distance(Point3D point) {
        double distance =  normal.getX() * point.getX();
        distance += normal.getY() * point.getY();
        distance += normal.getZ() * point.getZ();
        distance += constant;
        return distance / denom;
    }
    
    /**
     * Determines which side the point is to the plane
     * 
     * @param point
     * @return 
     */
    public double sidedness(Point3D point) {
        return normal.getX() * point.getX() + normal.getY() * point.getY() + normal.getZ() * point.getZ() + constant;
    }
    
    public Point3D getNormal() {
        return normal;
    }

    public double getConstant() {
        return constant;
    }
}
