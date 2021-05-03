package com.laoapps.socket.response;

import com.laoapps.database.entity.*;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ResponseData {
    private Object user;
    private String jwt;
    private Profiles profile;
    private Department department;
    private Attendance attendance;
    private Companies company;
    private ArrayList<Department> departments;
    private ArrayList<Attendance> attendances;
    private ArrayList<Invite> invites;
    private ArrayList<Companies> companies;
    private Integer totalPage;
    private Integer totalElement;
    private Integer currentPage;
    private Integer limit;
    private String companyJwt;
    private Invite invite;
}
