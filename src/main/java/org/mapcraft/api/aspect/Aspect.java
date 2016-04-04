/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.aspect;

import java.util.Map;

/**
 *
 * @author rmalot
 */
public abstract class Aspect {
    private AspectOwner owner;
    
    public Aspect() {}
    

    /**
     * Attaches to an aspect owner.
     *
     * @param owner the aspect owner to attach to
     * @return true if successful
     */
    public boolean attachTo(AspectOwner owner) {
        this.owner = owner;
        return true;
    }

    /**
     * Gets the aspect owner that owns this aspect.
     *
     * @return the aspect owner
     */
    public AspectOwner getOwner() {
            if (owner == null) {
                    throw new IllegalStateException("Trying to access the owner of this aspect before it was attached");
            }
            return owner;
    }

	/**
	 * Called when this aspect is attached to a owner.
	 */
	public void onAttached() {
	}

	/**
	 * Called when this aspect is detached from a owner.
	 */
	public void onDetached() {
	}

	/**
	 * Specifies whether or not this aspect can be detached, after it has already been attached to an owner..
	 *
	 * @return true if it can be detached
	 */
	public boolean isDetachable() {
		return true;
	}

	/**
	 * Gets the {@link SerializableMap} which an AspectOwner always has <p> This is merely a convenience method.
	 *
	 * @return Aspect Map of the owner
	 */
	public final Map getAspectMap() {
		return getOwner().getAspectMap();
	}
}