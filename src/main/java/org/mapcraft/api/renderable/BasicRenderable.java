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
public abstract class BasicRenderable implements Renderable {
    private Point3D position;
    private boolean renderable;
    
    @Override
    public final Point3D getPosition() {
        return position;
    }
    
    @Override
    public final boolean isRenderable() {
        return renderable;
    }

}
