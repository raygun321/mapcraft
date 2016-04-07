/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.tuple.Pair;
import org.mapcraft.api.event.object.Eventable;
import org.mapcraft.api.event.object.EventableListener;
import org.mapcraft.io.store.simple.SimpleStore;

/**
 *
 * @author rmalot
 */
public class SyncedStringMap extends StringToUniqueIntegerMap implements Eventable<SyncedMapEvent> {
	private final CopyOnWriteArrayList<EventableListener<SyncedMapEvent>> registeredListeners = new CopyOnWriteArrayList<>();
	private int id;

	protected SyncedStringMap(String name) {
		super(name);
	}

	protected SyncedStringMap(StringToUniqueIntegerMap parent, SimpleStore<Integer> store, int minId, int maxId, String name) {
		super(parent, store, minId, maxId, name);
	}

	public static SyncedStringMap create(String name) {
		SyncedStringMap map = new SyncedStringMap(name);
		map.id = SyncedMapRegistry.register(map);
		return map;
	}

	public static SyncedStringMap create(StringToUniqueIntegerMap parent, SimpleStore<Integer> store, int minId, int maxId, String name) {
		SyncedStringMap map = new SyncedStringMap(parent, store, minId, maxId, name);
		map.id = SyncedMapRegistry.register(map);
		return map;
	}

	@Override
	public int register(String key) {
		Integer id = store.get(key);
		if (id != null) {
			return id;
		}
		int local = super.register(key);
		callEvent(new SyncedMapEvent(this, SyncedMapEvent.Action.ADD, Arrays.asList(Pair.of(local, key))));
		return local;
	}

	@Override
	public boolean register(String key, int id) {
		Integer local = store.get(key);
		if (local != null) {
			return false;
		}
		callEvent(new SyncedMapEvent(this, SyncedMapEvent.Action.ADD, Arrays.asList(Pair.of(id, key))));
		return super.register(key, id);
	}

	public void handleUpdate(SyncedMapEvent message) {
		switch (message.getAction()) {
			case SET:
				super.clear();
			case ADD:
				for (Pair<Integer, String> pair : message.getModifiedElements()) {
					store.set(pair.getValue(), pair.getKey());
				}
				break;
			case REMOVE:
				for (Pair<Integer, String> pair : message.getModifiedElements()) {
					store.remove(pair.getValue());
				}
				break;
		}
		callEvent(new SyncedMapEvent(this, message.getAction(), message.getModifiedElements()));
	}

	@Override
	public void clear() {
		super.clear();
		callEvent(new SyncedMapEvent(this, SyncedMapEvent.Action.SET, new ArrayList<Pair<Integer, String>>()));
	}

	public int getId() {
		return id;
	}

	@Override
	public void registerListener(EventableListener<SyncedMapEvent> listener) {
		registeredListeners.add(listener);
	}

	@Override
	public void unregisterAllListeners() {
		registeredListeners.clear();
	}

	@Override
	public void unregisterListener(EventableListener<SyncedMapEvent> listener) {
		registeredListeners.remove(listener);
	}

	@Override
	public void callEvent(SyncedMapEvent event) {
		for (EventableListener<SyncedMapEvent> listener : registeredListeners) {
			listener.onEvent(event);
		}
	}
}