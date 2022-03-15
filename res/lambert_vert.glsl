precision mediump float;

uniform mat4 uModelViewMatrix;
uniform mat4 uProjectionMatrix;
uniform mat3 uNormalMatrix;

attribute vec3 aVertexPosition;
attribute vec3 aVertexNormal;

attribute vec2 aTexCoord;

// Only color is interpolated
varying vec3 vVertexPosition;
varying vec3 vVertexNormal;
varying vec2 vTexCoord;

void main(void) {
    vTexCoord = aTexCoord;
    vec4 pos= uModelViewMatrix* vec4(aVertexPosition, 1.0);
    vVertexPosition = pos;
    vVertexNormal = aVertexNormal;
    gl_Position= uProjectionMatrix*pos;
}