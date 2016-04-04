/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api;

import org.mapcraft.engine.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rmalot
 */
public class MapCraft {
    private static Engine instance = null; // Change Engine Type
    private static final Logger logger = LoggerFactory.getLogger(MapCraft.class);

    private MapCraft() {
            throw new IllegalStateException("Can not construct MapCraft instance");
    }

    
    public static Engine getEngine() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static boolean debugMode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
