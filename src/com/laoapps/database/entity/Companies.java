package com.laoapps.database.entity;

import com.laoapps.utils.Naming;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = Naming.COMPANIES_TABLE_NAME)
public class Companies {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Naming.ID)
    private int id;

    @Column(name = Naming.NAME)
    private String name;

    @Column(name = Naming.DESCRIPTION)
    private String description;

    @Column(name = Naming.PHONE_NUMBER)
    private String phoneNumber;

    @Column(name = Naming.EMAIL)
    private String email;

    @Column(name = Naming.FAX)
    private String fax;

    @Column(name = Naming.OWNER_UUID)
    private String ownerUuid;

    @Column(name = Naming.ADDRESS)
    private String address;

    @Column(name = Naming.LAT)
    private float lat;

    @Column(name = Naming.LNG)
    private float lng;

    @Column(name = Naming.ALT)
    private float alt;

    @Column(name = Naming.CREATED_AT)
    private String createdAt;

    @Column(name = Naming.UPDATED_AT)
    private String updatedAt;

    @Id
    @Column(name = Naming.UUID)
    private String uuid;

}
