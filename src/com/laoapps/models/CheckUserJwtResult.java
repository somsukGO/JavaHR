package com.laoapps.models;

import com.google.gson.JsonObject;
import com.laoapps.utils.Naming;
import lombok.Data;

@Data
public class CheckUserJwtResult {
    private boolean isPass;
    private String jwt;
    private String uuid;
    private String phoneNumber;

    public CheckUserJwtResult(JsonObject jsonObject) {
        JsonObject body = jsonObject.getAsJsonObject(Naming.body);
        isPass = body.get(Naming.status).getAsInt() == 1;

        if (isPass) {
            JsonObject data = body.getAsJsonObject(Naming.data);
            JsonObject checkJwt = data.getAsJsonObject(Naming.checkJwt);
            jwt = checkJwt.get(Naming.jwt).getAsString();
            uuid = checkJwt.get(Naming.UUID).getAsString();
            phoneNumber = checkJwt.get(Naming.phoneNumber).getAsString();
        }

    }
}
