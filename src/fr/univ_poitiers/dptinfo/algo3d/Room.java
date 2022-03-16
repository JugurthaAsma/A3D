/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_poitiers.dptinfo.algo3d;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


/**
 *
 * @author JUGURTHA
 */
public class Room {
    
    float[] floorVertexPos;
    
    int glposbuffer;
    int glelementbuffer;
    short[] wallsTsriangles;
    short[] ceillingTriangles;
    short[] floorTriangles;
    short[] bordersLines;
    float[] vertexpos;
    float[] textures;
    
    FloatBuffer posbuffer;
    
    MyVBO floorVbo;
    MyVBO ceillingVbo;
    MyVBO wallsVbo;
    MyVBO bordersVbo;
    
    
    // door
    // left quad
    Vec3f l1 = new Vec3f(-0.5f, 0f, -Scene.wallsize /*-3f*/);
    Vec3f l2 = new Vec3f(-0.5f, 1.5f, -Scene.wallsize /*-3f*/);
    Vec3f l3 = new Vec3f(-0.5f, Scene.wallheight, -Scene.wallsize /*-3f*/);
    
    //right quad
    Vec3f r1 = new Vec3f(0.5f, 0f, -Scene.wallsize /*-3f*/);
    Vec3f r2 = new Vec3f(0.5f, 1.5f, -Scene.wallsize /*-3f*/);
    Vec3f r3 = new Vec3f(0.5f, Scene.wallheight, -Scene.wallsize /*-3f*/);
    
    public Room(final GL2 gl, final Vec3f a1, final Vec3f b1, final Vec3f c1, final Vec3f d1, final Vec3f a2, final Vec3f b2, final Vec3f c2, final Vec3f d2, Texture texture){
        vertexpos = new float[]{
            // frontWall
            
            //left
            a2.x, a2.y, a2.z, // 0 (a2)
            l1.x, l1.y,l1.z,  // 1 
            l3.x, l3.y,l3.z,  // 2 
            d2.x, d2.y, d2.z, // 3 (d2)
            
            //right
            r1.x, r1.y,r1.z,  // 4 
            b2.x, b2.y, b2.z, // 5 (b2)
            c2.x, c2.y, c2.z, // 6 (c2)
            r3.x, r3.y,r3.z,  // 7 
            
            // backWall
            a1.x, a1.y, a1.z, // 8 (a1)
            b1.x, b1.y, b1.z, // 9 (b1)
            c1.x, c1.y, c1.z, // 10 (c1)
            d1.x, d1.y, d1.z, // 11 (d1)
            
            // righttWall
            b2.x, b2.y, b2.z, // 12  (b2)
            b1.x, b1.y, b1.z, // 13  (b1)
            c1.x, c1.y, c1.z, // 14 (c1)
            c2.x, c2.y, c2.z, // 15 (c2)
            
            // b1 c1 , c1 c2, c2 b2, b2 b1
            //13,14,  14,15,  15,12,  12,13

            // lefttWall
            a1.x, a1.y, a1.z, // 16 (a1)
            a2.x, a2.y, a2.z, // 17 (a2)
            d2.x, d2.y, d2.z, // 18 (d2)
            d1.x, d1.y, d1.z, // 19 (d1)
            
            // floor
            a1.x, a1.y, a1.z, // 20 (a1)
            b1.x, b1.y, b1.z, // 21 (b1)
            b2.x, b2.y, b2.z, // 22 (b2)
            a2.x, a2.y, a2.z, // 23 (a2)
            
            // ceilling
            d1.x, d1.y, d1.z, // 24 (d1)
            c1.x, c1.y, c1.z, // 25 (c1)
            c2.x, c2.y, c2.z, // 26 (c2)
            d2.x, d2.y, d2.z, // 27 (d2)
            
            // upper
            l2.x, l2.y,l2.z,  // 28 
            r2.x, r2.y,r2.z,  // 29 
            r3.x, r3.y,r3.z,  // 30 
            l3.x, l3.y,l3.z,  // 31
        };
        
        wallsTsriangles = new short[]{
            
            // frontWall
            // left
            0,1,2,
            3,0,2,
            
            // right
            4,5,6,
            7,4,6,
            
            // upper
            28,29,30,
            31,28,30,
            
            
            // righttWall
            14,15,13,
            13,15,12,

            // lefttWall
            17,19,16,
            18,19,17,
            
            // backWall
            9,8,10,
            11,10,8,
        };
        
        ceillingTriangles = new short[]{
            27,26,24,
            26,25,24,
        };
        
        floorTriangles = new short[] {
            23,20,22,
            22,20,21,
        };
        
        bordersLines = new short[] {
            // front wall
            0,1,
            1,28,
            28,29,
            29,4,
            4,5,
            5,6,
            6,3,
            3,0,
            
            // backWall
            8,9,
            9,10,
            10,11,
            11,8,
           
            // left wall
            16,17,
            18,19,
          
            // right wall
            12,13,
            14,15,
            
        };
        
        textures = new float[] {
            // frontWall
            
            //left
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            //right
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            // backWall
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            // righttWall
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            // b1 c1 , c1 c2, c2 b2, b2 b1
            //13,14,  14,15,  15,12,  12,13

            // lefttWall
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            // floor
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            // ceilling
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            // upper
            0.5F, 0,
            0, 0,
            0, 0.5F,
            0.5F, 0.5F,
            /*
            0.417F,1.F,//-0.5F, 0.F, -3.F,//20- p8
            0.584F,1.F,//0.5F, 0.F, -3.F,//21 - p9
            0.584F,0.2F,//p10//0.5F, 2.F, -3.F,//22 - p10
            0.417F, 0.2F,//p11//-0.5F, 2.F, -3.F,//23 - p11
            
            */
        };
        
        
        
        floorVbo = new MyVBO(gl, vertexpos, floorTriangles, texture, textures);
        ceillingVbo = new MyVBO(gl, vertexpos, ceillingTriangles, texture, textures);
        wallsVbo = new MyVBO(gl, vertexpos, wallsTsriangles, texture, textures);
        
        //bordersVbo = new MyVBO(gl, vertexpos, bordersLines);
        

    }
    
    public void draw(final GL2 gl,final /*NoLightShaders*/ LightingShaders shaders, final float[] colorCeilling, final float[] colorFloor, final float[] colorWalls, final float[] linesColor){  
        
        //drawBorders(gl, shaders, MyGLRenderer.black);
        drawCeilling(gl, shaders, colorCeilling, linesColor);
        drawFloor(gl, shaders, colorFloor, linesColor);
        drawWalls(gl, shaders, colorWalls, linesColor); 
        
   
    }
    
    public void draw(final GL2 gl,final /*NoLightShaders*/ LightingShaders shaders, final float[] colorCeilling, final float[] colorFloor, final float[] colorWalls) {
        this.draw(gl, shaders, colorCeilling, colorFloor, colorWalls, null);
    }
    
    public void drawCeilling(final GL2 gl,final /*NoLightShaders*/ LightingShaders shaders, final float[] color, final float[] linesColor) {
        
        //shaders.setColor(color);
        ceillingVbo.draw(gl, shaders, GL2.GL_TRIANGLES, color, linesColor);
    }
    
    public void drawFloor(final GL2 gl,final /*NoLightShaders*/ LightingShaders shaders, final float[] color, final float[] linesColor) {
        //shaders.setColor(color);
        floorVbo.draw(gl, shaders, GL2.GL_TRIANGLES, color, linesColor);
    }
    
    public void drawWalls(final GL2 gl,final /*NoLightShaders*/ LightingShaders shaders, final float[] color, final float[] linesColor) {
        //shaders.setColor(color);
        wallsVbo.draw(gl, shaders, GL2.GL_TRIANGLES, color, linesColor);
    }

    public void drawBorders(final GL2 gl,final /*NoLightShaders*/ LightingShaders shaders, final float[] color) {
        //gl.glLineWidth(3);
        //shaders.setColor(color);
        bordersVbo.draw(gl, shaders, GL2.GL_LINES, color, null);
    }
}
