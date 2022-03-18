/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_poitiers.dptinfo.algo3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import java.util.HashMap;



/**
 *
 * @author JUGURTHA
 */
public class Sphere  {
    
    private static Sphere coordInstance = null;
    private static Sphere subInstance = null;
 
    MyVBO faces;
    float[] vertexpos;
    short[] triangles;
    float[] textures;
    float radius;
    int nbRondelles;
    int nbQuartiers;
    float theta, phi;
    private short offset;
    private int trianglesnum;
    HashMap<MyPairMiddle, Short> myPairMiddleHashMap;
    Texture shpereTexture;
    
    final static short DEFAULT_NB_RONDELLES = 40;
    final static short DEFAULT_NB_QUARTIERS = 40;

    
    public static Sphere getSphereCoordInstance(final GL2 gl) {
        if (coordInstance == null) {
            coordInstance = new Sphere(gl, DEFAULT_NB_RONDELLES, DEFAULT_NB_QUARTIERS);
        }
        
        return coordInstance;
    }
    
    public static Sphere getSphereSubInstance(final GL2 gl) {
        if (subInstance == null) {
            subInstance = new Sphere(gl, 6);
        }
        
        return subInstance;
    }

    public Sphere(final GL2 gl, short nbRondelles, short nbQuartiers ){
        
        this.nbRondelles = nbRondelles;
        this.nbQuartiers = nbQuartiers;
        setVertexpos(nbQuartiers, nbRondelles);
        setTriangles(nbQuartiers, nbRondelles);
        setTextures();
        shpereTexture = MyGLRenderer.loadTexture(gl, "basketball.jpg");
        faces = new MyVBO(gl, vertexpos, triangles, textures);
    }
    
    private float[] setVertexpos(short nbQuartiers, short nbRondelles) {
        int size = nbQuartiers * nbRondelles * 3 * 2;
        vertexpos = new float[size];       
                
        int offset = 0;
        float phiStep = (float) (2 * Math.PI / nbQuartiers);
        float thetaStep = (float) (Math.PI / nbRondelles);
        
        for (int i = -nbQuartiers; i < nbQuartiers; i++) {
            theta = (float) (-Math.PI / 2 + i * thetaStep);

            for (int j = 0; j < nbRondelles; ++j) {
                phi = j * phiStep;
                vertexpos[offset++] = (float) (Math.cos(theta) * Math.cos(phi));
                vertexpos[offset++] = (float) (Math.sin(theta));
                vertexpos[offset++] = (float) (Math.cos(theta) * Math.sin(phi));
            }
        } 
        return vertexpos;
    }

    private short[] setTriangles(short nbQuartiers, short nbRondelles) {
        
        int size = (nbRondelles + 1) * (nbQuartiers + 1) * 2 * 3;
        triangles = new short[size];

        int pos = 0;
        short posX = 0;
        short posY = nbQuartiers;
        
        for (int i = 0; i <= nbRondelles; ++i)
            for (int j = 0; j <= nbQuartiers; ++j) {
                // First triangle
                triangles[pos++] = posX++;
                triangles[pos++]= posX;
                triangles[pos++]= posY;

                // Second Triangle
                triangles[pos++] = posY++;
                triangles[pos++]= posX;
                triangles[pos++]= posY;
            }

        return triangles;
    }
    
    private void setTextures() {
        textures = new float[(int) (2*(vertexpos.length/3))];
        int textCursor = 0;
        for (int i = 0; i < vertexpos.length; i+= 3) {
            textures[textCursor] = Math.abs(vertexpos[i+2]+1)/2;
            textures[textCursor+1] = Math.abs(vertexpos[i+1]+1)/2;
            textCursor += 2;
        }
    }

    void draw(GL2 gl, LightingShaders shaders, float[] cyan, float[] black) {
        faces.draw(gl, shaders, cyan, black, shpereTexture);
    }
    
    
    
    /******************************* subdivision ********************************/
    public Sphere(final GL2 gl, int sub) {
        shpereTexture = MyGLRenderer.loadTexture(gl, "beachball.jpg");
        myPairMiddleHashMap = new HashMap<>();
        offset = 6;
        trianglesnum = 0;
        
        vertexpos = new float[6 + 3 * (int) Math.pow(4, sub)];
        triangles = new short[3 * 8 * (int) Math.pow(4, sub - 1)];

        int firstOffset=-1;
        for (int i = 0 ; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                vertexpos[++firstOffset] = j == 0 ? (i == 0 ? 1 : -1) : 0;
                vertexpos[++firstOffset] = j == 1 ? (i == 0 ? 1 : -1) : 0;
                vertexpos[++firstOffset] = j == 2 ? (i == 0 ? 1 : -1) : 0;
            }
        }
        short[] firstTriangles = new short[] {
                0, 1, 2,
                1, 3, 2,
                3, 4, 2,
                4, 0, 2,
                5, 1, 0,
                5, 3, 1,
                5, 4, 3,
                5, 0, 4
        };

        if (sub > 1)
            for (int i = 0; i < firstTriangles.length; i++)
                subDiv(sub, firstTriangles[i], firstTriangles[++i], firstTriangles[++i]);
        else
            triangles = firstTriangles;
        
        setTextures();

        faces = new MyVBO(gl, vertexpos, triangles, vertexpos, textures);
    }

    private void subDiv(int sub, short a, short b, short c) {
        if (sub == 1) {
            triangles[trianglesnum++] = a;
            triangles[trianglesnum++] = b;
            triangles[trianglesnum++] = c;
        } else {
            short d = getMiddle(a, b);
            short e = getMiddle(b, c);
            short f = getMiddle(c, a);

            subDiv(sub - 1, a, d, f);
            subDiv(sub - 1, d, b, e);
            subDiv(sub - 1, f, e, c);
            subDiv(sub - 1, d, e, f);
        }
    }

    private short getMiddle(short v1, short v2) {
        MyPairMiddle middlekey = new MyPairMiddle(v1, v2);

        if (myPairMiddleHashMap.containsKey(middlekey))
            return myPairMiddleHashMap.get(middlekey);
        
        middlekey.setMiddle(vertexpos, offset);
        myPairMiddleHashMap.put(middlekey, offset);
        return offset++;
    }
    
    
    
}
