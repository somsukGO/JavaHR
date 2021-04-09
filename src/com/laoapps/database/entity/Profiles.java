package com.laoapps.database.entity;


import com.laoapps.utils.Naming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = Naming.PROFILES_TABLE_NAME)
public class Profiles {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Naming.ID)
    private int id;

    @Column(name = Naming.FULL_NAME)
    private String fullName;

    @Column(name = Naming.BIRTH_DATE)
    private String birthDate;

    @Column(name = Naming.ID_CARD)
    private String idCard;

    @Column(name = Naming.PASSPORT)
    private String passport;

    @Column(name = Naming.CREATED_AT)
    private String createdAt;

    @Column(name = Naming.UPDATED_AT)
    private String updatedAt;

    @Id
    @Column(name = Naming.UUID)
    private String uuid;
}