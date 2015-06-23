package org.mapcraft.dictionary;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class ColorDictionary {
	public static final FloatBuffer red = (FloatBuffer) (BufferUtils.createFloatBuffer(4).put(new float[] {0.8f, 0.1f, 0.0f, 1.0f})).flip();
	public static final FloatBuffer green = (FloatBuffer) (BufferUtils.createFloatBuffer(4).put(new float[] {0.0f, 0.8f, 0.2f, 1.0f})).flip();
	public static final FloatBuffer blue = (FloatBuffer) (BufferUtils.createFloatBuffer(4).put(new float[] {0.2f, 0.2f, 1.0f, 1.0f})).flip();
}
