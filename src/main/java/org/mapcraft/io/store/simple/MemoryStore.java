/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.io.store.simple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;

/**
 * This implements a SimpleStore that is stored in memory. It is not persisted between restarts.
 */
public class MemoryStore<T> implements SimpleStore<T> {
	private final Map<String, T> map;
	private final Map<T, String> reverseMap;

	public MemoryStore() {
		map = new HashMap<>();
		reverseMap = new HashMap<>();
	}

	@Override
	public synchronized boolean save() {
		return true;
	}

	@Override
	public synchronized boolean load() {
		return true;
	}

	@Override
	public synchronized Collection<String> getKeys() {
		return map.keySet();
	}

	@Override
	public synchronized Set<Map.Entry<String, T>> getEntrySet() {
		return map.entrySet();
	}

	@Override
	public synchronized int getSize() {
		return map.size();
	}

	@Override
	public synchronized boolean clear() {
		map.clear();
		reverseMap.clear();
		return true;
	}

	@Override
	public synchronized T get(String key) {
		return map.get(key);
	}

	@Override
	public synchronized String reverseGet(T value) {
		return reverseMap.get(value);
	}

	@Override
	public synchronized T get(String key, T def) {
		T value = get(key);
		if (value == null) {
			return def;
		}
		return value;
	}

	@Override
	public synchronized T remove(String key) {
		T value = map.remove(key);
		if (value != null) {
			reverseMap.remove(value);
		}
		return value;
	}

	@Override
	public synchronized T set(String key, T value) {
		Validate.notNull(key);
		Validate.notNull(value);

		T oldValue = map.put(key, value);
		if (oldValue != null) {
			reverseMap.remove(oldValue);
		}
		reverseMap.put(value, key);
		return oldValue;
	}

	@Override
	public synchronized boolean setIfAbsent(String key, T value) {
		if (map.get(key) != null) {
			return false;
		}

		if (reverseMap.get(value) != null) {
			return false;
		}

		set(key, value);
		return true;
	}
}