precision mediump float;

uniform bool uLighting;
uniform bool uNormalizing;
uniform bool uIsTexture;

uniform sampler2D uTextureUnit;

uniform mat3 uNormalMatrix;

uniform vec3 uLightPos;

uniform vec4 uAmbiantLight;
uniform vec4 uLightColor;
uniform vec4 uMaterialColor;
uniform vec4 uSpecularColor;
uniform vec4 uLightSpecular;
uniform vec3 uViewPos;

uniform float uConstantAttenuation;
uniform float uLinearAttenuation;
uniform float uQuadraticAttenuation;

uniform int uShininess;

// Only color is interpolated
varying vec2 vTexCoord;
varying vec3 vVertexNormal;
varying vec3 vLightPos;
varying vec4 vVertexPosition;
varying vec4 vMaterialColor;
varying vec4 vAmbiantLight;
varying vec4 vLightColor;


void main(void) {
    //normal
    vec3 normal = uNormalMatrix * vVertexNormal;
    if (uNormalizing){
         normal=normalize(normal);
    }

    //vecteur directeur lumiÃ¨re & viewPersonnage
    vec3 lightdir=normalize(uLightPos - vVertexPosition.xyz);
    vec3 ViewDir=normalize(uViewPos - vVertexPosition.xyz);

    //speculaire
    vec3 halfwayDir = normalize(lightdir+ViewDir);
    vec4 specular =uLightSpecular * pow(max(dot(normal, halfwayDir),0.0),uShininess)* uSpecularColor;

    //diffusion
    float weight = max(dot(normal, lightdir),0.0);
    vec4 diffuse = weight*uLightColor;

    if (uIsTexture)
    {
        if(uLighting){
            gl_FragColor = (uMaterialColor*texture2D(uTextureUnit,vTexCoord))*(uAmbiantLight+diffuse)+ specular;
            gl_FragColor.a=uMaterialColor.a;
        }
        else{
            gl_FragColor = (uMaterialColor*texture2D(uTextureUnit,vTexCoord));
            gl_FragColor.a=uMaterialColor.a;
        }
    }else{
        if(uLighting){
            gl_FragColor = uMaterialColor*(uAmbiantLight+ diffuse)+ specular;
            gl_FragColor.a=uMaterialColor.a;
        }
        else{
            gl_FragColor = uMaterialColor;
            gl_FragColor.a=uMaterialColor.a;
        }
    }
}
