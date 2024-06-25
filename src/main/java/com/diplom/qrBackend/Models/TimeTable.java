package com.diplom.qrBackend.Models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "timetable")
public class TimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dayOfWeek")
    private String dayOfWeek;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "scheduleGroupId")
    private Integer scheduleGroupId;
    @Column(name = "endTime")
    private String endTime;

    @Column(name = "groupName")
    private String groupName;

    @Column(name = "startTime")
    private String startTime;

    @Column(name = "subjectName")
    private String subjectName;

    @Column(name = "weekNumber")
    private int weekNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "date")
    private Date date;

    @ElementCollection
    @CollectionTable(name = "studentIds", joinColumns = @JoinColumn(name = "timetable_id"))
    private List<Integer> studentIds;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;



    @Column(name = "scanable")
    private Boolean scanable;

    public TimeTable() {
    }

    public TimeTable(String groupName, String subjectName, String dayOfWeek, String startTime, String endTime, int weekNumber) {
        this.groupName = groupName;
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekNumber = weekNumber;
    }

    public TimeTable(Integer scheduleGroupId,String groupName, String subjectName, String dayOfWeek, String startTime
            , String endTime,Room room, int weekNumber, Teacher teacher, Date date, Boolean scanable) {
        this.scheduleGroupId = scheduleGroupId;
        this.groupName = groupName;
        this.date = date;
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekNumber = weekNumber;
        this.teacher = teacher;
        this.room = room;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public List<Integer> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Integer> studentIds) {
        this.studentIds = studentIds;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }


    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Room getClassroom() {
        return room;
    }

    public void setClassroom(Room classroom) {
        this.room = classroom;
    }

    public Integer getScheduleGroupId() {
        return scheduleGroupId;
    }

    public void setScheduleGroupId(Integer scheduleGroupId) {
        this.scheduleGroupId = scheduleGroupId;
    }

    public Boolean isScanable() {
        return scanable;
    }

    public void setScanable(Boolean scanable) {
        this.scanable = scanable;
    }
}
