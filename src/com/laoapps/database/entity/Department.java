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
@Table(name = Naming.DEPARTMENTS_TABLE_NAME)
public class Department {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Naming.ID)
    private int id;

    @Column(name = Naming.NAME)
    private String name;

    @Column(name = Naming.PARENT)
    private String parent;

    @Column(name = Naming.REMARK)
    private String remark;

    @Column(name = Naming.CREATED_AT)
    private String createdAt;

    @Column(name = Naming.UPDATED_AT)
    private String updatedAt;

    @Id
    @Column(name = Naming.UUID)
    private String uuid;
}
