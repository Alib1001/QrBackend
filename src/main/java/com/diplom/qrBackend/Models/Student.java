package com.diplom.qrBackend.Models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Student")
public class Student extends User {

    @Column(name = "groupName")
    private String groupName;

    @Column(name = "specialization")
    private String specialization;

    @ManyToOne
    @JoinColumn(name = "timetableId")
    private TimeTable timeTable;


    public Student() {
        super();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String group) {
        this.groupName = group;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(TimeTable timeTable) {
        this.timeTable = timeTable;
    }


    public Student(String username, String password, String firstName, String lastName, String groupName, String specialization, String userType) {
        super(username, password, firstName, lastName, userType);
        this.groupName = groupName;
        this.specialization = specialization;
    }
}
