package com.ifreeshare.tools;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ifreeshare.tools.finals.TokenPayloadKeys;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * json web token的工具类
 */
public class JwtUtils {

    private static final ObjectMapper mapper;


    public static void main(String[] args) {




      try {
        System.out.println(JwtUtils.getTokenData(JwtUtils.getToken("a")));
      } catch (Exception e) {
          e.printStackTrace();
      }

      //
  }
    /**
     *
     */
    public static final Algorithm algorithm ;

    public static String public_key_file = "/Users/fangyuan/ifreeshare_cloud/ifreeshare-parent/ifreeshare-tools/src/main/java/com/ifreeshare/tools/public.key";

    public static String private_key_file = "/Users/fangyuan/ifreeshare_cloud/ifreeshare-parent/ifreeshare-tools/src/main/java/com/ifreeshare/tools/private.key";

    static {
        //获取公钥
      RSAPublicKey rsaPublicKey = null;
      try {
        rsaPublicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile(public_key_file,"RSA");
      } catch (IOException e) {
        e.printStackTrace();
      }
      //获取私钥
        RSAPrivateKey rsaPrivateKey = null;

        try {
            rsaPrivateKey = (RSAPrivateKey) PemUtils.readPrivateKeyFromFile(private_key_file,"RSA");
        } catch (IOException e) {
            e.printStackTrace();
        }
        algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);

        mapper = JsonMapper.builder()
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .build();

    }

    public static String getToken(String payload){
        String token = "";
        try {
            Map<String,Object> payLoadMap = new HashMap<>();
            //当前只放入ID就好
            payLoadMap.put(TokenPayloadKeys.USER_ID, payload);
             token = JWT.create()
                    .withIssuer("ifreeshare")
                     .withPayload(payLoadMap)
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            exception.printStackTrace();
//            exception
            // Invalid Signing configuration / Couldn't convert Claims.
        }
        return token;
    }


    /**
     *
     * @return 部分用户信息
     */
    public static Map<String, Object> getTokenData(String token){
        DecodedJWT decodedJWT;
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    // specify an specific claim validations
                    .withIssuer("ifreeshare")
                    // reusable verifier instance
                    .build();
            decodedJWT = verifier.verify(token);
           Map<String, Claim> claims = decodedJWT.getClaims();
           Map<String, Object> stringObjectMap = new HashMap<>();
           for (String str : claims.keySet()) {
                stringObjectMap.put(str, claims.get(str).asString());
           }
           return stringObjectMap;
        } catch (JWTVerificationException exception){
            exception.printStackTrace();
            // Invalid signature/claims
        }
        return null;

    }






}
