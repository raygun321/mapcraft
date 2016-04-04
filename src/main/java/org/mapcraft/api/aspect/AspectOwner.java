/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.aspect;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author rmalot
 */
public interface AspectOwner {
	/**
	 * Adds the aspect of the specified type to the owner and returns it if it is not present. <p> Otherwise, it returns the aspect of the specified type if there was one present. </p>
	 *
         * @param <T> 
         * @param type whose aspect is to be added to the owner
	 * @return the new aspect that was added, or the existing one if it had one
	 */
	public <T extends Aspect> T addAspect(Class<T> type);

	/**
	 * Returns the aspect of the specified type (or a child implementation) from the owner if it is present.
	 *
         * @param <T>
	 * @param type whose aspect is to be returned from the holder
	 * @return the aspect, or null if one was not found
	 */
	public <T extends Aspect> T getAspectLikeType(Class<T> type);

	/**
	 * Returns all aspects of the specified type (or a child implementation).
	 *
         * @param <T>
	 * @param type whose aspects are to be returned from the owner
	 * @return the aspect list.
	 */
	public <T extends Aspect> Collection<T> getAllAspectsLikeType(Class<T> type);

	/**
	 * Returns all instances of the specified type from the owner if they are present.
	 *
         * @param <T>
	 * @param type whose aspects are to be returned from the owner
	 * @return the aspect list.
	 */
	public <T> Collection<T> getAllOfType(Class<T> type);

	/**
	 * Returns the aspect of the specified type (not a child implementation) from the owner if it is present.
	 *
         * @param <T>
	 * @param type whose aspect is to be returned from the owner
	 * @return the aspect, or null if one was not found.
	 */
	public <T extends Aspect> T getAspectOfType(Class<T> type);

	/**
	 * Returns an instance of the specified type from the owner if it is present.
	 *
         * @param <T>
	 * @param type whose aspect is to be returned from the owner
	 * @return the aspect, or null if one was not found
	 */
	public <T> T getByType(Class<T> type);

	/**
	 * Removes the aspect of the specified type from the owner if it is present.
	 *
         * @param <T>
	 * @param type whose aspect is to be removed from the owner
	 * @return the removed aspect, or null if there was not one
	 */
	public <T extends Aspect> T detachAspect(Class<? extends Aspect> type);

	/**
	 * Gets all aspects held by the owner.
	 *
	 * @return A collection of held aspects
	 */
	public Collection<Aspect> getAspects();

	/**
	 * Gets the {@link Aspect} map of the owner.
	 *
	 * @return map of aspect
	 */
	public Map getAspectMap();
}
