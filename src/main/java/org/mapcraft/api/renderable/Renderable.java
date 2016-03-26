/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.renderable;

import javafx.geometry.Point3D;

/**
 *
 * @author rmalot
 */
public interface Renderable {
    /**
     * Every renderable object needs a position in the world.
     * 
     * @return Location of the object in scene coordinates.
     */
    public Point3D getPosition();
    
    /**
     * Orientation, or facing, of the object.
     * 
     * @return Vector representing the facing of the object.
     */
    public Point3D getOrientation();

    /**
     * Should the object be rendered in its current state.
     * 
     * @return boolean
     */
    public boolean isRenderable();
    
    /**
     * 
     */
    public void render();
    
}
