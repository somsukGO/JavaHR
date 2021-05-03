package com.laoapps.database.entity;

import com.laoapps.utils.Naming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = Naming.PERSONNEL_TABLE_NAME)
public class Personnel {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Naming.ID)
    private int id;

    @Column(name = Naming.FIRST_NAME)
    private String firstName;

    @Column(name = Naming.LAST_NAME)
    private String lastName;

    @Column(name = Naming.PHONE_NUMBER)
    private String phoneNumber;

    @Column(name = Naming.EMAIL)
    private String email;

    @Column(name = Naming.ADDRESS)
    private String address;

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

    @Column(name = Naming.POSITION)
    private String position;

    @Column(name = Naming.ROLE)
    private String role;

    @Column(name = Naming.DEPARTMENT_UUID)
    private String departmentUuid;

    @Column(name = Naming.SALARY)
    private String salary;

    @Column(name = Naming.USER_UUID)
    private String userUuid;

    @Id
    @Column(name = Naming.UUID)
    private String uuid;

    @Transient
    private int age;

    public void calculateAge() {
        age = Period.between(LocalDate.parse(birthDate), LocalDate.now()).getYears();
    }
}