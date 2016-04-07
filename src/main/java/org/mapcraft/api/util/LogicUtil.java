/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class LogicUtil {
	/**
	 * Removes duplicate values by using the objects' equals function<br> Note that this function does not use a Set, because no hashcode is used.
	 *
	 * @param input collection
	 * @return the input collection
	 */
	public static <T extends Collection<?>> T removeDuplicates(T input) {
		List<Object> unique = new ArrayList<>();
		for (Iterator<?> iter = input.iterator(); iter.hasNext(); ) {
			Object next = iter.next();
			if (unique.contains(next)) {
				iter.remove();
			} else {
				unique.add(next);
			}
		}
		return input;
	}

	/**
	 * Checks if the object equals one of the other objects given
	 *
	 * @param object to check
	 * @param objects to use equals against
	 * @return True if one of the objects equal the object
	 */
	public static <A, B> boolean equalsAny(A object, B... objects) {
		for (B o : objects) {
			if (bothNullOrEqual(o, object)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if object a and b are both null or are equal
	 *
	 * @return if both null or equal
	 */
	public static boolean bothNullOrEqual(Object a, Object b) {
		return (a == null || b == null) ? (a == b) : a.equals(b);
	}

	/**
	 * Gets if a bit is set in an integer value
	 *
	 * @param value to get it of
	 * @param bit to check
	 * @return True if the bit is set
	 */
	public static boolean getBit(int value, int bit) {
		return (value & bit) == bit;
	}

	/**
	 * Sets a single bit in a byte value
	 *
	 * @param value to set a bit of
	 * @param bit to set
	 * @param state to enable or disable the bit
	 * @return the resulting value
	 */
	public static byte setBit(byte value, int bit, boolean state) {
		return (byte) setBit((int) value, bit, state);
	}

	/**
	 * Sets a single bit in a short value
	 *
	 * @param value to set a bit of
	 * @param bit to set
	 * @param state to enable or disable the bit
	 * @return the resulting value
	 */
	public static short setBit(short value, int bit, boolean state) {
		return (short) setBit((int) value, bit, state);
	}

	/**
	 * Sets a single bit in an integer value
	 *
	 * @param value to set a bit of
	 * @param bit to set
	 * @param state to enable or disable the bit
	 * @return the resulting value
	 */
	public static int setBit(int value, int bit, boolean state) {
		return state ? value | bit : value & ~bit;
	}
}

