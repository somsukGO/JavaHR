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
@Table(name = Naming.ATTENDANCE_TABLE_NAME)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Naming.ID)
    private int id;

    @Column(name = Naming.DATE)
    private String date;

    @Column(name = Naming.START_TIME)
    private String startTime;

    @Column(name = Naming.END_TIME)
    private String endTime;

    @Column(name = Naming.MINUTES)
    private int minutes;

    @Column(name = Naming.START_DESCRIPTION)
    private String startDescription;

    @Column(name = Naming.START_REASON)
    private String startReason;

    @Column(name = Naming.START_ATTACHMENT)
    private String startAttachment;

    @Column(name = Naming.END_DESCRIPTION)
    private String endDescription;

    @Column(name = Naming.END_REASON)
    private String endReason;

    @Column(name = Naming.END_ATTACHMENT)
    private String endAttachment;

    @Column(name = Naming.BY_UUID)
    private String byUuid;

    @Column(name = Naming.APPROVED_BY_UUID)
    private String approvedByUuid;

    @Column(name = Naming.APPROVED_TIME)
    private String approvedTime;
}
