package fr.univ_poitiers.dptinfo.algo3d;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;


/**
 * Implementation class to manipulate shaders (in general)
 * @author Philippe Meseure
 * @version 1.0
 */
abstract public class BasicShaders
{
	static String getTextContent(InputStream file) throws IOException
	{
		BufferedReader reader=new BufferedReader(new InputStreamReader(file));
		String content="";
		while(reader.ready())
		{
			String line=reader.readLine();
			content+=line+'\n';
		}
		return content;
	}
	static protected int initializeShadersFromResources(GL2 gl,
		String vertname,String fragname)
	{		
		int shaderprogram=0;
		try
		{
			ClassLoader classloader=BasicShaders.class.getClassLoader();
			InputStream vertinput=classloader.getResourceAsStream(vertname);
			InputStream fraginput=classloader.getResourceAsStream(fragname);
			String vertsrc=getTextContent(vertinput);
			String fragsrc=getTextContent(fraginput);
			shaderprogram=initializeShaders(gl,vertsrc,fragsrc);
		}
		catch(IOException e)
		{
			System.err.println("IOException:"+e);
			System.exit(1);
		}
		return shaderprogram;
	}
	// Some static methods to load and initialize shaders...
					
	static protected int initializeShaders(GL2 gl,String vertsrc, String fragsrc)
	{
  	int vertexshader = loadShader(gl,GL2.GL_VERTEX_SHADER, vertsrc);
		int fragmentshader = loadShader(gl,GL2.GL_FRAGMENT_SHADER, fragsrc);
		
		int shaderprogram = gl.glCreateProgram();
		MyGLRenderer.checkGlError(gl, "glCreateProgram");
		
		if(shaderprogram == 0) { MainActivity.log("Unkwnon error: shader program == 0"); return 0; }
		
		gl.glAttachShader(shaderprogram, vertexshader);
		MyGLRenderer.checkGlError(gl, "glAttachShader Vertex");
		
		gl.glAttachShader(shaderprogram, fragmentshader);
		MyGLRenderer.checkGlError(gl, "glAttachShader Fragment");
		
		gl.glLinkProgram(shaderprogram);
		
		int[] linkStatus = new int[1];
    gl.glGetProgramiv(shaderprogram, GL2.GL_LINK_STATUS, linkStatus, 0);
    if (linkStatus[0] != GL2.GL_TRUE) {
			ByteBuffer bb = Buffers.newDirectByteBuffer(1024);
			IntBuffer size = Buffers.newDirectIntBuffer(1);

			int length = size.get();
			String infoLog = new String(bb.array());
			gl.glGetProgramInfoLog(shaderprogram, 1024, size, bb);
			throw new RuntimeException("Could not link program: "
				+infoLog.substring(0,length));
    }
		gl.glUseProgram(shaderprogram);
		MyGLRenderer.checkGlError(gl, "glUseProgram");

		MainActivity.log("Shaders initialized");
		return shaderprogram;
	}

	static protected int loadShader(GL2 gl,int type, String ShaderCode)
	{
		// create a vertex shader type (GL_VERTEX_SHADER)
		// or a fragment shader type (GL_FRAGMENT_SHADER)
		int shader = gl.glCreateShader(type);
		String extrait = ShaderCode.substring(0, 30);
		MyGLRenderer.checkGlError(gl,"glCreateShader "+extrait);
		if (shader==0) return shader; // Could not create ??
		
		// Add the source code to the shader and compile it
		IntBuffer buffV = Buffers.newDirectIntBuffer(new int[] { ShaderCode.length() });
		gl.glShaderSource(shader, 1, new String[] { ShaderCode }, buffV);
		MyGLRenderer.checkGlError(gl, "glShaderSource " + extrait);

		// Compile shader and check compile errors
		gl.glCompileShader(shader);

		int[] compiled = new int[1];
		gl.glGetShaderiv(shader, GL2.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			ByteBuffer bb = Buffers.newDirectByteBuffer(1024);
			IntBuffer size = Buffers.newDirectIntBuffer(1);
			gl.glGetShaderInfoLog(shader, 1024, size, bb);
			int length = size.get();
			String infoLog = new String(bb.array());
			throw new RuntimeException("Could not compile shader: "
					+ infoLog.substring(0, length));
		}
    return shader;
	}
	
	/********************************
	 *       Main class Data        *
	 ********************************/
	/**
	 * OpenGL Context (needed every where so it is stored here... not clean, but, well...
	 */
  GL2 gl=null;
	/**
	 * Shader program id (GLSL uniform variable)
	 */
	protected int shaderprogram=0;
	/**
	 * Matrix to represent to projection onto the screen (GLSL uniform variable)
	 */
	private int uProjectionMatrix=0;
	/**
	 * Matrix to represent a transformation from an object space into viewer's space
	 * (GLSL uniform variable)
	 */
	private int uModelViewMatrix=0;
	/**
	 * Index to give the array containing vertex position
	 */
	private int aVertexPosition=0;


	/**
	 * Constructor of the complete rendering Shader programs
	 * @param renderer Rendering context
	 */
	public BasicShaders(final MyGLRenderer renderer)
	{
		this.gl=renderer.getGL();
		this.shaderprogram=createProgram();
		this.findVariables();
	}
	
	/**
	 * Method to create shaders. Made abstract to make sure that it is
	 * created by downclasses
	 * @return program id created after compiling and linking shader programs
	 */
	public abstract int createProgram();
	
	/**
	 * Get Shaders variables (uniform, attributes, etc.)
	 * Should be called by any derivated classes
	 */
	public void findVariables()
	{
		// Variables for matrices
		this.uProjectionMatrix = gl.glGetUniformLocation(this.shaderprogram, "uProjectionMatrix");
		this.uModelViewMatrix = gl.glGetUniformLocation(this.shaderprogram, "uModelViewMatrix");

		// vertex attributes
		this.aVertexPosition = gl.glGetAttribLocation(this.shaderprogram, "aVertexPosition");
			gl.glEnableVertexAttribArray(this.aVertexPosition);
	}

	/*====================
		= Matrix functions =
		====================*/
	/**
	 * Set the uniform variable representing the Modelview Matrix
	 * Modelview = transformation from object's space to viewer's space
	 * @param matrix Matrix used to set the modelview matrix
	 */
	public void setModelViewMatrix(final float[] matrix)
	{
		gl.glUniformMatrix4fv(this.uModelViewMatrix, 1, false, matrix,0);
	}

	/**
	 * Set the uniform variable representing the projection Matrix
	 * @param matrix Matrix used to set the projection matrix
	 */
	public void setProjectionMatrix(final float[] matrix)
	{
		gl.glUniformMatrix4fv(this.uProjectionMatrix, 1, false, matrix, 0);
	}

	/* =======================
		 = Attributes handling =
		 ======================= */
	/**
	 * Provide the shaders with the list of vertex positions
	 * @param size Number of coordinates for each vertex
	 * @param dtype Type of coordinates
	 * @param stride Offset between two vertex coordinates
	 * @param buffer Buffer containing the vertex positions
	 */
	public void setPositionsPointer(final int size, final int dtype, final int stride,
																	final FloatBuffer buffer)
	{
		gl.glVertexAttribPointer(this.aVertexPosition,size,dtype,false,stride,buffer);
	}
	
	/**
	 * Set vertex position array for Buffer Object (TP2 and followings)
	 * @param size number of coordinates by vertices
	 * @param dtype type of coordinates
	 */
	public void setPositionsPointer(final int size,final int dtype)
	{
			gl.glVertexAttribPointer(this.aVertexPosition,size,dtype,false,0,0);
	}

}
