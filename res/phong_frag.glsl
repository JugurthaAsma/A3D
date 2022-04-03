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
uniform vec4 uMaterialSpecular;
uniform vec4 uLightSpecular;
uniform vec3 uViewPos;

uniform float uConstantAttenuation;
uniform float uLinearAttenuation;
uniform float uQuadraticAttenuation;
uniform int uMaterialShininess;

varying vec2 vTexCoord;
varying vec3 vVertexNormal;
varying vec4 vVertexPosition;

void main(void) {
    
    vec4 texture = vec4(1, 1, 1, 1);
    if (uIsTexture) {
        texture = texture2D(uTextureUnit,vTexCoord);
    }

    if(uLighting){
        //normal
        vec3 normal = uNormalMatrix * vVertexNormal;
        if (uNormalizing) normal=normalize(normal);
        
        //vecteur directeur lumière & viewPersonnage
        vec3 lightdir=normalize(uLightPos - vVertexPosition.xyz);
        vec3 ViewDir=normalize(uViewPos - vVertexPosition.xyz);

        // attenuation
        float distance = length(uLightPos - vVertexPosition.xyz);
        float attenuation = 1.0f / (uConstantAttenuation + (uLinearAttenuation * distance) + (uQuadraticAttenuation * pow(distance, 2)));

        //speculaire
        vec3 halfwayDir = normalize(lightdir+ViewDir);
        vec4 specular = attenuation * (uLightSpecular * pow(max(dot(normal, halfwayDir),0.0),uMaterialShininess)* uMaterialSpecular);
        
        //diffusion
        float weight = max(dot(normal, lightdir),0.0);
        vec4 diffuse = weight*uLightColor;

        gl_FragColor = (uMaterialColor*texture)*(uAmbiantLight+diffuse) * attenuation + specular;
        
    }
    else{
        gl_FragColor = (uMaterialColor*texture);
    }

    gl_FragColor.a=uMaterialColor.a;
}
