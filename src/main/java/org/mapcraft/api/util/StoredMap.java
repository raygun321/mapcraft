/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.util;

import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author rmalot
 */
public interface StoredMap<T> {
	public void clear();

	public List<Pair<T, String>> getItems();

	/**
	 * Returns a collection of all keys for all (key, value) pairs within the Store
	 *
	 * @return returns a Collection containing all the keys
	 */
	public Collection<String> getKeys();

	public String getName();

	/**
	 * Gets the String corresponding to a given value.
	 *
	 * @return the String or null if no match
	 */
	public String getString(T value);

	/**
	 * Gets the value corresponding to a given String
	 *
	 * @param key The key
	 * @return The value or null if no match
	 */
	public T getValue(String key);

	/**
	 * Registers a key/id pair with the map.  If the id is already in use the method will fail.<br> <br> The id must be lower than the min id for the map to prevent clashing with the dynamically
	 * allocated id
	 *
	 * @param key the key to be added
	 * @param value the desired value to be matched to the key
	 * @return true if the key/id pair was successfully registered
	 * @throws IllegalArgumentException if the id >= minId
	 */
	public boolean register(String key, T value);

	/**
	 * Saves the map to the persistence system
	 *
	 * @return returns true if the map saves correctly
	 */
	public boolean save();
}

