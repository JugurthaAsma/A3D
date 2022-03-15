precision mediump float;

uniform bool uLighting;
uniform bool uNormalizing;
uniform bool uIsTexture;
uniform sampler2D uTextureUnit;
uniform mat3 uNormalMatrix;
uniform vec4 uMaterialColor;

uniform vec3 uLightPos;
uniform vec4 uAmbiantLight;
uniform vec4 uLightColor;

// Only color is interpolated
varying vec3 vVertexNormal;
varying vec4 vVertexPosition;
varying vec3 vLightPos;
varying vec4 vMaterialColor;
varying vec4 vAmbiantLight;
varying vec4 vLightColor;
varying vec2 vTexCoord;

void main(void) {
    if(uIsTexture){
        vec3 normal = uNormalMatrix * vVertexNormal;
        if (uNormalizing) normal=normalize(normal);
        vec3 lightdir=normalize(uLightPos - vVertexPosition.xyz);
        float weight = max(dot(normal, lightdir),0.0);
        if (uLighting) {
            gl_FragColor = (uMaterialColor*texture2D(uTextureUnit,vTexCoord))*(uAmbiantLight+weight*uLightColor);
        }else{
            gl_FragColor = uMaterialColor*texture2D(uTextureUnit,vTexCoord);
        }
    } else {
        gl_FragColor = uMaterialColor;
    }
}