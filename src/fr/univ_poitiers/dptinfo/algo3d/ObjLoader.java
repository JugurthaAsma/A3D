/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_poitiers.dptinfo.algo3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.Matrix4;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author JUGURTHA
 */
public class ObjLoader{ 
    
    MyVBO shape;
    
    protected int glposbuffer;
    protected int glelementtribuffer;
    
    protected int nbTriangle;
    
    protected float[] vertexpos;
    protected int[] triangles;
    protected float[] normals;
    
    float x, y, z, scale;
        
    public ObjLoader(GL2 gl, String objName, float scale, float x, float y, float z){
        
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
        
        ArrayList<Float> vertexposArList = new ArrayList<>();
        ArrayList<Integer> trianglesArList = new ArrayList<>();
        ArrayList<Float> normalsArList = new ArrayList<>();
        
        try{
            // Le fichier d'entrée
            BufferedReader br = new BufferedReader(new InputStreamReader(ObjLoader.class.getResourceAsStream("./objs/" + objName))); 

            String line;
            while((line = br.readLine()) != null) {
                String[] dataLine = line.split(" "); 
                if(line.startsWith("v ")){
                    vertexposArList.add(Float.parseFloat(dataLine[1]));
                    vertexposArList.add(Float.parseFloat(dataLine[2]));
                    vertexposArList.add(Float.parseFloat(dataLine[3]));
                }else if(line.startsWith("f ")){
                    String[] sommet1 = dataLine[1].split("/");
                    String[] sommet2 = dataLine[2].split("/");
                    String[] sommet3 = dataLine[3].split("/");
                    trianglesArList.add(Integer.parseInt(sommet1[0])-1);
                    trianglesArList.add(Integer.parseInt(sommet2[0])-1);
                    trianglesArList.add(Integer.parseInt(sommet3[0])-1);
                } else if(line.startsWith("vn ")) {
                    normalsArList.add(Float.parseFloat(dataLine[1]));
                    normalsArList.add(Float.parseFloat(dataLine[2]));
                    normalsArList.add(Float.parseFloat(dataLine[3]));
                }
            }
            
            vertexpos = new float[vertexposArList.size()];
            for (int i = 0; i < vertexpos.length; i++) {
               vertexpos[i] = vertexposArList.get(i);
            }

            triangles = new int[trianglesArList.size()];
            for (int i = 0; i < triangles.length; i++) {
               triangles[i] = trianglesArList.get(i);
            }
            
            normals = new float[normalsArList.size()];
            for (int i = 0; i < normals.length; i++) {
               normals[i] = normalsArList.get(i);
            }
            
        }catch(IOException e){
            e.printStackTrace();
        }      
        
        shape = new MyVBO(gl, vertexpos, triangles, normals);
      
    }
    
    public void draw(final GL2 gl, final LightingShaders shaders, Matrix4 modelMatrix, float rotationAngle, float[] color, Scene scene){
        Matrix4 matrix = new Matrix4();
        matrix.loadIdentity();
        matrix.multMatrix(modelMatrix);
        matrix.translate(this.x + scene.objX, this.y + scene.objY, this.z + scene.objZ);
        matrix.scale(this.scale, this.scale, this.scale);
        matrix.rotate(rotationAngle, 0f, 1f, 0f);
        shaders.setModelViewMatrix(matrix.getMatrix());
        shape.draw(gl, shaders, color, MyGLRenderer.black, null);
    }
    
    public void draw(final GL2 gl,final LightingShaders shaders,float[] color,boolean drawTriangle){
        shape.draw(gl, shaders, color);
    }
}
