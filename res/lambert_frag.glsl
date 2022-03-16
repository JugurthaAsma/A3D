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

// for each pixel
varying vec3 vVertexNormal;
varying vec4 vVertexPosition;
varying vec2 vTexCoord;

void main(void) {

    vec4 texture = vec4(1, 1, 1, 1);
    if (uIsTexture) {
        texture = texture2D(uTextureUnit,vTexCoord);
    }

    if(uLighting){
        vec3 normal = uNormalMatrix * vVertexNormal;
        if (uNormalizing) normal=normalize(normal);
        vec3 lightdir=normalize(uLightPos - vVertexPosition.xyz);
        float weight = max(dot(normal, lightdir),0.0);
        gl_FragColor = (uMaterialColor*texture)*(uAmbiantLight+weight*uLightColor);
    }else{
        gl_FragColor = uMaterialColor*texture;
    }
    
}