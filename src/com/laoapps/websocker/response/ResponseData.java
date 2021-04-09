package com.laoapps.websocker.response;

import com.laoapps.database.entity.Department;
import com.laoapps.database.entity.Profiles;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ResponseData {
    private Object user;
    private String jwt;
    private Profiles profile;
    private Department department;
    private ArrayList<Department> departments;
    private int totalPage;
}
