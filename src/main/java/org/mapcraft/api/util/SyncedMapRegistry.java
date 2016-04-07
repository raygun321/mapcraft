/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.mapcraft.api.event.object.EventableBase;

/**
 * Represents a map for mapping Strings to unique ids.
 *
 * The class supports conversion of ids between maps and allocation of new unique ids for unknown Strings
 *
 * Conversions to and from parent/child maps are cached
 */
public final class SyncedMapRegistry extends EventableBase<SyncedMapEvent> {
	public static final byte REGISTRATION_MAP = -1;
	protected static final SyncedStringMap STRING_MAP_REGISTRATION = new SyncedStringMap("REGISTRATION_MAP"); // This is a special case
	protected static final ConcurrentMap<String, WeakReference<SyncedStringMap>> REGISTERED_MAPS = new ConcurrentHashMap<>();

	public static SyncedStringMap get(String name) {
		WeakReference<SyncedStringMap> ref = REGISTERED_MAPS.get(name);
		if (ref != null) {
			SyncedStringMap map = ref.get();
			if (map == null) {
				REGISTERED_MAPS.remove(name);
			}
			return map;
		}
		return null;
	}

	public static SyncedStringMap get(int id) {
		if (id == REGISTRATION_MAP) {
			return STRING_MAP_REGISTRATION;
		}
		String name = STRING_MAP_REGISTRATION.getString(id);
		if (name != null) {
			WeakReference<SyncedStringMap> ref = REGISTERED_MAPS.get(name);
			if (ref != null) {
				SyncedStringMap map = ref.get();
				if (map == null) {
					REGISTERED_MAPS.remove(name);
				}
				return map;
			}
		}
		return null;
	}

	public static Collection<SyncedStringMap> getAll() {
		Collection<WeakReference<SyncedStringMap>> rawMaps = REGISTERED_MAPS.values();
		List<SyncedStringMap> maps = new ArrayList<>(rawMaps.size());
		for (WeakReference<SyncedStringMap> ref : rawMaps) {
			SyncedStringMap map = ref.get();
			if (map != null) {
				maps.add(map);
			}
		}
		return maps;
	}

	public static SyncedStringMap getRegistrationMap() {
		return STRING_MAP_REGISTRATION;
	}

	public static int register(SyncedStringMap map) {
		int id = STRING_MAP_REGISTRATION.register(map.getName());
		REGISTERED_MAPS.put(map.getName(), new WeakReference<>(map));
		return id;
	}
}