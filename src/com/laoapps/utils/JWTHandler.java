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

    private final String SECRET_KEY = properties.getProperty(Naming.jwtSecretKey);
    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);

    public String jwtEncode(String phoneNumber, String uuid, String company) {

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuer(properties.getProperty("jwt.issuer"))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(6, ChronoUnit.HOURS)))
                .setSubject(phoneNumber)
                .signWith(signatureAlgorithm, apiKeySecretBytes)

                // payload
                .claim(Naming.phoneNumber, phoneNumber)
                .claim(Naming.UUID, uuid)
                .claim(Naming.company, company)

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
        String company = jsonObject.get(Naming.company).getAsString();

        try (Jedis jedis = new Jedis(Naming.HOST_NAME)) {

            if (!jwt.equals(jedis.hget(Naming.HR_JWT, phoneNumber))) return new CheckJwt(false, jwt, uuid, company);

            Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                    .requireIssuer(properties.getProperty("jwt.issuer"))
                    .parseClaimsJws(jwt);

            String newJwt = jwtEncode(phoneNumber, uuid, company);
            jedis.hset(Naming.HR_JWT, phoneNumber, newJwt);

            return new CheckJwt(true, jwt, uuid, company);

        } catch (SignatureException | IncorrectClaimException | MalformedJwtException e) {
            e.printStackTrace();
            MyCommon.printMessage(e.getMessage());
            return new CheckJwt(false, jwt, uuid, company);

        } catch (ExpiredJwtException e) {

            MyCommon.printMessage("generate new Jwt");

            String newJwt = jwtEncode(phoneNumber, uuid, company);
            try (Jedis jedis = new Jedis(Naming.HOST_NAME)) {
                jedis.hset(Naming.HR_JWT, phoneNumber, newJwt);
            }

            return new CheckJwt(true, newJwt, uuid, company);

        }
    }
}
