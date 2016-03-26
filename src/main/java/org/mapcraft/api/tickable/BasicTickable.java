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
public abstract class BasicTickable implements Tickable {
    @Override
    public final void tick(float dt) {
            if (canTick()) {
                    onTick(dt);
            }
    }
}
