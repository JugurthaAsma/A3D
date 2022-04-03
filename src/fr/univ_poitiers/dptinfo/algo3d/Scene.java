package fr.univ_poitiers.dptinfo.algo3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.Matrix4;
import fr.univ_poitiers.dptinfo.algo3d.Ball.SphereType;

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
    static final float wallsize=6F;
    static final float wallheight=3F;
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
    
    Room room;
    Sphere sphere, sun;
    Ball earth, mars, ball, armaBall;
    
    Pyramide pyramide;

    /**
     * An angle used to animate the viewer
     */
    float anglex,angley, step;
    float x = 0;
    float y = -1f;
    float z = 0;
    float stp = 0f;
    
    ObjLoader armadillo1;
    
    float objX = 0f;
    float objY = 0f;
    float objZ = 0f; 
    float objRotate = 0f; 

    float rebound = 0;
    float orientation = 0.05f;
    
    boolean interupteur = true;
    

    /**
     * Constructor : build each wall, the floor and the ceiling as quads
     */
    public Scene()
    {
        // Init observer's view angles
        angley=0.F;

    }       

    /**
     * Init some OpenGL and shaders uniform data to render the simulation scene
     * @param renderer Renderer
     */
    public void initGraphics(MyGLRenderer renderer)
    {
        MainActivity.log("Initializing graphics");
        
        GL2 gl=renderer.getGL();
        
        LightingShaders shaders=renderer.getShaders();
        
        shaders.setAmbiantLight(MyGLRenderer.darkgray);
        shaders.setLightColor(MyGLRenderer.lightgray);
        shaders.setLightSpecular(MyGLRenderer.white);
        shaders.setMaterialSpecular(MyGLRenderer.white);
        shaders.setMaterialShininess(1000);
        shaders.setLightAttenuation(1.0f, 0.001f, 0.03f);
        
        float [] lightPos = {0f, 0f, 0f};
        shaders.setLightPosition(lightPos);
        shaders.setNormalizing(true);
        //shaders.setIsTexture(true); // chaque objet fait ce qu'il faut

        room = new Room(gl);
        
        sun = new Sphere(gl,7);
        earth = new Ball(gl, 3f, 0f, 0.3f, MyGLRenderer.cyan, SphereType.subdivision);
        mars = new Ball(gl, -2f, 0f, 0.2f, MyGLRenderer.red, SphereType.coordinate);
        ball = new Ball(gl, 0f, 4f, 0.5f, MyGLRenderer.magenta, SphereType.subdivision);
        armaBall = new Ball(gl, wallsize / 2, 0, 0.5f, MyGLRenderer.white, SphereType.coordinate);
        pyramide = new Pyramide(gl, 1, 1, 1);
        armadillo1 = new ObjLoader(gl, "armadillo.obj", 10f, wallsize / 2, 1f, wallsize / 2);
        
        gl.glDepthFunc(GL2.GL_LESS);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        // le mur profond reprends parfois le dessus
        
        gl.glPolygonOffset(2.F,4.F);
        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        // Afficher les murs plein
        //gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
   
        // Set the background frame color
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // Allow back face culling !!
        gl.glEnable(GL2.GL_CULL_FACE);
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

        step += 0.01F;
        if (rebound > wallsize / 2 - 1 || rebound < -wallsize / 2 + 1)
            orientation = -orientation;
        
        rebound += orientation;
    }

    /**
     * Draw the current simulation state
     * @param renderer Renderer
     */
    public void draw(MyGLRenderer renderer)
    {
        

        MainActivity.log("Starting rendering");
        // Get OpenGL context
        GL2 gl=renderer.getGL();
        
        Matrix4 modelviewmatrix=new Matrix4();
        // Get shader to send uniform data
        LightingShaders shaders=renderer.getShaders();
        shaders.setLighting(interupteur);
        
        // draw the reflected objects
        gl.glFrontFace(GL2.GL_CW);
        drawObjects(gl, shaders, modelviewmatrix, true);
        gl.glFrontFace(GL2.GL_CCW);
        drawObjects(gl, shaders, modelviewmatrix, false);
        
        
        MainActivity.log("Rendering terminated.");
    } 
    
    
    public void drawObjects(GL2 gl, LightingShaders shaders, Matrix4 modelviewmatrix, Boolean reflect)
    {
        // Place viewer in the right position and orientation
        modelviewmatrix.loadIdentity();
        shaders.setViewPos(new float [] {x, y, z});
        // rotation
        modelviewmatrix.rotate(anglex , -1.0F,0.0F,0.0F);
        modelviewmatrix.rotate(angley , 0.0F,-1.0F,0.0F);        
        //translation
        modelviewmatrix.translate(x,y,z);  
    
        if (reflect)
            modelviewmatrix.scale(1, -1, 1);
        
        // application
        shaders.setModelViewMatrix(modelviewmatrix.getMatrix());
        // afficher la room avec un plafond rouge, un sol bleu et les murs en gris,    decommentez pour tracer les triangles en jaune
        room.draw(gl, shaders, MyGLRenderer.red, MyGLRenderer.transparentblue, MyGLRenderer.gray /*, MyGLRenderer.yellow*/); 
        
        
        //************** ROOM 2 *******************
        Matrix4 modelviewmatrix2=new Matrix4();
        modelviewmatrix2.loadIdentity();
        modelviewmatrix2.multMatrix(modelviewmatrix);
        modelviewmatrix2.rotate((float) Math.PI, 0.0F,1.0F,0.0F); // faire tourner de 180 deg
        modelviewmatrix2.translate(0,0,wallsize*2 + 0.001F);// la faire deplacer de la taille d'un mur + un petit espacement
        
        
        shaders.setModelViewMatrix(modelviewmatrix2.getMatrix());
        room.draw(gl, shaders, MyGLRenderer.yellow, MyGLRenderer.transparentorange, MyGLRenderer.gray);
        
        //*****************************************
        
        
        //************** SPHERE  *******************
        Matrix4 modelviewmatrixSphere = new Matrix4();
        modelviewmatrixSphere.loadIdentity();
        modelviewmatrixSphere.multMatrix(modelviewmatrix);
        modelviewmatrixSphere.translate(0,1,-wallsize*2);//translation par rapport à la vue
        modelviewmatrixSphere.rotate(step, 0, 1, 0);
        
        shaders.setModelViewMatrix(modelviewmatrixSphere.getMatrix());
        sun.draw(gl, shaders, MyGLRenderer.yellow, MyGLRenderer.black);
        
        
        //************** BALLS  *******************
        earth.draw(gl, shaders, this, step , modelviewmatrixSphere );
        mars.draw(gl, shaders, this, -step , modelviewmatrixSphere); 
        ball.draw(gl, shaders, this, -step, modelviewmatrix);
        armaBall.draw(gl, shaders, this, step, modelviewmatrix, rebound);
        //*****************************************
        pyramide.draw(gl, shaders, modelviewmatrix, step, MyGLRenderer.yellow);
        
        armadillo1.draw(gl, shaders, modelviewmatrix, 0F, MyGLRenderer.blue, this);
        
        Matrix4 modelviewmatrixSphereArmadillo = new Matrix4();
        modelviewmatrixSphereArmadillo.loadIdentity();
        modelviewmatrixSphereArmadillo.multMatrix(modelviewmatrix);
        modelviewmatrixSphereArmadillo.translate(0,0,-6);//translation par rapport à la vue
        shaders.setModelViewMatrix(modelviewmatrixSphereArmadillo.getMatrix());
        armadillo1.draw(gl, shaders, modelviewmatrixSphereArmadillo, (float) Math.PI, MyGLRenderer.red, this);
        
        
    } 

}
