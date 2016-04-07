/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.event.object;

import org.mapcraft.api.util.thread.annotation.Threadsafe;

/**
 * Base interface for an object that can accept listeners for a specified event
 *
 * @param <T> The type of event allowed
 */
public interface Eventable<T extends ObjectEvent<?>> {
	/**
	 * Add a listener for the type of event this {@link Eventable} covers.
	 *
	 * @param listener The listener to register.
	 */
	@Threadsafe
	public void registerListener(EventableListener<T> listener);

	/**
	 * Remove all listeners for the event
	 */
	@Threadsafe
	public void unregisterAllListeners();

	/**
	 * Unregister a specific listener
	 *
	 * @param listener The listener to unregister
	 */
	@Threadsafe
	public void unregisterListener(EventableListener<T> listener);

	/**
	 * Call the event with all current listeners.
	 *
	 * @param event The event instance to call
	 */
	@Threadsafe
	public void callEvent(T event);
}
