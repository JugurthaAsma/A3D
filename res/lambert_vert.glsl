precision mediump float;

uniform mat4 uModelViewMatrix;
uniform mat4 uProjectionMatrix;


attribute vec3 aVertexPosition;
attribute vec3 aVertexNormal;
attribute vec2 aTexCoord;

// for each pixel
varying vec4 vVertexPosition;
varying vec3 vVertexNormal;
varying vec2 vTexCoord;

void main(void) {

    vTexCoord = aTexCoord;
    vVertexPosition = uModelViewMatrix * vec4(aVertexPosition, 1.0);
    vVertexNormal = aVertexNormal;

    gl_Position= uProjectionMatrix * vVertexPosition;
}