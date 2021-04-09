package com.laoapps.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.models.CheckJwt;
import io.jsonwebtoken.*;
import redis.clients.jedis.Jedis;

import javax.xml.bind.DatatypeConverter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

public class JWTHandler {

    // encode algorithm
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private final String SECRET_KEY = "mySecretKey";
    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);

    private JWTHandler() {
    }

    private static JWTHandler jwtHandler = null;

    public static JWTHandler getInstance() {
        if (jwtHandler == null) {
            jwtHandler = new JWTHandler();
        }

        return jwtHandler;
    }

    private final Gson gson = new Gson();

    private final Properties properties = UtilConfig.getProperties();

    public String jwtEncode(String phoneNumber, String uuid, String parent) {

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuer(properties.getProperty("jwt.issuer"))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                .setSubject(phoneNumber)
                .signWith(signatureAlgorithm, apiKeySecretBytes)

                // payload
                .claim(Naming.phoneNumber, phoneNumber)
                .claim(Naming.UUID, uuid)
                .claim(Naming.PARENT, parent)

                .compact();
    }

    public String jwtDecodePayload(String jwt) {
        return new String(Base64.getUrlDecoder().decode(jwt));
    }

    public CheckJwt jwtValidation(String jwt) {

        String payloadDecode = jwtDecodePayload(jwt.split("\\.")[1]);
        JsonObject jsonObject = gson.fromJson(payloadDecode, JsonObject.class);

        String phoneNumber = jsonObject.get(Naming.phoneNumber).getAsString();
        String uuid = jsonObject.get(Naming.UUID).getAsString();
        String parent = jsonObject.get(Naming.PARENT).getAsString();

        try (Jedis jedis = new Jedis(Naming.HOST_NAME)) {

            if (!jedis.hexists(Naming.HR_JWT, phoneNumber)) {
                String message = "Jwt not exists";
                MyCommon.printMessage(message);
                return new CheckJwt(false, jwt, uuid, parent);
            }

            Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                    .requireIssuer(properties.getProperty("jwt.issuer"))
                    .parseClaimsJws(jwt);

            return new CheckJwt(true, jwt, uuid, parent);

        } catch (SignatureException | IncorrectClaimException | MalformedJwtException e) {
            MyCommon.printMessage(e.getMessage());
            return new CheckJwt(false, jwt, uuid, parent);

        } catch (ExpiredJwtException e) {

            MyCommon.printMessage("Generate new Jwt");

            String newJwt = jwtEncode(phoneNumber, uuid, parent);
            try (Jedis jedis = new Jedis(Naming.HOST_NAME)) {
                jedis.hset(Naming.HR_JWT, phoneNumber, newJwt);
            }

            return new CheckJwt(true, newJwt, uuid, parent);

        }
    }
}
