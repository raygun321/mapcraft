/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.mapcraft.io.store.simple.SimpleStore;

public class SimpleStoredMap<T> implements StoredMap<T> {
	protected final SimpleStore<T> store;
	protected final String name;

	/**
	 * @param store the store to store id
	 * @param name The name of this StringMap
	 */
	public SimpleStoredMap(SimpleStore<T> store, String name) {
		this.store = store;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Registers a key/id pair with the map.  If the id is already in use the method will fail.<br> <br> The id must be lower than the min id for the map to prevent clashing with the dynamically
	 * allocated id
	 *
	 * @param key the key to be added
	 * @param value the desired value to be matched to the key
	 * @return true if the key/id pair was successfully registered
	 * @throws IllegalArgumentException if the id >= minId
	 */
	@Override
	public boolean register(String key, T value) {
		return store.setIfAbsent(key, value);
	}

	/**
	 * Gets the String corresponding to a given value.
	 *
	 * @return the String or null if no match
	 */
	@Override
	public String getString(T value) {
		return store.reverseGet(value);
	}

	/**
	 * Gets the value corresponding to a given String
	 *
	 * @param key The key
	 * @return The value or null if no match
	 */
	@Override
	public T getValue(String key) {
		return store.get(key);
	}

	/**
	 * Saves the map to the persistence system
	 *
	 * @return returns true if the map saves correctly
	 */
	@Override
	public boolean save() {
		return store.save();
	}

	/**
	 * Returns a collection of all keys for all (key, value) pairs within the Store
	 *
	 * @return returns a Collection containing all the keys
	 */
	@Override
	public Collection<String> getKeys() {
		return store.getKeys();
	}

	@Override
	public List<Pair<T, String>> getItems() {
		List<Pair<T, String>> items = new ArrayList<>();
		for (Map.Entry<String, T> entry : store.getEntrySet()) {
			items.add(new ImmutablePair<>(entry.getValue(), entry.getKey()));
		}
		return items;
	}

	@Override
	public void clear() {
		store.clear();
	}
}