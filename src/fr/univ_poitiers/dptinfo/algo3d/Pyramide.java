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
            -width/2, 0, lenght/2,
            width/2, 0, lenght/2,
            width/2, 0, -lenght/2,
            -width/2, 0, -lenght/2,
            
            // sommet
            0, height, 0,
        };
        
        
        triangles = new short[] {
            
            //base
            0, 1, 2,
            0, 2, 3,
            
            // faces
            0, 1, 4,
            3, 0, 4,
            2, 3, 4,
            1, 2, 4
        };
            
        faces = new MyVBO(gl, vertexpos, triangles);
    }
    
    public void draw(final GL2 gl,final LightingShaders shaders, Scene scene, float step, final float[] color) {
        
        Matrix4 matrix = new Matrix4();
        
         // fixer sur un point (par rapport a la vue)
        matrix.rotate(scene.anglex, -0.1F, 0.F, 0.0F);
        matrix.rotate(scene.angley, 0.0F, -0.1F, 0.0F);
        matrix.translate(scene.x - 2 ,scene.y , scene.z);
        
        // rotation sur soit meme
        matrix.rotate(step * 5, 0.0F, -0.1F, 0.0F); 
        
        //matrix.scale(0.2f, 0.2f, 0.2f);
        
        shaders.setModelViewMatrix(matrix.getMatrix());
        //shaders.setColor(color);
        faces.draw(gl, shaders, GL2.GL_TRIANGLES, color, MyGLRenderer.black, null);
    }
}
