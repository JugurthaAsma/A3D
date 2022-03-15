package fr.univ_poitiers.dptinfo.algo3d;

/**
 * Abstract class to represent shaders (vertex and fragment ones) that allow a
 * computation of plong lighting with no texture.
 * @author Philippe Meseure
 * @version 1.0
 */
public abstract class LightingShaders extends BasicShaders
{
	// ==============================
	// Uniform variables for matrices
	// ==============================
	/**
	 * GLSL uniform transformation matrix for normal (from object's space to viewer's space)
	 */
	protected int uNormalMatrix;
	// ======================================
	// Uniform variables for (a single) light
	// ======================================
	/**
	 * GLSL uniform boolean value to turn lighting on/off
	 */
	protected int uLighting;
	/**
	 * GLSL uniform light position
	 */
	protected int uLightPosition;
	/**
	 * GLSL  ambient light color
	 */
	protected int uAmbiantLight;
	/**
	 * GLSL uniform light color
	 */
	protected int uLightColor;
	/**
	 * GLSL uniform specular light color
	 */
	protected int uLightSpecular;
	/**
	 * GLSL uniform constant (non distance-dependant) attenuation of the light
	 */
	protected int uConstantAttenuation;
	/**
	 * GLSL uniform linear attenuation with respect to the distance between light and lighted point
	 */
	protected int uLinearAttenuation;
	/**
	 * GLSL uniform quadratic attenuation (see linear attenuation)
	 */
	protected int uQuadraticAttenuation;
	// =====================================
	// Uniform variables for object material
	// =====================================
	/**
	 * GLSL uniform boolean to normalize normals (or not)
	 */
	protected int uNormalizing;
	/**
	 * GLSL uniform material color of the object
	 */
	protected int uMaterialColor;
	/**
	 * GLSL uniform material specular color
	 */
	protected int uMaterialSpecular;
	/**
	 * GLSL uniform Shininess of the material (for specular component)
	 */
	protected int uMaterialShininess;
	// ================================
	// Attributes to manage GLES arrays
	// ================================
	/**
	 * GLSL attribute for vertex normal arrays
	 */
	protected int aVertexNormal;
        
        /**
         * Textures
         */
        protected int aTexCoord;
        protected int uTextureUnit;
        protected int uIsTexture;


	/**
	 * Constructor. nothing to do, everything is done in the super class...
	 */
	public LightingShaders(final MyGLRenderer renderer)
	{
		super(renderer);
	}


	/**
	 * Get all the uniform variables and attributes defined in the shaders
	 */
	@Override
	public void findVariables()
	{
            super.findVariables();
            // Variables for matrices
            this.uNormalMatrix = gl.glGetUniformLocation(this.shaderprogram, "uNormalMatrix");

            // Variables for light source
            this.uLighting = gl.glGetUniformLocation(this.shaderprogram, "uLighting");
            this.uLightPosition = gl.glGetUniformLocation(this.shaderprogram, "uLightPos");
            this.uAmbiantLight = gl.glGetUniformLocation(this.shaderprogram, "uAmbiantLight");
            this.uLightColor = gl.glGetUniformLocation(this.shaderprogram, "uLightColor");
            this.uLightSpecular = gl.glGetUniformLocation(this.shaderprogram, "uLightSpecular");
            this.uConstantAttenuation = gl.glGetUniformLocation(this.shaderprogram, "uConstantAttenuation");
            this.uLinearAttenuation = gl.glGetUniformLocation(this.shaderprogram, "uLinearAttenuation");
            this.uQuadraticAttenuation = gl.glGetUniformLocation(this.shaderprogram, "uQuadraticAttenuation");

            // Variables for material
            this.uNormalizing = gl.glGetUniformLocation(this.shaderprogram, "uNormalizing");
            this.uMaterialColor = gl.glGetUniformLocation(this.shaderprogram, "uMaterialColor");
            this.uMaterialSpecular = gl.glGetUniformLocation(this.shaderprogram, "uMaterialSpecular");
            this.uMaterialShininess = gl.glGetUniformLocation(this.shaderprogram, "uMaterialShininess");

            // vertex attributes
            this.aVertexNormal = gl.glGetAttribLocation(this.shaderprogram, "aVertexNormal");
            gl.glEnableVertexAttribArray(this.aVertexNormal);

            //texture
            this.aTexCoord = gl.glGetAttribLocation(this.shaderprogram, "aTexCoord");
            gl.glEnableVertexAttribArray(this.aTexCoord);

            this.uTextureUnit = gl.glGetUniformLocation(this.shaderprogram, "uTextureUnit");
            this.uIsTexture = gl.glGetUniformLocation(this.shaderprogram, "uIsTexture");
                
	}

	/**
	 * Convert a ModelView Matrix into a NormalMatrix
	 * More exactly, the translation and scale component of the transformation must be ignored,
	 * while only the rotation component must be taken into account
	 * OpenGL redbook mainly recommend a transposition of the inverse matrix...
	 * @param a ModelView matrix (in)
	 * @param b Normal matrix (out)
	 */
	static void convertMVtoNM(final float[] a,float[] b)
	{
		float c=a[0],d=a[1],e=a[2],
			g=a[4],f=a[5],h=a[6],
			i=a[8],j=a[9],k=a[10],
			l=k*f-h*j,o=-k*g+h*i,m=j*g-f*i,n=c*l+d*o+e*m;
		if(n==0.) return;
		n=1.f/n;
		b[0]=l*n; b[3]=(-k*d+e*j)*n; b[6]=(h*d-e*f)*n;
		b[1]=o*n; b[4]=(k*c-e*i)*n; b[7]=(-h*c+e*g)*n;
		b[2]=m*n; b[5]=(-j*c+d*i)*n; b[8]=(f*c-d*g)*n;
	}

	// ================
	// Matrix functions
	// ================
	/**
	 * Set the model view matrix (transformation from object's space to viewer's space)
	 * @param matrix matrix to set to the GLSL modelview matrix
	 */
	@Override
	public void setModelViewMatrix(final float[] matrix)
	{
		float[] normal_matrix=new float[9];

		// Set modelview matrix
		super.setModelViewMatrix(matrix);

		// Set normal matrix according to the modelview matrix.
		// Scaling and translation must not be applied, only rotations...
		convertMVtoNM(matrix, normal_matrix);
		gl.glUniformMatrix3fv(this.uNormalMatrix, 1, false, normal_matrix,0);
	}

	// =====================
	// Source light function
	// =====================

	/**
	 * Set lighting on/off
	 * @param state on/off value
	 */
	public void setLighting(final boolean state)
	{
		gl.glUniform1i(this.uLighting,state?1:0);
	}

	/**
	 * Set the light position
	 * @param lightpos position of the light
	 */
	public void setLightPosition(final float[] lightpos)
	{
		gl.glUniform3fv(this.uLightPosition,1,lightpos,0);
	}

	/**
	 * Set the ambient light color
	 * @param amblight color of light
	 */
	public void setAmbiantLight(final float[] amblight)
	{
		gl.glUniform4fv(this.uAmbiantLight,1,amblight,0);
	}

	/**
	 * Set the (diffuse) light color
	 * @param lightcolor color of the diffuse light component
	 */
	public void setLightColor(final float[] lightcolor)
	{
		gl.glUniform4fv(this.uLightColor,1,lightcolor,0);
	}

	/**
	 * Set the specular light color
	 * @param lightspec specular light component
	 */
	public void setLightSpecular(final float[] lightspec)
	{
		gl.glUniform4fv(this.uLightSpecular,1,lightspec,0);
	}

	/**
	 * Set light attenuation parameters
	 * @param constant,linear,quadratic constant, linear and quadratic light attenuation
	 */
	public void setLightAttenuation(final float constant,final float linear,final float quadratic)
	{
		gl.glUniform1f(this.uConstantAttenuation,constant);
		gl.glUniform1f(this.uLinearAttenuation,linear);
		gl.glUniform1f(this.uQuadraticAttenuation,quadratic);
	}

	// ==================
	// Material functions
	// ==================

	/**
	 * Set normalizing of normals
	 * @param state on/off
	 */
	public void setNormalizing(final boolean state)
	{
		gl.glUniform1i(this.uNormalizing,state?1:0);
	}

	/**
	 * Set object (diffuse and ambient) color
	 * @param matcolor color to set to the object
	 */
	public void setMaterialColor(final float[] matcolor)
	{
		gl.glUniform4fv(this.uMaterialColor,1,matcolor,0);
	}

	/**
	 * Set object specular color
	 * @param matspec specular color to set to the object
	 */
	public void setMaterialSpecular(final float[] matspec)
	{
		gl.glUniform4fv(this.uMaterialSpecular,1,matspec,0);
	}

	/**
	 * Set the object shininess (for specular component)
	 * @param shininess shininess of the object
	 */
	public void setMaterialShininess(final float shininess)
	{
		gl.glUniform1f(this.uMaterialShininess,shininess);
	}

	// ===================
	// Attributes handling
	// ===================
	/**
	 * Set normal array for future drawings
	 * @param size number of coordinates by normals
	 * @param dtype type of coordinates
	 */
	public void setNormalsPointer(int size,int dtype)
	{
            gl.glVertexAttribPointer(this.aVertexNormal, size, dtype, false, 0, 0);
	}
        
        public void setTextureCoordsPointer(int size, int dtype)
	{
            gl.glVertexAttribPointer(this.aTexCoord, size, dtype, false, 0, 0);
	}
        
        public void setTextureUnit(int pos)
	{
            gl.glUniform1i(this.uTextureUnit, pos);
	}
        
        public void setIsTexture(boolean state)
	{
            gl.glUniform1i(this.uIsTexture,state?1:0);
	}
}
