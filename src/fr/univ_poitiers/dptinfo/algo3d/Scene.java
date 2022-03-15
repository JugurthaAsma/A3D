package fr.univ_poitiers.dptinfo.algo3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.texture.Texture;
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
    Sphere sphere, sphereSub;
    Ball sun, earth, mars, ball, armaBall;
    
    Pyramide pyramide;
    
    Vec3f a1 = new Vec3f(-wallsize, 0f, wallsize);
    Vec3f b1 = new Vec3f(wallsize, 0f, wallsize);
    Vec3f c1 = new Vec3f(wallsize, wallheight, wallsize);
    Vec3f d1 = new Vec3f(-wallsize, wallheight, wallsize);

    Vec3f a2 = new Vec3f(-wallsize, 0f, -wallsize);
    Vec3f b2 = new Vec3f(wallsize, 0f, -wallsize);
    Vec3f c2 = new Vec3f(wallsize, wallheight, -wallsize);
    Vec3f d2 = new Vec3f(-wallsize, wallheight, -wallsize);

    /**
     * An angle used to animate the viewer
     */
    float anglex,angley, step;
    float x = 0;
    float y = -0.5f;
    float z = 0;
    float stp = 0f;
    
    ObjLoader armadillo1;
    
    float objX = 0f;
    float objY = 0f;
    float objZ = 0f; 
    float objRotate = 0f; 

    float rebound = 0;
    float orientation = 0.05f;
    
    private Texture wallTexture;
    
    boolean interupteur = true;
    

    /**
     * Constructor : build each wall, the floor and the ceiling as quads
     */
    public Scene()
    {

        // Init observer's view angles
        angley=0.F;

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
        MainActivity.log("Initializing graphics");
        
        GL2 gl=renderer.getGL();
        
        LightingShaders shaders=renderer.getShaders();
        
        
        shaders.setAmbiantLight(MyGLRenderer.darkgray);
        shaders.setLightColor(MyGLRenderer.lightgray);
        shaders.setLightSpecular(MyGLRenderer.white);
        
        float [] lightPos = {0f, 0f, 0f};
        shaders.setLightPosition(lightPos);
        shaders.setNormalizing(true);
        //shaders.setIsTexture(true); // chaque objet fait ce qu'il faut
        
        wallTexture = MyGLRenderer.loadTexture(gl, "ceiling.jpg");
        
        room = new Room(gl, a1, b1, c1, d1, a2, b2, c2, d2, wallTexture);
        
        sphereSub = new Sphere(gl,5);
        sun = new Ball(gl, 0f, 0f, 1f, MyGLRenderer.yellow, SphereType.subdivision);
        earth = new Ball(gl, 3f, 0f, 0.3f, MyGLRenderer.cyan, SphereType.coordinate);
        mars = new Ball(gl, -2f, 0f, 0.2f, new float[]  {(156F / 255), (46F / 255), (53F / 255), 1F}, SphereType.subdivision);
        ball = new Ball(gl, 0f, 4f, 0.5f, MyGLRenderer.green, SphereType.subdivision);
        armaBall = new Ball(gl, wallsize / 2, 0, 0.5f, MyGLRenderer.orange, SphereType.coordinate);
        pyramide = new Pyramide(gl, 1, 1, 1);
        armadillo1 = new ObjLoader(gl, "armadillo.obj", 0.8f, wallsize / 2, 0.8f, wallsize / 2);
        
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
        /*NoLightShaders*/ LightingShaders shaders=renderer.getShaders();
        // Place viewer in the right position and orientation
        modelviewmatrix.loadIdentity();
        
        // rotation
        modelviewmatrix.rotate(anglex , -1.0F,0.0F,0.0F);
        modelviewmatrix.rotate(angley , 0.0F,-1.0F,0.0F);        
        //translation
        modelviewmatrix.translate(x,y,z);        
        // application
        shaders.setModelViewMatrix(modelviewmatrix.getMatrix());
        
        shaders.setLighting(interupteur);

        // afficher la room avec un plafond rouge, un sol bleu et les murs en gris,    decommentez pour tracer les triangles en jaune
        room.draw(gl, shaders, MyGLRenderer.red, MyGLRenderer.blue, MyGLRenderer.gray /*, MyGLRenderer.yellow*/); 
        
        
        //************** ROOM 2 *******************
        Matrix4 modelviewmatrix2=new Matrix4();
        modelviewmatrix2.loadIdentity();
        modelviewmatrix2.multMatrix(modelviewmatrix);
        modelviewmatrix2.rotate((float) Math.PI, 0.0F,1.0F,0.0F); // faire tourner de 180 deg
        modelviewmatrix2.translate(0,0,wallsize*2);// la faire deplacer de la taille d'un mur
        
        shaders.setModelViewMatrix(modelviewmatrix2.getMatrix());
        room.draw(gl, shaders, MyGLRenderer.yellow, MyGLRenderer.orange, MyGLRenderer.gray);
        //*****************************************
        
        
        //************** SPHERE  *******************
        Matrix4 modelviewmatrixSphere = new Matrix4();
        modelviewmatrixSphere.loadIdentity();
        modelviewmatrixSphere.multMatrix(modelviewmatrix);
        modelviewmatrixSphere.translate(0,1,-wallsize*2);//translation par rapport à la vue
        modelviewmatrixSphere.rotate(step, 0, 1, 0);
        shaders.setModelViewMatrix(modelviewmatrixSphere.getMatrix());
        sphereSub.draw(gl, shaders, MyGLRenderer.yellow, MyGLRenderer.black);
        
        
        //************** BALLS  *******************
        earth.draw(gl, shaders, this, step , modelviewmatrixSphere );
        mars.draw(gl, shaders, this, -step , modelviewmatrixSphere); 
        
        ball.draw(gl, shaders, this, -step, null);
        
        armaBall.draw(gl, shaders, this, step, null, rebound);
        //*****************************************
        
        pyramide.draw(gl, shaders, this, step, MyGLRenderer.yellow);
        
        //armadillo1.draw(gl, shaders, modelviewmatrix, 0F, MyGLRenderer.white, this);
        
        
        /*
        Matrix4 modelviewmatrixSphereArmadillo = new Matrix4();
        modelviewmatrixSphereArmadillo.loadIdentity();
        modelviewmatrixSphereArmadillo.multMatrix(modelviewmatrix);
        modelviewmatrixSphereArmadillo.translate(0,0,-6);//translation par rapport à la vue
        shaders.setModelViewMatrix(modelviewmatrixSphereArmadillo.getMatrix());
        armadillo1.draw(gl, shaders, modelviewmatrixSphereArmadillo, (float) Math.PI, MyGLRenderer.red, this);
        */
        MainActivity.log("Rendering terminated.");
    }    
    
}
