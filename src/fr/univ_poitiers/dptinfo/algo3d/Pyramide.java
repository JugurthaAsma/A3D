  /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_poitiers.dptinfo.algo3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.Matrix4;

/**
 *
 * @author JUGURTHA
 */
public class Pyramide {
    
    MyVBO faces;
    float[] vertexpos;
    short[] triangles;
    
    float lenght, width, height;
    
    public Pyramide(GL2 gl, float lenght, float width, float height){
        
        this.lenght= lenght;
        this.width= width; 
        this.height = height;
        
        vertexpos = new float[]{
            
            // base
            -width/2, 0, lenght/2,  // a
            width/2, 0, lenght/2,   // b
            width/2, 0, -lenght/2,  // c
            -width/2, 0, -lenght/2, // d
            
            // sommet
            0, height, 0,           // s
            
            // face 1 
            0, height, 0,           // s
            width/2, 0, lenght/2,   // b
            -width/2, 0, lenght/2,  // a
            
            
            // face 2
            0, height, 0,           // s
            width/2, 0, -lenght/2,  // c
            width/2, 0, lenght/2,   // b

            
            // face 3
            0, height, 0,           // s
            -width/2, 0, -lenght/2, // d
            width/2, 0, -lenght/2,  // c
            
            // face 4
            0, height, 0,           // s
            -width/2, 0, lenght/2,  // a
            -width/2, 0, -lenght/2, // d
            
            
        };
        
        
        triangles = new short[] {
            
            //base
            0, 1, 2,
            0, 2, 3,
              
//            // faces
//            5, 6, 7,
//            8, 9, 10,
//            11, 12, 13,
//            14, 15, 16,
//            
            7, 6, 5,
            10, 9, 8,
            13, 12, 11,
            16, 15, 14,
        };
            
        faces = new MyVBO(gl, vertexpos, triangles);
    }
    
    public void draw(final GL2 gl,final LightingShaders shaders, Matrix4 medelViewMatrix, float step, final float[] color) {
        
        Matrix4 matrix = new Matrix4();
        matrix.multMatrix(medelViewMatrix);
        matrix.translate( - 2 ,0 , 0);
        
        // rotation sur soit meme
        matrix.rotate(step * 5, 0.0F, -0.1F, 0.0F); 
        
        //matrix.scale(0.2f, 0.2f, 0.2f);
        
        shaders.setModelViewMatrix(matrix.getMatrix());
        //shaders.setColor(color);
        faces.draw(gl, shaders, GL2.GL_TRIANGLES, color, MyGLRenderer.black, null);
    }
}
