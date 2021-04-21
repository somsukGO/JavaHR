package com.laoapps.socket.response;

import com.laoapps.database.entity.Attendance;
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
    private Attendance attendance;
    private ArrayList<Department> departments;
    private ArrayList<Attendance> attendances;
    private Integer totalPage;
    private Integer totalElement;
    private Integer currentPage;
    private Integer limit;
}
