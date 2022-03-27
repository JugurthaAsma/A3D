/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_poitiers.dptinfo.algo3d;

import static com.jogamp.common.nio.Buffers.SIZEOF_INT;
import static com.jogamp.common.nio.Buffers.SIZEOF_SHORT;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author JUGURTHA
 */
public class MyVBO {
    
    int glposbuffer, glnormalbuffer, glelementbuffer, gltexturesbuffer;
    int type, shaderType, shaderSize, nbIndices;

    short[] shortTriangles = {};
    int[] intTriangles = {};
    float[] vertexpos = {};
    float[] normals = {};
    float[] textures = {};
    
    GL2 gl;


    
    public MyVBO(
        final GL2 gl,
        float[] vertexpos,
        short[] shortTriangles,
        float[] normals,
        float[] textures
    ) {
        
        if (normals != null) {
            this.normals = normals;
        }
        
        if (textures != null) {
            this.textures = textures;
        }
        this.gl = gl;
        this.vertexpos = vertexpos;
        
        this.type = Short.BYTES;
        this.shortTriangles = shortTriangles;
        nbIndices = shortTriangles.length;
        shaderType = GL2.GL_UNSIGNED_SHORT;
        shaderSize = SIZEOF_SHORT;

        allocateVertexPosBuffer();
        allocateTriangleBuffer();
        allocateNormalsBuffer();
        allocateTextureBuffer();
    }
    
    public MyVBO(final GL2 gl, float[] vertexpos, short[] shortTriangles, float[] textures) {
        this(gl, vertexpos, shortTriangles, null, textures);
    }
    
    public MyVBO(final GL2 gl, float[] vertexpos, short[] shortTriangles) {
        this(gl, vertexpos, shortTriangles, null);
    }
    
    // pour les .obj
    public MyVBO(final GL2 gl, float[] vertexpos, int[] intTriangles, float[] normals) {
        this.gl = gl;
        this.vertexpos = vertexpos;
        
        this.type = Integer.BYTES;
        this.intTriangles = intTriangles;
        nbIndices = intTriangles.length;
        shaderType = GL2.GL_UNSIGNED_INT;
        shaderSize = SIZEOF_INT;
    
        allocateVertexPosBuffer();
        allocateTriangleBuffer();
        allocateNormalsBuffer();
    }
    
    
    
    
    public void draw(final GL2 gl, final LightingShaders shaders, int elementType, float[] objectColor, float[] linesColor, Texture texture) {
        shaders.setMaterialColor(objectColor);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,glposbuffer);
        // Activation du buffer de position
        shaders.setPositionsPointer(3,GL2.GL_FLOAT);
        
        // Normales
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,glnormalbuffer);
        shaders.setNormalsPointer(3,GL2.GL_FLOAT); // méthode définie dans LightingShaders
        
        
        if (texture != null) {
            shaders.setIsTexture(true);
            // Textures
            gl.glActiveTexture(GL2.GL_TEXTURE0); // Activation de l’unité 0 (attention au paramètre)
            texture.bind(gl);
            // associer la texture à l’unité courante (objet fourni par loadTexture)
            shaders.setTextureUnit(0); 
            gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,gltexturesbuffer);
            // texcoordbuffer est le buffer de la carte graphique
            //contenant les coordonnées de texture des sommets
            shaders.setTextureCoordsPointer(2, GL2.GL_FLOAT);
            // aTexCoord est l’attribut des shaders dédié aux cooordonnées de texture des sommets
            
        } else {
            shaders.setIsTexture(false);
        }
       
        
        
        // Indication que ce sont les positions des sommets qui se trouvent dans ce buffer
        // Chaque sommet récupère 3 coordonnées de type float
        // Attention, cette routine est propre au shaders fournis pour le TP
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, glelementbuffer);
        // Activation du buffer d’element (créé par vos soins)
        
        
        
        //shaders.setMaterialColor(objectColor);
        shaders.setLightColor(MyGLRenderer.lightgray);
        gl.glDrawElements(elementType, nbIndices, shaderType, 0);
        /*
        if (linesColor != null) {
            shaders.setMaterialColor(linesColor);
            for(int i=0;i<nbIndices;i+=3) // i.e. pour chaque triangle commençant à l’indice i
               gl.glDrawElements(GL2.GL_LINE_LOOP,3, shaderType, i * shaderSize);
        }
        */
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,0); // Désactivation du buffer de positions
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,0); // Désactivation du buffer des normales
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER,0); // Désactivation du buffer d’éléments
        // Désactivations nécessaires pour éviter de perturber les tracés suivants
    }
    

    
    public void draw(final GL2 gl,final LightingShaders shaders, float[] objectColor, float[] linesColor, Texture texture){
        this.draw(gl, shaders, GL2.GL_TRIANGLES, objectColor, linesColor, texture);
    }
    
    public void draw(final GL2 gl,final LightingShaders shaders, float[] objectColor){
        this.draw(gl, shaders, GL2.GL_TRIANGLES, objectColor, null, null);
    }
    
    private void allocateVertexPosBuffer(){
        
        ByteBuffer posbytebuf = ByteBuffer.allocateDirect(vertexpos.length * Float.BYTES);
        // Allocation mémoire du buffer (SIZEOF_FLOAT=4) !!
        posbytebuf.order(ByteOrder.nativeOrder()); // LITTLE_ENDIAN ou BIG_ENDIAN ?
        FloatBuffer floatPosbuffer = posbytebuf.asFloatBuffer();
        // Transformation du buffer en un buffer de float
        floatPosbuffer.put(vertexpos);
        // copie des positions dans le buffer (en tenant compte de « endianness »)
        floatPosbuffer.position(0); // Retour à la position 0 pour l’exploitation ultérieure du buffer
        
        int[] buffers = new int[1]; // Besoin d’un buffer sur la carte graphique
        gl.glGenBuffers(1, buffers, 0); // Allocation du buffer
        glposbuffer =buffers[0];

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, glposbuffer); // Activation du buffer alloué
        gl.glBufferData(
                GL2.GL_ARRAY_BUFFER,
                vertexpos.length * Float.BYTES,
                floatPosbuffer,
                GL2.GL_STATIC_DRAW // GL_STATIC_DRAW = les données sont téléchargées une seule fois
        );
    }
    
    private void allocateTriangleBuffer(){
        
        int length = ((this.type == Integer.BYTES) ? intTriangles.length : shortTriangles.length ) * this.type;
        ByteBuffer trianglebytebuf = ByteBuffer.allocateDirect(length);
        // Allocation mémoire du buffer (SIZEOF_FLOAT=4) !!
        trianglebytebuf.order(ByteOrder.nativeOrder()); // LITTLE_ENDIAN ou BIG_ENDIAN ?
        
        
        //tiangle
        int[] tribuffers = new int[1]; // Besoin d’un buffer sur la carte graphique
        gl.glGenBuffers(1, tribuffers, 0); // Allocation du buffer
        glelementbuffer =tribuffers[0];
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, glelementbuffer); // Activation du buffer alloué

        
        
        if (this.type == Short.BYTES) {
            ShortBuffer shortTrianglesbuffer = trianglebytebuf.asShortBuffer();
            // Transformation du buffer en un buffer de float
            shortTrianglesbuffer.put(shortTriangles);
            // copie des positions dans le buffer (en tenant compte de « endianness »)
            shortTrianglesbuffer.position(0); // Retour à la position 0 pour l’exploitation ultérieure du buffer

            gl.glBufferData(
                    GL2.GL_ELEMENT_ARRAY_BUFFER,
                    shortTriangles.length * this.type,
                    shortTrianglesbuffer,
                    GL2.GL_STATIC_DRAW
        );
        
        } else {
            IntBuffer intTrianglesbuffer = trianglebytebuf.asIntBuffer();
            // Transformation du buffer en un buffer de float
            intTrianglesbuffer.put(intTriangles);
            // copie des positions dans le buffer (en tenant compte de « endianness »)
            intTrianglesbuffer.position(0); // Retour à la position 0 pour l’exploitation ultérieure du buffer
        
            gl.glBufferData(
                GL2.GL_ELEMENT_ARRAY_BUFFER,
                intTriangles.length * this.type,
                intTrianglesbuffer,
                GL2.GL_STATIC_DRAW
            );
        
        }
    }
    
    private void allocateNormalsBuffer() {
       
        if (normals.length == 0)
            computeNormals();
        
        ByteBuffer nomrbytebuf = ByteBuffer.allocateDirect(normals.length * Float.BYTES);
        // Allocation mémoire du buffer (SIZEOF_FLOAT=4) !!
        nomrbytebuf.order(ByteOrder.nativeOrder()); // LITTLE_ENDIAN ou BIG_ENDIAN ?
        FloatBuffer normbuffer = nomrbytebuf.asFloatBuffer();
        // Transformation du buffer en un buffer de float
        normbuffer.put(normals);
        // copie des positions dans le buffer (en tenant compte de « endianness »)
        normbuffer.position(0); // Retour à la position 0 pour l’exploitation ultérieure du buffer

        
        int[] buffers = new int[1]; // Besoin d’un buffer sur la carte graphique
        gl.glGenBuffers(1, buffers, 0); // Allocation du buffer
        glnormalbuffer =buffers[0];
        
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, glnormalbuffer); // Activation du buffer alloué
        gl.glBufferData(
                GL2.GL_ARRAY_BUFFER,
                normals.length * Float.BYTES,
                normbuffer,
                GL2.GL_STATIC_DRAW
        );
        // Transfert des données entre les deux buffers
    }
    
    private void allocateTextureBuffer(){
        
        
        ByteBuffer texturebytebuf = ByteBuffer.allocateDirect(textures.length * Float.BYTES);
        // Allocation mémoire du buffer (SIZEOF_FLOAT=4) !!
        texturebytebuf.order(ByteOrder.nativeOrder()); // LITTLE_ENDIAN ou BIG_ENDIAN ?
        FloatBuffer floatTexturesBuffer = texturebytebuf.asFloatBuffer();
        // Transformation du buffer en un buffer de float
        floatTexturesBuffer.put(textures);
        // copie des positions dans le buffer (en tenant compte de « endianness »)
        floatTexturesBuffer.position(0); // Retour à la position 0 pour l’exploitation ultérieure du buffer
        
        int[] buffers = new int[1]; // Besoin d’un buffer sur la carte graphique
        gl.glGenBuffers(1, buffers, 0); // Allocation du buffer
        gltexturesbuffer =buffers[0];

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, gltexturesbuffer); // Activation du buffer alloué
        gl.glBufferData(
                GL2.GL_ARRAY_BUFFER,
                textures.length * Float.BYTES,
                floatTexturesBuffer,
                GL2.GL_STATIC_DRAW // GL_STATIC_DRAW = les données sont téléchargées une seule fois
        );
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,0);
    }
    
    private void computeNormals() {
        normals = new float[vertexpos.length];

        for (int i = 0; i < shortTriangles.length; i += 3) {
            Vec3f A = new Vec3f(vertexpos[3 * shortTriangles[i]], vertexpos[3 * shortTriangles[i] + 1], vertexpos[3 * shortTriangles[i] + 2]);
            Vec3f B = new Vec3f(vertexpos[3 * shortTriangles[i + 1]], vertexpos[3 * shortTriangles[i + 1] + 1], vertexpos[3 * shortTriangles[i + 1] + 2]);
            Vec3f C = new Vec3f(vertexpos[3 * shortTriangles[i + 2]], vertexpos[3 * shortTriangles[i + 2] + 1], vertexpos[3 * shortTriangles[i + 2] + 2]);

            Vec3f X = new Vec3f();
            Vec3f Y = new Vec3f();

            X.setSub(B, A);
            Y.setSub(C, A);

            Vec3f vec3f = new Vec3f();
            vec3f.setCrossProduct(X, Y);
            vec3f.normalize();

            for (int j = 0; j < 3; j++) {
                normals[3 * shortTriangles[i + j]] = vec3f.x;
                normals[3 * shortTriangles[i + j] + 1] = vec3f.y;
                normals[3 * shortTriangles[i + j] + 2] = vec3f.z;
            }
        }
    }
    
}
