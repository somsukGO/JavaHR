package com.laoapps.websocker.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBody {
    private String object;
    private String method;
    private int status;
    private String message;
    private Object data;
}
