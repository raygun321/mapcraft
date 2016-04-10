/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.geo.discrete;

import java.io.IOException;
import java.lang.reflect.Field;
import javafx.geometry.Point3D;
import javafx.scene.effect.Light.Point;
import javafx.scene.layout.Region;
import mapcraft.block.Block;
import mapcraft.block.Chunk;
import mapcraft.map.SimpleWorld;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mapcraft.api.MapCraft;
import org.mapcraft.api.geo.LoadOption;
import org.mapcraft.api.geo.WorldSource;

/**
 *
 * @author rmalot
 */
public class WorldPoint3D extends Point3D implements WorldSource {
	protected final SimpleWorld world;
	public static final WorldPoint3D INVALID = new WorldPoint3D(null, 0, 0, 0);
	/**
	 * Hashcode caching
	 */
	private transient volatile boolean hashed = false;
	private transient volatile int hashcode = 0;

	public WorldPoint3D(WorldPoint3D point) {
		super(point.getX(), point.getY(), point.getZ());
		world = point.getWorld();
	}

	public WorldPoint3D(Point3D vector, SimpleWorld w) {
		super(vector.getX(), vector.getY(), vector.getZ());
		world = w;
	}

	public WorldPoint3D(SimpleWorld world, float x, float y, float z) {
		super(x, y, z);
		this.world = world;
	}

//	@Override
//	public WorldPoint3D divide(float val) {
//		return new WorldPoint3D(super.div(val), world);
//	}
//
//	@Override
//	public WorldPoint3D div(double val) {
//		return new WorldPoint3D(super.div(val), world);
//	}
//
//	@Override
//	public WorldPoint3D div(Point3D other) {
//		return new WorldPoint3D(super.div(other), world);
//	}
//
//	@Override
//	public WorldPoint3D div(double x, double y, double z) {
//		return new WorldPoint3D(super.div(x, y, z), world);
//	}
//
//	@Override
//	public WorldPoint3D div(float x, float y, float z) {
//		return new WorldPoint3D(super.div(x, y, z), world);
//	}

//	@Override
	public WorldPoint3D multiply(float val) {
		return new WorldPoint3D(super.multiply(val), world);
                
	}

	@Override
	public WorldPoint3D multiply(double val) {
		return new WorldPoint3D(super.multiply(val), world);
	}

//	@Override
//	public WorldPoint3D multiply(Point3D other) {
//		return new WorldPoint3D(super.multiply(other), world);
//	}
//
//	@Override
//	public WorldPoint3D multiply(double x, double y, double z) {
//		return new WorldPoint3D(super.multiply(x, y, z), world);
//	}
//
//	@Override
//	public WorldPoint3D multiply(float x, float y, float z) {
//		return new WorldPoint3D(super.multiply(x, y, z), world);
//	}

	public WorldPoint3D add(WorldPoint3D other) {
		if (world != other.world) {
			throw new IllegalArgumentException("Cannot add two points in seperate worlds");
		}
		return new WorldPoint3D(super.add(other), world);
	}

	@Override
	public WorldPoint3D add(Point3D other) {
		return new WorldPoint3D(super.add(other), world);
	}

        //@Override
	public WorldPoint3D add(float x, float y, float z) {
		return new WorldPoint3D(super.add(x, y, z), world);
	}

	@Override
	public WorldPoint3D add(double x, double y, double z) {
		return new WorldPoint3D(super.add(x, y, z), world);
	}

	@Override
	public WorldPoint3D subtract(Point3D other) {
		return new WorldPoint3D(super.subtract(other), world);
	}

	//@Override
	public WorldPoint3D subtract(float x, float y, float z) {
		return new WorldPoint3D(super.subtract(x, y, z), world);
	}

	@Override
	public WorldPoint3D subtract(double x, double y, double z) {
		return new WorldPoint3D(super.subtract(x, y, z), world);
	}

        public int getFloorX() {
		return (int) Math.floor(super.getX());
	}

	public int getFloorY() {
		return (int) Math.floor(super.getY());
	}

	public int getFloorZ() {
		return (int) Math.floor(super.getZ());
	}
        
	public int getBlockX() {
		return this.getFloorX();
	}

	public int getBlockY() {
		return this.getFloorY();
	}

	public int getBlockZ() {
		return this.getFloorZ();
	}

	public int getChunkX() {
		return this.getFloorX() >> Chunk.BLOCKS.BITS;
	}

	public int getChunkY() {
		return this.getFloorY() >> Chunk.BLOCKS.BITS;
	}

	public int getChunkZ() {
		return this.getFloorZ() >> Chunk.BLOCKS.BITS;
	}

	public Chunk getChunk(LoadOption loadopt) {
		return world.getChunk(getChunkX(), getChunkY(), getChunkZ(), loadopt);
	}

	public Region getRegion(LoadOption loadopt) {
		return world.getRegionFromChunk(getChunkX(), getChunkY(), getChunkZ(), loadopt);
	}

	/**
	 * Gets the square of the distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 */
	public double getSquaredDistance(WorldPoint3D other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		double dx = getX() - other.getX();
		double dy = getY() - other.getY();
		double dz = getZ() - other.getZ();
		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Gets the distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 */
	public double getDistance(WorldPoint3D other) {
		return Math.sqrt(getSquaredDistance(other));
	}

	/**
	 * Gets the Manhattan distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 */
	public double getManhattanDistance(WorldPoint3D other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		return Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY()) + Math.abs(getZ() - other.getZ());
	}

	/**
	 * Gets the largest distance between two points, when projected onto one of the axes.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the max distance.
	 */
	public double getMaxDistance(WorldPoint3D other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		return Math.max(Math.abs(getX() - other.getX()),
						Math.max(Math.abs(getY() - other.getY()),
						Math.abs(getZ() - other.getZ())));
	}

	/**
	 * Gets the world this point is locate in
	 *
	 * @return the world
	 */
	@Override
	public SimpleWorld getWorld() {
		return world;
	}

	/**
	 * Gets the block this point is locate in
	 *
	 * @return the world
	 */
	public Block getBlock() {
		return world.getBlock(getX(), getY(), getZ());
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			hashcode = new HashCodeBuilder(5033, 61).appendSuper(super.hashCode()).append(world).toHashCode();
			hashed = true;
		}
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WorldPoint3D)) {
			return false;
		} else {
			WorldPoint3D point = (WorldPoint3D) obj;
			boolean worldEqual = point.world == world || (point.world != null && point.world.equals(world));
			return worldEqual && point.getX() == getX() && point.getY() == getY() && point.getZ() == getZ();
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":{" + world.getName() +":" + getX() +","+ getY() +","+ getZ() + "}";
	}

	public String toBlockString() {
		return "{" + world.getName() + ":" + getBlockX() + ", " + getBlockY() + ", " + getBlockZ() + "}";
	}

	public String toChunkString() {
		return "{" + world.getName() + ":" + getChunkX() + ", " + getChunkY() + ", " + getChunkZ() + "}";
	}

	//Custom serialization logic because world can not be made serializable
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeDouble(this.getX());
		out.writeDouble(this.getY());
		out.writeDouble(this.getZ());
		out.writeUTF(world != null ? world.getName() : "null");
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException {
		double x = in.readDouble();
		double y = in.readDouble();
		double z = in.readDouble();
		String world = in.readUTF();
		SimpleWorld w = MapCraft.getEngine().getWorld(world, true);
		try {
			Field field;

			field = Point3D.class.getDeclaredField("x");
			field.setAccessible(true);
			field.set(this, x);

			field = Point3D.class.getDeclaredField("y");
			field.setAccessible(true);
			field.set(this, y);

			field = Point3D.class.getDeclaredField("z");
			field.setAccessible(true);
			field.set(this, z);

			field = Point.class.getDeclaredField("world");
			field.setAccessible(true);
			field.set(this, w);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			if (MapCraft.debugMode()) {
				e.printStackTrace();
			}
		}
	}
}
