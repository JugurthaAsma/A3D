package fr.univ_poitiers.dptinfo.algo3d;

/**
 * Implementation class of shaders to compute diffuse lighting using Gouraud interpolation.
 * @author Philippe Meseure
 * @version 1.0
 */
public class GouraudShaders extends LightingShaders
{
	/**
	 * Constructor : compile shaders and group them in a GLES program,
	 * and set the various GLSL variables
	 * @param renderer
	 */
	public GouraudShaders(final MyGLRenderer renderer)
	{
		super(renderer);
	}
	/**
	 * Method to create shaders.
	 * @return program id created after compiling and linking shader programs
	 */
	@Override
	public int createProgram()
	{
		return initializeShadersFromResources(gl,"gouraud_vert.glsl","gouraud_frag.glsl");
	}
}
