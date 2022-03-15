/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_poitiers.dptinfo.algo3d;

/**
 *
 * @author JUGURTHA
 */
public class MyPairMiddle  {
    
    short v1, v2;
    
    public MyPairMiddle(short v1, short v2) {
        
        this.v1 = v1 < v2 ? v1 : v2;
        this.v2 = v1 < v2 ? v2 : v1;
                
    }

    public void setMiddle(float[] vertexpos, int offset) {
        for (int i = 0; i < 3; i++)
            vertexpos[3 * offset + i] = 0.5F * (vertexpos[3 * v1 + i] + vertexpos[3 * v2 + i]);
        
        float norme = (float) (1.0F / Math.sqrt(Math.pow(vertexpos[3 * offset], 2) + Math.pow(vertexpos[3 * offset + 1], 2) + Math.pow(vertexpos[3 * offset + 2], 2)));

        for (int i = 0; i < 3; i++)
            vertexpos[3 * offset + i] *= norme;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof MyPairMiddle))
            return false;
        
        MyPairMiddle p = (MyPairMiddle) obj;
        return (v1 == p.v1 && v2 == p.v2); 
    }
    
    @Override
    public int hashCode() { 
        return (v1 + " , " + v2).hashCode();
    }
}
