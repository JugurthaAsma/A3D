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
public class Ball {
    
    public enum SphereType {subdivision, coordinate};
    
    Matrix4 matrix;
    private static Sphere sphereCoord = null;
    private static Sphere sphereSub = null;
    float[] color;
    float x,z,radius;
    SphereType type;
    
    public Ball (GL2 gl, float x, float z, float radius, float[] color, SphereType type) {
        this.x = x;
        this.z = z;
        this.radius = radius;
        this.type = type;
        this.color = color;
        short nbRondelles = 40;
        short nbQuartiers = 40;
        
        switch (type) {
            case coordinate : if (sphereCoord == null) sphereCoord = new Sphere(gl, nbRondelles, nbQuartiers) ; break;
            case subdivision : if (sphereSub == null) sphereSub = new Sphere(gl, 4) ; break;
        }
        
    }

    public void draw(final GL2 gl,final /*NoLightShaders*/ LightingShaders shaders, Scene scene, float step, Matrix4 modelMatrix, float rebound) {
        
        matrix = new Matrix4();
        matrix.loadIdentity();
        
        if (modelMatrix != null) { 
            matrix.multMatrix(modelMatrix);
            matrix.translate(x, radius, z); //translation par rapport à la vue
            matrix.rotate(step * 10, 0.0F, -0.9F, 0.0F); // rotation sur soit meme
            matrix.scale(radius,radius,radius);
            
            shaders.setModelViewMatrix(matrix.getMatrix());
            
        } else { 
            
            matrix.rotate(scene.anglex, -0.1F, 0.F, 0.0F);
            matrix.rotate(scene.angley, 0.0F, -0.1F, 0.0F);//rotation par rapport à la vue
            if (rebound != 0) {
                matrix.translate(scene.x + x + scene.objX, scene.y + radius + 2f + scene.objY - Math.abs(rebound / 3), scene.z + z + rebound + scene.objZ);//translation par rapport à la vue
            } else {
                matrix.translate(scene.x + x, scene.y + radius,scene.z + z);//translation par rapport à la vue
            }
            matrix.rotate(step, 0.0F, -0.5F, 0.0F); // rotation sur soit meme
            matrix.scale(radius,radius,radius);
            shaders.setModelViewMatrix(matrix.getMatrix());
        }
        
        switch (type) {
            case coordinate : sphereCoord.draw(gl, shaders, color, MyGLRenderer.black); break;
            case subdivision : sphereSub.draw(gl, shaders, color, MyGLRenderer.black); break;
        }
    }
   
    public void draw(final GL2 gl,final /*NoLightShaders*/ LightingShaders shaders, Scene scene, float step, Matrix4 modelMatrix) {
        this.draw(gl, shaders, scene, step, modelMatrix, 0);
    }
    
}
