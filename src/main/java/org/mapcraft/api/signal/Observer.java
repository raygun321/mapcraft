/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.observer;

/**
 *
 * @author rmalot
 */
public interface Observer {
    public void update();
    
    public void setSubject(Subject subject);
}
