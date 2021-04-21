package com.laoapps.utils;

import com.google.gson.JsonObject;
import com.laoapps.models.CheckJwt;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class MyCommon {
    private MyCommon() {
    }

    private static final SimpleDateFormat currentTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public static String currentTime() {
        Date date = new Date();
        return currentTimeFormat.format(date);
    }

    public static String date() {
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String time() {
        Date date = new Date();
        return timeFormat.format(date);
    }

    public static int betweenMinutes(String startTime, String endTime) {
        String[] startTimeSplit = startTime.split(":");
        String[] endTimeSplit = endTime.split(":");

        LocalTime time1 = LocalTime.of(Integer.parseInt(startTimeSplit[0]), Integer.parseInt(startTimeSplit[1]), Integer.parseInt(startTimeSplit[2]));
        LocalTime time2 = LocalTime.of(Integer.parseInt(endTimeSplit[0]), Integer.parseInt(endTimeSplit[1]), Integer.parseInt(endTimeSplit[2]));

        return (int) ChronoUnit.MINUTES.between(time1, time2);
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
            Response jwtFieldNotExistsResponse = new Response(new ResponseBody(object, method, Naming.fail, "jwt field not exists", null));
            checkJwtResult.setResponse(jwtFieldNotExistsResponse);
            return checkJwtResult;
        }

        CheckJwt checkJwt = jwtHandler.jwtValidation(data.get(Naming.jwt).getAsString());

        if (!checkJwt.isValid()) {
            Response jwtNotValid = new Response(new ResponseBody(object, method, Naming.fail, "authentication fail", null));
            checkJwtResult.setResponse(jwtNotValid);
            return checkJwtResult;
        }

        checkJwtResult.setPass(true);
        checkJwtResult.setCheckJwt(checkJwt);

        return checkJwtResult;
    }

    public static ArrayList<Object> getKeyword(JsonObject data) {

        String keyword = null;
        int keywordId = 0;

        if (data.has(Naming.keyword)) {
            if (!data.get(Naming.keyword).getAsString().isBlank()) {
                keyword = data.get(Naming.keyword).getAsString();
                try {
                    keywordId = Integer.parseInt(keyword);
                } catch (Exception e) {
                    keywordId = 0;
                }
            }
        }

        return new ArrayList<>(Arrays.asList(keyword, keywordId));

    }

}
