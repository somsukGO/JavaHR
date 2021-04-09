package com.laoapps.utils;

import com.google.gson.JsonObject;
import com.laoapps.models.CheckJwt;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.websocker.response.Response;
import com.laoapps.websocker.response.UtilsResponse;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MyCommon {
    private MyCommon() {
    }

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String currentTime() {
        Date date = new Date();
        return format.format(date);
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public static String generateSignature() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }

    public static void printMessage(String message) {
        System.out.println("-".repeat(25));
        System.out.println(message);
        System.out.println("-".repeat(25));
    }

    private static final JWTHandler jwtHandler = JWTHandler.getInstance();

    public static CheckJwtResult checkJwtResult(JsonObject data, String object, String method) {

        CheckJwtResult checkJwtResult = new CheckJwtResult();
        checkJwtResult.setPass(false);

        if (!data.has(Naming.jwt)) {
            Response jwtFieldNotExistsResponse = new Response(new UtilsResponse(object, method, Naming.fail, "Jwt field not exists", null));
            checkJwtResult.setResponse(jwtFieldNotExistsResponse);
            return checkJwtResult;
        }

        CheckJwt checkJwt = new CheckJwt();
        try {
            checkJwt = jwtHandler.jwtValidation(data.get(Naming.jwt).getAsString());
        } catch (Exception e) {
            if (!checkJwt.isValid()) {
                Response jwtNotValid = new Response(new UtilsResponse(object, method, Naming.fail, "Authentication fail", null));
                checkJwtResult.setResponse(jwtNotValid);
                return checkJwtResult;
            }
        }

        checkJwtResult.setPass(true);
        checkJwtResult.setCheckJwt(checkJwt);

        return checkJwtResult;
    }

}
