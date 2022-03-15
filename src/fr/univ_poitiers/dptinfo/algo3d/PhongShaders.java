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
public class PhongShaders extends LightingShaders {

    public PhongShaders(MyGLRenderer renderer) {
        super(renderer);
    }

    @Override
    public int createProgram() {
        return initializeShadersFromResources(gl,"phong_vert.glsl","phong_frag.glsl");
    }
    
}
