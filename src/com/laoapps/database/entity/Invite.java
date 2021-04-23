package com.laoapps.database.entity;

import com.laoapps.utils.Naming;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = Naming.INVITE)
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Naming.ID)
    private int id;

    @Column(name = Naming.POSITION)
    private String position;

    @Column(name = Naming.DESCRIPTION)
    private String description;

    @Column(name = Naming.ROLE)
    private String role;

    @Column(name = Naming.SALARY)
    private String salary;

    @Column(name = Naming.COMPANY_UUID)
    private String companyUuid;

    @Column(name = Naming.TO_UUID)
    private String toUuid;

    @Column(name = Naming.CREATED_AT)
    private String createdAt;

    @Column(name = Naming.UPDATED_AT)
    private String updatedAt;

    @Column(name = Naming.ACCEPTED_AT)
    private String acceptedAt;

    @Column(name = Naming.STATUS)
    private String status;

    @Column(name = Naming.UUID)
    private String uuid;

}
