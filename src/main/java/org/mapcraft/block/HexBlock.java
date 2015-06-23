package org.mapcraft.block;

import static org.lwjgl.opengl.GL11.GL_AMBIENT_AND_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_QUAD_STRIP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glMaterialfv;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.nio.FloatBuffer;

public class HexBlock implements Drawable {
	private int callListId;
	boolean horizontalFlag;
	float angleOffset;

	@Override
	public void render() {
		glCallList(callListId);
	}
	
	public HexBlock(boolean horizontalFlag, float scale, FloatBuffer color) {
		this.horizontalFlag = horizontalFlag;
		if(horizontalFlag) {
			angleOffset = 30.0f;
		}
		else {
			angleOffset = 0.0f;
		}

		float d2r = (float)Math.PI/180;
		float[] angles = {  (float)d2r * (0.0f+angleOffset), 
							(float)d2r * (60.0f+angleOffset), 
							(float)d2r * (120.0f+angleOffset), 
							(float)d2r * (180.0f+angleOffset), 
							(float)d2r * (240.0f+angleOffset), 
							(float)d2r * (300.0f+angleOffset) };
		
		callListId = glGenLists(1);
		glNewList(callListId, GL_COMPILE);
		glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, color);

//		glShadeModel(GL_FLAT);
		glShadeModel(GL_SMOOTH);

		glNormal3f(0.0f, 0.0f, 1.0f);
		
		/* draw side faces */
		glBegin(GL_QUAD_STRIP);
		for ( int i = 0; i <= 6; i++ ) {
			float angle;
			if(i != 6)
				angle = angles[i];
			else
				angle = angles[0];
			
			// 2	4	6
			// 1	3	5	

			glVertex3f(scale * (float)Math.cos(angle), scale * (float)Math.sin(angle), scale * 0.5f);
			glVertex3f(scale * (float)Math.cos(angle), scale * (float)Math.sin(angle), scale * -0.5f);
		}
		glEnd();
		
		// Draw Top Face
		//		3,2	5,1
		// 1,3			6,0
		//		2,4	4,5
		glBegin(GL_TRIANGLE_STRIP);
		glVertex3f(scale * (float)Math.cos(angles[3]), scale * (float)Math.sin(angles[3]), scale * 0.5f);
		glVertex3f(scale * (float)Math.cos(angles[4]), scale * (float)Math.sin(angles[4]), scale * 0.5f);
		glVertex3f(scale * (float)Math.cos(angles[2]), scale * (float)Math.sin(angles[2]), scale * 0.5f);
		glVertex3f(scale * (float)Math.cos(angles[5]), scale * (float)Math.sin(angles[5]), scale * 0.5f);
		glVertex3f(scale * (float)Math.cos(angles[1]), scale * (float)Math.sin(angles[1]), scale * 0.5f);
		glVertex3f(scale * (float)Math.cos(angles[0]), scale * (float)Math.sin(angles[0]), scale * 0.5f);
		glEnd();

		
		// Draw Bottom Face
		//		2,2	4,1
		// 1,3			6,0
		//		3,4	5,5
		glBegin(GL_TRIANGLE_STRIP);
		glVertex3f(scale * (float)Math.cos(angles[3]), scale * (float)Math.sin(angles[3]), scale * -0.5f);
		glVertex3f(scale * (float)Math.cos(angles[2]), scale * (float)Math.sin(angles[2]), scale * -0.5f);
		glVertex3f(scale * (float)Math.cos(angles[4]), scale * (float)Math.sin(angles[4]), scale * -0.5f);
		glVertex3f(scale * (float)Math.cos(angles[1]), scale * (float)Math.sin(angles[1]), scale * -0.5f);
		glVertex3f(scale * (float)Math.cos(angles[5]), scale * (float)Math.sin(angles[5]), scale * -0.5f);
		glVertex3f(scale * (float)Math.cos(angles[0]), scale * (float)Math.sin(angles[0]), scale * -0.5f);
		glEnd();
		
		glEndList();
	}}
