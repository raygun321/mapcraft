/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.render;

import javafx.geometry.Point3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.MatrixType;
import org.mapcraft.api.geo.Cuboid;

/**
 *
 * @author rmalot
 */
public class ViewFrustum {
	// This is the renderer subsize in bloc scale ( because it is in aggregator scale when it's pass through intersects )
//	Point3D rendererSize = new Point3D(2, 2, 2);
	double rendererSize = 2.0;
	Point3D position = null;
	double[][] frustum = new double[6][4];

	public void update(Affine projection, Affine view, Point3D paramPosition) {

		position = paramPosition;

		// http://www.crownandcutlass.com/features/technicaldetails/frustum.html
                Affine temp = projection.clone();
                temp.append(view.toArray(MatrixType.MT_3D_3x4), MatrixType.MT_3D_3x4, 0);
		double[] clip = temp.toArray(MatrixType.MT_3D_4x4);

		/* Extract the numbers for the RIGHT plane */
		frustum[0][0] = clip[3] - clip[0];
		frustum[0][1] = clip[7] - clip[4];
		frustum[0][2] = clip[11] - clip[8];
		frustum[0][3] = clip[15] - clip[12];

		/* Extract the numbers for the LEFT plane */
		frustum[1][0] = clip[3] + clip[0];
		frustum[1][1] = clip[7] + clip[4];
		frustum[1][2] = clip[11] + clip[8];
		frustum[1][3] = clip[15] + clip[12];

		/* Extract the BOTTOM plane */
		frustum[2][0] = clip[3] + clip[1];
		frustum[2][1] = clip[7] + clip[5];
		frustum[2][2] = clip[11] + clip[9];
		frustum[2][3] = clip[15] + clip[13];

		/* Extract the TOP plane */
		frustum[3][0] = clip[3] - clip[1];
		frustum[3][1] = clip[7] - clip[5];
		frustum[3][2] = clip[11] - clip[9];
		frustum[3][3] = clip[15] - clip[13];

		/* Extract the FAR plane */
		frustum[4][0] = clip[3] - clip[2];
		frustum[4][1] = clip[7] - clip[6];
		frustum[4][2] = clip[11] - clip[10];
		frustum[4][3] = clip[15] - clip[14];

		/* Extract the NEAR plane */
		frustum[5][0] = clip[3] + clip[2];
		frustum[5][1] = clip[7] + clip[6];
		frustum[5][2] = clip[11] + clip[10];
		frustum[5][3] = clip[15] + clip[14];
		
		
		
		/* Normalize the result */
		/* You can eliminate all the code that has to
		 * do with normalizing the plane values. This will result in scaled
		 * distances when you compare a point to a plane. The point and box
		 * tests will still work, but the sphere test won't. If you aren't
		 * using bounding spheres at all this will save a few expensive
		 * calculations per frame, but those probably won't be an issue on
		 * most systems.*/

		/*for (int i=0 ; i<6 ; i++) {
			double t = sqrt(frustum[i][0] * frustum[i][0] + frustum[i][1] * frustum[i][1] + frustum[i][2] * frustum[i][2]);
			frustum[i][0] /= t;
			frustum[i][1] /= t;
			frustum[i][2] /= t;
			frustum[i][3] /= t;
		}*/

	}

	/**
	 * Compute the distance between a point and the given plane
	 *
	 * @param p The id of the plane
	 * @param v The vector
	 * @return The distance
	 */
	private double distance(int p, Point3D v) {
		return frustum[p][0] * v.getX() + frustum[p][1] * v.getY() + frustum[p][2] * v.getZ() + frustum[p][3];
	}

	/**
	 * Checks if the frustum of this camera intersects the given Cuboid.
	 *
	 * @param c The cuboid to check the frustum against.
	 * @return True if the frustum intersects the cuboid.
	 */
	public boolean intersects(Cuboid c) {

		Point3D[] vertices = c.getVertices();

		for (int i = 0; i < 6; i++) {

			if (distance(i, vertices[0].multiply(rendererSize).subtract(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[1].multiply(rendererSize).subtract(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[2].multiply(rendererSize).subtract(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[3].multiply(rendererSize).subtract(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[4].multiply(rendererSize).subtract(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[5].multiply(rendererSize).subtract(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[6].multiply(rendererSize).subtract(position)) > 0) {
				continue;
			}

			if (distance(i, vertices[7].multiply(rendererSize).subtract(position)) > 0) {
				continue;
			}

			return false;
		}

		return true;
	}

	/**
	 * Checks if the frustum contains the given Vector3.
	 *
	 * @param vec The Vector3 to check the frustum against.
	 * @return True if the frustum contains the Vector3.
	 */
	public boolean contains(Point3D vec) {
		for (int p = 0; p < 6; p++) {
			if (distance(p, vec) <= 0) {
				return false;
			}
		}
		return true;
	}
}
