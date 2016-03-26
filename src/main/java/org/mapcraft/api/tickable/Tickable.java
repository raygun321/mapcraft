/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.tickable;

/**
 *
 * @author rmalot
 */
public interface Tickable {
    /**
     * Called each tick.<br/> Ticks are a measure of game time, but occur when triggered.
     *
     * @param dt time since the last tick in seconds
     */
    public void onTick(float dt);

    /**
     * Whether or not this Tickable can perform a tick
     *
     * @return true if it can tick, false if not
     */
    public boolean canTick();

    /**
     * Ticks the Tickable.<br/> Call this to make the Tickable tick.<br/>
     *
     * Standard implementation is if(canTick()) { onTick(dt); }<br/>
     *
     * @param dt time since the last tick in seconds
     */
    public void tick(float dt);
}
