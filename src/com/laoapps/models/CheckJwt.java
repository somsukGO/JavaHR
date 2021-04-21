package com.laoapps.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckJwt {
    private boolean isValid;
    private String jwt, uuid, company;
}