/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.util;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.mapcraft.api.event.object.ObjectEvent;

/**
 * Event called when modifications occur on a StringMap
 */
public class SyncedMapEvent extends ObjectEvent<SyncedStringMap> {
	public static enum Action {
		ADD,
		SET,
		REMOVE,
	}

	private final Action action;
	private final List<Pair<Integer, String>> modifiedElements;

	public SyncedMapEvent(SyncedStringMap map, Action action, List<Pair<Integer, String>> modifiedElements) {
		super(map);
		this.action = action;
		this.modifiedElements = Collections.unmodifiableList(modifiedElements);
	}

	public Action getAction() {
		return action;
	}

	public List<Pair<Integer, String>> getModifiedElements() {
		return modifiedElements;
	}
}
