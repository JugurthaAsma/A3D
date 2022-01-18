package fr.univ_poitiers.dptinfo.algo3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.Matrix4;

/**
 * Class to represent the scene. It includes all the objects to display, in this case a room
 * @author Philippe Meseure
 * @version 1.0
 */
public class Scene
{
	/**
	 * A constant for the size of the wall
	 */
	static final float wallsize=3.F;
	/**
	 * 4 quads to represent the walls of the room
	 */
	Quad wall1,wall2,wall3,wall4;
	/**
	 * A quad to represent a floor
	 */
	Quad floor;
	/**
	 * A quad to represent the ceiling of the room
	 */
	Quad ceiling;

	/**
	 * An angle used to animate the viewer
	 */
	float anglex,angley;
        float x,y;


	/**
	 * Constructor : build each wall, the floor and the ceiling as quads
	 */
	public Scene()
	{
            
		// Init observer's view angles
		angley=0.F;
                
                Vec3f a1 = new Vec3f(-3f, 0f, 3f);
                Vec3f b1 = new Vec3f(3f, 0f, 3f);
                Vec3f c1 = new Vec3f(3f, 2.5f, 3f);
                Vec3f d1 = new Vec3f(-3f, 2.5f, 3f);
                
                Vec3f a2 = new Vec3f(-3f, 0f, -3);
                Vec3f b2 = new Vec3f(3f, 0f, -3f);
                Vec3f c2 = new Vec3f(3f, 2.5f, -3f);
                Vec3f d2 = new Vec3f(-3f, 2.5f, -3f);
                
                
                // Create the front wall
                this.wall1=new Quad(a2 , b2, c2, d2); 

                // Create the right wall
                this.wall2=new Quad(b2, b1, c1, c2); // horaire
                
                // Create the left wall
                this.wall3=new Quad(a1, a2, d2, d1); // anti-horaire
                
                // create the back wall
                this.wall4=new Quad(b1, a1, d1, c1); // horaire

                // Create the floor of the room
                this.floor=new Quad(a1, b1, b2, a2);

                // Create the ceiling of the room
                this.ceiling=new Quad(d2, c2, c1, d1);
                
	}       


	/**
	 * Init some OpenGL and shaders uniform data to render the simulation scene
	 * @param renderer Renderer
	 */
	public void initGraphics(MyGLRenderer renderer)
	{
		GL2 gl=renderer.getGL();

		MainActivity.log("Initializing graphics");
		// Set the background frame color
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// Allow back face culling !!
		//gl.glEnable(GL2.GL_CULL_FACE);
		MainActivity.log("Graphics initialized");
	}


	/**
	 * Make the scene evoluate, to produce an animation for instance
	 * Here, only the viewer rotates
	 */
	public void step()
	{
		//angley+=0.1F;
                //anglex+=0.1F;
            
	}

	/**
	 * Draw the current simulation state
	 * @param renderer Renderer
	 */
	public void draw(MyGLRenderer renderer)
	{
		Matrix4 modelviewmatrix=new Matrix4();

		MainActivity.log("Starting rendering");

		// Get shader to send uniform data
		NoLightShaders shaders=renderer.getShaders();

		// Place viewer in the right position and orientation
		modelviewmatrix.loadIdentity();
                
                modelviewmatrix.rotate((anglex * 0.001f), -1.0F,0.0F,0.0F);
                modelviewmatrix.rotate((angley * 0.001f), 0.0F,-1.0F,0.0F);
                
                
                
		modelviewmatrix.translate(0.F,-1.6F,0.F);
		shaders.setModelViewMatrix(modelviewmatrix.getMatrix());
                
                

		// Get OpenGL context
		GL2 gl=renderer.getGL();

		// to complete :
		// Draw walls, floor and ceil in selected colors
                shaders.setColor(MyGLRenderer.blue);
                this.wall1.draw(gl, shaders);
                
                shaders.setColor(MyGLRenderer.green);
                this.wall2.draw(gl, shaders);
                
                shaders.setColor(MyGLRenderer.red);
                this.wall3.draw(gl, shaders);
                
                shaders.setColor(MyGLRenderer.orange);
                this.wall4.draw(gl, shaders);
                
                shaders.setColor(MyGLRenderer.yellow);
                this.floor.draw(gl, shaders);
                
                shaders.setColor(MyGLRenderer.white);
                this.ceiling.draw(gl, shaders);

		// Add some wireframe drawings if you want to enhance display

		MainActivity.log("Rendering terminated.");
	}
}
