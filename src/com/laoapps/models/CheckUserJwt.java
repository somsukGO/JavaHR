package com.laoapps.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckUserJwt {
    String object;
    String method;
    JwtData data;

    public CheckUserJwt(String object, String method, String jwt) {
        this.object = object;
        this.method = method;
        this.data = new JwtData(jwt);
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class JwtData {
    String jwt;
}
