package fr.univ_poitiers.dptinfo.algo3d;

/**
 * Implementation class to manipulate shaders for non-lighted objects (uniform color).
 * @author Philippe Meseure
 * @version 1.0
 */
public class NoLightShaders extends BasicShaders
{
	/**
	 * Color of the object (GLSL uniform variable)
	 */
	private int uColor;

	/**
	 * Fragment shader : the uniform color is used for each pixel
	 */
	private static final String FRAGSRC=
		"precision mediump float;\n"
		+"uniform vec4 uColor;\n"
		+"void main()\n"
		+"{\n"
		+"    gl_FragColor=uColor;\n"
		+"}\n";

	/**
	 * Vertex shader: it only transform vertex coordinates into viewer's space and project to screen
	 */
	private static final String VERTSRC=
		"precision mediump float;\n"
		// Matrices
		+"uniform mat4 uModelViewMatrix;\n"
		+"uniform mat4 uProjectionMatrix;\n"

		// Vertex attributes
		+"attribute vec3 aVertexPosition;\n"

		+"void main() {\n"
		+"  gl_Position= uProjectionMatrix*uModelViewMatrix*vec4(aVertexPosition, 1.0);\n"
		+"}\n";

	/**
	 * Constructor of the complete rendering Shader programs
	 */
	public NoLightShaders(final MyGLRenderer renderer)
	{
		super(renderer);
	}

	@Override
	public int createProgram()
	{
		// First approach : get shader sources as String stored in the object
		// return initializeShaders(gl,VERTSRC,FRAGSRC);

		// Second method : get shader codes from ressource files			
		return initializeShadersFromResources(gl,"nolight_vert.glsl","nolight_frag.glsl");
	}

	/**
	 * Get Shaders variables (uniform, attributes, etc.)
	 */
	@Override
	public void findVariables()
	{
		// findVariables for super class
		super.findVariables();
		// Find variables for material
		this.uColor = gl.glGetUniformLocation(this.shaderprogram, "uColor");
	}

	/* ======================
		 = Material functions =
		 ====================== */

	/**
	 * Set the color of the object (uniform variable)
	 * @param color Color of the object
	 */
	public void setColor(final float[] color)
	{
		gl.glUniform4fv(this.uColor,1,color,0);
	}
}
