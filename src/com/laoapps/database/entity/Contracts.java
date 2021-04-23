package com.laoapps.database.entity;

import com.laoapps.utils.Naming;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = Naming.CONTRACTS_TABLE_NAME)
public class Contracts {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Naming.ID)
    private int id;

    @Column(name = Naming.USER_UUID)
    private String userUuid;

    @Column(name = Naming.COMPANY_UUID)
    private String companyUuid;

    @Column(name = Naming.INVITE_UUID)
    private String inviteUuid;

    @Column(name = Naming.CREATED_AT)
    private String createdAt;

    @Id
    @Column(name = Naming.UUID)
    private String uuid;

}
