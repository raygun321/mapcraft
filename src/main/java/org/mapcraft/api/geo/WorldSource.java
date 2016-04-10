/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.geo;

import mapcraft.map.SimpleWorld;

/**
 *
 * @author rmalot
 */
public interface WorldSource {
    /**
     * Gets the World
     *
     * @return the World
     */
    public SimpleWorld getWorld();
}
