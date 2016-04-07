/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.gui.render;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.shape.Rectangle;

/**
 * Represents anything that can be rendered on the client.
 */
public class RenderPart {
        static final Rectangle RECTANGLE_ZERO = new Rectangle(0,0,0,0);
	private Rectangle source = RECTANGLE_ZERO;
	private Rectangle sprite = RECTANGLE_ZERO;
	private int zIndex = 0;
	private Color color = Color.WHITE;
        final private List<Point3D> points = new ArrayList<>();
        final private List<Point2D> textureCoordinates = new ArrayList<>();
        final private List<ImmutableFace> faces = new ArrayList<>();
        
        private boolean dirty = false;
        
	/**
	 * Sets the bounds of the source of the render part. This is commonly used for sprite sheets and should be left at zero for simple colored rectangles.
	 *
	 * @param source of part
	 */
	public void setSource(Rectangle source) {
		this.source = source;
                dirty = true;
	}

	/**
	 * Returns the bounds of the source of the render part. This is commonly used for sprite sheets and should be left at zero for simple colored rectangles.
	 *
	 * @return source of part
	 */
	public Rectangle getSource() {
		return source;
	}

	/**
	 * Sets the bounds of the actual sprite of the render material. This is used for specifying the actual visible size of the render part.
	 *
	 * @param sprite of render part
	 */
	public void setSprite(Rectangle sprite) {
		this.sprite = sprite;
                dirty = true;
        }

	/**
	 * Returns the bounds of the actual sprite of the render material. This is used for specifying the actual visible size of the render part.
	 *
	 * @return sprite of render part
	 */
	public Rectangle getSprite() {
		return sprite;
	}

	/**
	 * Returns the layer that this should be rendered on. Something with a higher z-index will be rendered on top of something with a lower z-index and something with a lower z-index will be rendered
	 * under the higher z-index part.
	 *
	 * @param zIndex of part
	 */
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	/**
	 * Returns the layer that this should be rendered on. Something with a higher z-index will be rendered on top of something with a lower z-index and something with a lower z-index will be rendered
	 * under the higher z-index part.
	 *
	 * @return z-index of part
	 */
	public int getZIndex() {
		return zIndex;
	}

	/**
	 * Returns the color of this part.
	 *
	 * @return color of part
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color of this part.
	 *
	 * @param color of part
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Returns the points used for rendering the part.
	 *
	 * @return points of part
	 */
	public List<Point3D> getPoints() {
		if(dirty) initPart();

		return points;
	}

	/**
	 * Returns the texture coordinates used for rendering the part.
	 *
	 * @return points of part
	 */
	public List<Point2D> getTextureCoordinates() {
		if(dirty) initPart();

		return textureCoordinates;
	}
        
        /**
	 * Returns the points and texture coordinates used for defining the faces.
	 *
	 * @return points of part
	 */
	public List<ImmutableFace> getFaces() {
		if(dirty) initPart();
                
                return faces;
	}

//	@Override
//	public int compareTo(RenderPart arg0) {
//		return arg0.getZIndex() - getZIndex();
//	}
        
        private void initPart() {
            points.clear();
            points.add( new Point3D(sprite.getX(), sprite.getY(), 0) ); // 0 - TL
            points.add( new Point3D(sprite.getX() + sprite.getWidth(), sprite.getY(), 0) ); // 1 - TR
            points.add( new Point3D(sprite.getX() + sprite.getWidth(), sprite.getY() - sprite.getHeight(), 0) );    // 2 - BR
            points.add( new Point3D(sprite.getX(), sprite.getY() - sprite.getHeight(), 0) );    // 3 - BL
            
            textureCoordinates.clear();
            textureCoordinates.add( new Point2D(source.getX(), source.getY()) );
            textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY()) );
            textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY() + source.getHeight()) );
            textureCoordinates.add( new Point2D(source.getX(), source.getY() + source.getHeight()) );
            
            faces.clear();
            faces.add(new ImmutableFace(0,0, 1,1, 2,2));
            faces.add(new ImmutableFace(2,2, 3,3, 0,0));
            
            dirty = false;
        }
}
