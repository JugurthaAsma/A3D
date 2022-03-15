precision mediump float;

uniform bool uIsMirror;

uniform mat4 uModelViewMatrix;
uniform mat4 uProjectionMatrix;

// vertex attributes
attribute vec3 aVertexPosition;
attribute vec3 aVertexNormal;
attribute vec2 aTexCoord;


//texture 
varying vec2 vTexCoord;
varying vec3 vVertexNormal;
varying vec4 vVertexPosition;


void main(void) {
    vTexCoord = aTexCoord;
    vVertexNormal = aVertexNormal;
    vVertexPosition = uModelViewMatrix * vec4(aVertexPosition, 1.0);

    gl_Position= uProjectionMatrix*vVertexPosition;
}
