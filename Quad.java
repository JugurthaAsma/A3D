package fr.univ_poitiers.dptinfo.algo3d;

import com.jogamp.opengl.GL2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * Creation of graphics quad composed of two adjacent trianges, stored in a vertex array
 * (on client side)
 * @author Philippe Meseure
 * @version 1.0
 */
public class Quad
{
	/**
	 * Buffer containing the positions of the vertices of the quad
	 */
	protected FloatBuffer vertexbuffer;
	/**
	 * Number of vertices of the triangles issued from the quad (must be 6)
	 */
	protected int nbvertices;
	/**
	 * Constructor: set the vertex, normal and element buffer
	 * @param p0,p1,p2,p3 Four vertices of the quad
	 */
	public Quad(final Vec3f p0, final Vec3f p1, final Vec3f p2, final Vec3f p3)
	{
		// Create two triangles with the four vertices
		float[] vertexpos = {
			// To complete : x0,y0,z0,x1,y1,z1....
                          
                    p0.x, p0.y, p0.z,
                    p1.x, p1.y, p1.z,
                    p3.x, p3.y, p3.z,
                    
                    p2.x, p2.y, p2.z,
                    p3.x, p3.y, p3.z,
                    p1.x, p1.y, p1.z
                    
                    
                    
                    
		};
		// Number of vertices for the two triangles (6 !!!)
		this.nbvertices=vertexpos.length/3;

		// Create buffer containing the vertex positions
		ByteBuffer vertexbytebuf = ByteBuffer.allocateDirect(vertexpos.length * Float.BYTES);
		vertexbytebuf.order(ByteOrder.nativeOrder());
		this.vertexbuffer = vertexbytebuf.asFloatBuffer();
		this.vertexbuffer.put(vertexpos);
		this.vertexbuffer.position(0);
	}

	/**
	 * Draw quad as two triangles
	 * @param gl OpenGL context
	 * @param shaders Shader to represent the quad. This shader is not supposed to compute lighting
	 */
	public void draw(final GL2 gl,final NoLightShaders shaders)
	{
		shaders.setPositionsPointer(3, GL2.GL_FLOAT,3*Float.BYTES,this.vertexbuffer);
		gl.glDrawArrays(GL2.GL_TRIANGLES,0,this.nbvertices);
		MyGLRenderer.checkGlError(gl,"glDrawArrays (GL_TRIANGLES)");
	}
	/**
	 * Draw quad in wireframe
	 * @param gl OpenGL context
	 * @param shaders Shader to represent the quad. This shader is not supposed to compute lighting
	 */
	public void drawWireframe(final GL2 gl,final NoLightShaders shaders)
	{
		shaders.setPositionsPointer(3, GL2.GL_FLOAT,3*Float.BYTES,this.vertexbuffer);
		gl.glDrawArrays(GL2.GL_LINE_LOOP,0,4); // Only 4 vertices to draw a quad...
		MyGLRenderer.checkGlError(gl,"glDrawArrays (GL_LINE_LOOP)");
	}
}

