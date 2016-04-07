/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.event.object;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Basic implementation of {@link Eventable}
 */
public class EventableBase<T extends ObjectEvent<?>> implements Eventable<T> {
	private final CopyOnWriteArrayList<EventableListener<T>> registeredListeners = new CopyOnWriteArrayList<>();

	@Override
	public void registerListener(EventableListener<T> listener) {
		registeredListeners.add(listener);
	}

	@Override
	public void unregisterAllListeners() {
		registeredListeners.clear();
	}

	@Override
	public void unregisterListener(EventableListener<T> listener) {
		registeredListeners.remove(listener);
	}

	@Override
	public void callEvent(T event) {
		for (EventableListener<T> listener : registeredListeners) {
			listener.onEvent(event);
		}
	}
}
