package com.laoapps.models;

import com.laoapps.websocker.response.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckJwtResult {
    private boolean isPass;
    private CheckJwt checkJwt;
    private Response response;
}
