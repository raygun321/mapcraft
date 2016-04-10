/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.geo;

import javafx.geometry.Point3D;
import mapcraft.map.SimpleWorld;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mapcraft.api.geo.discrete.WorldPoint3D;

/**
 *
 * @author rmalot
 */
public class Cuboid implements WorldSource {
    	protected final WorldPoint3D base;
	protected final Point3D size;
	private final int x;
	private final int y;
	private final int z;
	/**
	 * Hash code caching
	 */
	private volatile boolean hashed = false;
	private volatile int hashcode = 0;
	/**
	 * Vertex cache
	 */
	private Point3D[] vertices = null;

	/**
	 * Constructs a cuboid with the point as the base point, and
	 */
	public Cuboid(WorldPoint3D base, Point3D size) {
		this.base = base;
		this.size = size;
		this.x = (int) (base.getX() / size.getX());
		this.y = (int) (base.getY() / size.getY());
		this.z = (int) (base.getZ() / size.getZ());
	}

	public Point3D getBase() {
		return base;
	}

	public Point3D getSize() {
		return size;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	@Override
	public SimpleWorld getWorld() {
		return base.getWorld();
	}

	/**
	 * Returns the vertices of this Cuboid.
	 *
	 * @return The vertices
	 */
	public Point3D[] getVertices() {
		if (vertices == null) {
			vertices = new Point3D[8];

			// Front
			vertices[0] = new Point3D(base.getX(), base.getY(), base.getZ() + size.getZ());
			vertices[1] = new Point3D(base.getX() + size.getX(), base.getY(), base.getZ() + size.getZ());
			vertices[2] = new Point3D(base.getX() + size.getX(), base.getY() + size.getY(), base.getZ() + size.getZ());
			vertices[3] = new Point3D(base.getX(), base.getY() + size.getY(), base.getZ() + size.getZ());
			// Back
			vertices[4] = new Point3D(base.getX(), base.getY(), base.getZ());
			vertices[5] = new Point3D(base.getX() + size.getX(), base.getY(), base.getZ());
			vertices[6] = new Point3D(base.getX() + size.getX(), base.getY() + size.getY(), base.getZ());
			vertices[7] = new Point3D(base.getX(), base.getY() + size.getY(), base.getZ());
		}

		return vertices;
	}

	public boolean contains(Point3D vec) {
		Point3D max = base.add(size);
		return (base.getX() <= vec.getX() && vec.getX() < max.getX()) && (base.getY() <= vec.getY() && vec.getY() < max.getY()) && (base.getZ() <= vec.getZ() && vec.getZ() < max.getZ());
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			hashcode = new HashCodeBuilder(563, 21).append(base).append(size).toHashCode();
			hashed = true;
		}
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		} else if (!(obj instanceof Cuboid)) {
			return false;
		} else {
			Cuboid cuboid = (Cuboid) obj;

			return cuboid.size.getX() == size.getX() && cuboid.size.getY() == size.getY() && cuboid.size.getZ() == size.getZ() && cuboid.getWorld().equals(getWorld()) && cuboid.getX() == getX() && cuboid.getY() == getY() && cuboid.getZ() == getZ();
		}
	}

	@Override
	public String toString() {
		return "Cuboid[" + size.getX() + ", " + size.getY() + ", " + size.getZ() + "]@[" + getX() + ", " + getY() + ", " + getZ() + "]";
	}
}
