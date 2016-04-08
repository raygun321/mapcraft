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
import org.mapcraft.api.block.BlockFace;

/**
 * Represents anything that can be rendered on the client.
 */
public class RenderPart {
        static final Rectangle RECTANGLE_ZERO = new Rectangle(0,0,0,0);
	private Rectangle source = RECTANGLE_ZERO;
	private Rectangle sprite = RECTANGLE_ZERO;
        private BlockFace facing = BlockFace.TOP;
	private int zIndex = 0;
	private Color color = Color.WHITE;
        final private List<Point3D> points = new ArrayList<>();
        final private List<Point2D> textureCoordinates = new ArrayList<>();
        final private List<ImmutableFace> faces = new ArrayList<>();
        
        private boolean dirty = false;
        
        public void setFacing(BlockFace face) {
            facing = face;
        }
        
        public BlockFace getFacing() {
            return facing;
        }
        
	/**
	 * Sets the bounds of the source texture of the render part. This is commonly used for sprite sheets and should be left at zero for simple colored rectangles.
	 *
	 * @param source of part
	 */
	public void setSource(Rectangle source) {
		this.source = source;
                dirty = true;
	}

	/**
	 * Returns the bounds of the source texture of the render part. This is commonly used for sprite sheets and should be left at zero for simple colored rectangles.
	 *
	 * @return source of part
	 */
	public Rectangle getSource() {
		return source;
	}

	/**
	 * Sets the bounds of the actual sprite size of the render material. This is used for specifying the actual visible size of the render part.
	 *
	 * @param sprite of render part
	 */
	public void setSprite(Rectangle sprite) {
		this.sprite = sprite;
                dirty = true;
        }

	/**
	 * Returns the bounds of the actual sprite size of the render material. This is used for specifying the actual visible size of the render part.
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
                
                //TOP - ?, BOTTOM - ?, NORTH, SOUTH, East, West
//                if(facing == BlockFace.SOUTH || facing == BlockFace.EAST || facing == BlockFace.WEST || facing == BlockFace.TOP || facing == BlockFace.BOTTOM )
                    return faces;
  //              else
    //                return new ArrayList<ImmutableFace>();
	}

//	@Override
//	public int compareTo(RenderPart arg0) {
//		return arg0.getZIndex() - getZIndex();
//	}
        
        private void initPart() {
            points.clear();
            textureCoordinates.clear();
        
            switch(facing) {
                case TOP:
                    points.add( new Point3D(sprite.getX()-0.5f,                     0.5f, sprite.getY() - 0.5f) ); // 0 - TL
                    points.add( new Point3D(sprite.getX()-0.5f + sprite.getWidth(), 0.5f, sprite.getY() - 0.5f) ); // 1 - TR
                    points.add( new Point3D(sprite.getX()-0.5f + sprite.getWidth(), 0.5f, sprite.getY() - 0.5f - sprite.getHeight()) );    // 2 - BR
                    points.add( new Point3D(sprite.getX()-0.5f,                     0.5f, sprite.getY() - 0.5f - sprite.getHeight()) );    // 3 - BL
                    textureCoordinates.add( new Point2D(source.getX(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY() + source.getHeight()) );
                    textureCoordinates.add( new Point2D(source.getX(), source.getY() + source.getHeight()) );
                    break;
                case BOTTOM:
                    points.add( new Point3D(sprite.getX()-0.5f,                     -0.5f, sprite.getY() - 0.5f - sprite.getHeight()) );    // 3 - BL
                    points.add( new Point3D(sprite.getX()-0.5f + sprite.getWidth(), -0.5f, sprite.getY() - 0.5f - sprite.getHeight()) );    // 2 - BR
                    points.add( new Point3D(sprite.getX()-0.5f + sprite.getWidth(), -0.5f, sprite.getY() - 0.5f) ); // 1 - TR
                    points.add( new Point3D(sprite.getX()-0.5f,                     -0.5f, sprite.getY() - 0.5f) ); // 0 - TL
                    textureCoordinates.add( new Point2D(source.getX(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY() + source.getHeight()) );
                    textureCoordinates.add( new Point2D(source.getX(), source.getY() + source.getHeight()) );
                    break;
                case NORTH:
                    points.add(new Point3D(-0.5f, sprite.getX()-0.5f,                     sprite.getY() - 0.5f) ); // 0 - TL
                    points.add(new Point3D(-0.5f, sprite.getX()-0.5f + sprite.getWidth(), sprite.getY() - 0.5f) ); // 1 - TR
                    points.add(new Point3D(-0.5f, sprite.getX()-0.5f + sprite.getWidth(), sprite.getY() - 0.5f - sprite.getHeight()) );    // 2 - BR
                    points.add(new Point3D(-0.5f, sprite.getX()-0.5f,                     sprite.getY() - 0.5f - sprite.getHeight()) );    // 3 - BL
                    textureCoordinates.add( new Point2D(source.getX(), source.getY() + source.getHeight()) );
                    textureCoordinates.add( new Point2D(source.getX(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY() + source.getHeight()) );
                    break; 
                case SOUTH:
                    points.add(new Point3D(0.5f, sprite.getX()-0.5f,                     sprite.getY() - 0.5f - sprite.getHeight()) );    // 3 - BL
                    points.add(new Point3D(0.5f, sprite.getX()-0.5f + sprite.getWidth(), sprite.getY() - 0.5f - sprite.getHeight()) );    // 2 - BR
                    points.add(new Point3D(0.5f, sprite.getX()-0.5f + sprite.getWidth(), sprite.getY() - 0.5f) ); // 1 - TR
                    points.add(new Point3D(0.5f, sprite.getX()-0.5f,                     sprite.getY() - 0.5f) ); // 0 - TL
                    textureCoordinates.add( new Point2D(source.getX(), source.getY() + source.getHeight()) );
                    textureCoordinates.add( new Point2D(source.getX(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY() + source.getHeight()) );
                    break; 
                case EAST:
                    points.add(new Point3D(sprite.getX() - 0.5f,                     sprite.getY() + 0.5f,                      -1.5f ) ); // 0 - TL
                    points.add(new Point3D(sprite.getX() - 0.5f + sprite.getWidth(), sprite.getY() + 0.5f,                      -1.5f ) ); // 1 - TR
                    points.add(new Point3D(sprite.getX() - 0.5f + sprite.getWidth(), sprite.getY() + 0.5f - sprite.getHeight(), -1.5f ) );    // 2 - BR
                    points.add(new Point3D(sprite.getX() - 0.5f,                     sprite.getY() + 0.5f - sprite.getHeight(), -1.5f ) );    // 3 - BL
                    textureCoordinates.add( new Point2D(source.getX(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY() + source.getHeight()) );
                    textureCoordinates.add( new Point2D(source.getX(), source.getY() + source.getHeight()) );
                    break; 
                case WEST:
                    points.add(new Point3D(sprite.getX() - 0.5f,                     sprite.getY() + 0.5f - sprite.getHeight(), -0.5f ) );    // 3 - BL
                    points.add(new Point3D(sprite.getX() - 0.5f + sprite.getWidth(), sprite.getY() + 0.5f - sprite.getHeight(), -0.5f ) );    // 2 - BR
                    points.add(new Point3D(sprite.getX() - 0.5f + sprite.getWidth(), sprite.getY() + 0.5f,                      -0.5f ) ); // 1 - TR
                    points.add(new Point3D(sprite.getX() - 0.5f,                     sprite.getY() + 0.5f,                      -0.5f ) ); // 0 - TL
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY() + source.getHeight()) );
                    textureCoordinates.add( new Point2D(source.getX(), source.getY() + source.getHeight()) );
                    textureCoordinates.add( new Point2D(source.getX(), source.getY()) );
                    textureCoordinates.add( new Point2D(source.getX() + source.getWidth(), source.getY()) );
                    break; 
            }  
            
            faces.clear();
            faces.add(new ImmutableFace(0,0, 1,1, 2,2));
            faces.add(new ImmutableFace(2,2, 3,3, 0,0));
            
            dirty = false;
        }
}
