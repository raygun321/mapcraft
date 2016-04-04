/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.render;

import javafx.scene.transform.Affine;

/**
 *
 * @author rmalot
 */
public interface Camera {
    /**
    * Get the projection matrix associated with this camera
    *
    * @return 4x4 matrix representing the projection
    */
    public Affine getProjection();

    /**
    * Gets the view matrix
    */
    public Affine getView();

    /**
    * Update the view matrix.
    */
    public void updateView();

    /**
    * Update the view matrix for the reflected render
    */
    public void updateReflectedView();

    /**
    * Gets the view frustum of this Camera.
    */
    public ViewFrustum getFrustum();

    public Affine getRotation();
}
