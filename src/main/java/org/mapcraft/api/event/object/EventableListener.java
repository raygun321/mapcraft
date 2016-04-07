/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.event.object;

/**
 * Listener for eventable object listeners
 */
public interface EventableListener<T> {
	public void onEvent(T event);
}