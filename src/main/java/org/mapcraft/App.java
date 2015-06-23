package org.mapcraft;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mapcraft.block.*;
import org.mapcraft.dictionary.ColorDictionary;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class App {
	// Strongly reference callback instances.
	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback   keyCallback;
	
	// Window handle
	private long window;
	private int WIDTH;
	private int HEIGHT;
	
	// Objects
	private List<Drawable> drawList = new ArrayList<Drawable>();
	
	// Meta
	private static final float view_rotx = 20.0f;
	private static final float view_roty = 30.0f;
	private float view_rotz;
	private float angle;
	
	public void run() {
		System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
		
		try {
			init();
			initGLState();
			loop();
			
			// Release window and window callbacks
			glfwDestroyWindow(window);
			keyCallback.release();
		} finally {
			// Terminate GLFW and release the FLFWerrorfun
			glfwTerminate();
			errorCallback.release();
		}
	}
	
	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
		
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (glfwInit() != GL11.GL_TRUE )
			throw new IllegalStateException("Unable to initialize GLFW");
		
		// Configure the window
		glfwDefaultWindowHints(); //optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // resizable window
		
		WIDTH = 600;
		HEIGHT = 600;
		
		//Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World", NULL, NULL);
		if (window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int modes) {
				if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
					glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
			}
		});
		
		// Get the resolution of the primary monitor
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		//Center our window
		glfwSetWindowPos(
				window,
				(GLFWvidmode.width(vidmode) - WIDTH) / 2,
				(GLFWvidmode.height(vidmode) - HEIGHT) / 2
		);
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		
		//Enable v-sync
		glfwSwapInterval(1);
		
		//Make the window visible
		glfwShowWindow(window);
	}
	
	private void initGLState() {
		// This line is critical for LWJGL's inter-operation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// created the ContextCapabilities instance and make the OpenGL
		// bindings available for use.
		GLContext.createFromCurrent();
		
		System.err.println("GL_VENDOR: " + glGetString(GL_VENDOR));
		System.err.println("GL_RENDERER: " + glGetString(GL_RENDERER));
		System.err.println("GL_VERSION: " + glGetString(GL_VERSION));
		
		FloatBuffer pos = BufferUtils.createFloatBuffer(4).put(new float[] {5.0f, 5.0f, 10.0f, 0.0f});
		pos.flip();

		glLightfv(GL_LIGHT0, GL_POSITION, pos);
		glEnable(GL_CULL_FACE);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glEnable(GL_DEPTH_TEST);
		
		/* making the gears */
//		drawList.add(new Gear(1.0f, 4.0f, 1.0f, 20, 0.7f, ColorDictionary.red));
//		drawList.add(new Gear(0.5f, 2.0f, 2.0f, 10, 0.7f, ColorDictionary.green));
//		drawList.add(new Gear(1.3f, 2.0f, 0.5f, 10, 0.7f, ColorDictionary.blue));
		drawList.add(new HexBlock(true, 2.0f,  ColorDictionary.red));
		drawList.add(new HexBlock(false,  1.0f,  ColorDictionary.green));
		drawList.add(new HexBlock(true,  1.0f,  ColorDictionary.blue));
		
		glEnable(GL_NORMALIZE);
		glMatrixMode(GL_PROJECTION);
		
		float h = (float) WIDTH / (float) HEIGHT;
		glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glTranslatef(0.0f, 0.0f, -40.0f);
	}
	
	private void loop() {
		// Set the clear color
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while( glfwWindowShouldClose(window) == GL_FALSE ) {
			angle += 2.0f;
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the frame buffer
//			glLoadIdentity();	// Reset the view
//			
//			glTranslatef(-1.5f, 0.0f, -6.0f); // Move left 1.5 and into 6.0
//			
//			glBegin(GL_TRIANGLES);
//				glVertex3f(0.0f, 1.0f, 0.0f);	// Top
//				glVertex3f(-1.0f, -1.0f, 0.0f);	// Bottom Left
//				glVertex3f(1.0f, -1.0f, 0.0f);	// Bottom Right
//			glEnd();
//			
//			glTranslatef(3.0f, 0.0f, 0.0f);
//			
//			glBegin(GL_QUADS);
//				glVertex3f(-1.0f, 1.0f, 0.0f);	// Top Left
//				glVertex3f( 1.0f, 1.0f, 0.0f);	// Top Right
//				glVertex3f( 1.0f,-1.0f, 0.0f);	// Bottom Right
//				glVertex3f(-1.0f,-1.0f, 0.0f);	// Bottom Left
//			glEnd();
			
			glPushMatrix();
			glRotatef(view_rotx, 1.0f, 0.0f, 0.0f);
			glRotatef(view_roty, 0.0f, 1.0f, 0.0f);
			glRotatef(view_rotz, 0.0f, 0.0f, 1.0f);

			glPushMatrix();
			glTranslatef(-3.0f, -2.0f, 0.0f);
			glRotatef(angle, 0.0f, 0.0f, 1.0f);
			drawList.get(0).render();
			glPopMatrix();

			glPushMatrix();
			glTranslatef(3.1f, -2.0f, 0.0f);
			glRotatef(-2.0f * angle - 9.0f, 0.0f, 0.0f, 1.0f);
			drawList.get(1).render();
			glPopMatrix();

			glPushMatrix();
			glTranslatef(-3.1f, 4.2f, 0.0f);
			glRotatef(-2.0f * angle - 25.0f, 0.0f, 0.0f, 1.0f);
			drawList.get(2).render();
			glPopMatrix();

			glPopMatrix();

			glfwSwapBuffers(window); // swap the color buffer
			
			//Poll for window events. The key callback above will only be invoked during
			// this call.
			glfwPollEvents();
		}
	}
	
	
	public static void main(String[] args) {
		new App().run();
	}
}
