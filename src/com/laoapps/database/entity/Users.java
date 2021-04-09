package com.laoapps.database.entity;

import com.laoapps.utils.Naming;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = Naming.USERS_TABLE_NAME)
public class Users {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Naming.ID)
    private int id;

    @Column(name = Naming.PHONE_NUMBER)
    private String phoneNumber;

    @Column(name = Naming.PASSWORD)
    private String password;

    @Column(name = Naming.CREATED_AT)
    private String createdAt;

    @Column(name = Naming.UPDATED_AT)
    private String updatedAt;

    @Id
    @Column(name = Naming.UUID)
    private String uuid;

    @Column(name = Naming.PARENT)
    private String parent;

    @Column(name = Naming.ROLE)
    private String role;

    @Column(name = Naming.SIGNATURE)
    private String signature;

}
