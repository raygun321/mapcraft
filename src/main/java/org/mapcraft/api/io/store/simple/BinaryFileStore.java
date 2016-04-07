/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.io.store.simple;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.mapcraft.io.store.simple.MemoryStore;

/**
 *
 * @author rmalot
 */
public class BinaryFileStore extends MemoryStore<Integer> {
	private File file;
	private boolean dirty = true;

	public BinaryFileStore(File file) {
		super();
		this.file = file;
	}

	public BinaryFileStore() {
		this(null);
	}

	public synchronized void setFile(File file) {
		this.file = file;
	}

	public synchronized File getFile() {
		return file;
	}

	@Override
	public synchronized boolean clear() {
		dirty = true;
		return super.clear();
	}

	@Override
	public synchronized boolean save() {
		if (!dirty) {
			return true;
		}

		boolean saved = true;
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			Iterator<Map.Entry<String, Integer>> itr = super.getEntrySet().iterator();

			while (itr.hasNext()) {
				Map.Entry<String, Integer> next = itr.next();
				out.writeInt(next.getValue());
				out.writeUTF(next.getKey());
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			saved = false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				saved = false;
			}
			if (saved) {
				dirty = false;
			}
		}
		return saved;
	}

	@Override
	public synchronized boolean load() {
		boolean loaded = true;
		DataInputStream in = null;
		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

			boolean eof = false;
			while (!eof) {
				try {
					Integer id = in.readInt();
					String key = in.readUTF();
					set(key, id);
				} catch (EOFException eofe) {
					eof = true;
				}
			}
		} catch (IOException ioe) {
			loaded = false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ioe) {
				loaded = false;
			}
		}
		if (loaded) {
			dirty = false;
		}
		return loaded;
	}

	@Override
	public synchronized Integer remove(String key) {
		Integer value = super.remove(key);
		if (value != null) {
			dirty = true;
		}
		return value;
	}

	@Override
	public synchronized Integer set(String key, Integer value) {
		dirty = true;
		return super.set(key, value);
	}
}